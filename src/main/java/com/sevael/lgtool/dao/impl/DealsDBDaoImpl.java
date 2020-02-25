package com.sevael.lgtool.dao.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.sevael.lgtool.configuration.LgtDBFactory;
import com.sevael.lgtool.dao.CommonDBDao;
import com.sevael.lgtool.dao.DealsDBDao;
import com.sevael.lgtool.model.DashboardActivityModel;
import com.sevael.lgtool.utils.AppConstants;
import com.sevael.lgtool.utils.UtilConstants;

@Repository
public class DealsDBDaoImpl implements DealsDBDao, AppConstants, UtilConstants {

	@Autowired
	private CommonDBDao commonDBDao;

	@Override
	public String save(JsonObject dealDetails) {
		MongoClient cli = null;
		JsonObject returnStatus = new JsonObject();
		String dealID = "";
		LocalDate currDate = LocalDate.now();
		JsonArray servicesArray = new JsonArray();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_DEALS);

			Document dealDoc = new Document();
			int seq = commonDBDao.getNextSequence(db, COLL_NAME_DEALS);
			dealID = COLL_NAME_DEALS + seq;
			dealDoc.put("_id", dealID);
			dealDoc.put("seq", seq);
			dealDoc.put("creationdate", currDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
			dealDoc.put("creationdateval", currDate.toEpochDay());
			Document dueDateDoc = new Document();
			dealDoc.put("status", "pending");
			String[] fields = { "custname", "custmiddlename", "custlastname", "custphone", "custemail", "custid",
					"userid", "custtype", "notes" };
			for (String field : fields) {
				if (dealDetails.has(field)) {
					dealDoc.put(field, dealDetails.get(field).getAsString());
				}
			}
			dealDoc.put("activitiesseq", 0);
			dealDoc.put("servicesseq", 0);
			table.insertOne(dealDoc);
			MongoCollection<Document> dueDateTable = db.getCollection(COLL_NAME_DEALS_DUE_DATE);
			dueDateDoc.put("duedate", dealDetails.get("duedate").getAsString());
			dueDateDoc.put("duedateval",
					LocalDate.parse(dealDetails.get("duedate").getAsString(), formatter).toEpochDay());
			int dueDocseq = commonDBDao.getNextSequence(db, COLL_NAME_DEALS_DUE_DATE);
			String dueID = COLL_NAME_DEALS_DUE_DATE + dueDocseq;
			dueDateDoc.put("_id", dueID);
			dueDateDoc.put("dealid", dealID);
			dueDateDoc.put("seq", dueDocseq);
			dueDateDoc.put("isactive", 1);
			dueDateTable.insertOne(dueDateDoc);
			if (dealDetails.has("services")) {
				servicesArray = dealDetails.getAsJsonArray("services");
				if (servicesArray.size() > 0) {
					for (int i = 0; i < servicesArray.size(); i++) {
						Document servicesDoc = new Document();
						JsonObject servicesObj = new JsonObject();
						servicesObj = servicesArray.get(i).getAsJsonObject();
						int nextServTypeseq = getNextServicesSequence(db, "servicesseq", dealID);
						String servicesID = "services" + nextServTypeseq;
						servicesDoc.put("status", "pending");
						servicesDoc.put("_id", servicesID);
						if (servicesObj.has("servicetype")) {
							servicesDoc.put("servicetype", servicesObj.get("servicetype").getAsString());
						}
						if (servicesObj.has("name")) {
							servicesDoc.put("name", servicesObj.get("name").getAsString());
						}
						if (servicesObj.has("price")) {
							servicesDoc.put("price", servicesObj.get("price").getAsString());
						}
						if (servicesObj.has("date")) {
							servicesDoc.put("date", servicesObj.get("date").getAsString());
						}

						if (servicesObj.has("serviceid")) {
							servicesDoc.put("serviceid", servicesObj.get("serviceid").getAsString());
						}
						table.updateOne(Filters.eq("_id", dealID),
								new Document("$push", new Document("services", servicesDoc)));
						table.updateOne(Filters.eq("_id", dealID),
								new Document("$inc", new Document("servicesseq", 1)));
					}
				}
			}

			returnStatus.addProperty("_id", dealID);
			returnStatus.addProperty(RS_MESSAGE, ADD_COMMON_SUCCESS);
			returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);

		} catch (Exception ex) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			ex.printStackTrace();
		}

		return returnStatus.toString();
	}

	@Override
	public String updateDeal(String dealid, String serviceid, String status) {
		JsonObject retStatus = new JsonObject();
		MongoClient cli = LgtDBFactory.getMongoClient();
		MongoDatabase db = cli.getDatabase(DATABASE_NAME);
		MongoCollection<Document> table = db.getCollection(COLL_NAME_DEALS);
		Document servUpdateDoc = new Document("services.$.status", status);
		servUpdateDoc.put("closeddate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		servUpdateDoc.put("closeddateval", LocalDate.now().toEpochDay());
		table.updateOne(Filters.and(Filters.eq("_id", dealid), Filters.eq("services._id", serviceid)),
				new Document("$set", servUpdateDoc), new UpdateOptions().upsert(true));
		Document dealDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_DEALS, Filters.eq("_id", dealid),
				null);
		List<Document> servicesList = (List<Document>) dealDoc.get("services");
		boolean dealStatus = false;
		Document currService = null;
		for (Document service : servicesList) {
			if (service.getString("_id").equalsIgnoreCase(serviceid)) {
				currService = service;
			}
			if (!service.getString("status").equalsIgnoreCase("pending")) {
				dealStatus = true;
			} else {
				dealStatus = false;
				break;
			}
		}
		Document updateDoc = new Document();
		if (dealStatus) {
			updateDoc.put("status", "closed");
			dealDoc.put("closeddate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
			dealDoc.put("closeddateval", LocalDate.now().toEpochDay());
		} else {
			updateDoc.put("status", "partial");
		}

		table.updateOne(Filters.eq("_id", dealid), new Document("$set", updateDoc));

		MongoCollection<Document> activityTable = db.getCollection(COLL_NAME_DASHBOARD_ACTIVITY_DATA);
		Document dashboardActivityDoc = new Document();
		int actseq = commonDBDao.getNextSequence(db, COLL_NAME_DASHBOARD_ACTIVITY_DATA);
		dashboardActivityDoc.put("_id", COLL_NAME_DASHBOARD_ACTIVITY_DATA + actseq);
		dashboardActivityDoc.put("seq", actseq);
		dashboardActivityDoc.put("serviceid", currService.getString("serviceid"));
		dashboardActivityDoc.put("servicetype", currService.getString("servicetype"));
		dashboardActivityDoc.put("status", status);
		dashboardActivityDoc.put("custid", dealDoc.getString("custid"));
		dashboardActivityDoc.put("userid", dealDoc.getString("userid"));

		activityTable.insertOne(dashboardActivityDoc);

		MongoCollection<Document> usertable = db.getCollection(COLL_NAME_RETAIL);
		usertable.updateOne(Filters.eq("_id", dealDoc.getString("custid")),
				new Document("$push", new Document("services", currService)));
		if (status.equalsIgnoreCase("won")) {
			double worth = 0;
			if (currService.getString("serviceid").contains("Training")) {
				worth = (0.2 * Double.parseDouble(currService.getString("price").replace(",", "")));
			} else {
				String prefAmt = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_CERTIFICATION,
						Filters.eq("_id", currService.getString("serviceid")), null).getString("preferredamt");
				worth = Double.parseDouble(prefAmt.replace(",", ""));
			}

			Document userDashboardDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_DASHBOARD_USERS_DATA,
					Filters.and(Filters.eq("month", LocalDate.now().getMonthValue()),
							Filters.eq("userid", dealDoc.getString("userid"))),
					null);
			MongoCollection<Document> userDashTable = db.getCollection(COLL_NAME_DASHBOARD_USERS_DATA);
			if (userDashboardDoc != null) {
				userDashTable.updateOne(Filters.eq("_id", userDashboardDoc.getString("_id")),
						new Document("$inc", new Document("worth", worth)));
			} else {
				userDashboardDoc = new Document();
				userDashboardDoc.put("month", LocalDate.now().getMonthValue());
				userDashboardDoc.put("userid", dealDoc.getString("userid"));
				userDashboardDoc.put("worth", worth);
				int seq = commonDBDao.getNextSequence(db, COLL_NAME_DASHBOARD_USERS_DATA);
				userDashboardDoc.put("_id", COLL_NAME_DASHBOARD_USERS_DATA + seq);
				userDashboardDoc.put("seq", seq);
				userDashTable.insertOne(userDashboardDoc);
			}

			Document serviceDashboardDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME,
					COLL_NAME_DASHBOARD_SERVICES_DATA, Filters.and(Filters.eq("month", LocalDate.now().getMonthValue()),
							Filters.eq("serviceid", currService.getString("serviceid"))),
					null);
			MongoCollection<Document> serviceTable = db.getCollection(COLL_NAME_DASHBOARD_SERVICES_DATA);
			if (serviceDashboardDoc != null) {
				serviceTable.updateOne(Filters.eq("_id", serviceDashboardDoc.getString("_id")),
						new Document("$inc", new Document("count", 1)));
			} else {
				serviceDashboardDoc = new Document();
				serviceDashboardDoc.put("month", LocalDate.now().getMonthValue());
				serviceDashboardDoc.put("year", LocalDate.now().getYear());
				serviceDashboardDoc.put("serviceid", currService.getString("serviceid"));
				serviceDashboardDoc.put("servicetype", currService.getString("servicetype"));
				serviceDashboardDoc.put("count", 1);
				int seq = commonDBDao.getNextSequence(db, COLL_NAME_DASHBOARD_SERVICES_DATA);
				serviceDashboardDoc.put("_id", COLL_NAME_DASHBOARD_SERVICES_DATA + seq);
				serviceDashboardDoc.put("seq", seq);
				serviceTable.insertOne(serviceDashboardDoc);
			}

			Document dealDashboardDoc = new Document();
			dealDashboardDoc.put("worth", worth);
			dealDashboardDoc.put("dateval", LocalDate.now().toEpochDay());
			int seq = commonDBDao.getNextSequence(db, COLL_NAME_DASHBOARD_DEALS_DATA);
			dealDashboardDoc.put("_id", COLL_NAME_DASHBOARD_DEALS_DATA + seq);
			dealDashboardDoc.put("seq", seq);
			MongoCollection<Document> dealTable = db.getCollection(COLL_NAME_DASHBOARD_DEALS_DATA);
			dealTable.insertOne(dealDashboardDoc);
			if (commonDBDao
					.getEntityWithFilter(DATABASE_NAME, COLL_NAME_RETAIL,
							Filters.eq("_id", dealDoc.getString("custid")), null)
					.getString("type").equalsIgnoreCase("lead")) {
				Document userUpdateDoc = new Document("type", "contact");
				userUpdateDoc.put("converteddate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
				userUpdateDoc.put("converteddateval", LocalDate.now().toEpochDay());
				usertable.updateOne(Filters.eq("_id", dealDoc.getString("custid")),
						new Document("$set", userUpdateDoc));

			}

		}
		retStatus.addProperty("_id", "");
		retStatus.addProperty(RS_MESSAGE, ADD_COMMON_SUCCESS);
		retStatus.addProperty(RS_STATUS, MSG_SUCCESS);

		return retStatus.toString();
	}

	@Override
	public String updateDueDate(String dealid, String duedate, String justification) {
		JsonObject retStatus = new JsonObject();
		MongoClient cli = LgtDBFactory.getMongoClient();
		MongoDatabase db = cli.getDatabase(DATABASE_NAME);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		MongoCollection<Document> table = db.getCollection(COLL_NAME_DEALS_DUE_DATE);
		table.updateMany(Filters.eq("dealid", dealid), new Document("$set", new Document("isactive", 0)));
		Document dealUpdateDoc = new Document("duedate", duedate);
		dealUpdateDoc.put("duedateval", LocalDate.parse(duedate, formatter).toEpochDay());
		dealUpdateDoc.put("justification", justification);
		int dueDocseq = commonDBDao.getNextSequence(db, COLL_NAME_DEALS_DUE_DATE);
		String dueID = COLL_NAME_DEALS_DUE_DATE + dueDocseq;
		dealUpdateDoc.put("_id", dueID);
		dealUpdateDoc.put("dealid", dealid);
		dealUpdateDoc.put("seq", dueDocseq);
		dealUpdateDoc.put("isactive", 1);
		table.insertOne(dealUpdateDoc);
		retStatus.addProperty("_id", "");
		retStatus.addProperty(RS_MESSAGE, ADD_COMMON_SUCCESS);
		retStatus.addProperty(RS_STATUS, MSG_SUCCESS);
		return retStatus.toString();
	}

	@Override
	public String addActivity(JsonObject activity, String id) {
		Document activDoc = new Document();
		MongoClient cli = null;
		String dealActivityID = "";
		JsonObject returnStatus = new JsonObject();
		try {
			cli = LgtDBFactory.getMongoClient();
			MongoDatabase db = cli.getDatabase(DATABASE_NAME);
			MongoCollection<Document> table = db.getCollection(COLL_NAME_DEALS_ACTIVITY);
			MongoCollection<Document> dealTable = db.getCollection(COLL_NAME_DEALS);

			int seq = commonDBDao.getNextSequence(db, COLL_NAME_DEALS_ACTIVITY);
			dealActivityID = COLL_NAME_DEALS_ACTIVITY + seq;
			activDoc.put("_id", dealActivityID);
			activDoc.put("seq", seq);
			activDoc.put("type", activity.get("type").getAsString());
			activDoc.put("notes", activity.get("notes").getAsString());
			activDoc.put("dealsid", id);
			activDoc.put("username", activity.get("username").getAsString());
			activDoc.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
			activDoc.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyy")));

//			table.updateOne(Filters.eq("_id", id), new Document("$push", new Document("activity", activDoc)));
			table.insertOne(activDoc);

			dealTable.updateOne(Filters.eq("_id", id), new Document("$inc", new Document("activitiesseq", 1)));

			returnStatus.addProperty("_id", id);
			returnStatus.addProperty(MSG_SUCCESS, ADD_UPDATECOMMON_SUCCESS);
			returnStatus.addProperty(RS_STATUS, MSG_SUCCESS);
		} catch (Exception ex) {
			returnStatus.addProperty(RS_STATUS, MSG_FAILURE);
			System.out.println("ContactDaoImpl --> " + ex.toString());
		}
		return returnStatus.toString();

	}

	private int getNextServicesSequence(MongoDatabase db, String typeOfseq, String servicesID) {
		int nextSeq = 0;
		Document seqDoc = (Document) db.getCollection(COLL_NAME_DEALS).find(Filters.eq("_id", servicesID))
				.projection(Projections.include(typeOfseq)).sort(Sorts.descending(typeOfseq)).limit(1).first();
		System.out.println("BusinessDBDaoImpl --> line 161 --- >" + seqDoc);
		if (seqDoc != null) {
			nextSeq = seqDoc.getInteger(typeOfseq);
		}
		return nextSeq + 1;
	}

	@Override
	public List<Document> list(String searchstr, int page) {
		final String METHODNAME = "[getAllClients]:";
		List<Document> deals = null;
		Bson mainFilter = null;
		String[] retailfields = { "custname", "custmiddlename", "custlastname", "custemail", "custphone" };

		int pageLimit = 10;
		int numberOfRecordsSkipPerPage = 0;
		if (page > 1) {
			numberOfRecordsSkipPerPage = (10 * page);
			numberOfRecordsSkipPerPage -= 10;
		}

		try {
			if (searchstr != null && searchstr.length() > 0) {
				deals = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_DEALS,
						Filters.regex("custname", "^" + searchstr + ".*", "i"), Sorts.descending("seq"), null, 0);
				for (String field : retailfields) {
					if (mainFilter != null) {
						mainFilter = Filters.or(mainFilter, Filters.regex(field, "^" + searchstr + ".*", "i"));
					} else {
						mainFilter = Filters.regex(field, "^" + searchstr + ".*", "i");
					}
				}
			}

			deals = commonDBDao.getAllEntityWithPagination(DATABASE_NAME, COLL_NAME_DEALS, mainFilter,
					Sorts.descending("seq"), null, numberOfRecordsSkipPerPage, pageLimit);

			for (Document deal : deals) {
				deal.put("duedate", commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_DEALS_DUE_DATE,
						Filters.and(Filters.eq("dealid", deal.getString("_id")), Filters.eq("isactive", 1)), null));

				deal.put("activity", commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_DEALS_ACTIVITY,
						Filters.eq("dealsid", deal.getString("_id")), Sorts.descending("seq"), null, 0));

				deal.put("retaildetail", commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_RETAIL,
						Filters.eq("_id", deal.getString("custid")), null));

			}
		} catch (Exception ex) {
			System.out.println("ClientsDBDaoImpl ---> getAllClients ---> " + ex.toString());
		}
		return deals;
	}

	@Override
	public Document get(String id) {
		Document dealDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_DEALS, Filters.eq("_id", id), null);
		dealDoc.put("duedate", commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_DEALS_DUE_DATE,
				Filters.eq("dealid", id), null, null, 0));
		dealDoc.put("retaildetail", commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_RETAIL,
				Filters.eq("_id", dealDoc.getString("custid")), null));
		dealDoc.put("activity", commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_DEALS_ACTIVITY,
				Filters.eq("dealsid", dealDoc.getString("_id")), Sorts.descending("seq"), null, 0));
		return dealDoc;
	}

	@Override
	public String getPaginationCount(String searchstr) {
		final String METHODNAME = "[getAllClients]:";
		List<Document> deals = null;
		Bson mainFilter = null;
		JsonObject returnStatus = new JsonObject();
		String[] retailfields = { "custname", "custmiddlename", "custlastname", "custemail", "custphone" };
		returnStatus.addProperty(PAGE_COUNT, 0);
		try {
			if (searchstr != null && searchstr.length() > 0) {
				deals = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_DEALS,
						Filters.regex("custname", "^" + searchstr + ".*", "i"), Sorts.descending("seq"), null, 0);
				for (String field : retailfields) {
					if (mainFilter != null) {
						mainFilter = Filters.or(mainFilter, Filters.regex(field, "^" + searchstr + ".*", "i"));
					} else {
						mainFilter = Filters.regex(field, "^" + searchstr + ".*", "i");
					}
				}
			}

			deals = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_DEALS, mainFilter, Sorts.descending("seq"), null,
					0);

			if (deals.size() > 0) {
				returnStatus.addProperty(PAGE_COUNT, deals.size());
			}
		} catch (Exception ex) {
			System.out.println("ClientsDBDaoImpl ---> getAllClients ---> " + ex.toString());
		}
		return returnStatus.toString();
	}

}
