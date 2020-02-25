package com.sevael.lgtool.dao;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.gridfs.GridFSDBFile;

public interface RetailDBDao {

	String save(JsonObject retailObj);

	String update(JsonObject updateDetails);

	String filter(JsonArray filterby);

	String downloadExcel(String status);

	String updateServiceStatus(String _id, String serv_id, String status);

	String downloadFilterExcel(JsonObject filterDetails);

	String uploadRetail(InputStream inputStream, String filename);

	GridFSDBFile getProfileImage(String _id);

	String uploadImage(String _id, InputStream file);

	Map<String, List<Document>> search(String searchstr);

	Document get(String id);

	String addActivity(JsonObject activity, String id);

	List<Document> list(String type, String searchstr, int page);

	String getPaginationCount(String type, String searchstr);

}
