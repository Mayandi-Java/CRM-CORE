package com.sevael.lgtool.service;

import java.util.List;

import org.bson.Document;

import com.google.gson.JsonObject;

public interface LevelService {

	String save(JsonObject levelDetails);

	List<Document> list(String searchstr, int page);

	String update(JsonObject levelDetails);

	Document get(String id);

	String getPaginationCount(String searchstr);

}
