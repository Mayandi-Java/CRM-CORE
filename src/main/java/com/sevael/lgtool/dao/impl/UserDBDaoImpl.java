package com.sevael.lgtool.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
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
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.gridfs.GridFSDBFile;
import com.sevael.lgtool.configuration.LgtDBFactory;
import com.sevael.lgtool.dao.CommonDBDao;
import com.sevael.lgtool.dao.UserDBDao;
import com.sevael.lgtool.utils.AppConstants;
import com.sevael.lgtool.utils.JsonUtil;
import com.sevael.lgtool.utils.UtilConstants;

@Repository
public class UserDBDaoImpl implements AppConstants, UtilConstants, UserDBDao {

	@Autowired
	private CommonDBDao commonDBDao;

	@Override
	public String save(JsonObject userDetails) {
		MongoClient cli = null;
		final String METHODNAME = "[addEmployee]";
		JsonObject returnStatus = new JsonObject();
		String employeeID = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:mm:ss a");
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_USERS);

			Document employeeDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_USERS, Filters.or(
					Filters.regex("phonenumber", "^" + userDetails.get("phonenumber").getAsString() + "$", "i")), null);
			Document employeeDoc2 = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_USERS,
					Filters.or(Filters.regex("emailid", "^" + userDetails.get("emailid").getAsString() + "$", "i")),
					null);

			if (employeeDoc == null && employeeDoc2 == null) {
				employeeDoc = new Document();
				int seq = commonDBDao.getNextSequence(db, COLL_USERS);
				employeeID = COLL_USERS + seq;
				employeeDoc.put("_id", employeeID);
				employeeDoc.put("seq", seq);
				employeeDoc.put("name", userDetails.get("name").getAsString());
				employeeDoc.put("emailid", userDetails.get("emailid").getAsString());
				String[] fields = { "address", "countryphcode", "altercountryphcode", "phonenumber", "alterphonenum",
						"lastname", "location", "employeecode", "organization", "middlename", "city", "pincode",
						"state", "country", "doorno", "street", "dob", "bloodgroup" };
				for (String field : fields) {
					if (userDetails.has(field)) {
						employeeDoc.put(field, userDetails.get(field).getAsString());
					}
				}
				employeeDoc.put("status", "active");
				employeeDoc.put("creationdate", formatter.format(new Date()));
				table.insertOne(employeeDoc);

				MongoCollection<Document> authTable = db.getCollection(COLL_AUTH);
				Document authDoc = new Document();
				seq = commonDBDao.getNextSequence(db, COLL_AUTH);
				String authID = COLL_AUTH + seq;
				authDoc.put("_id", authID);
				authDoc.put("userid", employeeID);
				authDoc.put("seq", seq);
				authDoc.put("name", userDetails.get("name").getAsString());
				authDoc.put("emailid", userDetails.get("emailid").getAsString());
				authDoc.put("password", "sevael@123");
				authDoc.put("status", "active");
				authDoc.put("userrole", "user");
				authDoc.put("creationdate", formatter.format(new Date()));
				authTable.insertOne(authDoc);
				returnStatus.addProperty("employeeID", employeeID);
				returnStatus.addProperty(MSG_SUCCESS, ADD_BUSINESS_EMPLOYEE);
				returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);
			} else {
				if (employeeDoc != null & employeeDoc2 != null) {
					returnStatus.addProperty("isexists", "both");
				} else if (employeeDoc != null) {
					returnStatus.addProperty("isexists", "phone");
				} else if (employeeDoc2 != null) {
					returnStatus.addProperty("isexists", "email");
				}
				returnStatus.addProperty(RS_MESSAGE, ADD_COMMON_EXISTS);
			}

		} catch (MongoException e) {
			System.out.println("OrganizationDBDaoImpl 103-->" + METHODNAME + " Mongo Exception : " + e);
		} catch (Exception ex) {
			System.out.println("OrganizationDBDaoImpl 105-->" + METHODNAME + " Exception : " + ex.toString());
			ex.printStackTrace();
		} finally {
			if (cli != null) {
				LgtDBFactory.closeMongoCLient(cli);
			}
		}
		return returnStatus.toString();
	}

	// @Override
	public List<Document> list(String searchstr, int page) {
		List<Document> contacts = null;
		Bson mainFilter = Filters.eq("status", "active");
		String[] retailfields = { "name", "emailid", "countryphcode", "phonenumber" };
		Bson searchFilter = null;
		int pageLimit = 10;
		int numberOfRecordsSkipPerPage = 0;
		if (page > 1) {
			numberOfRecordsSkipPerPage = (10 * page);
			numberOfRecordsSkipPerPage -= 10;
		}
		try {
			if (searchstr != null && searchstr.length() > 0) {
				for (String field : retailfields) {
					if (searchFilter != null) {
						searchFilter = Filters.or(searchFilter, Filters.regex(field, "^" + searchstr + ".*", "i"));
					} else {
						searchFilter = Filters.regex(field, "^" + searchstr + ".*", "i");
					}
				}

				mainFilter = Filters.and(mainFilter, searchFilter);
			}

			contacts = commonDBDao.getAllEntityWithPagination(DATABASE_NAME, COLL_USERS, mainFilter,
					Sorts.descending("seq"), null, numberOfRecordsSkipPerPage, pageLimit);
		} catch (Exception ex) {
			System.out.println("OrganizationDBDaoImpl --- getAllEmployees ---> " + ex.toString());
		}
		return contacts;
	}

	// @Override
	public String searchEmployee(String searchstr) {
		List<Document> searchLBDoc = null;

		Bson searchValFilter = null;
		try {
			searchValFilter = searchstr.length() <= 0 ? null : Filters.regex("name", "^" + searchstr + ".*", "i");
			searchLBDoc = commonDBDao.getAllEntity(DATABASE_NAME, COLL_USERS, searchValFilter, null, null, 0);
		} catch (Exception ex) {
			System.out.println("OrganizationDBDaoImpl --> searchEmployee --> " + ex.toString());
		}
		JsonArray searchLBArray = new JsonArray();
		for (Document doc : searchLBDoc) {
			searchLBArray.add(JsonUtil.parseJSON(doc));
		}
		return searchLBArray.toString();
	}

	// @Override
	public String update(JsonObject updateEmployeeDetails) {
		MongoClient cli = null;
		JsonObject returnStatus = new JsonObject();
		String employeeID = "";
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_USERS);

			employeeID = updateEmployeeDetails.get("_id").getAsString();

			Document updateDoc = new Document();
			String[] fields = { "name", "address", "countryphcode", "altercountryphcode", "phonenumber", "emailid",
					"alterphonenum", "lastname", "location", "employeecode", "organization", "middlename", "city",
					"pincode", "state", "country", "doorno", "street", "dob", "bloodgroup" };

			for (String field : fields) {
				if (updateEmployeeDetails.has(field)) {
					updateDoc.put(field, updateEmployeeDetails.get(field).getAsString());
				}
			}
			table.updateOne(Filters.eq("_id", employeeID), new Document("$set", updateDoc));
			returnStatus.addProperty("employeeID", employeeID);
			returnStatus.addProperty(MSG_SUCCESS, ADD_UPDATECOMMON_SUCCESS);
			returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);

		} catch (Exception ex) {
			System.out.println("ContactDaoImpl --> " + ex.toString());
		}
		return returnStatus.toString();
	}

	@Override
	public Document get(String id) {
		return commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_USERS, Filters.eq("_id", id), null);
	}

	@Override
	public String saveImage(InputStream file, String filename, String id) {
		String status = "";
		MongoClient cli = null;
		final String METHODNAME = "[addEmployee]";
		JsonObject returnStatus = new JsonObject();
		String employeeID = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:mm:ss a");
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_USERS);
			table.updateOne(Filters.eq("_id", id), new Document("$set", new Document("image", filename)));
			status = commonDBDao.saveImage(file, filename, COLL_USERS_IMAGES);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public String getImage(String filename) {
		String loc_filename = "/tmp/tmp" + filename;
		try {
			commonDBDao.getImage(filename, COLL_USERS_IMAGES).writeTo(loc_filename);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return loc_filename;
	}

	@Override
	public String getPaginationCount(String searchstr) {
		List<Document> contacts = null;
		JsonObject returnStatus = new JsonObject();
		Bson mainFilter = Filters.eq("status", "active");
		String[] retailfields = { "name", "emailid", "countryphcode", "phonenumber" };
		Bson searchFilter = null;
		returnStatus.addProperty(PAGE_COUNT, 0);
		try {
			if (searchstr != null && searchstr.length() > 0) {
				for (String field : retailfields) {
					if (searchFilter != null) {
						searchFilter = Filters.or(searchFilter, Filters.regex(field, "^" + searchstr + ".*", "i"));
					} else {
						searchFilter = Filters.regex(field, "^" + searchstr + ".*", "i");
					}
				}

				mainFilter = Filters.and(mainFilter, searchFilter);
			}

			contacts = commonDBDao.getAllEntity(DATABASE_NAME, COLL_USERS, mainFilter, Sorts.descending("seq"), null,
					0);
			if (contacts.size() > 0) {
				returnStatus.addProperty(PAGE_COUNT, contacts.size());
			}
		} catch (Exception ex) {
			System.out.println("OrganizationDBDaoImpl --- getAllEmployees ---> " + ex.toString());
		}
		return returnStatus.toString();
	}
}
