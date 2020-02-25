package com.sevael.lgtool.configuration;

import com.mongodb.MongoClient;
import com.sevael.lgtool.utils.AppConstants;


public class LgtDBFactory implements AppConstants{
	
	public static MongoClient getMongoClient() {
		try {
			//log.debug("Initializing Mongo DB client");
			return new MongoClient(DATABASE_IP, DATABASE_PORT);
		} catch (Exception e) {
			System.out.println("Exception");
			e.printStackTrace();
		}
		return null;
	}
	
	public static void closeMongoCLient(MongoClient mongo){
		mongo.close();
	}
	
}
