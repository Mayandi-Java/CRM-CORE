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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.sevael.lgtool.configuration.LgtDBFactory;
import com.sevael.lgtool.dao.CertificationDBDao;
import com.sevael.lgtool.dao.CommonDBDao;
import com.sevael.lgtool.utils.AppConstants;
import com.sevael.lgtool.utils.JsonUtil;
import com.sevael.lgtool.utils.UtilConstants;

@Repository
public class CertificationDBDaoImpl implements CertificationDBDao, AppConstants, UtilConstants {

	@Autowired
	CommonDBDao commonDBDao;

	@Override
	public String save(JsonObject certificationDetails) {
		MongoClient cli = null;
		JsonObject returnStatus = new JsonObject();
		String certificationID = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:mm:ss a");
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_CERTIFICATION);

			Document certificationDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_CERTIFICATION,
					Filters.or(Filters.regex("name", "^" + certificationDetails.get("name").getAsString() + "$", "i")),
					null);
			if (certificationDoc == null) {
				certificationDoc = new Document();
				int seq = commonDBDao.getNextSequence(db, COLL_NAME_CERTIFICATION);
				certificationID = COLL_NAME_CERTIFICATION + seq;
				certificationDoc.put("_id", certificationID);
				certificationDoc.put("name", certificationDetails.get("name").getAsString());
				if (certificationDetails.has("preferredamt")) {
					certificationDoc.put("preferredamt", certificationDetails.get("preferredamt").getAsString());
				}
				certificationDoc.put("clientid", certificationDetails.get("clientid").getAsString());
				certificationDoc.put("clientname", certificationDetails.get("clientname").getAsString());
				certificationDoc.put("providerid", certificationDetails.get("providerid").getAsString());
				certificationDoc.put("providername", certificationDetails.get("providername").getAsString());
				certificationDoc.put("levelid", certificationDetails.get("levelid").getAsString());
				certificationDoc.put("levelname", certificationDetails.get("levelname").getAsString());
				certificationDoc.put("categoryid", certificationDetails.get("categoryid").getAsString());
				certificationDoc.put("categoryname", certificationDetails.get("categoryname").getAsString());
				certificationDoc.put("code", certificationDetails.get("code").getAsString());
				certificationDoc.put("price", certificationDetails.get("price").getAsString());
				certificationDoc.put("pricetype", certificationDetails.get("pricetype").getAsString());
				certificationDoc.put("seq", seq);
				certificationDoc.put("creationdate", formatter.format(new Date()));
				table.insertOne(certificationDoc);

				returnStatus.addProperty("_id", certificationID);
				returnStatus.addProperty(RS_MESSAGE, ADD_COMMON_SUCCESS);
				returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);
			} else {
				returnStatus.addProperty("isexists", true);
				returnStatus.addProperty(RS_MESSAGE, ADD_COMMON_EXISTS);
			}

		} catch (Exception ex) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			System.out.println("CertificationDBDaoImpl --> " + ex.toString());
			ex.printStackTrace();
		}

		return returnStatus.toString();
	}

	@Override
	public List<Document> list(String searchstr, int page) {
		List<Document> Certifications = null;
		int pageLimit = 10;
		int numberOfRecordsSkipPerPage = 0;
		if (page > 1) {
			numberOfRecordsSkipPerPage = (10 * page);
			numberOfRecordsSkipPerPage -= 10;
		}
		try {
			if (searchstr != null && searchstr.length() > 0) {
				Certifications = commonDBDao.getAllEntityWithPagination(DATABASE_NAME, COLL_NAME_CERTIFICATION,
						Filters.or(Filters.regex("name", "^" + searchstr + ".*", "i"),
								Filters.regex("code", "^" + searchstr + ".*", "i")),
						Sorts.descending("seq"), null, numberOfRecordsSkipPerPage, pageLimit);
			} else {
				Certifications = commonDBDao.getAllEntityWithPagination(DATABASE_NAME, COLL_NAME_CERTIFICATION, null,
						Sorts.descending("seq"), null, numberOfRecordsSkipPerPage, pageLimit);
			}

		} catch (Exception ex) {
			System.out.println("CertificationDBDaoImpl --- getAllCertifications ---> " + ex.toString());
		}
		return Certifications;
	}

	@Override
	public String searchCertifications(String searchstr) {
		List<Document> searchLBDoc = null;

		Bson searchValFilter = null;
		try {
			searchValFilter = searchstr.length() <= 0 ? null
					: Filters.regex("certificationname", "^" + searchstr + ".*", "i");
			searchLBDoc = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_CERTIFICATION, searchValFilter, null, null,
					0);
		} catch (Exception ex) {
			System.out.println("CertificationDBDaoImpl --> searchCertifications --> " + ex.toString());
		}
		JsonArray searchLBArray = new JsonArray();
		for (Document doc : searchLBDoc) {
			searchLBArray.add(JsonUtil.parseJSON(doc));
		}
		return searchLBArray.toString();
	}

	@Override
	public String update(JsonObject updateCertificationDetails) {
		MongoClient cli = null;
		JsonObject returnStatus = new JsonObject();
		String certiID = "";

		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_CERTIFICATION);
			Document certificationDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_CERTIFICATION,
					Filters.and(Filters.ne("_id", updateCertificationDetails.get("_id").getAsString()), Filters
							.regex("name", "^" + updateCertificationDetails.get("name").getAsString() + "$", "i")),
					null);
			if (certificationDoc == null) {

				certiID = updateCertificationDetails.get("_id").getAsString();

				Document updateDoc = new Document();
				String[] fields = { "name", "preferredamt", "clientid", "clientname", "providerid", "providername",
						"levelid", "levelname", "categoryid", "categoryname", "code", "price", "pricetype", };
				for (String field : fields) {

					if (updateCertificationDetails.has(field)) {
						updateDoc.put(field, updateCertificationDetails.get(field).getAsString());
					}
				}
				table.updateOne(Filters.eq("_id", certiID), new Document("$set", updateDoc));

				returnStatus.addProperty("_id", certiID);
				returnStatus.addProperty(MSG_SUCCESS, ADD_UPDATECOMMON_SUCCESS);
				returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);
			} else {
				returnStatus.addProperty("isexists", true);
				returnStatus.addProperty(RS_MESSAGE, ADD_COMMON_EXISTS);
			}
		} catch (Exception ex) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			System.out.println("CertificationDBDaoImpl --> " + ex.toString());
		}

		return returnStatus.toString();
	}

	@Override
	public String filterCertification(JsonObject filterCert) {
		JsonArray filterCertby = new JsonArray();
		filterCertby = filterCert.get("servProvList").getAsJsonArray();
		System.out.println("filterCertby--> " + filterCertby.toString());
		List<Document> filteredCertification = null;
		System.out.println("filterCertby ---> " + filterCertby);
		Bson finalFilter = null;
		Bson servprovidFilter = null;
		List<String> certiList = new ArrayList<>();
		if (filterCertby != null) {
			for (int i = 0; i < filterCertby.size(); i++) {
				certiList.add(filterCertby.get(i).getAsString());
			}
		}
		servprovidFilter = certiList.size() == 0 ? null : Filters.in("servprovidername", certiList);

		if (servprovidFilter != null) {
			finalFilter = Filters.and(servprovidFilter);
		}
		filteredCertification = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_CERTIFICATION, finalFilter, null,
				null, 0);

		JsonArray filterContacts = new JsonArray();
		for (Document doc : filteredCertification) {
			filterContacts.add(JsonUtil.parseJSON(doc));
		}

		return filterContacts.toString();
	}

	@Override
	public Document get(String id) {
		return commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_CERTIFICATION, Filters.eq("_id", id), null);
	}

	@Override
	public String getPaginationCount(String searchstr) {
		List<Document> certifications = null;
		JsonObject returnStatus = new JsonObject();
		returnStatus.addProperty(PAGE_COUNT, 0);
		try {
			if (searchstr != null && searchstr.length() > 0) {
				certifications = commonDBDao
						.getAllEntity(DATABASE_NAME, COLL_NAME_CERTIFICATION,
								Filters.or(Filters.regex("name", "^" + searchstr + ".*", "i"),
										Filters.regex("code", "^" + searchstr + ".*", "i")),
								Sorts.descending("seq"), null, 0);
			} else {
				certifications = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_CERTIFICATION, null,
						Sorts.descending("seq"), null, 0);
			}

			if (certifications.size() > 0) {
				returnStatus.addProperty(PAGE_COUNT, certifications.size());
			}

		} catch (Exception ex) {
			System.out.println("CertificationDBDaoImpl --- getAllCertifications ---> " + ex.toString());
		}
		return returnStatus.toString();
	}
}