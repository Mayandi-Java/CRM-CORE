package com.sevael.lgtool.service.impl;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.sevael.lgtool.dao.BusinessDBDao;
import com.sevael.lgtool.service.BusinessService;

@Service
@Transactional(readOnly = true)
public class BusinessServiceImpl implements BusinessService {

	@Autowired
	public BusinessDBDao businessDBDao;

	@Override
	public String save(JsonObject businessObj) {
		return businessDBDao.save(businessObj);
	}

	@Override
	public List<Document> list(String status, String searchstr, int page) {
		return businessDBDao.list(status, searchstr, page);
	}

	@Override
	public String searchBusinessContact(String searchstr) {
		return businessDBDao.searchBusinessContact(searchstr);
	}

	@Override
	public String filterBusinessContacts(JsonObject filterby) {
		return businessDBDao.filterBusinessContacts(filterby);
	}

	@Override
	public String update(JsonObject updateBusinessDetails) {
		return businessDBDao.update(updateBusinessDetails);
	}

	@Override
	public String delete(String _id, String serv_id) {
		return businessDBDao.delete(_id, serv_id);
	}

	@Override
	public String download(String status) {
		return businessDBDao.download(status);
	}

	@Override
	public Document get(String id) {
		return businessDBDao.get(id);
	}

	@Override
	public String addActivity(JsonObject activity, String id) {
		return businessDBDao.addActivity(activity, id);
	}

	@Override
	public String getPaginationCount(String type, String searchstr) {
		return businessDBDao.getPaginationCount(type, searchstr);
	}

}
