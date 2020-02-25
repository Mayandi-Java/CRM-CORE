package com.sevael.lgtool.dao;

import java.io.InputStream;
import java.util.List;

import org.bson.Document;

import com.google.gson.JsonObject;

public interface EmailRequestDao {

	String emailRequest(JsonObject requestDetails);

	List<Document> getAllRequeter();

	String uploadPdfFile(InputStream inputStream, String filename, String certificationid);

}
