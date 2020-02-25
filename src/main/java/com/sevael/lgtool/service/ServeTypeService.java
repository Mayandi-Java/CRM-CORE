package com.sevael.lgtool.service;

import java.io.InputStream;
import java.util.List;

import org.bson.Document;

public interface ServeTypeService {

	public String addServiceType(String serveType, String userType);

	public List<Document> getAllServices();

	public String deleteServices(String _id);

	public String saveServiceImage(InputStream filePart, String fileName);
}
