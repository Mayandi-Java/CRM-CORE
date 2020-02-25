package com.sevael.lgtool.service.impl;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.sevael.lgtool.dao.CategoryDBDao;
import com.sevael.lgtool.service.CategoryService;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	CategoryDBDao categoryDBDao;

	@Override
	public String save(JsonObject trainingCatDetails) {
		return categoryDBDao.save(trainingCatDetails);
	}

	@Override
	public List<Document> list(String searchstr, int page) {
		return categoryDBDao.list(searchstr, page);
	}

	@Override
	public String update(JsonObject updateTrainingCatDetails) {
		return categoryDBDao.update(updateTrainingCatDetails);
	}

	@Override
	public Document get(String id) {
		return categoryDBDao.get(id);
	}

	@Override
	public String getPaginationCount(String searchstr) {
		return categoryDBDao.getPaginationCount(searchstr);
	}

}
