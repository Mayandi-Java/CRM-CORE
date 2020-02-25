package com.sevael.lgtool.dao;

import java.io.InputStream;
import java.util.List;

import org.bson.Document;

public interface ServeTypeDBDao {

	public String addServiceType(String serviceName, String userType);

	public List<Document> getAllServices();

	public String deleteServices(String _id);

	public String saveServiceImage(InputStream filePart, String fileName);
}
