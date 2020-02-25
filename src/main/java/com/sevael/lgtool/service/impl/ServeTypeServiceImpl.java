package com.sevael.lgtool.service.impl;

import java.io.InputStream;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sevael.lgtool.dao.ServeTypeDBDao;
import com.sevael.lgtool.service.ServeTypeService;

@Service
@Transactional(readOnly = true)
public class ServeTypeServiceImpl implements ServeTypeService {

	@Autowired
	private ServeTypeDBDao serveTypeDB;

	public String addServiceType(String serviceName, String userType) {
		return serveTypeDB.addServiceType(serviceName, userType);
	}

	public List<Document> getAllServices() {
		return serveTypeDB.getAllServices();
	}

	public String deleteServices(String _id) {
		return serveTypeDB.deleteServices(_id);
	}

	@Override
	public String saveServiceImage(InputStream filePart, String fileName) {
		return serveTypeDB.saveServiceImage(filePart , fileName);
	}
}
