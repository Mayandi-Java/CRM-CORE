package com.sevael.lgtool.service.impl;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.sevael.lgtool.dao.CategoryDBDao;
import com.sevael.lgtool.dao.LevelDBDao;
import com.sevael.lgtool.service.CategoryService;
import com.sevael.lgtool.service.LevelService;

@Service
@Transactional(readOnly = true)
public class LevelServiceImpl implements LevelService {

	@Autowired
	LevelDBDao levelDBDao;

	@Override
	public String save(JsonObject trainingCatDetails) {
		return levelDBDao.save(trainingCatDetails);
	}

	@Override
	public List<Document> list(String searchstr, int page) {
		return levelDBDao.list(searchstr, page);
	}

	@Override
	public String update(JsonObject updateTrainingCatDetails) {
		return levelDBDao.update(updateTrainingCatDetails);
	}

	@Override
	public Document get(String id) {
		return levelDBDao.get(id);
	}

	@Override
	public String getPaginationCount(String searchstr) {
		return levelDBDao.getPaginationCount(searchstr);
	}

}
