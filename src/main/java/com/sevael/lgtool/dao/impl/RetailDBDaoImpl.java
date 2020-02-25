package com.sevael.lgtool.dao.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.sevael.lgtool.configuration.LgtDBFactory;
import com.sevael.lgtool.dao.CommonDBDao;
import com.sevael.lgtool.dao.RetailDBDao;
import com.sevael.lgtool.model.ContactModel;
import com.sevael.lgtool.utils.AppConstants;
import com.sevael.lgtool.utils.JsonUtil;
import com.sevael.lgtool.utils.UtilConstants;

@Repository
public class RetailDBDaoImpl implements AppConstants, UtilConstants, RetailDBDao {

	@Autowired
	private CommonDBDao commonDBDao;

	@Override
	public String save(JsonObject retailObj) {
		MongoClient cli = null;
		final String METHODNAME = "[save]";
		JsonObject returnStatus = new JsonObject();
		String contactID = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:mm:ss a");
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_RETAIL);
			Document retailDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_RETAIL,
					Filters.or(
							Filters.regex("phonenumber", "^" + retailObj.get("phonenumber").getAsString() + "$", "i")),
					null);
			if (retailDoc == null) {
				retailDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_RETAIL,
						Filters.or(Filters.regex("email", "^" + retailObj.get("email").getAsString() + "$", "i")),
						null);
				if (retailDoc == null) {
					retailDoc = new Document();
					int seq = commonDBDao.getNextSequence(db, COLL_NAME_RETAIL);
					contactID = COLL_NAME_RETAIL + seq;
					retailDoc.put("_id", contactID);
					retailDoc.put("seq", seq);
					String[] fields = { "firstname", "middlename", "lastname", "email", "countryphcode",
							"altercountryphcode", "phonenumber", "alterphonenum", "placeofbirth", "leadtype",
							"additionalinfo", "city", "pincode", "state", "country", "doorno", "street", "userrole",
							"dob", "gender", "orgname", "type" };
					for (String field : fields) {
						if (retailObj.has(field)) {
							retailDoc.put(field, retailObj.get(field).getAsString());
						}
					}

					retailDoc.put("isimgfound", "false");
					retailDoc.put("creationdate", formatter.format(new Date()));
					retailDoc.put("creationdateval", LocalDate.now().toEpochDay());

					table.insertOne(retailDoc);

					addLeadCount(db);
					returnStatus.addProperty("_id", contactID);
					returnStatus.addProperty(MSG_SUCCESS, ADD_CONTACT_SUCCESS);
					returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);
				} else {
					returnStatus.addProperty("isexists", "email");
					returnStatus.addProperty(RS_MESSAGE, ADD_CONTACT_EXISTS);
				}
			} else {
				returnStatus.addProperty("isexists", "phone");
				returnStatus.addProperty(RS_MESSAGE, ADD_CONTACT_EXISTS);
			}
		} catch (MongoException e) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			System.out.println("ContactDaoImpl 95-->" + METHODNAME + " Mongo Exception : " + e);
		} catch (Exception ex) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			ex.printStackTrace();
		} finally {
			if (cli != null) {
				LgtDBFactory.closeMongoCLient(cli);
			}
		}
		System.out.println("returnStatus.toString()" + returnStatus.toString());
		return returnStatus.toString();
	}

	private void addLeadCount(MongoDatabase db) {
		Document leadDashboardDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_DASHBOARD_LEAD_DATA,
				Filters.eq("month", LocalDate.now().getMonthValue()),

				null);
		MongoCollection<Document> dashtable = db.getCollection(COLL_NAME_DASHBOARD_LEAD_DATA);
		if (leadDashboardDoc != null) {
			dashtable.updateOne(Filters.eq("_id", leadDashboardDoc.getString("_id")),
					new Document("$inc", new Document("count", 1)));
		} else {
			leadDashboardDoc = new Document();
			leadDashboardDoc.put("month", LocalDate.now().getMonthValue());
			leadDashboardDoc.put("count", 1);
			int dealseq = commonDBDao.getNextSequence(db, COLL_NAME_DASHBOARD_LEAD_DATA);
			leadDashboardDoc.put("_id", COLL_NAME_DASHBOARD_LEAD_DATA + dealseq);
			leadDashboardDoc.put("seq", dealseq);
			dashtable.insertOne(leadDashboardDoc);
		}

	}

	public GridFSDBFile getProfileImage(String _id) {
		MongoClient cli = LgtDBFactory.getMongoClient();
		DB db = cli.getDB(CONTACT_IMAGE_DATABASE_NAME);// IMAGE_DATABASE_NAME
		GridFS gfs = new GridFS(db);
		GridFSDBFile gfsdb = null;
		DBObject query = new BasicDBObject();
		query.put("cus_filename", _id);
		gfsdb = gfs.findOne(query);
		return gfsdb;
	}

	private int getNextServiceTypeSequence(MongoDatabase db, String typeOfseq, String contactID) {
		int nextSeq = 0;
		Document seqDoc = (Document) db.getCollection(COLL_NAME_RETAIL).find(Filters.eq("_id", contactID))
				.projection(Projections.include(typeOfseq)).sort(Sorts.descending(typeOfseq)).limit(1).first();
		System.out.println("ContactDaoImpl --> line 161 --- >" + seqDoc);
		if (seqDoc != null) {
			nextSeq = seqDoc.getInteger(typeOfseq);
		}
		return nextSeq + 1;
	}

	@Override
	public List<Document> list(String type, String searchstr, int page) {
		List<Document> contacts = new ArrayList<Document>();
		Bson retailFinalFilter = null;

		String[] retailfields = { "firstname", "email", "countryphcode", "phonenumber", "alterphonenum" };

		int pageLimit = 10;
		int numberOfRecordsSkipPerPage = 0;
		if (page > 1) {
			numberOfRecordsSkipPerPage = (10 * page);
			numberOfRecordsSkipPerPage -= 10;
		}

		System.out.println("numberOfRecordsSkipPerPage " + numberOfRecordsSkipPerPage);

		Bson mainFilter = Filters.eq("type", type);

		try {
			if (searchstr != null && searchstr.length() > 0) {
				for (String field : retailfields) {
					if (retailFinalFilter != null) {
						retailFinalFilter = Filters.or(retailFinalFilter,
								Filters.regex(field, "^" + searchstr + ".*", "i"));
					} else {
						retailFinalFilter = Filters.regex(field, "^" + searchstr + ".*", "i");
					}
				}
				mainFilter = Filters.and(retailFinalFilter, mainFilter);
			}

			contacts = commonDBDao.getAllEntityWithPagination(DATABASE_NAME, COLL_NAME_RETAIL, mainFilter,
					Sorts.descending("seq"), null, numberOfRecordsSkipPerPage, pageLimit);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return contacts;
	}

	@Override
	public Map<String, List<Document>> search(String searchstr) {
		Map<String, List<Document>> searchresult = new HashMap<String, List<Document>>();
		Bson retailFinalFilter = null;
		Bson businessFinalFilter = null;

//		String[] retailfields = { "firstname", "middlename", "lastname", "email", "phonenumber", "alterphonenum",
//				"placeofbirth", "leadtype", "additionalinfo", "city", "pincode", "state", "country", "doorno", "street",
//				"userrole", "dob", "gender", "orgname", "type" };

		String[] retailfields = { "firstname", "email", "countryphcode", "phonenumber",
				"alterphonenum" };

		if (searchstr != null && searchstr.length() > 0) {
			for (String field : retailfields) {
				if (retailFinalFilter != null) {
					retailFinalFilter = Filters.or(retailFinalFilter,
							Filters.regex(field, "^" + searchstr + ".*", "i"));
				} else {
					retailFinalFilter = Filters.regex(field, "^" + searchstr + ".*", "i");
				}
			}
		}

		String[] businessfields = { "companyname", "city", "pincode", "state", "country", "doorno", "street", "type",
				"companyemailid", "gst", "leadtype", "leadname" };

		if (searchstr != null && searchstr.length() > 0) {
			for (String field : businessfields) {
				if (businessFinalFilter != null) {
					businessFinalFilter = Filters.or(businessFinalFilter,
							Filters.regex(field, "^" + searchstr + ".*", "i"));
				} else {
					businessFinalFilter = Filters.regex(field, "^" + searchstr + ".*", "i");
				}

			}
		}
		List<Document> retail = new ArrayList<Document>();
		List<Document> data = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_RETAIL,
				Filters.and(retailFinalFilter, Filters.eq("type", "lead")), null, null, 0);
		if (data != null) {
			retail.addAll(data);
		}

		data = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_RETAIL,
				Filters.and(retailFinalFilter, Filters.eq("type", "contact")), null, null, 0);
		if (data != null) {
			retail.addAll(data);
		}

		List<Document> business = new ArrayList<Document>();
		data = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_BUSINESS,
				Filters.and(businessFinalFilter, Filters.eq("type", "lead")), null, null, 0);
		if (data != null) {
			business.addAll(data);
		}

		data = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_BUSINESS,
				Filters.and(businessFinalFilter, Filters.eq("type", "contact")), null, null, 0);
		if (data != null) {
			business.addAll(data);
		}

		searchresult.put("retail", retail);
		searchresult.put("business", business);
		return searchresult;
	}

	@Override
	public String update(JsonObject updateDetails) {
		MongoClient cli = null;
		JsonObject returnStatus = new JsonObject();
		String contactID = "";
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_RETAIL);
			Document dealupdateDoc = new Document();
			contactID = updateDetails.get("_id").getAsString();
			Document updateDoc = new Document();
			String[] fields = { "firstname", "middlename", "lastname", "email", "countryphcode", "altercountryphcode",
					"phonenumber", "alterphonenum", "placeofbirth", "leadtype", "additionalinfo", "city", "pincode",
					"state", "country", "doorno", "street", "userrole", "dob", "gender", "orgname", "type" };
			for (String field : fields) {
				if (updateDetails.has(field)) {
					updateDoc.put(field, updateDetails.get(field).getAsString());
					if (field.equalsIgnoreCase("phonenumber")) {
						dealupdateDoc.put("custphone", updateDetails.get(field).getAsString());
					} else if (field.equalsIgnoreCase("email")) {
						dealupdateDoc.put("custemail", updateDetails.get(field).getAsString());
					} else if (field.equalsIgnoreCase("firstname")) {
						dealupdateDoc.put("custname", updateDetails.get(field).getAsString());
					}
				}
			}
			table.updateOne(Filters.eq("_id", contactID), new Document("$set", updateDoc));
			MongoCollection<Document> dealTable = db.getCollection(COLL_NAME_DEALS);
			dealTable.updateMany(Filters.eq("custid", contactID), new Document("$set", dealupdateDoc),
					new UpdateOptions());
			returnStatus.addProperty("businessID", contactID);
			returnStatus.addProperty(MSG_SUCCESS, ADD_UPDATECOMMON_SUCCESS);
			returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);

		} catch (Exception ex) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			System.out.println("ContactDaoImpl --> " + ex.toString());
			ex.printStackTrace();
		}
		return returnStatus.toString();
	}

	public String filter(JsonArray filterby) {
		List<Document> filteredContacts = null;
		System.out.println("filterby ---> " + filterby);
		Bson finalFilter = null;
		Bson serveTypeFilter = null;
		List<String> serveTypeList = new ArrayList<>();
		if (filterby != null) {
			for (int i = 0; i < filterby.size(); i++) {
				serveTypeList.add(filterby.get(i).getAsString());
			}
		}
		serveTypeFilter = serveTypeList.size() == 0 ? null : Filters.in("serveid", serveTypeList);

		if (serveTypeFilter != null) {
			finalFilter = Filters.and(serveTypeFilter);
		}
		filteredContacts = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_RETAIL, finalFilter, null, null, 0);

		JsonArray filterContacts = new JsonArray();
		for (Document doc : filteredContacts) {
			filterContacts.add(JsonUtil.parseJSON(doc));
		}

		return filterContacts.toString();
	}

	@Override
	public String downloadExcel(String type) {

		ContactGenerateExcel nmcsGenerateExcel = new ContactGenerateExcel();
		String excelname = "";
		List<Document> retail = null;
		try {
			if (type.equals("all")) {
				retail = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_RETAIL, null, null, null, 0);
			} else {
				retail = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_RETAIL, Filters.eq("type", type), null, null,
						0);
			}
			excelname = nmcsGenerateExcel.getRetailExcel(retail);

		} catch (Exception ex) {
			System.out.println("ContactsDaoImpl --- downloadExcel ---> " + ex.toString());
		}

		return excelname;

	}

	@Override
	public String updateServiceStatus(String _id, String serv_id, String status) {
		MongoClient cli = null;
		List<Document> servetypeList = new ArrayList<Document>();
		JsonArray filterServTypeList = new JsonArray();
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_RETAIL);

			table.updateOne(Filters.and(Filters.eq("_id", _id), Filters.eq("serviceType._id", serv_id)),
					new Document("$set", new Document("serviceType.$.status", status)));

			List<Document> contacts = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_RETAIL, Filters.eq("_id", _id),
					null, null, 0);
			System.out.println("ContactDaoImpl -->serveType response " + contacts.toString());
			for (Document contactDoc : contacts) {
				servetypeList = (List<Document>) contactDoc.get("serviceType");
			}
			String found = null;

			for (Document doc : servetypeList) {
				if (doc.getString("status").toString().equals("lead")) {
					found = "yes";
				}
				filterServTypeList.add(JsonUtil.parseJSON(doc));
			}

			if (found == null) {
				table.updateOne(Filters.eq("_id", _id), new Document("$set", new Document("status", "active")));
			}
		} catch (Exception ex) {
			System.out.println("ContactsDaoImpl --- deleteService ---> " + ex.toString());
		}

		return filterServTypeList.toString();
	}

	@Override
	public String downloadFilterExcel(JsonObject filterDetails) {
		MongoClient cli = null;
		ContactGenerateExcel contactGenerateExcel = new ContactGenerateExcel();
		String excelname = null;
		JsonObject returnStatus = new JsonObject();
		List<Document> contacts = null;
		List<Document> filterContacts = new ArrayList<>();
		String servename = filterDetails.get("servename").getAsString();
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_RETAIL);
			contacts = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_RETAIL, null, null, null, 0);

