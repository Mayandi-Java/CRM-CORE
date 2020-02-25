package com.sevael.lgtool.dao.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.sevael.lgtool.configuration.LgtDBFactory;
import com.sevael.lgtool.dao.BusinessDBDao;
import com.sevael.lgtool.dao.CommonDBDao;
import com.sevael.lgtool.utils.AppConstants;
import com.sevael.lgtool.utils.JsonUtil;
import com.sevael.lgtool.utils.UtilConstants;

@Repository
public class BusinessDBDaoImpl implements AppConstants, UtilConstants, BusinessDBDao {

	@Autowired
	private CommonDBDao commonDBDao;

	public String save(JsonObject businessObj) {
		MongoClient cli = null;
		final String METHODNAME = "[createBusinessContact]";
		JsonObject returnStatus = new JsonObject();
		String businessID = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:mm:ss a");
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_BUSINESS);

			Document businessDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_BUSINESS, Filters.or(
					Filters.regex("companyname", "^" + businessObj.get("companyname").getAsString() + "$", "i")), null);

			JsonArray contactPersonArray = new JsonArray();

			if (businessDoc == null) {
				businessDoc = new Document();
				int seq = commonDBDao.getNextSequence(db, COLL_NAME_BUSINESS);
				businessID = COLL_NAME_BUSINESS + seq;
				businessDoc.put("_id", businessID);
				businessDoc.put("seq", seq);
				businessDoc.put("companyname", businessObj.get("companyname").getAsString());
				String[] fields = { "city", "pincode", "state", "country", "doorno", "street", "type", "companyemailid",
						"gst", "leadtype", "leadname" };
				for (String field : fields) {
					if (businessObj.has(field)) {
						businessDoc.put(field, businessObj.get(field).getAsString());
					}
				}

				businessDoc.put("contactpersonseq", 0);
				businessDoc.put("servtypeseq", 0);
				businessDoc.put("status", "active");
				businessDoc.put("creationdate", formatter.format(new Date()));
				table.insertOne(businessDoc);

				if (businessObj.has("contactpersons")) {
					contactPersonArray = businessObj.getAsJsonArray("contactpersons");
					if (contactPersonArray.size() > 0) {
						for (int i = 0; i < contactPersonArray.size(); i++) {
							Document contactPersonDoc = new Document();
							JsonObject contactpersonObj = new JsonObject();
							contactpersonObj = contactPersonArray.get(i).getAsJsonObject();
							int nextServTypeseq = getNextcontactTypeSequence(db, "contactpersonseq", businessID);
							String cpID = "contactperson" + nextServTypeseq;

							contactPersonDoc.put("_id", cpID);
							if (contactpersonObj.has("cpalternumber")) {
								contactPersonDoc.put("cpalternumber",
										contactpersonObj.get("cpalternumber").getAsString());
							}
							if (contactpersonObj.has("cpname")) {
								contactPersonDoc.put("cpname", contactpersonObj.get("cpname").getAsString());
							}
							if (contactpersonObj.has("cpemail")) {
								contactPersonDoc.put("cpemail", contactpersonObj.get("cpemail").getAsString());
							}
							if (contactpersonObj.has("cpphone")) {
								contactPersonDoc.put("cpphone", contactpersonObj.get("cpphone").getAsString());
							}

							if (contactpersonObj.has("countryphcode")) {
								contactPersonDoc.put("countryphcode",
										contactpersonObj.get("countryphcode").getAsString());
							}
							if (contactpersonObj.has("altercountryphcode")) {
								contactPersonDoc.put("altercountryphcode",
										contactpersonObj.get("altercountryphcode").getAsString());
							}

							table.updateOne(Filters.eq("_id", businessID),
									new Document("$push", new Document("contactpersons", contactPersonDoc)));
							table.updateOne(Filters.eq("_id", businessID),
									new Document("$inc", new Document("contactpersonseq", 1)));
						}
					}
				}

				returnStatus.addProperty("_id", businessID);
				returnStatus.addProperty(RS_MESSAGE, ADD_BUSINESS_SUCCESS);
				returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);
			} else {
				returnStatus.addProperty("isexists", true);
				returnStatus.addProperty(RS_MESSAGE, ADD_BUSINESS_EXISTS);
			}

		} catch (MongoException e) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			System.out.println("BusinessDBDaoImpl 95-->" + METHODNAME + " Mongo Exception : " + e);
		} catch (Exception ex) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			System.out.println("BusinessDBDaoImpl 98-->" + METHODNAME + " Exception : " + ex.toString());
			ex.printStackTrace();
		} finally {
			if (cli != null) {
				LgtDBFactory.closeMongoCLient(cli);
			}
		}
		return returnStatus.toString();
	}

	private int getNextcontactTypeSequence(MongoDatabase db, String typeOfseq, String businessID) {
		int nextSeq = 0;
		Document seqDoc = (Document) db.getCollection(COLL_NAME_BUSINESS).find(Filters.eq("_id", businessID))
				.projection(Projections.include(typeOfseq)).sort(Sorts.descending(typeOfseq)).limit(1).first();
		System.out.println("BusinessDBDaoImpl --> line 161 --- >" + seqDoc);
		if (seqDoc != null) {
			nextSeq = seqDoc.getInteger(typeOfseq);
		}
		return nextSeq + 1;
	}

	@Override
	public List<Document> list(String type, String searchstr, int page) {
		List<Document> contacts = null;

		int pageLimit = 10;
		int numberOfRecordsSkipPerPage = 0;
		if (page > 1) {
			numberOfRecordsSkipPerPage = (10 * page);
			numberOfRecordsSkipPerPage -= 10;
		}
		Bson mainFilter = Filters.eq("type", type);

		Bson businessFinalFilter = null;
		String[] businessfields = { "companyname", "companyemailid" };
		try {
			if (searchstr != null && searchstr.length() > 0) {
				for (String field : businessfields) {
					if (businessFinalFilter != null) {
						businessFinalFilter = Filters.or(businessFinalFilter,
								Filters.regex(field, "^" + searchstr + ".*", "i"));
					} else {
						businessFinalFilter = Filters.regex(field, "^" + searchstr + ".*", "i");
					}
				}
				mainFilter = Filters.and(businessFinalFilter, mainFilter);
			}
			contacts = commonDBDao.getAllEntityWithPagination(DATABASE_NAME, COLL_NAME_BUSINESS, mainFilter,
					Sorts.descending("seq"), null, numberOfRecordsSkipPerPage, pageLimit);
		} catch (Exception ex) {
			System.out.println("BusinessDBDaoImpl --- getAllBusinessContacts ---> " + ex.toString());
		}
		return contacts;
	}

	@Override
	public String searchBusinessContact(String searchstr) {
		List<Document> searchLBDoc = new ArrayList<Document>();
		Bson searchValFilter = null;
		try {
			searchValFilter = searchstr.length() <= 0 ? null
					: Filters.regex("companyname", "^" + searchstr + ".*", "i");
			searchLBDoc = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_BUSINESS, searchValFilter, null, null, 0);
		} catch (Exception ex) {
			System.out.println("BusinessDBDaoImpl --> searchClient --> " + ex.toString());
		}

		JsonArray searchLBArray = new JsonArray();
		for (Document doc : searchLBDoc) {
			searchLBArray.add(JsonUtil.parseJSON(doc));
		}
		return searchLBArray.toString();
	}

	@Override
	public String filterBusinessContacts(JsonObject filterby) {
		List<Document> filterBusinessContacts = null;
		Bson finalFilter = null;
		Bson serveTypeFilter = null;
		JsonArray serveTypeArray = filterby.get("serveTypeArray").getAsJsonArray();
		List<String> serveTypeList = new ArrayList<>();
		if (serveTypeArray != null) {
			for (int i = 0; i < serveTypeArray.size(); i++) {
				serveTypeList.add(serveTypeArray.get(i).getAsString());
			}
		}
		serveTypeFilter = serveTypeList.size() == 0 ? null : Filters.in("serveid", serveTypeList);

		if (serveTypeFilter != null) {
			finalFilter = Filters.and(serveTypeFilter);
		}
		filterBusinessContacts = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_BUSINESS, finalFilter, null, null,
				0);

		JsonArray filterContacts = new JsonArray();
		for (Document doc : filterBusinessContacts) {
			filterContacts.add(JsonUtil.parseJSON(doc));
		}

		return filterContacts.toString();
	}

	@Override
	public String update(JsonObject updateBusinessDetails) {
		MongoClient cli = null;
		JsonObject returnStatus = new JsonObject();
		String businessID = "";
		JsonArray filterServTypeList = new JsonArray();
		JsonArray contactPersonArray = new JsonArray();

		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_BUSINESS);

			businessID = updateBusinessDetails.get("_id").getAsString();
			Document updateDoc = new Document();
			String[] fields = { "companyname", "city", "pincode", "state", "country", "doorno", "street", "type",
					"companyemailid", "gst", "leadtype", "leadname" };
			for (String field : fields) {

				if (updateBusinessDetails.has(field)) {
					updateDoc.put(field, updateBusinessDetails.get(field).getAsString());
				}
			}
			table.updateOne(Filters.eq("_id", businessID), new Document("$set", updateDoc));

			if (updateBusinessDetails.has("contactpersons")) {
				System.out.println("BusinessDBDaoImpl --> contactPersonArray-->>"
						+ updateBusinessDetails.getAsJsonArray("contactPersons"));
				contactPersonArray = updateBusinessDetails.getAsJsonArray("contactpersons");
				if (contactPersonArray.size() > 0) {
					for (int i = 0; i < contactPersonArray.size(); i++) {
						Document contactPersonDoc = new Document();
						JsonObject contactpersonObj = new JsonObject();
						contactpersonObj = contactPersonArray.get(i).getAsJsonObject();
						contactPersonDoc.put("_id", contactpersonObj.get("_id").getAsString());
						if (contactpersonObj.has("cpalternumber")) {
							contactPersonDoc.put("cpalternumber", contactpersonObj.get("cpalternumber").getAsString());
						}
						if (contactpersonObj.has("cpname")) {
							contactPersonDoc.put("cpname", contactpersonObj.get("cpname").getAsString());
						}
						if (contactpersonObj.has("cpemail")) {
							contactPersonDoc.put("cpemail", contactpersonObj.get("cpemail").getAsString());
						}
						if (contactpersonObj.has("cpphone")) {
							contactPersonDoc.put("cpphone", contactpersonObj.get("cpphone").getAsString());
						}
						if (contactpersonObj.has("countryphcode")) {
							contactPersonDoc.put("countryphcode", contactpersonObj.get("countryphcode").getAsString());
						}
						if (contactpersonObj.has("altercountryphcode")) {
							contactPersonDoc.put("altercountryphcode",
									contactpersonObj.get("altercountryphcode").getAsString());
						}
						table.updateOne(Filters.eq("_id", businessID),
								new Document("$pull", new Document("contactpersons",
										new Document("_id", contactpersonObj.get("_id").getAsString()))));
						table.updateOne(Filters.eq("_id", businessID),
								new Document("$push", new Document("contactpersons", contactPersonDoc)));
						table.updateOne(Filters.eq("_id", businessID),
								new Document("$inc", new Document("contactpersonseq", 1)));
					}
				}
			}

			returnStatus.addProperty("businessID", businessID);
			returnStatus.addProperty(MSG_SUCCESS, ADD_UPDATECOMMON_SUCCESS);
			returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);

		} catch (Exception ex) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			System.out.println("BusinessDBDaoImpl --> " + ex.toString());
		}

		if (filterServTypeList.size() > 0) {
			return filterServTypeList.toString();
		} else {
			return returnStatus.toString();
		}
	}

	@Override
	public String delete(String _id, String serv_id) {
		System.out.println("BusinessDBDaoImpl --- deleteService ---> " + _id + "  service id   " + serv_id);
		MongoClient cli = null;
		List<Document> servetypeList = new ArrayList<Document>();
		JsonArray filterServTypeList = new JsonArray();
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_BUSINESS);

			table.updateOne(Filters.and(Filters.eq("_id", _id), Filters.eq("serviceType._id", serv_id)),
					new Document("$set", new Document("serviceType.$.status", "deleted")));

			List<Document> contacts = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_BUSINESS,
					Filters.eq("_id", _id), null, null, 0);
			System.out.println("BusinessDBDaoImpl -->serveType response " + contacts.toString());
			for (Document contact : contacts) {
				servetypeList = (List<Document>) contact.get("serviceType");
			}
			for (Document doc : servetypeList) {
				filterServTypeList.add(JsonUtil.parseJSON(doc));
			}

		} catch (Exception ex) {
			System.out.println("BusinessDBDaoImpl --- deleteService ---> " + ex.toString());
		}

		return filterServTypeList.toString();
	}

	@Override
	public String download(String type) {
		ContactGenerateExcel nmcsGenerateExcel = new ContactGenerateExcel();
		String excelname = null;
		List<Document> businesscontacts = null;
		try {
			if (type.equals("all")) {
				businesscontacts = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_BUSINESS,
						Filters.eq("status", "active"), null, null, 0);
			} else {
				businesscontacts = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_BUSINESS, Filters.eq("type", type),
						null, null, 0);
			}
			excelname = nmcsGenerateExcel.getBusinessExcel(businesscontacts);
		} catch (Exception ex) {
			System.out.println("BusinessDBDaoImpl --- downloadBusinessContacts ---> " + ex.toString());
		}
		return excelname;
	}

	@Override
	public String addActivity(JsonObject activity, String id) {
		Document activDoc = new Document();
		MongoClient cli = null;
		JsonObject returnStatus = new JsonObject();
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_BUSINESS);
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
	public Document get(String id) {
		return commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_BUSINESS, Filters.eq("_id", id), null);
	}

	@Override
	public String getPaginationCount(String type, String searchstr) {
		List<Document> contacts = null;
		JsonObject returnStatus = new JsonObject();
		Bson mainFilter = Filters.eq("type", type);
		Bson businessFinalFilter = null;
		String[] businessfields = { "companyname", "companyemailid" };
		returnStatus.addProperty(PAGE_COUNT, 0);
		try {
			if (searchstr != null && searchstr.length() > 0) {
				for (String field : businessfields) {
					if (businessFinalFilter != null) {
						businessFinalFilter = Filters.or(businessFinalFilter,
								Filters.regex(field, "^" + searchstr + ".*", "i"));
					} else {
						businessFinalFilter = Filters.regex(field, "^" + searchstr + ".*", "i");
					}
				}
				mainFilter = Filters.and(businessFinalFilter, mainFilter);
			}
			contacts = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_BUSINESS, mainFilter,
					Sorts.ascending("companyname"), null, 0);

			if (contacts.size() > 0) {
				returnStatus.addProperty(PAGE_COUNT, contacts.size());
			}
		} catch (Exception ex) {
			System.out.println("BusinessDBDaoImpl --- getAllBusinessContacts ---> " + ex.toString());
		}
		return returnStatus.toString();
	}

}
