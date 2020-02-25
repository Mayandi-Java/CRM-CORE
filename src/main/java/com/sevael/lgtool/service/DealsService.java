package com.sevael.lgtool.service;

import java.util.List;

import org.bson.Document;

import com.google.gson.JsonObject;

public interface DealsService {

	String save(JsonObject dealDetails);

	List<Document> list(String searchstr, int page);

	Document get(String id);

	String updateDeal(String dealid, String serviceid, String status);

	String addActivity(JsonObject activity, String id);

	String updateDueDate(String dealid, String duedate, String justification);

	String getPaginationCount(String searchstr);

}
