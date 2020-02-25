package com.sevael.lgtool.service;

import java.util.List;

import org.bson.Document;

import com.google.gson.JsonObject;

public interface TrainingService {

	String save(JsonObject trainingDetails);

	List<Document> list(String searchstr, int page);

	String searchTraining(String searchstr);

	String update(JsonObject updateTrainingDetails);

	Document get(String id);

	String getPaginationCount(String searchstr);

}
