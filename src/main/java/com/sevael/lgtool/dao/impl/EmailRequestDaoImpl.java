package com.sevael.lgtool.dao.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.sevael.lgtool.configuration.LgtDBFactory;
import com.sevael.lgtool.dao.CommonDBDao;
import com.sevael.lgtool.dao.EmailRequestDao;
import com.sevael.lgtool.utils.AppConstants;
import com.sevael.lgtool.utils.SevaelEmailAlerter;
import com.sevael.lgtool.utils.UtilConstants;

@Repository
public class EmailRequestDaoImpl implements EmailRequestDao, AppConstants, UtilConstants {
	@Autowired
	CommonDBDao commonDBDao;

	@Override
	public String emailRequest(JsonObject requestDetails) {
		MongoClient cli = null;
		JsonObject returnStatus = new JsonObject();
		String requestID = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:mm:ss a");
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_REQUEST);
			Document category;

			category = new Document();
			int seq = commonDBDao.getNextSequence(db, COLL_NAME_REQUEST);
			requestID = COLL_NAME_REQUEST + seq;
			category.put("_id", requestID);
			category.put("seq", seq);
			if (requestDetails.has("emailid")) {
				category.put("emailid", requestDetails.get("emailid").getAsString());
			}
			if (requestDetails.has("examid")) {
				category.put("examid", requestDetails.get("examid").getAsString());
			}
			category.put("status", "active");
			category.put("creationdate", formatter.format(new Date()));
			table.insertOne(category);
			
			List<Document> singleDocList = new ArrayList<Document>();
			try {
				singleDocList = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_CERTIFICATION, Filters.eq("_id", requestDetails.get("examid").getAsString()),
						null,
						Projections.fields(Projections.include("_id", "certificationname", "examtype", "duration",
								"clientname", "servprovidername", "certificationcode", "certificationprice",
								"certificationpricetype", "creationdate")),
						0);
			} catch (Exception ex) {
				System.out.println("CertificationDBDaoImpl --> getSingleCertification --> " + ex.toString());
			}
			
			SevaelEmailAlerter.sendMail(requestDetails);

			returnStatus.addProperty("_id", requestID);
			returnStatus.addProperty(MSG_SUCCESS, "You will get email from us soon..!");
			returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);
		} catch (MongoException e) {
			System.out.println("EmailRequestDaoImpl ---MongoException emailRequest ---> " + e.toString());
		} catch (Exception ex) {
			System.out.println("EmailRequestDaoImpl --- Exception emailRequest ---> " + ex.toString());
		} finally {
			if (cli != null) {
				LgtDBFactory.closeMongoCLient(cli);
			}
		}
		return returnStatus.toString();
	}

	@Override
	public List<Document> getAllRequeter() {
		List<Document> requestersList = null;
		try {
			requestersList = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_REQUEST, Filters.ne("status", "deleted"),
					null, Projections.fields(Projections.include("emailid", "_id", "examid", "status", "creationdate")),
					0);
		} catch (Exception ex) {
			System.out.println("EmailRequestDaoImpl --- getAllRequeter ---> " + ex.toString());
		}

		return requestersList;
	}

	@Override
	public String uploadPdfFile(InputStream inputStream, String filename, String certificationid) {
		String fullfilename = "C://tmp/tmp" + filename + ".pdf";
		copyFileToLocal(inputStream, fullfilename);
		
		MongoClient cli = null;
		JsonObject returnStatus = new JsonObject();
		String certificationID = certificationid;
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_CERTIFICATION);
			
			
			table.updateOne(Filters.eq("_id", certificationID),
					new Document("$set", new Document("filepath", fullfilename)));
			
			returnStatus.addProperty("_id", certificationID);
			returnStatus.addProperty(RS_MESSAGE, ADD_UPDATECOMMON_SUCCESS);
			returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);
					
		}catch(Exception ex) {
			System.out.println("ServiceProviderDBDaoImpl --> "+ex.toString());
			returnStatus.addProperty(RS_MESSAGE, ADD_WRONG);
		}
		
		return null;
	}
	
	private void copyFileToLocal(InputStream fileStream, String filename) {
		OutputStream outputStream = null;
		filename = "C://tmp/tmp" + filename + ".pdf";
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

}
