package com.sevael.lgtool.utils;

public interface AppConstants {

	public static String PROPERTYCATEGORY = Config.instance().getProperty("property.category", "com.sevael.lgtool");
	public String LOGGINGCATEGORY = Config.instance().getProperty(PROPERTYCATEGORY, "FRAMEWORK.LOGCATEGORY", "Lgtool");
	public static final String DATABASE_IP = Config.instance().getProperty(PROPERTYCATEGORY, "DB.IP", "localhost");
//	public static final String DATABASE_IP= Config.instance().getProperty( PROPERTYCATEGORY , "DB.IP" , "139.59.75.83" );
	public static final int DATABASE_PORT = Integer
			.parseInt(Config.instance().getProperty(PROPERTYCATEGORY, "DB.PORT", "27017"));
//	public static final String DATABASE_NAME = Config.instance().getProperty(PROPERTYCATEGORY, "DB.NAME", "Lgtool");

	// Testing DB Name
	public static final String DATABASE_NAME = Config.instance().getProperty(PROPERTYCATEGORY, "DB.NAME",
			"TestingLeadManagement");

	// Live DB Name
//	public static final String DATABASE_NAME = Config.instance().getProperty(PROPERTYCATEGORY, "DB.NAME",
//			"LeadManagement");

	public static final String DB_NAME_PREFIX = Config.instance().getProperty(PROPERTYCATEGORY, "DB.NAME.PREFIX",
			"LeadManagement");
	public static final String DB_NAME_SUFFIX = Config.instance().getProperty(PROPERTYCATEGORY, "DB.NAME.SUFFIX", "db");
	public static final String SERVICE_IMAGE_DATABASE_NAME = Config.instance().getProperty(PROPERTYCATEGORY,
			"IMAGE.DB.NAME", "Lgtool_Service_images");
	public static final String CONTACT_IMAGE_DATABASE_NAME = Config.instance().getProperty(PROPERTYCATEGORY,
			"IMAGE.DB.NAME", "Lead_management_Contact_Prof_images");

	public static final String filePath = "/opt/tomcat/temp/AllContacts_";
	public static final String excelfilePath = "/opt/tomcat/temp/excel/";

//	public static final String excelfilePath = "C://tmp/";
//	public static final String filePath = "C://tmp//AllContacts_";

	public static final String COLL_NAME_SERVICES = "Services";
	public static final String COLL_NAME_RETAIL = "Retail";
	public static final String COLL_NAME_TRAINING = "Training";
	public static final String COLL_NAME_REQUEST = "Request";
	public static final String COLL_NAME_CERTIFICATION = "Certifications";
	public static final String COLL_NAME_CLIENTS = "Clients";
	public static final String COLL_NAME_SERVICEPROVIDER = "ServiceProvider";
	public static final String COLL_NAME_CATEGORY = "Category";
	public static final String COLL_NAME_LEVEL = "Level";
	public static final String COLL_NAME_BUSINESS = "Business";
	public static final String COLL_NAME_DEALS = "Deals";
	public static final String COLL_NAME_DEALS_ACTIVITY = "DealsActivity";
	public static final String COLL_NAME_DEALS_DUE_DATE = "DealsDueDate";
	public static final String COLL_BUSINESS_ORGANIZATTION = "Organization";
	public static final String COLL_USERS = "Users";
	public static final String COLL_USERS_IMAGES = "User_Images";
	public static final String COLL_AUTH = "Auth";

	public static final String COLL_NAME_DASHBOARD_LEAD_DATA = "DashboardLeadsData";
	public static final String COLL_NAME_DASHBOARD_DEALS_DATA = "DashboardDealsData";
	public static final String COLL_NAME_DASHBOARD_SERVICES_DATA = "DashboardServicesData";
	public static final String COLL_NAME_DASHBOARD_USERS_DATA = "DashboardUsersData";
	public static final String COLL_NAME_DASHBOARD_ACTIVITY_DATA = "DashboardActivityData";

}
