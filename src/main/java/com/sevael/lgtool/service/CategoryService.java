package com.sevael.lgtool.service;

import java.util.List;

import org.bson.Document;

import com.google.gson.JsonObject;

public interface CategoryService {

	String save(JsonObject trainingCatDetails);

	List<Document> list(String searchstr, int page);

	String update(JsonObject updateTrainingCatDetails);

	Document get(String id);

	String getPaginationCount(String searchstr);

}
