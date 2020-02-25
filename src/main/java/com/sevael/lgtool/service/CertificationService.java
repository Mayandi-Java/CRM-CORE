package com.sevael.lgtool.service;

import java.util.List;

import org.bson.Document;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public interface CertificationService {

	String save(JsonObject certificationDetails);

	List<Document> list(String searchstr, int page);

	String searchCertifications(String searchstr);

	String update(JsonObject updateCertificationDetails);

	String filterCertification(JsonObject filterCertby);

	Document get(String id);

	String getPaginationCount(String searchstr);
}
