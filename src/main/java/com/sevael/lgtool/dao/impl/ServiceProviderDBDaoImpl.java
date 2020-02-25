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
import com.sevael.lgtool.dao.CommonDBDao;
import com.sevael.lgtool.dao.ServiceProviderDBDao;
import com.sevael.lgtool.utils.AppConstants;
import com.sevael.lgtool.utils.JsonUtil;
import com.sevael.lgtool.utils.UtilConstants;

@Repository
public class ServiceProviderDBDaoImpl implements ServiceProviderDBDao, AppConstants, UtilConstants {

	@Autowired
	private CommonDBDao commonDBDao;
	@Autowired
	private CertificationDBDaoImpl certificationDBDaoImpl;

	@Override
	public String save(JsonObject servproviderDetails) {
		MongoClient cli = null;
		final String METHODNAME = "[addServiceProvider]";
		JsonObject returnStatus = new JsonObject();
		String SpID = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:mm:ss a");
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_SERVICEPROVIDER);

			Document trainingDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_SERVICEPROVIDER,
					Filters.or(Filters.regex("name", "^" + servproviderDetails.get("name").getAsString() + "$", "i")),
					null);

			if (trainingDoc == null) {
				trainingDoc = new Document();
				int seq = commonDBDao.getNextSequence(db, COLL_NAME_SERVICEPROVIDER);
				SpID = COLL_NAME_SERVICEPROVIDER + seq;
				trainingDoc.put("_id", SpID);
				trainingDoc.put("name", servproviderDetails.get("name").getAsString());
				trainingDoc.put("seq", seq);
				trainingDoc.put("creationdate", formatter.format(new Date()));
				table.insertOne(trainingDoc);
				returnStatus.addProperty("_id", SpID);
				returnStatus.addProperty(RS_MESSAGE, ADD_COMMON_SUCCESS);
				returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);
			} else {
				returnStatus.addProperty("isexists", true);
				returnStatus.addProperty(RS_MESSAGE, ADD_COMMON_EXISTS);
			}

		} catch (Exception ex) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			System.out.println("ServiceProviderDBDaoImpl --> " + ex.toString());
		}

		return returnStatus.toString();
	}

	@Override
	public List<Document> list(String searchstr, int page) {
		final String METHODNAME = "[getAllServiceProvider]:";
		List<Document> serviceProviderList = null;
		int pageLimit = 10;
		int numberOfRecordsSkipPerPage = 0;
		if (page > 1) {
			numberOfRecordsSkipPerPage = (10 * page);
			numberOfRecordsSkipPerPage -= 10;
		}
		try {
			if (searchstr != null && searchstr.length() > 0) {
				serviceProviderList = commonDBDao.getAllEntityWithPagination(DATABASE_NAME, COLL_NAME_SERVICEPROVIDER,
						Filters.regex("name", "^" + searchstr + ".*", "i"), Sorts.descending("seq"), null,
						numberOfRecordsSkipPerPage, pageLimit);
			} else {
				serviceProviderList = commonDBDao.getAllEntityWithPagination(DATABASE_NAME, COLL_NAME_SERVICEPROVIDER,
						null, Sorts.descending("seq"), null, numberOfRecordsSkipPerPage, pageLimit);
			}

		} catch (Exception ex) {
			// logger.error(CLASSNAME + METHODNAME + " Exception : ", ex);
			System.out.println("ServiceProviderDBDaoImpl --- getAllServiceProvider ---> " + ex.toString());
		}
		return serviceProviderList;
	}

	@Override
	public String update(JsonObject updateServiceProvider) {
		MongoClient cli = null;
		JsonObject returnStatus = new JsonObject();
		String ServiceProviderID = "";
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_SERVICEPROVIDER);
			Document trainingDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_SERVICEPROVIDER,
					Filters.and(Filters.ne("_id", updateServiceProvider.get("_id").getAsString()),
							Filters.regex("name", "^" + updateServiceProvider.get("name").getAsString() + "$", "i")),
					null);

			if (trainingDoc == null) {
				ServiceProviderID = updateServiceProvider.get("_id").getAsString();
				table.updateOne(Filters.eq("_id", ServiceProviderID),
						new Document("$set", new Document("name", updateServiceProvider.get("name").getAsString())));
				returnStatus.addProperty("_id", ServiceProviderID);
				returnStatus.addProperty(RS_MESSAGE, ADD_UPDATECOMMON_SUCCESS);
				returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);
			} else {
				returnStatus.addProperty("isexists", true);
				returnStatus.addProperty(RS_MESSAGE, ADD_COMMON_EXISTS);
			}
		} catch (Exception ex) {
			System.out.println("ServiceProviderDBDaoImpl --> " + ex.toString());
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
		}

		return returnStatus.toString();
	}

	@Override
	public String searchServiceProvider(String searchstr, String status) {
		List<Document> searchLBDoc = new ArrayList<Document>();

		Bson finalFilter = null;
		Bson searchValFilter = null;
		Bson statusFilter = null;
		try {
			searchValFilter = searchstr.length() <= 0 ? null
					: Filters.regex("servprovidername", "^" + searchstr + ".*", "i");

			if (status.equalsIgnoreCase("active")) {
				statusFilter = Filters.eq("status", status);
				System.out.println("earchServiceProvider --> statusFilter---> " + statusFilter);
			}
			if (statusFilter != null) {
				finalFilter = Filters.and(searchValFilter, statusFilter);
			} else {
				finalFilter = Filters.and(searchValFilter);
			}

			searchLBDoc = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_SERVICEPROVIDER, finalFilter, null, null,
					0);
		} catch (Exception ex) {
			System.out.println("ServiceProviderDBDaoImpl --> searchServiceProvider --> " + ex.toString());
		}
		JsonArray searchLBArray = new JsonArray();
		for (Document doc : searchLBDoc) {
			searchLBArray.add(JsonUtil.parseJSON(doc));
		}
		return searchLBArray.toString();
	}

	@Override
	public Document get(String id) {
		return commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_SERVICEPROVIDER, Filters.eq("_id", id), null);
	}

	@Override
	public String getPaginationCount(String searchstr) {
		final String METHODNAME = "[getAllServiceProvider]:";
		List<Document> serviceProviderList = null;
		JsonObject returnStatus = new JsonObject();
		returnStatus.addProperty(PAGE_COUNT, 0);
		try {
			if (searchstr != null && searchstr.length() > 0) {
				serviceProviderList = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_SERVICEPROVIDER,
						Filters.regex("name", "^" + searchstr + ".*", "i"), Sorts.descending("seq"), null, 0);
			} else {
				serviceProviderList = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_SERVICEPROVIDER, null,
						Sorts.descending("seq"), null, 0);
			}

			if (serviceProviderList.size() > 0) {
				returnStatus.addProperty(PAGE_COUNT, serviceProviderList.size());
			}

		} catch (Exception ex) {
			// logger.error(CLASSNAME + METHODNAME + " Exception : ", ex);
			System.out.println("ServiceProviderDBDaoImpl --- getAllServiceProvider ---> " + ex.toString());
		}
		return returnStatus.toString();
	}

}
