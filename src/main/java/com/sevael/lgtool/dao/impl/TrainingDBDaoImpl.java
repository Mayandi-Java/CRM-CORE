package com.sevael.lgtool.dao.impl;

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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.sevael.lgtool.configuration.LgtDBFactory;
import com.sevael.lgtool.dao.CommonDBDao;
import com.sevael.lgtool.dao.TrainingDBDao;
import com.sevael.lgtool.utils.AppConstants;
import com.sevael.lgtool.utils.JsonUtil;
import com.sevael.lgtool.utils.UtilConstants;

@Repository
public class TrainingDBDaoImpl implements AppConstants, UtilConstants, TrainingDBDao {

	@Autowired
	private CommonDBDao commonDBDao;

	@Override
	public String save(JsonObject trainingDetails) {
		MongoClient cli = null;
		JsonObject returnStatus = new JsonObject();
		String trainingID = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:mm:ss a");
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_TRAINING);

			Document trainingDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_TRAINING,
					Filters.or(Filters.regex("name", "^" + trainingDetails.get("name").getAsString() + "$", "i")),
					null);

			if (trainingDoc == null) {
				trainingDoc = new Document();
				int seq = commonDBDao.getNextSequence(db, COLL_NAME_TRAINING);
				trainingID = COLL_NAME_TRAINING + seq;
				trainingDoc.put("_id", trainingID);
				trainingDoc.put("name", trainingDetails.get("name").getAsString());
				trainingDoc.put("price", trainingDetails.get("price").getAsString());
				trainingDoc.put("pricetype", trainingDetails.get("pricetype").getAsString());
				trainingDoc.put("trainingdesc", trainingDetails.get("trainingdesc").getAsString());
				trainingDoc.put("clientid", trainingDetails.get("clientid").getAsString());
				trainingDoc.put("clientname", trainingDetails.get("clientname").getAsString());
				trainingDoc.put("providerid", trainingDetails.get("providerid").getAsString());
				trainingDoc.put("providername", trainingDetails.get("providername").getAsString());
				trainingDoc.put("levelid", trainingDetails.get("levelid").getAsString());
				trainingDoc.put("levelname", trainingDetails.get("levelname").getAsString());
				trainingDoc.put("categoryid", trainingDetails.get("categoryid").getAsString());
				trainingDoc.put("categoryname", trainingDetails.get("categoryname").getAsString());
				trainingDoc.put("seq", seq);
				trainingDoc.put("code", trainingDetails.get("code").getAsString());
				trainingDoc.put("creationdate", formatter.format(new Date()));
				table.insertOne(trainingDoc);

				returnStatus.addProperty("trainingid", trainingID);
				returnStatus.addProperty(RS_MESSAGE, ADD_TRAINING_SUCCESS);
				returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);
			} else {
				returnStatus.addProperty("isexists", true);
				returnStatus.addProperty(RS_MESSAGE, ADD_CONTACT_EXISTS);
			}

		} catch (Exception ex) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			System.out.println("TrainingDBDaoImpl --> " + ex.toString());
			ex.printStackTrace();
		}

		return returnStatus.toString();
	}

	@Override
	public List<Document> list(String searchstr, int page) {
		List<Document> trainings = null;
		int pageLimit = 10;
		int numberOfRecordsSkipPerPage = 0;
		if (page > 1) {
			numberOfRecordsSkipPerPage = (10 * page);
			numberOfRecordsSkipPerPage -= 10;
		}
		try {
			if (searchstr != null && searchstr.length() > 0) {

				trainings = commonDBDao.getAllEntityWithPagination(DATABASE_NAME, COLL_NAME_TRAINING,
						Filters.or(Filters.regex("name", "^" + searchstr + ".*", "i"),
								Filters.regex("code", "^" + searchstr + ".*", "i")),
						Sorts.descending("seq"), null, numberOfRecordsSkipPerPage, pageLimit);
			} else {
				trainings = commonDBDao.getAllEntityWithPagination(DATABASE_NAME, COLL_NAME_TRAINING, null,
						Sorts.descending("seq"), null, numberOfRecordsSkipPerPage, pageLimit);
			}
		} catch (Exception ex) {
			// logger.error(CLASSNAME + METHODNAME + " Exception : ", ex);
			System.out.println("TrainingDBDaoImpl --- getAllTraining ---> " + ex.toString());
		}

		return trainings;
	}

	@Override
	public String searchTraining(String searchstr) {
		List<Document> searchLBDoc = null;

		Bson searchValFilter = null;
		try {
			searchValFilter = searchstr.length() <= 0 ? null
					: Filters.regex("trainingname", "^" + searchstr + ".*", "i");
			searchLBDoc = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_TRAINING, searchValFilter, null, null, 0);
		} catch (Exception ex) {
			System.out.println("TrainingDBDaoImpl --> searchTraining --> " + ex.toString());
		}
		JsonArray searchLBArray = new JsonArray();
		for (Document doc : searchLBDoc) {
			searchLBArray.add(JsonUtil.parseJSON(doc));
		}
		return searchLBArray.toString();
	}

	@Override
	public String update(JsonObject updateTrainingDetails) {
		MongoClient cli = null;
		JsonObject returnStatus = new JsonObject();
		String trainingID = "";

		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_TRAINING);
			Document trainingDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_TRAINING,
					Filters.and(Filters.ne("_id", updateTrainingDetails.get("_id").getAsString()),
							Filters.regex("name", "^" + updateTrainingDetails.get("name").getAsString() + "$", "i")),

					null);

			if (trainingDoc == null) {

				trainingID = updateTrainingDetails.get("_id").getAsString();

				Document updateDoc = new Document();
				String[] fields = { "name", "trainingdesc", "clientid", "clientname", "providerid", "providername",
						"levelid", "levelname", "categoryid", "categoryname", "code", "price", "pricetype", };
				for (String field : fields) {

					if (updateTrainingDetails.has(field)) {
						updateDoc.put(field, updateTrainingDetails.get(field).getAsString());
					}
				}
				table.updateOne(Filters.eq("_id", trainingID), new Document("$set", updateDoc));

				returnStatus.addProperty("trainingID", trainingID);
				returnStatus.addProperty(MSG_SUCCESS, ADD_UPDATECOMMON_SUCCESS);
				returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);
			} else {
				returnStatus.addProperty("isexists", true);
				returnStatus.addProperty(RS_MESSAGE, ADD_CONTACT_EXISTS);

			}
		} catch (Exception ex) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			System.out.println("ContactDaoImpl --> " + ex.toString());
		}

		return returnStatus.toString();
	}

	@Override
	public Document get(String id) {
		return commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_TRAINING, Filters.eq("_id", id), null);
	}

	@Override
	public String getPaginationCount(String searchstr) {
		List<Document> trainings = null;
		JsonObject returnStatus = new JsonObject();
		returnStatus.addProperty(PAGE_COUNT, 0);
		try {
			if (searchstr != null && searchstr.length() > 0) {
				trainings = commonDBDao
						.getAllEntity(DATABASE_NAME, COLL_NAME_TRAINING,
								Filters.or(Filters.regex("name", "^" + searchstr + ".*", "i"),
										Filters.regex("code", "^" + searchstr + ".*", "i")),
								Sorts.descending("seq"), null, 0);
			} else {
				trainings = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_TRAINING, null, Sorts.descending("seq"),
						null, 0);
			}
			if (trainings.size() > 0) {
				returnStatus.addProperty(PAGE_COUNT, trainings.size());
			}
		} catch (Exception ex) {
			// logger.error(CLASSNAME + METHODNAME + " Exception : ", ex);
			System.out.println("TrainingDBDaoImpl --- getAllTraining ---> " + ex.toString());
		}

		return returnStatus.toString();
	}

}
