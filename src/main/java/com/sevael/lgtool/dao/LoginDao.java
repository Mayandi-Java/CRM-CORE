package com.sevael.lgtool.dao;

import com.google.gson.JsonObject;

public interface LoginDao {

	public String login(JsonObject loginDetails);	
}
