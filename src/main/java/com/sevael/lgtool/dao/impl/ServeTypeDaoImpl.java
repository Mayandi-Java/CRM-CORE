package com.sevael.lgtool.dao.impl;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.sevael.lgtool.configuration.LgtDBFactory;
import com.sevael.lgtool.dao.CommonDBDao;
import com.sevael.lgtool.dao.ServeTypeDBDao;
import com.sevael.lgtool.utils.AppConstants;
import com.sevael.lgtool.utils.UtilConstants;

@Repository
public class ServeTypeDaoImpl implements ServeTypeDBDao, AppConstants, UtilConstants {

	@Autowired
	CommonDBDao commonDBDao;

	public String addServiceType(String serveType, String type) {
		MongoClient cli = null;
		final String METHODNAME = "[addServiceType]";
		JsonObject returnStatus = new JsonObject();
		String catID = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:mm:ss a");
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_SERVICES);
			Document category = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_SERVICES,
					Filters.regex("serveType", "^" + serveType + "$", "i"), null);

			if (category == null) {
				category = new Document();
				int seq = commonDBDao.getNextSequence(db, COLL_NAME_SERVICES);
				catID = COLL_NAME_SERVICES + seq;
				category.put("_id", catID);
				category.put("seq", seq);
				category.put("type", type);
//				category.put("userrole", userrole);
//				category.put("useremail", useremail);
				category.put("serveType", serveType);
				category.put("status", "active");
				category.put("creationdate", formatter.format(new Date()));
				table.insertOne(category);
				returnStatus.addProperty("servetypeid", catID);
				returnStatus.addProperty(MSG_SUCCESS, ADD_SERVICE_SUCCESS);
			} else {
				returnStatus.addProperty("isexists", true);
				returnStatus.addProperty(RS_MESSAGE, ADD_SERVICE_EXISTS);
			}
			returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);
		} catch (MongoException e) {
			System.out.println("ServiceTypeDaoImpl -->" + METHODNAME + " Mongo Exception : " + e);
		} catch (Exception ex) {
			System.out.println("ServiceTypeDaoImpl -->" + METHODNAME + " Exception : " + ex.toString());
		} finally {
			if (cli != null) {
				LgtDBFactory.closeMongoCLient(cli);
			}
		}
		return returnStatus.toString();
	}

	public List<Document> getAllServices() {
		List<Document> services = null;

		try {
			services = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_SERVICES, Filters.ne("status", "deleted"),
					null, Projections.fields(Projections.include("serveType", "type", "creationdate", "_id", "status")),
					0);
		} catch (Exception ex) {
			System.out.println("ServiceTypeDaoImpl --- getallservices ---> " + ex.toString());
		}

		return services;
	}

	@Override
	public String deleteServices(String _id) {
		MongoClient cli = null;
		JsonObject returnStatus = new JsonObject();
		System.out.println("delete id  --->> " + _id);
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection table = db.getCollection(COLL_NAME_SERVICES);
			Document updateTechDoc = new Document();

			table.updateOne(Filters.eq("_id", _id), new Document("$set", new Document("status", "deleted")));

			returnStatus.addProperty(RS_MESSAGE, DELETE_SERVICE);
		} catch (Exception e) {
			System.out.println("delete service  --->> " + e.toString());
		} finally {
			if (cli != null) {
				LgtDBFactory.closeMongoCLient(cli);
			}
		}
		return returnStatus.toString();
	}

	@Override
	public String saveServiceImage(InputStream filePart, String fileName) {
		JsonObject returnStatus = new JsonObject();
		MongoClient cli = null;
		final String METHODNAME = "[saveServiceImage]:";
		try {
			cli = LgtDBFactory.getMongoClient();
			@SuppressWarnings("deprecation")
			DB db = cli.getDB(SERVICE_IMAGE_DATABASE_NAME);
			GridFS gfs = new GridFS(db);
			GridFSDBFile gfsdb = null;
			DBObject query = new BasicDBObject();
			query.put("filename", fileName);
			gfs.remove(query);
			GridFSInputFile gfsFile = null;
			gfsFile = gfs.createFile(filePart);
			gfsFile.put("filename", fileName);
			gfsFile.save();
			returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);
		} catch (MongoException e) {
			// logger.error(CLASSNAME + METHODNAME + " Mongo Exception : ", e);
		} catch (Exception ex) {
			// logger.error(CLASSNAME + METHODNAME + " Exception : ", ex);
		} finally {
			if (cli != null) {
				LgtDBFactory.closeMongoCLient(cli);
			}
		}
		return returnStatus.toString();
	}

}
