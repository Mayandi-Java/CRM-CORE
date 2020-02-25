package com.sevael.lgtool.dao;

import java.io.InputStream;
import java.util.List;

import org.bson.Document;

import com.google.gson.JsonObject;
import com.mongodb.gridfs.GridFSDBFile;

public interface UserDBDao {

	String save(JsonObject userDetails);

	List<Document> list(String searchstr, int page);

	String searchEmployee(String searchstr);

	String update(JsonObject updateEmployeeDetails);

	Document get(String id);

	String getImage(String filename);

	String saveImage(InputStream file, String filename, String id);

	String getPaginationCount(String searchstr);

}
