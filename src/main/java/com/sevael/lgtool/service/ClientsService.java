package com.sevael.lgtool.service;

import java.util.List;

import org.bson.Document;

import com.google.gson.JsonObject;

public interface ClientsService {

	String save(JsonObject clientsDetails);

	List<Document> list(String searchstr, int page);

	String update(JsonObject updateclientDetails);

	Document get(String id);

	String getPaginationCount(String searchstr);

}
