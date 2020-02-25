package com.sevael.lgtool.dao;

import java.util.List;

import org.bson.Document;

import com.google.gson.JsonObject;

public interface OrganizationDBDao {

	String addEmployee(JsonObject employeeDetails);

	List<Document> getAllEmployees();

	String searchEmployee(String searchstr);

	String updateEmployee(JsonObject updateEmployeeDetails);

}
