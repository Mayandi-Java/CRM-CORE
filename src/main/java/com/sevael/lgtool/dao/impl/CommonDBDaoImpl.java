package com.sevael.lgtool.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.sevael.lgtool.configuration.LgtDBFactory;
import com.sevael.lgtool.dao.CommonDBDao;
import com.sevael.lgtool.utils.AppConstants;
import com.sevael.lgtool.utils.UtilConstants;

@Repository
public class CommonDBDaoImpl implements AppConstants, UtilConstants, CommonDBDao {

	/** The Constant CLASSNAME. */
	private static final String CLASSNAME = "[CommonDBFacade]:";

	public int getNextSequence(MongoDatabase db, String collName) {
		Document seqDoc = (Document) db.getCollection(collName).find().projection(Projections.include("seq"))
				.sort(Sorts.descending("seq")).limit(1).first();
		int nextSeq = 0;
		if (seqDoc != null) {
			nextSeq = seqDoc.getInteger("seq");
		}
//		logger.debug(CLASSNAME + " next seq is " + nextSeq + 1);
		return nextSeq + 1;
	}

	public Document getEntityWithFilter(String databaseName, String collName, Bson filter, Bson projection) {
		MongoClient cli = null;
		final String METHODNAME = "[getEntityWithFilter]:";
		Document entityDoc = null;
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(databaseName);
			MongoCollection table = db.getCollection(collName);
			if (projection != null) {
				entityDoc = (Document) table.find(filter).projection(projection).first();
			} else {
				entityDoc = (Document) table.find(filter).first();
			}
		} catch (MongoException e) {
			System.out.println(CLASSNAME + METHODNAME + " Mongo Exception : " + e);
		} catch (Exception ex) {
			System.out.println(CLASSNAME + METHODNAME + " Exception : " + ex);
		} finally {
			if (cli != null) {
				LgtDBFactory.closeMongoCLient(cli);
			}
		}
		return entityDoc;
	}

	public String randomColor() {
		// create a list of Integer type
		List<String> list = new ArrayList<>();
		// add 5 element in ArrayList
		list.add("#3190FF");
		list.add("#EEAA3B");
		list.add("#0DC0CB");
		list.add("#6D9D3B");
		list.add("#EE707F");
		list.add("#C15FE2");
		list.add("#5dbdac");
		list.add("#a8bb42");
		list.add("#e77a2f");
		list.add("#014192");
		list.add("#5a3a6a");
		list.add("#b63038");

		Random rand = new Random();
		return list.get(rand.nextInt(list.size()));

	}

	public List<Document> getAllEntity(String databaseName, String collName, Bson filter, Bson sort, Bson projection,
			int limit) {
		MongoClient cli = null;
		final String METHODNAME = "[getAllEntity]:";
		List<Document> entityDocList = null;
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(databaseName);
			MongoCollection table = db.getCollection(collName);
			if (filter != null) {
				entityDocList = (List<Document>) table.find(filter).sort(sort).projection(projection).limit(limit)
						.into(new ArrayList<Document>());
			} else {
				entityDocList = (List<Document>) table.find().sort(sort).projection(projection).limit(limit)
						.into(new ArrayList<Document>());
			}
		} catch (MongoException e) {
			System.out.println(CLASSNAME + METHODNAME + " Mongo Exception getAllEntity: 106" + e);
		} catch (Exception ex) {
			System.out.println(CLASSNAME + METHODNAME + " Exception getAllEntity: 108" + ex);
		} finally {
			if (cli != null) {
				LgtDBFactory.closeMongoCLient(cli);
			}
		}
		return entityDocList;
	}

	public List<Document> getAllEntityWithPagination(String databaseName, String collName, Bson filter, Bson sort,
			Bson projection, int skip, int limit) {
		MongoClient cli = null;
		final String METHODNAME = "[getAllEntity]:";
		List<Document> entityDocList = null;
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(databaseName);
			MongoCollection table = db.getCollection(collName);
			if (filter != null) {
				entityDocList = (List<Document>) table.find(filter).sort(sort).projection(projection).skip(skip)
						.limit(limit).into(new ArrayList<Document>());
			} else {
				entityDocList = (List<Document>) table.find().sort(sort).projection(projection).skip(skip).limit(limit)
						.into(new ArrayList<Document>());
			}
		} catch (MongoException e) {
			System.out.println(CLASSNAME + METHODNAME + " Mongo Exception getAllEntity: 106" + e);
		} catch (Exception ex) {
			System.out.println(CLASSNAME + METHODNAME + " Exception getAllEntity: 108" + ex);
		} finally {
			if (cli != null) {
				LgtDBFactory.closeMongoCLient(cli);
			}
		}
		return entityDocList;
	}

	@Override
	public List<Document> searchContact(String searchstr, String collName) {
		MongoClient cli = null;
		List<Document> searchDocList = null;
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection table = null;
			if (collName.equals(COLL_NAME_RETAIL)) {
				table = db.getCollection(COLL_NAME_RETAIL);
				searchDocList = (List<Document>) table
						.aggregate(Arrays.asList(
								Aggregates.match(Filters.or(Filters.regex("contactname", "^" + searchstr + ".*"),
										Filters.regex("phonenumber", "^" + searchstr + ".*"),
										Filters.regex("address", "^" + searchstr + ".*"),
										Filters.regex("compOrInst", "^" + searchstr + ".*"),
										Filters.regex("email", "^" + searchstr + ".*")))))
						.into(new ArrayList<Document>());
			} else if (collName.equals(COLL_NAME_BUSINESS)) {
				table = db.getCollection(COLL_NAME_BUSINESS);
				searchDocList = (List<Document>) table
						.aggregate(Arrays.asList(
								Aggregates.match(Filters.or(Filters.regex("companyname", "^" + searchstr + ".*"),
										Filters.regex("GST", "^" + searchstr + ".*"),
										Filters.regex("address", "^" + searchstr + ".*"),
										Filters.regex("CPEmail", "^" + searchstr + ".*"),
										Filters.regex("contactPerson", "^" + searchstr + ".*"),
										Filters.regex("address", "^" + searchstr + ".*"),
										Filters.regex("CPPhone", "^" + searchstr + ".*")))))
						.into(new ArrayList<Document>());
			} else if (collName.equals(COLL_NAME_CLIENTS)) {
				table = db.getCollection(COLL_NAME_CLIENTS);
				searchDocList = (List<Document>) table
						.aggregate(Arrays.asList(Aggregates
								.match(Filters.or(Filters.regex("clientname", "^" + searchstr.toLowerCase() + ".*")))))
						.into(new ArrayList<Document>());
			} else if (collName.equals(COLL_NAME_SERVICEPROVIDER)) {
				table = db.getCollection(COLL_NAME_SERVICEPROVIDER);
				searchDocList = (List<Document>) table
						.aggregate(Arrays.asList(Aggregates
								.match(Filters.or(Filters.regex("servprovidername", "^" + searchstr + ".*")))))
						.into(new ArrayList<Document>());
			} else if (collName.equals(COLL_NAME_CATEGORY)) {
				table = db.getCollection(COLL_NAME_CATEGORY);
				searchDocList = (List<Document>) table
						.aggregate(Arrays.asList(
								Aggregates.match(Filters.or(Filters.eq("trainingcatname", "^" + searchstr + ".*")))))
						.into(new ArrayList<Document>());
			}
		} catch (MongoException e) {
			// logger.error(CLASSNAME + METHODNAME + " Mongo Exception : ", e);
			System.out.println("CommonDBDAOImpl  ---> 114" + e.toString());
		} catch (Exception ex) {
			// logger.error(CLASSNAME + METHODNAME + " Exception : ", ex);
			System.out.println("CommonDBDAOImpl  ---> 118" + ex.toString());
		} finally {
			if (cli != null) {
				LgtDBFactory.closeMongoCLient(cli);
			}
		}
		return searchDocList;

	}

	@Override
	public String saveImage(InputStream file, String filename, String dbName) throws IOException {
		JsonObject returnStatus = new JsonObject();
		MongoClient cli = null;
		String METHODNAME = "[saveImage]:";
		try {
			cli = LgtDBFactory.getMongoClient();
			DB db = cli.getDB(dbName);
			GridFS gfs = new GridFS(db);

			BasicDBObject basicDBObject = new BasicDBObject();
			basicDBObject.put("cus_filename", filename);
			gfs.remove(basicDBObject);

			GridFSInputFile gfsFile = null;
			gfsFile = gfs.createFile(file);
			gfsFile.put("cus_filename", filename);
			gfsFile.save();
			returnStatus.addProperty("status", "success");
		} catch (MongoException e) {
			System.out.println("CommonDBDAOImpl  ---> 118" + e.toString());
		} catch (Exception ex) {
			System.out.println("CommonDBDAOImpl  ---> 118" + ex.toString());
		} finally {
			if (cli != null) {
				LgtDBFactory.closeMongoCLient(cli);
			}
		}
		return returnStatus.toString();
	}

	@Override
	public GridFSDBFile getImage(String filename, String dbName) {
		MongoClient cli = LgtDBFactory.getMongoClient();
		DB db = cli.getDB(dbName);
		GridFS gfs = new GridFS(db);
		Object gfsdb = null;
		BasicDBObject basicDBObject = new BasicDBObject();
		basicDBObject.put("cus_filename", filename);
		return gfs.findOne(basicDBObject);
	}

}
