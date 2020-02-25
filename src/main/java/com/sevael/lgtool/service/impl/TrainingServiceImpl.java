package com.sevael.lgtool.service.impl;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.sevael.lgtool.dao.TrainingDBDao;
import com.sevael.lgtool.service.TrainingService;

@Service
@Transactional(readOnly = true)
public class TrainingServiceImpl implements TrainingService {

	@Autowired
	public TrainingDBDao trainingDBDao;

	@Override
	public String save(JsonObject trainingDetails) {
		return trainingDBDao.save(trainingDetails);
	}

	@Override
	public List<Document> list(String searchstr, int page) {
		return trainingDBDao.list(searchstr, page);
	}

	@Override
	public String searchTraining(String searchstr) {
		return trainingDBDao.searchTraining(searchstr);
	}

	@Override
	public String update(JsonObject updateTrainingDetails) {
		return trainingDBDao.update(updateTrainingDetails);
	}

	@Override
	public Document get(String id) {
		return trainingDBDao.get(id);
	}
	
	@Override
	public String getPaginationCount(String searchstr) {
		return trainingDBDao.getPaginationCount(searchstr);
	}

}
