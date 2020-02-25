package com.sevael.lgtool.service.impl;

import java.io.InputStream;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.sevael.lgtool.dao.OrganizationDBDao;
import com.sevael.lgtool.dao.UserDBDao;
import com.sevael.lgtool.service.UserService;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

	@Autowired
	UserDBDao userDBDao;

	@Override
	public String save(JsonObject employeeDetails) {
		return userDBDao.save(employeeDetails);
	}

	@Override
	public List<Document> list(String searchstr, int page) {
		return userDBDao.list(searchstr, page);
	}

	@Override
	public String searchEmployee(String searchstr) {
		return userDBDao.searchEmployee(searchstr);
	}

	@Override
	public String update(JsonObject updateEmployeeDetails) {
		return userDBDao.update(updateEmployeeDetails);
	}

	@Override
	public Document get(String id) {
		return userDBDao.get(id);
	}

	@Override
	public String saveImage(InputStream file, String filename, String id) {
		return userDBDao.saveImage(file, filename, id);
	}

	@Override
	public String getImage(String filename) {
		return userDBDao.getImage(filename);
	}

	@Override
	public String getPaginationCount(String searchstr) {
		return userDBDao.getPaginationCount(searchstr);
	}
}