//			Document categoryDoc = (Document) table.aggregate(
//					Arrays.asList(Aggregates.match(Filters.eq("status", "active")), Aggregates.unwind("$serviceType"),
//							Aggregates.match(Filters.eq("serviceType.servename", servename))));
//			System.out.println("ContactsDaoImpl --- downloadExcel ---> categoryDoc  === " + categoryDoc.toString());
//			for (int i = 0; i < contacts.size(); i++) {
//				Document docObj = contacts.get(i);
//				System.out.println("ContactsDaoImpl --> next "+ i +"   --->_id"+ docObj.get("_id").toString() );
//				BsonDocument  categoryDoc1 = (BsonDocument) table.aggregate(
//						Arrays.asList(Aggregates.match(Filters.eq("_id", docObj.get("_id").toString())), Aggregates.unwind("$serviceType"),
//								Aggregates.match(Filters.eq("serviceType.servename", servename))));
//				System.out.println("ContactsDaoImpl --- downloadExcel ---> categoryDoc1  === " + categoryDoc1.toString());
//				filterContacts.add(categoryDoc1);
//			}

			for (int i = 0; i < contacts.size(); i++) {
				Document docObj = contacts.get(i);

				List<Document> serviceTypeList = new ArrayList<>();
				if (docObj.containsKey("serviceType")) {
					serviceTypeList = (List<Document>) docObj.get("serviceType");
					if (serviceTypeList != null) {
						if (serviceTypeList.size() > 0) {
							for (int j = 0; j < serviceTypeList.size(); j++) {
								if (serviceTypeList.get(j).get("servename").toString().equals(servename)) {
									filterContacts.add(docObj);
									break;
								}
							}
						}
					}
				}

//				for (Document document : cursor) {
//				    // the serviceTypeList attribute is an array of sub documents
//				    // so read it as a List and then iterate over that List
//				    // with each element in the List being a Document
//				    serviceTypeList = document.get("serviceType", List.class);
//				}

			}
			System.out.println("ContactsDaoImpl --- filterContacts list--> " + filterContacts.toString());
			if (filterContacts.size() > 0) {
				excelname = contactGenerateExcel.getRetailExcel(filterContacts);
			} else {
				returnStatus.addProperty(RS_STATUS, "Unable to create excel sheet");
			}

		} catch (Exception ex) {
			System.out.println("ContactsDaoImpl --- downloadFilterExcel ---> " + ex.toString());
		}
		return excelname;
	}

	private void copyFileToLocal(InputStream fileStream, String filename) {
		OutputStream outputStream = null;
		filename = excelfilePath + filename + ".xlsx";
		try {
			outputStream = new FileOutputStream(new File(filename));
			int read = 0;
			byte[] bytes = new byte[1024];
			try {
				while ((read = fileStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				if (fileStream != null) {
					try {
						fileStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	private String evaluateCell(FormulaEvaluator objFormulaEvaluator, DataFormatter formatter, Cell cell) {
		objFormulaEvaluator.evaluate(cell);
		String cellValueStr = formatter.formatCellValue(cell, objFormulaEvaluator);
		return cellValueStr;
	}

	@Override
	public String addActivity(JsonObject activity, String id) {
		Document activDoc = new Document();
		MongoClient cli = null;
		JsonObject returnStatus = new JsonObject();
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_RETAIL);
			activDoc.put("type", activity.get("type").getAsString());
			activDoc.put("notes", activity.get("notes").getAsString());
			activDoc.put("username", activity.get("username").getAsString());
			activDoc.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
			activDoc.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyy")));
			table.updateOne(Filters.eq("_id", id), new Document("$push", new Document("activity", activDoc)));
			returnStatus.addProperty("businessID", id);
			returnStatus.addProperty(MSG_SUCCESS, ADD_UPDATECOMMON_SUCCESS);
			returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);
		} catch (Exception ex) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			System.out.println("ContactDaoImpl --> " + ex.toString());
		}
		return returnStatus.toString();

	}

	@Override
	public String uploadRetail(InputStream inputStream, String filename) {
		System.out.println("updatebulkList - excel upload");
		copyFileToLocal(inputStream, filename);
		filename = excelfilePath + filename + ".xlsx";
		JsonObject returnStatus = new JsonObject();
		try {
			FileInputStream excelFile = new FileInputStream(new File(filename));
			Workbook wb = new XSSFWorkbook(excelFile);
			Sheet mySheet = wb.getSheetAt(0);
			DataFormatter formatter = new DataFormatter();
			FormulaEvaluator objFormulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) wb);
			Iterator<Row> rowIterator = mySheet.iterator();
			ContactModel contactModel = null;
			List<ContactModel> contactModelList = new ArrayList<ContactModel>();
			Iterator<Cell> cellIterator;
			while (rowIterator.hasNext()) {
				contactModel = new ContactModel();
				Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
						Pattern.CASE_INSENSITIVE);
				Pattern namePattern = Pattern.compile("^[a-zA-Z]+$");
				Pattern phonePattern = Pattern.compile("^[0-9]+$");

				Row row = (Row) rowIterator.next();
				if (row.getRowNum() != 0) {
					cellIterator = row.cellIterator();
					while (cellIterator.hasNext()) {
						Cell cell = (Cell) cellIterator.next();

						if (cell.getColumnIndex() == 0) {
							String cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
							if (namePattern.matcher(cellValueStr).matches()) {
								contactModel.setFirstname(cellValueStr);
							}
						}
						if (cell.getColumnIndex() == 1) {
							String cellValueStr = "";
							if (cell != null && cell.getCellType() != 3) {
								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
								if (cellValueStr.length() == 0)
									cellValueStr = "";
							}
							if (cellValueStr.length() > 0 && namePattern.matcher(cellValueStr).matches()) {
								contactModel.setMiddlename(cellValueStr);
							}
						}

						if (cell.getColumnIndex() == 2) {
							String cellValueStr = "";
							if (cell != null && cell.getCellType() != 3) {
								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
								if (cellValueStr.length() == 0)
									cellValueStr = "";
							}
							if (cellValueStr.length() > 0 && namePattern.matcher(cellValueStr).matches()) {
//								contactModel.setMiddlename(cellValueStr);
								contactModel.setLastname(cellValueStr);
							}

//							contactModel.setLastname(cellValueStr);
						}
						if (cell.getColumnIndex() == 3) {
							String cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
							if (cellValueStr.length() > 0 && emailPattern.matcher(cellValueStr).matches()) {
								contactModel.setEmailid(cellValueStr);
							}
						}

						if (cell.getColumnIndex() == 4) {
							String cellValueStr = "";
							if (cell != null && cell.getCellType() != 3) {
								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);

								if (cellValueStr.length() == 0) {
									cellValueStr = "";
								}
							}
							if (cellValueStr.length() > 0) {
								contactModel.setCountryphcode(cellValueStr);
							} else {
								contactModel.setCountryphcode(cellValueStr);
							}

						}

						if (cell.getColumnIndex() == 5) {
							String cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
							if (cellValueStr.length() > 0 && phonePattern.matcher(cellValueStr).matches()) {
								contactModel.setPhonenum(cellValueStr);
							}
						}

						if (cell.getColumnIndex() == 6) {
							String cellValueStr = "";
							if (cell != null && cell.getCellType() != 3) {
								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);

								if (cellValueStr.length() == 0) {
									cellValueStr = "";
								}
							}
							if (cellValueStr.length() > 0) {
								contactModel.setAltercountryphcode(cellValueStr);
							} else {
								contactModel.setAltercountryphcode(cellValueStr);
							}

						}

						if (cell.getColumnIndex() == 7) {
							String cellValueStr = "";
							if (cell != null && cell.getCellType() != 3) {
								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
								if (cellValueStr.length() == 0)
									cellValueStr = "";
							}
							if (cellValueStr.length() > 0 && phonePattern.matcher(cellValueStr).matches()) {
								contactModel.setAlterphonenum(cellValueStr);
							}
						}

						if (cell.getColumnIndex() == 8) {
							String cellValueStr = "";
							if (cell != null && cell.getCellType() != 3) {
								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
								if (cellValueStr.length() == 0)
									cellValueStr = "";
							}
							contactModel.setGender(cellValueStr);
						}

						if (cell.getColumnIndex() == 9) {
							String cellValueStr = "";
							if (cell != null && cell.getCellType() != 3) {
								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
								if (cellValueStr.length() == 0)
									cellValueStr = "";
							}
							contactModel.setDateofbirth(cellValueStr);
						}

						if (cell.getColumnIndex() == 10) {
							String cellValueStr = "";
							if (cell != null && cell.getCellType() != 3) {
								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
								if (cellValueStr.length() == 0)
									cellValueStr = "";
							}
							contactModel.setPlaceofbirth(cellValueStr);
						}

						if (cell.getColumnIndex() == 11) {
							String cellValueStr = "";
							if (cell != null && cell.getCellType() != 3) {
								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
								if (cellValueStr.length() == 0)
									cellValueStr = "";
							}
							contactModel.setUserrole(cellValueStr);
						}

						if (cell.getColumnIndex() == 12) {
							String cellValueStr = "";
							if (cell != null && cell.getCellType() != 3) {
								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
								if (cellValueStr.length() == 0)
									cellValueStr = "";
							}
							contactModel.setLeadtype(cellValueStr);
						}

						if (cell.getColumnIndex() == 13) {
							String cellValueStr = "";
							if (cell != null && cell.getCellType() != 3) {
								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
								if (cellValueStr.length() == 0)
									cellValueStr = "";
							}
							contactModel.setOrgname(cellValueStr);
						}

						if (cell.getColumnIndex() == 14) {
							String cellValueStr = "";
							if (cell != null && cell.getCellType() != 3) {
								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
								if (cellValueStr.length() == 0)
									cellValueStr = "";
							}
							contactModel.setAdditionalinfo(cellValueStr);
						}

//						if (cell.getColumnIndex() == 13) {
//							String cellValueStr = "";
//							if (cell != null && cell.getCellType() != 3) {
//								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
//								if (cellValueStr.length() == 0)
//									cellValueStr = "";
//							}
//							contactModel.setEmpemail(cellValueStr);
//						}

						if (cell.getColumnIndex() == 15) {
							String cellValueStr = "";
							if (cell != null && cell.getCellType() != 3) {
								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
								if (cellValueStr.length() == 0)
									cellValueStr = "";
							}
							contactModel.setDoorno(cellValueStr);
						}

						if (cell.getColumnIndex() == 16) {
							String cellValueStr = "";
							if (cell != null && cell.getCellType() != 3) {
								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
								if (cellValueStr.length() == 0)
									cellValueStr = "";
							}
							contactModel.setStreet(cellValueStr);
						}

						if (cell.getColumnIndex() == 17) {
							String cellValueStr = "";
							if (cell != null && cell.getCellType() != 3) {
								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
								if (cellValueStr.length() == 0)
									cellValueStr = "";
							}
							contactModel.setCity(cellValueStr);
						}

						if (cell.getColumnIndex() == 18) {
							String cellValueStr = "";
							if (cell != null && cell.getCellType() != 3) {
								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
								if (cellValueStr.length() == 0)
									cellValueStr = "";
							}
							contactModel.setState(cellValueStr);
						}

						if (cell.getColumnIndex() == 19) {
							String cellValueStr = "";
							if (cell != null && cell.getCellType() != 3) {
								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
								if (cellValueStr.length() == 0)
									cellValueStr = "";
							}
							contactModel.setCountry(cellValueStr);
						}

						if (cell.getColumnIndex() == 20) {
							String cellValueStr = "";
							if (cell != null && cell.getCellType() != 3) {
								cellValueStr = evaluateCell(objFormulaEvaluator, formatter, cell);
								if (cellValueStr.length() == 0)
									cellValueStr = "";
							}
							contactModel.setPincode(cellValueStr);
						}

					}
					contactModelList.add(contactModel);
				}
			}

			if (contactModelList.size() > 0) {
				for (ContactModel contactModel1 : contactModelList) {
					long id = updateContact(contactModel1);
				}
				System.out.println("Updated success - excel upload");
				returnStatus.addProperty("status", "success");
			} else {
				returnStatus.addProperty("status", "Failed");
			}
			wb.close();
		} catch (FileNotFoundException e1) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			e1.printStackTrace();
			returnStatus.addProperty("status", e1.toString());
		} catch (IOException e) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			e.printStackTrace();
			returnStatus.addProperty("status", e.toString());
		} catch (Exception e) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			e.printStackTrace();
			returnStatus.addProperty("status", e.toString());
		} finally {
			try {
				Files.deleteIfExists(Paths.get(filename, new String[0]));
			} catch (NoSuchFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return returnStatus.toString();

	}

	private String getDateFormat(String cellDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		String final_date = "";
		try {
			Date varDate = dateFormat.parse(cellDate);
			dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			final_date = dateFormat.format(varDate);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return final_date;
	}

	private long updateContact(ContactModel contactModel) {

		MongoClient cli = null;
		final String METHODNAME = "[createContactExcel]";
		String contactID = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:mm:ss a");

		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_RETAIL);

			Document contactDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_RETAIL,
					Filters.or(Filters.regex("phonenumber", "^" + contactModel.getPhonenum().toString() + "$", "i")),
					null);
			if (contactDoc == null) {
				contactDoc = new Document();
				int seq = commonDBDao.getNextSequence(db, COLL_NAME_RETAIL);
				contactID = COLL_NAME_RETAIL + seq;
				contactDoc.put("_id", contactID);
				contactDoc.put("seq", seq);
				contactDoc.put("firstname", contactModel.getFirstname().toString());
				if (contactModel.getMiddlename() != null) {
					contactDoc.put("middlename", contactModel.getMiddlename().toString());
				} else {
					contactDoc.put("middlename", "");
				}
				if (contactModel.getLastname() != null) {
					contactDoc.put("lastname", contactModel.getLastname().toString());
				} else {
					contactDoc.put("lastname", "");
				}
				contactDoc.put("phonenumber", contactModel.getPhonenum().toString());
				contactDoc.put("email", contactModel.getEmailid().toString());

				contactDoc.put("userrole", contactModel.getUserrole());
				contactDoc.put("leadtype", contactModel.getLeadtype());

				contactDoc.put("countryphcode", contactModel.getCountryphcode());
				contactDoc.put("altercountryphcode", contactModel.getAltercountryphcode());

				contactDoc.put("additionalinfo", contactModel.getAdditionalinfo());
				contactDoc.put("alterphonenum", contactModel.getAlterphonenum());
				contactDoc.put("city", contactModel.getCity());
				contactDoc.put("country", contactModel.getCountry());
				contactDoc.put("dob", contactModel.getDateofbirth());
				contactDoc.put("doorno", contactModel.getDoorno());
//				if (contactModel.getEmpemail() != null && contactModel.getEmpemail().length() > 0) {
//					contactDoc.put("empid", "");
//				} else {
//					contactDoc.put("empid", "");
//				}
				contactDoc.put("gender", contactModel.getGender());
				contactDoc.put("orgname", contactModel.getOrgname());
				contactDoc.put("pincode", contactModel.getPincode());
				contactDoc.put("placeofbirth", contactModel.getPlaceofbirth());
				contactDoc.put("state", contactModel.getState());
				contactDoc.put("street", contactModel.getStreet());
				contactDoc.put("leadtype", contactModel.getLeadtype());
				contactDoc.put("type", "lead");

				contactDoc.put("servtypeseq", 0);
				contactDoc.put("status", "active");
				contactDoc.put("creationdate", formatter.format(new Date()));
				table.insertOne(contactDoc);
				addLeadCount(db);
			}
		} catch (MongoException e) {
			System.out.println("ContactDaoImpl 95-->" + METHODNAME + " Mongo Exception : " + e);
		} catch (Exception ex) {
			System.out.println("ContactDaoImpl 98-->" + METHODNAME + " Exception : " + ex.toString());
			ex.printStackTrace();
		} finally {
			if (cli != null) {
				LgtDBFactory.closeMongoCLient(cli);
			}
		}

		return 0;
	}

	@Override
	public String uploadImage(String _id, InputStream file) {
		JsonObject returnStatus = new JsonObject();
		returnStatus.addProperty("_id", _id);
		MongoClient cli = null;
		try {
			cli = LgtDBFactory.getMongoClient();
			DB db = cli.getDB(CONTACT_IMAGE_DATABASE_NAME);
			GridFS gfs = new GridFS(db);
			GridFSDBFile gfsdb = null;
			DBObject query = new BasicDBObject();
			query.put("cus_filename", _id);
			gfs.remove(query);
			GridFSInputFile gfsFile = null;
			gfsFile = gfs.createFile(file);
			gfsFile.put("cus_filename", _id);
			gfsFile.save();
			returnStatus.addProperty("status", "success");

			MongoDatabase db1 = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db1.getCollection(COLL_NAME_RETAIL);
			table.updateOne(Filters.eq("_id", _id), new Document("$set", new Document("isImgFound", "true")));

		} catch (MongoException e) {
			System.out.println("247 Mongo Exception : " + e.toString());
			returnStatus.addProperty("status", "failed");
		} catch (Exception ex) {
			System.out.println(" 251 Exception : " + ex.toString());
			returnStatus.addProperty("status", "failed");
		} finally {
			if (cli != null) {
				LgtDBFactory.closeMongoCLient(cli);
			}
		}

		return returnStatus.toString();
	}

	@Override
	public Document get(String id) {
		return commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_RETAIL, Filters.eq("_id", id), null);
	}

	@Override
	public String getPaginationCount(String type, String searchstr) {
		JsonObject returnStatus = new JsonObject();
		List<Document> contacts = new ArrayList<Document>();
		Bson retailFinalFilter = null;
		Bson mainFilter = Filters.eq("type", type);

		String[] retailfields = { "firstname", "email", "countryphcode", "phonenumber", "alterphonenum" };
		returnStatus.addProperty(PAGE_COUNT, 0);
		try {
			if (searchstr != null && searchstr.length() > 0) {
				for (String field : retailfields) {
					if (retailFinalFilter != null) {
						retailFinalFilter = Filters.or(retailFinalFilter,
								Filters.regex(field, "^" + searchstr + ".*", "i"));
					} else {
						retailFinalFilter = Filters.regex(field, "^" + searchstr + ".*", "i");
					}
				}
				mainFilter = Filters.and(retailFinalFilter, mainFilter);
			}

			contacts = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_RETAIL, mainFilter, Sorts.descending("seq"),
					null, 0);
			if (contacts.size() > 0) {
				returnStatus.addProperty(PAGE_COUNT, contacts.size());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return returnStatus.toString();

	}

}
