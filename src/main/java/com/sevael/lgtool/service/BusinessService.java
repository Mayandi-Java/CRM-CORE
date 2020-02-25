package com.sevael.lgtool.service;

import java.util.List;

import org.bson.Document;

import com.google.gson.JsonObject;

public interface BusinessService {

	String save(JsonObject businessObj);

	List<Document> list(String type, String searchstr, int page);

	String searchBusinessContact(String searchstr);

	String filterBusinessContacts(JsonObject filterby);

	String update(JsonObject updateBusinessDetails);

	String delete(String _id, String serv_id);

	String download(String status);

	Document get(String id);

	String addActivity(JsonObject activity, String id);

	String getPaginationCount(String type, String searchstr);

}
