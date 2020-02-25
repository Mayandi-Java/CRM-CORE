package com.sevael.lgtool.service.impl;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.sevael.lgtool.dao.ServiceProviderDBDao;
import com.sevael.lgtool.service.ServProviderService;

@Service
@Transactional(readOnly = true)
public class ServProviderServiceImpl implements ServProviderService {

	@Autowired
	public ServiceProviderDBDao serviceProviderDBDao;

	@Override
	public String save(JsonObject servproviderDetails) {
		return serviceProviderDBDao.save(servproviderDetails);
	}

	@Override
	public List<Document> list(String searchstr, int page) {
		return serviceProviderDBDao.list(searchstr, page);
	}

	@Override
	public String update(JsonObject updateServiceProvider) {
		return serviceProviderDBDao.update(updateServiceProvider);
	}

	@Override
	public Document get(String id) {
		return serviceProviderDBDao.get(id);
	}

	@Override
	public String getPaginationCount(String searchstr) {
		return serviceProviderDBDao.getPaginationCount(searchstr);
	}

}
