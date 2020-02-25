package com.sevael.lgtool.dao;

import java.util.List;

import org.bson.Document;

import com.google.gson.JsonObject;

public interface BusinessDBDao {

	String save(JsonObject businessObj);

	String searchBusinessContact(String searchstr);

	String filterBusinessContacts(JsonObject filterby);

	String update(JsonObject updateBusinessDetails);

	String delete(String _id, String serv_id);

	String download(String status);

	Document get(String id);

	String addActivity(JsonObject activity, String id);

	List<Document> list(String type, String searchstr, int page);

	String getPaginationCount(String type, String searchstr);

}
