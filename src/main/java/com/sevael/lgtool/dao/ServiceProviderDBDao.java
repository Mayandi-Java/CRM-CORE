package com.sevael.lgtool.dao;

import java.util.List;

import org.bson.Document;

import com.google.gson.JsonObject;

public interface ServiceProviderDBDao {

	String save(JsonObject servproviderDetails);

	String update(JsonObject updateServiceProvider);

	String searchServiceProvider(String searchstr, String status);

	List<Document> list(String searchstr, int page);

	Document get(String id);

	String getPaginationCount(String searchstr);

}
