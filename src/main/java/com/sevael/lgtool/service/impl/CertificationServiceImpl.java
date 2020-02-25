package com.sevael.lgtool.service.impl;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sevael.lgtool.dao.CertificationDBDao;
import com.sevael.lgtool.service.CertificationService;

@Service
@Transactional(readOnly = true)
public class CertificationServiceImpl implements CertificationService {

	@Autowired
	CertificationDBDao certificatioDBDao;

	@Override
	public String save(JsonObject certificationDetails) {
		return certificatioDBDao.save(certificationDetails);
	}

	@Override
	public List<Document> list(String searchstr, int page) {
		return certificatioDBDao.list(searchstr, page);
	}

	@Override
	public String searchCertifications(String searchstr) {
		return certificatioDBDao.searchCertifications(searchstr);
	}

	@Override
	public String update(JsonObject updateCertificationDetails) {
		return certificatioDBDao.update(updateCertificationDetails);
	}

	@Override
	public String filterCertification(JsonObject filterCertby) {
		return certificatioDBDao.filterCertification(filterCertby);
	}

	@Override
	public Document get(String id) {
		return certificatioDBDao.get(id);
	}

	@Override
	public String getPaginationCount(String searchstr) {
		return certificatioDBDao.getPaginationCount(searchstr);
	}
}
