package com.sevael.lgtool.dao;

import java.util.List;

import org.bson.Document;

import com.google.gson.JsonObject;

public interface DealsDBDao {

	String save(JsonObject dealDetails);

	List<Document> list(String searchstr, int page);

	Document get(String id);

	String updateDeal(String dealid, String serviceid, String status);

	String addActivity(JsonObject activity, String id);

	String updateDueDate(String dealid, String duedate,String justification);

	String getPaginationCount(String searchstr);

}
