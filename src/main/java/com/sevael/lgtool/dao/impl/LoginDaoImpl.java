package com.sevael.lgtool.dao.impl;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;
import com.sevael.lgtool.dao.CommonDBDao;
import com.sevael.lgtool.dao.LoginDao;
import com.sevael.lgtool.utils.AppConstants;
import com.sevael.lgtool.utils.UtilConstants;

@Repository
public class LoginDaoImpl implements LoginDao, AppConstants, UtilConstants {

	@Autowired
	private CommonDBDao commonDBDao;

	@Override
	public String login(JsonObject loginDetails) {
		String email = loginDetails.get("email").getAsString();
		String password = loginDetails.get("password").getAsString();
		JsonObject returnStatus = new JsonObject();
		Document authDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_AUTH, Filters.and(
				Filters.eq("emailid", email), Filters.eq("password", password), Filters.eq("status", "active")), null);
		if (authDoc != null) {
			returnStatus.addProperty("message", "success");
			returnStatus.addProperty("name", authDoc.getString("name"));
			returnStatus.addProperty("email", authDoc.getString("emailid"));
			returnStatus.addProperty("userrole", authDoc.getString("userrole"));
			returnStatus.addProperty("userid", authDoc.getString("userid"));
		} else {
//			if (email.equalsIgnoreCase("wesly@aatralz.com") && password.equalsIgnoreCase("Muruga@19")) {
//				returnStatus.addProperty("message", "success");
//				returnStatus.addProperty("name", "Wesly");
//				returnStatus.addProperty("email", "wesly@aatralz.com");
//				returnStatus.addProperty("userrole", "admin");
//				returnStatus.addProperty("userid", "");
//			} else {
				returnStatus.addProperty("message", "Invalid Credentials");
//			}
		}
		return returnStatus.toString();
	}

}
