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
import com.sevael.lgtool.dao.LevelDBDao;
import com.sevael.lgtool.dao.CategoryDBDao;
import com.sevael.lgtool.utils.AppConstants;
import com.sevael.lgtool.utils.JsonUtil;
import com.sevael.lgtool.utils.UtilConstants;

@Repository
public class LevelDBDaoImpl implements LevelDBDao, AppConstants, UtilConstants {

	@Autowired
	private CommonDBDao commonDBDao;

	@Override
	public String save(JsonObject trainingCatDetails) {
		MongoClient cli = null;
		final String METHODNAME = "[addTrainingCategory]";
		JsonObject returnStatus = new JsonObject();
		String trainingCatId = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:mm:ss a");
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_LEVEL);

			Document trainingCatDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_LEVEL,
					Filters.or(Filters.regex("name", "^" + trainingCatDetails.get("name").getAsString() + "$", "i")),
					null);
			if (trainingCatDoc == null) {
				trainingCatDoc = new Document();
				int seq = commonDBDao.getNextSequence(db, COLL_NAME_LEVEL);
				trainingCatId = COLL_NAME_LEVEL + seq;
				trainingCatDoc.put("_id", trainingCatId);
				trainingCatDoc.put("name", trainingCatDetails.get("name").getAsString());
				trainingCatDoc.put("seq", seq);
				trainingCatDoc.put("creationdate", formatter.format(new Date()));
				table.insertOne(trainingCatDoc);

				returnStatus.addProperty("_id", trainingCatId);
				returnStatus.addProperty(RS_MESSAGE, ADD_COMMON_SUCCESS);
				returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);
			} else {
				returnStatus.addProperty("isexists", true);
				returnStatus.addProperty(RS_MESSAGE, ADD_COMMON_EXISTS);
			}

		} catch (Exception ex) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			System.out.println("TrainingCategoryDBDaoImpl --> " + ex.toString());
		}

		return returnStatus.toString();
	}

	@Override
	public List<Document> list(String searchstr, int page) {
		List<Document> categoryList = null;
		int pageLimit = 10;
		int numberOfRecordsSkipPerPage = 0;
		if (page > 1) {
			numberOfRecordsSkipPerPage = (10 * page);
			numberOfRecordsSkipPerPage -= 10;
		}
		try {
			if (searchstr != null && searchstr.length() > 0) {
				categoryList = commonDBDao.getAllEntityWithPagination(DATABASE_NAME, COLL_NAME_LEVEL,
						Filters.regex("name", "^" + searchstr + ".*", "i"), Sorts.descending("seq"), null,
						numberOfRecordsSkipPerPage, pageLimit);
			} else {
				categoryList = commonDBDao.getAllEntityWithPagination(DATABASE_NAME, COLL_NAME_LEVEL, null,
						Sorts.descending("seq"), null, numberOfRecordsSkipPerPage, pageLimit);
			}
		} catch (Exception ex) {
			// logger.error(CLASSNAME + METHODNAME + " Exception : ", ex);
			System.out.println("TrainingCategoryDBDaoImpl --- getAllTrainingCategory ---> " + ex.toString());
		}
		return categoryList;
	}

	@Override
	public String update(JsonObject updateTrainingCatDetails) {
		MongoClient cli = null;
		JsonObject returnStatus = new JsonObject();
		String trainingCatID = "";
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_LEVEL);

			Document trainingCatDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_LEVEL,
					Filters.and(Filters.ne("_id", updateTrainingCatDetails.get("_id").getAsString()),
							Filters.regex("name", "^" + updateTrainingCatDetails.get("name").getAsString() + "$", "i")),
					null);
			if (trainingCatDoc == null) {
				trainingCatID = updateTrainingCatDetails.get("_id").getAsString();
				if (updateTrainingCatDetails.has("name")) {
					table.updateOne(Filters.eq("_id", trainingCatID), new Document("$set",
							new Document("name", updateTrainingCatDetails.get("name").getAsString())));

				}
				returnStatus.addProperty("_id", trainingCatID);
				returnStatus.addProperty(RS_MESSAGE, ADD_UPDATECOMMON_SUCCESS);
				returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);

			} else {
				returnStatus.addProperty("isexists", true);
				returnStatus.addProperty(RS_MESSAGE, ADD_COMMON_EXISTS);

			}

		} catch (Exception ex) {
			System.out.println("TrainingCategoryDBDaoImpl --> updateTrainingCategory-->  " + ex.toString());
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
		}

		return returnStatus.toString();
	}

	@Override
	public String searchTrainingCategory(String searchstr, String status) {
		List<Document> searchLBDoc = new ArrayList<Document>();
		Bson finalFilter = null;
		Bson searchValFilter = null;
		Bson statusFilter = null;
		try {
			searchValFilter = searchstr.length() <= 0 ? null
					: Filters.regex("trainingcatname", "^" + searchstr + ".*", "i");
			if (status.equalsIgnoreCase("active")) {
				statusFilter = Filters.eq("status", status);
				System.out.println("searchTrainingCategory -->> statusFilter---> " + statusFilter);
			}
			if (statusFilter != null) {
				finalFilter = Filters.and(searchValFilter, statusFilter);
			} else {
				finalFilter = Filters.and(searchValFilter);
			}
			searchLBDoc = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_LEVEL, finalFilter, null, null, 0);
		} catch (Exception ex) {
			System.out.println("TrainingCategoryDBDaoImpl --> searchTrainingCategory --> " + ex.toString());
		}
		JsonArray searchLBArray = new JsonArray();
		for (Document doc : searchLBDoc) {
			searchLBArray.add(JsonUtil.parseJSON(doc));
		}
		return searchLBArray.toString();
	}

	@Override
	public Document get(String id) {
		return commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_LEVEL, Filters.eq("_id", id), null);

	}

	@Override
	public String getPaginationCount(String searchstr) {
		List<Document> categoryList = null;
		JsonObject returnStatus = new JsonObject();
		returnStatus.addProperty(PAGE_COUNT, 0);
		try {
			if (searchstr != null && searchstr.length() > 0) {
				categoryList = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_LEVEL,
						Filters.regex("name", "^" + searchstr + ".*", "i"), Sorts.descending("seq"), null, 0);
			} else {
				categoryList = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_LEVEL, null, Sorts.descending("seq"),
						null, 0);
			}
			if (categoryList.size() > 0) {
				returnStatus.addProperty(PAGE_COUNT, categoryList.size());
			}
		} catch (Exception ex) {
			// logger.error(CLASSNAME + METHODNAME + " Exception : ", ex);
			System.out.println("TrainingCategoryDBDaoImpl --- getAllTrainingCategory ---> " + ex.toString());
		}
		return returnStatus.toString();
	}
}
