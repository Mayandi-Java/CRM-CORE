package com.sevael.lgtool.utils;

import org.bson.Document;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonUtil {
	public static JsonElement parseJSON(Document docStr){
		JsonParser jp = new JsonParser();
		return jp.parse(docStr.toJson());
	}
	

}
