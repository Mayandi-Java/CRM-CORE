package com.sevael.lgtool.service;

import java.util.List;

import org.bson.Document;

import com.google.gson.JsonObject;

public interface ServProviderService {

	String save(JsonObject servproviderDetails);

	List<Document> list(String searchstr, int page);

	String update(JsonObject updateServiceProvider);

	Document get(String id);

	String getPaginationCount(String searchstr);

}
