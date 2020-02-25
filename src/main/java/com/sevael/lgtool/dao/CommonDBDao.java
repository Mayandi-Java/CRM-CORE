package com.sevael.lgtool.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoDatabase;
import com.mongodb.gridfs.GridFSDBFile;

public interface CommonDBDao {

	public int getNextSequence(MongoDatabase db, String collName);

	public Document getEntityWithFilter(String databaseName, String collName, Bson filter, Bson projection);

	public List<Document> getAllEntity(String databaseName, String collName, Bson filter, Bson sort, Bson projection,
			int limit);

	public List<Document> searchContact(String searchstr, String collName);

	public String randomColor();

	GridFSDBFile getImage(String filename, String dbName);

	String saveImage(InputStream file, String filename, String dbName) throws IOException;

	public List<Document> getAllEntityWithPagination(String databaseName, String collName, Bson filter, Bson sort,
			Bson projection, int skip, int limit);

}
