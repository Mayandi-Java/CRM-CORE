package com.sevael.lgtool.dao.impl;

import java.text.SimpleDateFormat;
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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.sevael.lgtool.configuration.LgtDBFactory;
import com.sevael.lgtool.dao.ClientsDBDao;
import com.sevael.lgtool.dao.CommonDBDao;
import com.sevael.lgtool.utils.AppConstants;
import com.sevael.lgtool.utils.JsonUtil;
import com.sevael.lgtool.utils.UtilConstants;

@Repository
public class ClientsDBDaoImpl implements ClientsDBDao, AppConstants, UtilConstants {

	@Autowired
	private CommonDBDao commonDBDao;

	@Override
	public String save(JsonObject clientsDetails) {
		MongoClient cli = null;
		JsonObject returnStatus = new JsonObject();
		String clientsID = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:mm:ss a");
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_CLIENTS);

			Document trainingDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_CLIENTS,
					Filters.or(Filters.regex("name", "^" + clientsDetails.get("name").getAsString() + "$", "i")), null);

			if (trainingDoc == null) {
				trainingDoc = new Document();
				int seq = commonDBDao.getNextSequence(db, COLL_NAME_CLIENTS);
				clientsID = COLL_NAME_CLIENTS + seq;
				trainingDoc.put("_id", clientsID);
				trainingDoc.put("name", clientsDetails.get("name").getAsString());
				trainingDoc.put("seq", seq);
				trainingDoc.put("creationdate", formatter.format(new Date()));
				table.insertOne(trainingDoc);

				returnStatus.addProperty("_id", clientsID);
				returnStatus.addProperty(RS_MESSAGE, ADD_COMMON_SUCCESS);
				returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);
			} else {
				returnStatus.addProperty("isexists", true);
				returnStatus.addProperty(RS_MESSAGE, ADD_COMMON_EXISTS);
			}

		} catch (Exception ex) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			System.out.println("ClientsDBDaoImpl --> " + ex.toString());
		}

		return returnStatus.toString();
	}

	@Override
	public List<Document> list(String searchstr, int page) {
		final String METHODNAME = "[getAllClients]:";
		List<Document> clients = null;
		int pageLimit = 10;
		int numberOfRecordsSkipPerPage = 0;
		if (page > 1) {
			numberOfRecordsSkipPerPage = (10 * page);
			numberOfRecordsSkipPerPage -= 10;
		}
		try {
			if (searchstr != null && searchstr.length() > 0) {
				clients = commonDBDao.getAllEntityWithPagination(DATABASE_NAME, COLL_NAME_CLIENTS,
						Filters.regex("name", "^" + searchstr + ".*", "i"), Sorts.descending("seq"), null,
						numberOfRecordsSkipPerPage, pageLimit);
			} else {
				clients = commonDBDao.getAllEntityWithPagination(DATABASE_NAME, COLL_NAME_CLIENTS, null,
						Sorts.descending("seq"), null, numberOfRecordsSkipPerPage, pageLimit);
			}
		} catch (Exception ex) {
			System.out.println("ClientsDBDaoImpl ---> getAllClients ---> " + ex.toString());
		}
		return clients;
	}

	@Override
	public String update(JsonObject updateclientDetails) {
		MongoClient cli = null;
		JsonObject returnStatus = new JsonObject();
		String clientsID = "";
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_CLIENTS);
			Document trainingDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_CLIENTS,
					Filters.and(Filters.ne("_id", updateclientDetails.get("_id").getAsString()),
							Filters.regex("name", "^" + updateclientDetails.get("name").getAsString() + "$", "i")),
					null);

			if (trainingDoc == null) {
				clientsID = updateclientDetails.get("_id").getAsString();
				if (updateclientDetails.has("name")) {
					table.updateOne(Filters.eq("_id", clientsID),
							new Document("$set", new Document("name", updateclientDetails.get("name").getAsString())));
				}

				returnStatus.addProperty("_id", clientsID);
				returnStatus.addProperty(RS_MESSAGE, ADD_UPDATECOMMON_SUCCESS);
				returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);
			} else {
				returnStatus.addProperty("isexists", true);
				returnStatus.addProperty(RS_MESSAGE, ADD_COMMON_EXISTS);
			}
		} catch (Exception ex) {
			System.out.println("ClientsDBDaoImpl --> " + ex.toString());
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
		}

		return returnStatus.toString();
	}

	@Override
	public String searchClient(String searchstr, String status) {
		List<Document> searchLBDoc = new ArrayList();

		Bson finalFilter = null;
		Bson searchValFilter = null;
		Bson statusFilter = null;
		try {
			searchValFilter = searchstr.length() <= 0 ? null : Filters.regex("clientname", "^" + searchstr + ".*", "i");
			if (status.equalsIgnoreCase("active")) {
				statusFilter = Filters.eq("status", status);
				System.out.println("searchClient --> statusFilter---> " + statusFilter);
			}
			if (statusFilter != null) {
				finalFilter = Filters.and(searchValFilter, statusFilter);
			} else {
				finalFilter = Filters.and(searchValFilter);
			}
			searchLBDoc = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_CLIENTS, finalFilter, null, null, 0);
		} catch (Exception ex) {
			System.out.println("ClientsDBDaoImpl --> searchClient --> " + ex.toString());
		}
		JsonArray searchLBArray = new JsonArray();
		if (searchLBDoc.size() > 0) {
			for (Document doc : searchLBDoc) {
				searchLBArray.add(JsonUtil.parseJSON(doc));
			}
		}
		return searchLBArray.toString();
	}

	@Override
	public Document get(String id) {
		return commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_CLIENTS, Filters.eq("_id", id), null);
	}

	@Override
	public String getPaginationCount(String searchstr) {
		final String METHODNAME = "[getAllClients]:";
		List<Document> clients = null;
		JsonObject returnStatus = new JsonObject();
		returnStatus.addProperty(PAGE_COUNT, 0);
		try {
			if (searchstr != null && searchstr.length() > 0) {
				clients = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_CLIENTS,
						Filters.regex("name", "^" + searchstr + ".*", "i"), Sorts.descending("seq"), null, 0);
			} else {
				clients = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_CLIENTS, null, Sorts.descending("seq"),
						null, 0);
			}
			if (clients.size() > 0) {
				returnStatus.addProperty(PAGE_COUNT, clients.size());
			}
		} catch (Exception ex) {
			System.out.println("ClientsDBDaoImpl ---> getAllClients ---> " + ex.toString());
		}
		return returnStatus.toString();
	}

}
