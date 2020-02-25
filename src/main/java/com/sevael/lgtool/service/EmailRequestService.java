package com.sevael.lgtool.service;

import java.io.InputStream;
import java.util.List;

import org.bson.Document;

import com.google.gson.JsonObject;

public interface EmailRequestService {

	String emailRequest(JsonObject requestDetails);

	List<Document> getAllRequeter();

	String uploadPdfFile(InputStream inputStream, String filename, String certificationid);

}
