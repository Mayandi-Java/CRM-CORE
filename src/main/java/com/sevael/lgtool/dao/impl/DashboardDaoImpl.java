package com.sevael.lgtool.dao.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.sevael.lgtool.configuration.LgtDBFactory;
import com.sevael.lgtool.dao.CommonDBDao;
import com.sevael.lgtool.dao.DashboardDao;
import com.sevael.lgtool.model.DashboardActivityModel;
import com.sevael.lgtool.model.DashboardDealTrendModel;
import com.sevael.lgtool.model.DashboardLeadTrendModel;
import com.sevael.lgtool.model.DashboardModel;
import com.sevael.lgtool.model.DashboardTopServiceModel;
import com.sevael.lgtool.model.DashboardTopUserModel;
import com.sevael.lgtool.utils.AppConstants;
import com.sevael.lgtool.utils.UtilConstants;

@Repository
public class DashboardDaoImpl implements DashboardDao, AppConstants, UtilConstants {

	@Autowired
	private CommonDBDao commonDBDao;

	@Override
	public DashboardModel getDashboard(String userid) {
		MongoClient cli = null;
		DashboardModel dashboardModel = new DashboardModel();
		List<Document> docList = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_DASHBOARD_DEALS_DATA,
				Filters.and(
						Filters.gte("dateval", LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).toEpochDay()),
						Filters.lte("dateval", LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).toEpochDay())),
				null, null, 0);
		List<Document> prevMonthDocList = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_DASHBOARD_DEALS_DATA,
				Filters.and(
						Filters.gte("dateval",
								LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).toEpochDay()),
						Filters.lte("dateval",
								LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).toEpochDay())),
				null, null, 0);

		double worth = 0;
		for (Document doc : docList) {
			worth = worth + doc.getDouble("worth");
		}
		
		
		dashboardModel.setWondealsworth(Math.round(worth));
		dashboardModel.setWondeals(docList.size());
		dashboardModel.setDealsdiff(docList.size() - prevMonthDocList.size());

		docList = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_RETAIL, Filters.and(
				Filters.gte("converteddateval", LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).toEpochDay()),
				Filters.lte("converteddateval", LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).toEpochDay())),
				null, null, 0);
		prevMonthDocList = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_RETAIL,
				Filters.and(Filters.eq("type", "contact"),
						Filters.gte("converteddateval",
								LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).toEpochDay()),
						Filters.lte("converteddateval",
								LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).toEpochDay())),
				null, null, 0);

		dashboardModel.setNewcontacts(docList.size());
		dashboardModel.setConverted(docList.size());

		docList = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_RETAIL, Filters.eq("type", "contact"), null, null,
				0);
		dashboardModel.setTotalcontacts(docList.size());

		dashboardModel.setContactdiff(docList.size() - prevMonthDocList.size());

		docList = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_RETAIL, Filters.and(Filters.eq("type", "lead"),
				Filters.gte("creationdateval", LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).toEpochDay()),
				Filters.lte("creationdateval", LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).toEpochDay())),
				null, null, 0);

		prevMonthDocList = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_RETAIL,
				Filters.and(Filters.eq("type", "lead"),
						Filters.gte("creationdateval",
								LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).toEpochDay()),
						Filters.lte("creationdateval",
								LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).toEpochDay())),
				null, null, 0);
		Document leadDashDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_DASHBOARD_LEAD_DATA,
				Filters.eq("month", LocalDate.now().getMonthValue()), null);
		int leadDashCount = 0;
		if (leadDashDoc != null) {
			leadDashCount = leadDashDoc.getInteger("count", 0);
		}
		dashboardModel.setNewleads(leadDashCount);
		dashboardModel.setLeaddiff(docList.size() - prevMonthDocList.size());
		dashboardModel.setActivities(getActivities());
		return dashboardModel;
	}

	private List<DashboardActivityModel> getActivities() {
		List<DashboardActivityModel> actList = new LinkedList<DashboardActivityModel>();
		List<Document> docList = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_DASHBOARD_ACTIVITY_DATA, null,
				Sorts.descending("seq"), null, 5);
		DashboardActivityModel dashboardActivityModel = null;
		String collname = "";
		for (Document doc : docList) {
			dashboardActivityModel = new DashboardActivityModel();
			dashboardActivityModel.setCustname(commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_RETAIL,
					Filters.eq("_id", doc.getString("custid")), null).getString("firstname"));
			if (doc.getString("servicetype").equalsIgnoreCase("Training")) {
				collname = COLL_NAME_TRAINING;
			} else {
				collname = COLL_NAME_CERTIFICATION;
			}
			dashboardActivityModel.setServicename(commonDBDao
					.getEntityWithFilter(DATABASE_NAME, collname, Filters.eq("_id", doc.getString("serviceid")), null)
					.getString("name"));
			dashboardActivityModel.setServicetype(doc.getString("servicetype"));
			dashboardActivityModel.setStatus(doc.getString("status"));
			dashboardActivityModel.setUsername(commonDBDao
					.getEntityWithFilter(DATABASE_NAME, COLL_USERS, Filters.eq("_id", doc.getString("userid")), null)
					.getString("name"));

			actList.add(dashboardActivityModel);
		}
		return actList;
	}

	@Override
	public List<DashboardTopServiceModel> getTopTrainingDashboard(String userid, int type) {
		List<DashboardTopServiceModel> topTrainingList = new ArrayList<DashboardTopServiceModel>();
		Bson filter = Filters.eq("servicetype", "Training");
		if (type == 1) {
			filter = Filters.and(filter, Filters.eq("month", LocalDate.now().getMonthValue()));
		} else if (type == 2) {
			if (LocalDate.now().getMonthValue() == 1 || LocalDate.now().getMonthValue() == 2
					|| LocalDate.now().getMonthValue() == 3) {
				filter = Filters.and(filter, Filters.and(Filters.gte("month", 1), Filters.lte("month", 3)));
			} else if (LocalDate.now().getMonthValue() == 4 || LocalDate.now().getMonthValue() == 5
					|| LocalDate.now().getMonthValue() == 6) {
				filter = Filters.and(filter, Filters.and(Filters.gte("month", 4), Filters.lte("month", 6)));
			} else if (LocalDate.now().getMonthValue() == 7 || LocalDate.now().getMonthValue() == 8
					|| LocalDate.now().getMonthValue() == 9) {
				filter = Filters.and(filter, Filters.and(Filters.gte("month", 7), Filters.lte("month", 9)));
			} else if (LocalDate.now().getMonthValue() == 10 || LocalDate.now().getMonthValue() == 11
					|| LocalDate.now().getMonthValue() == 12) {
				filter = Filters.and(filter, Filters.and(Filters.gte("month", 10), Filters.lte("month", 12)));
			}
		} else if (type == 3) {
			filter = Filters.and(filter, Filters.eq("year", LocalDate.now().getYear()));
		}

		List<Document> docList = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_DASHBOARD_SERVICES_DATA, filter,
				Sorts.descending("count"), null, 5);
		DashboardTopServiceModel dashboardTopServiceModel = null;
		int i = 1;
		for (Document doc : docList) {
			dashboardTopServiceModel = new DashboardTopServiceModel();
			Document serviceDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_TRAINING,
					Filters.eq("_id", doc.getString("serviceid")), null);
			dashboardTopServiceModel.setName(serviceDoc.getString("name"));
			dashboardTopServiceModel.setPosition(i++);
			dashboardTopServiceModel.setPrevposition(0);
			dashboardTopServiceModel.setValue(doc.getInteger("count", 0));
			topTrainingList.add(dashboardTopServiceModel);
		}
		return topTrainingList;
	}

	@Override
	public List<DashboardTopServiceModel> getTopCertDashboard(String userid, int type) {
		List<DashboardTopServiceModel> topCertList = new ArrayList<DashboardTopServiceModel>();
		Bson filter = Filters.eq("servicetype", "Certification");
		if (type == 1) {
			filter = Filters.and(filter, Filters.eq("month", LocalDate.now().getMonthValue()));
		} else if (type == 2) {
			if (LocalDate.now().getMonthValue() == 1 || LocalDate.now().getMonthValue() == 2
					|| LocalDate.now().getMonthValue() == 3) {
				filter = Filters.and(filter, Filters.and(Filters.gte("month", 1), Filters.lte("month", 3)));
			} else if (LocalDate.now().getMonthValue() == 4 || LocalDate.now().getMonthValue() == 5
					|| LocalDate.now().getMonthValue() == 6) {
				filter = Filters.and(filter, Filters.and(Filters.gte("month", 4), Filters.lte("month", 6)));
			} else if (LocalDate.now().getMonthValue() == 7 || LocalDate.now().getMonthValue() == 8
					|| LocalDate.now().getMonthValue() == 9) {
				filter = Filters.and(filter, Filters.and(Filters.gte("month", 7), Filters.lte("month", 9)));
			} else if (LocalDate.now().getMonthValue() == 10 || LocalDate.now().getMonthValue() == 11
					|| LocalDate.now().getMonthValue() == 12) {
				filter = Filters.and(filter, Filters.and(Filters.gte("month", 10), Filters.lte("month", 12)));
			}
		} else if (type == 3) {
			filter = Filters.and(filter, Filters.eq("year", LocalDate.now().getYear()));
		}

		List<Document> docList = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_DASHBOARD_SERVICES_DATA, filter,
				Sorts.descending("count"), null, 5);
		DashboardTopServiceModel dashboardTopServiceModel = null;
		int i = 1;
		for (Document doc : docList) {
			dashboardTopServiceModel = new DashboardTopServiceModel();
			Document serviceDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_NAME_CERTIFICATION,
					Filters.eq("_id", doc.getString("serviceid")), null);
			dashboardTopServiceModel.setName(serviceDoc.getString("name"));
			dashboardTopServiceModel.setPosition(i++);
			dashboardTopServiceModel.setPrevposition(0);
			dashboardTopServiceModel.setValue(doc.getInteger("count", 0));
			topCertList.add(dashboardTopServiceModel);
		}
		return topCertList;
	}

	@Override
	public List<DashboardTopUserModel> getTopUserDashboard(String userid, int type) {
		List<DashboardTopUserModel> topUserList = new ArrayList<DashboardTopUserModel>();

		List<Document> docList = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_DASHBOARD_USERS_DATA, null,
				Sorts.descending("worth"), null, 5);
		DashboardTopUserModel dashboardTopUserModel = null;
		int i = 1;
		for (Document doc : docList) {
			dashboardTopUserModel = new DashboardTopUserModel();
			Document userDoc = commonDBDao.getEntityWithFilter(DATABASE_NAME, COLL_USERS,
					Filters.eq("_id", doc.getString("userid")), null);
			dashboardTopUserModel.setUsername(userDoc.getString("name"));
			dashboardTopUserModel.setPosition(i++);
			dashboardTopUserModel.setValue(doc.getDouble("worth"));
			topUserList.add(dashboardTopUserModel);
		}

		return topUserList;
	}

	@Override
	public List<DashboardDealTrendModel> getDealTrend(String userid, int type) {
		List<DashboardDealTrendModel> dealtrendList = new LinkedList<DashboardDealTrendModel>();
		DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
		LocalDate startOfCurrentWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
		for (int i = 0; i < 7; i++) {
			DashboardDealTrendModel dashboardDealTrendModel = new DashboardDealTrendModel();
			List<Document> docList = commonDBDao.getAllEntity(DATABASE_NAME, COLL_NAME_DASHBOARD_DEALS_DATA,
					Filters.eq("dateval", startOfCurrentWeek.plusDays(i).toEpochDay()), null, null, 0);
			double worth = 0;
			for (Document doc : docList) {
				worth = worth + doc.getDouble("worth");
			}
			dashboardDealTrendModel.setDeals(docList.size());
			dashboardDealTrendModel.setWorth(Math.round(worth));
			dashboardDealTrendModel.setDateval(startOfCurrentWeek.plusDays(i).format(formatter));
			dealtrendList.add(dashboardDealTrendModel);
		}
		return dealtrendList;
	}

	@Override
	public List<DashboardLeadTrendModel> getLeadTypeTrend() {
		List<DashboardLeadTrendModel> leadTrendList = new ArrayList<DashboardLeadTrendModel>();
		MongoClient cli = LgtDBFactory.getMongoClient();
		MongoDatabase db = cli.getDatabase(DATABASE_NAME);
		MongoCollection<Document> table = db.getCollection(COLL_NAME_RETAIL);
		AggregateIterable<Document> docList = table
				.aggregate(Arrays.asList(Aggregates.match(Filters.ne("leadtype", "")),
						Aggregates.group("$leadtype", Accumulators.sum("count", 1))));
		DashboardLeadTrendModel leadTrend = new DashboardLeadTrendModel();
		for (Document doc : docList) {
			if (doc.getString("_id") != null) {
				leadTrend = new DashboardLeadTrendModel();
				leadTrend.setCount(doc.getInteger("count", 0));
				leadTrend.setType(doc.getString("_id"));
				leadTrendList.add(leadTrend);
			}
		}
		return leadTrendList;
	}
}
