package com.sevael.lgtool.dao;

import java.util.List;

import org.bson.Document;

import com.google.gson.JsonObject;

public interface ClientsDBDao {

	String save(JsonObject clientsDetails);

	String update(JsonObject updateclientDetails);

	String searchClient(String searchstr, String status);

	List<Document> list(String searchstr, int page);

	Document get(String id);

	String getPaginationCount(String searchstr);

}
