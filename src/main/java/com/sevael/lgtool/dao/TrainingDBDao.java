package com.sevael.lgtool.dao;

import java.util.List;

import org.bson.Document;

import com.google.gson.JsonObject;

public interface TrainingDBDao {

	String save(JsonObject trainingDetails);

	List<Document> list(String searchstr, int page);

	String searchTraining(String searchstr);

	String update(JsonObject updateTrainingDetails);

	Document get(String id);

	String getPaginationCount(String searchstr);

}
