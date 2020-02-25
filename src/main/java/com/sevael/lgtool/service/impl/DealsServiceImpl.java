package com.sevael.lgtool.service.impl;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.sevael.lgtool.dao.BusinessDBDao;
import com.sevael.lgtool.dao.DealsDBDao;
import com.sevael.lgtool.service.BusinessService;
import com.sevael.lgtool.service.DealsService;

@Service
@Transactional(readOnly = true)
public class DealsServiceImpl implements DealsService {

	@Autowired
	public DealsDBDao dealsDBDao;

	@Override
	public String save(JsonObject dealDetails) {
		return dealsDBDao.save(dealDetails);
	}

	@Override
	public List<Document> list(String searchstr, int page) {
		return dealsDBDao.list(searchstr, page);
	}

	@Override
	public Document get(String id) {
		return dealsDBDao.get(id);
	}

	@Override
	public String updateDeal(String dealid, String serviceid, String status) {
		return dealsDBDao.updateDeal(dealid, serviceid, status);
	}

	@Override
	public String addActivity(JsonObject activity, String id) {
		return dealsDBDao.addActivity(activity, id);
	}

	@Override
	public String updateDueDate(String dealid, String duedate, String justification) {
		return dealsDBDao.updateDueDate(dealid, duedate, justification);
	}

	@Override
	public String getPaginationCount(String searchstr) {
		return dealsDBDao.getPaginationCount(searchstr);
	}

}
