package com.sevael.lgtool.service.impl;

import java.io.InputStream;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.sevael.lgtool.dao.EmailRequestDao;
import com.sevael.lgtool.service.EmailRequestService;
@Service
@Transactional(readOnly = true)
public class EmailRequestServiceImpl implements EmailRequestService{
	
	@Autowired
	EmailRequestDao emailRequestdao;

	@Override
	public String emailRequest(JsonObject requestDetails) {
		return emailRequestdao.emailRequest(requestDetails);
	}

	@Override
	public List<Document> getAllRequeter() {
		return emailRequestdao.getAllRequeter();
	}

	@Override
	public String uploadPdfFile(InputStream inputStream, String filename, String certificationid) {
		return emailRequestdao.uploadPdfFile(inputStream,filename, certificationid);
	}
}
