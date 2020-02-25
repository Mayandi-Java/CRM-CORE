package com.sevael.lgtool.dao;

import java.util.List;

import org.bson.Document;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public interface CertificationDBDao {

	String save(JsonObject certificationDetails);

	List<Document> list(String searchstr, int page);

	String searchCertifications(String searchstr);

	String update(JsonObject updateCertificationDetails);

	String filterCertification(JsonObject filterCertby);

	Document get(String id);

	String getPaginationCount(String searchstr);

}
