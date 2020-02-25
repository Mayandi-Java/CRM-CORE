package com.sevael.lgtool.service.impl;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.sevael.lgtool.dao.RetailDBDao;
import com.sevael.lgtool.service.RetailService;

@Service
@Transactional(readOnly = true)
public class RetailServiceImpl implements RetailService {

	@Autowired
	public RetailDBDao retailDBDao;

	@Override
	public String save(JsonObject retailObj) {
		return retailDBDao.save(retailObj);
	}

	@Override
	public Map<String, List<Document>> search(String searchstr) {
		return retailDBDao.search(searchstr);
	}

	@Override
	public String update(JsonObject updateDetails) {
		return retailDBDao.update(updateDetails);
	}

	@Override
	public String filter(JsonArray filterby) {
		return retailDBDao.filter(filterby);
	}

	@Override
	public String uploadRetail(InputStream inputStream, String filename) {
		return retailDBDao.uploadRetail(inputStream, filename);
	}

	@Override
	public String uploadImage(String _id, InputStream file) {
		return retailDBDao.uploadImage(_id, file);
	}

	@Override
	public String downloadExcel(String status) {
		return retailDBDao.downloadExcel(status);
	}

	@Override
	public String updateServiceStatus(String _id, String serv_id, String status) {
		return retailDBDao.updateServiceStatus(_id, serv_id, status);
	}

	@Override
	public String downloadFilterExcel(JsonObject filterDetails) {
		return retailDBDao.downloadFilterExcel(filterDetails);
	}

	@Override
	public GridFSDBFile getProfileImage(String _id) {
		return retailDBDao.getProfileImage(_id);
	}

	@Override
	public Document get(String id) {
		return retailDBDao.get(id);
	}

	@Override
	public String addActivity(JsonObject activity, String id) {
		return retailDBDao.addActivity(activity, id);
	}

	@Override
	public List<Document> list(String type, String searchstr, int page) {
		return retailDBDao.list(type, searchstr, page);
	}

	@Override
	public String getPaginationCount(String type, String searchstr) {
		return retailDBDao.getPaginationCount(type, searchstr);
	}

}
