package com.sevael.lgtool.dao.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sevael.lgtool.configuration.LgtDBFactory;
import com.sevael.lgtool.utils.AppConstants;

@Repository
public class AgeingEsclationThreadImp implements AppConstants {

//	@Autowired
//	private SessionFactory sessionFactory;

	@Transactional
	public void getAgingCount() {
		MongoClient cli = null;

		cli = LgtDBFactory.getMongoClient();
		MongoDatabase db = cli.getDatabase(DATABASE_NAME);
		MongoCollection<Document> table = db.getCollection(COLL_NAME_RETAIL);

	}

	private String getMailID(Long id) {
//		String sqlQuery = "select email from " + UserMaster.class.getName() + " where id = :id";
//		Query query = sessionFactory.getCurrentSession().createQuery(sqlQuery);
//		query.setParameter("id", id);
//		return (String) query.uniqueResult();
		return "0";
	}

//	private void sendMail(SignOffStatus signOffStatus, int level) {
//		Query query = sessionFactory.getCurrentSession()
//				.createQuery("from " + PackagingMaster.class.getName() + " where id = :packmasterid");
//		query.setParameter("packmasterid", signOffStatus.getPacksignoffid());
//		PackagingMaster packMaster = (PackagingMaster) query.uniqueResult();
//		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
//		String teamname = signOffStatus.getTeamname();
//		JsonObject jsonObj = new JsonObject();
//		JsonArray toArr = new JsonArray();
//		JsonArray ccArr = new JsonArray();
//		toArr.add(getMailID(signOffStatus.getApproverid()));
//		String teamNames = "'MHE'";
//		if (teamname.equalsIgnoreCase("IPL")) {
//			teamNames = teamNames + ",'SMQ','production'";
//		} else if (teamname.equalsIgnoreCase("production")) {
//			teamNames = teamNames + ",'SMQ','IPL'";
//		} else if (teamname.equalsIgnoreCase("SMQ")) {
//			teamNames = teamNames + ",'IPL','production'";
//		}
//		query = sessionFactory.getCurrentSession().createQuery("from " + SignOffStatus.class.getName()
//				+ " where teamname IN (" + teamNames + ") and packsignoffid = :packsignoffid");
//
//		query.setParameter("packsignoffid", packMaster.getId());
//		List<SignOffStatus> signOffStatusList = (List<SignOffStatus>) query.getResultList();
//		for (SignOffStatus signOff : signOffStatusList) {
//			ccArr.add(getMailID(signOff.getApproverid()));
//		}
//		if (level == 2) {
//			for (SignOffStatus signOff : signOffStatusList) {
//				Query query1 = sessionFactory.getCurrentSession()
//						.createQuery("select teamid from " + UserMaster.class.getName() + " where id = :id");
//				query1.setParameter("id", signOff.getApproverid());
//				Long teamid = (Long) query1.uniqueResult();
//
//				query1 = sessionFactory.getCurrentSession()
//						.createQuery("select l4userid from " + TeamMaster.class.getName() + " where id = :id");
//				query1.setParameter("id", teamid);
//				long l4id = (long) query1.uniqueResult();
//
//				query1 = sessionFactory.getCurrentSession()
//						.createQuery("select email from " + UserMaster.class.getName() + " where id = :id");
//				query1.setParameter("id", l4id);
//				String l4mail = (String) query1.uniqueResult();
//				ccArr.add(l4mail);
//			}
//		}
//		jsonObj.add("to", toArr);
//		jsonObj.add("cc", ccArr);
//		String subject = "DICV - Packaging Sign Off - Pending with " + signOffStatus.getTeamname() + "-Team. ";
//		if (level == 1) {
//			subject = subject + " First Remider";
//		} else {
//			subject = subject + " Second Remider";
//		}
//
//		jsonObj.addProperty("subject",
//				subject + packMaster.getPartDetails().getPartnumber() + "-"
//						+ packMaster.getSupplierDetails().getSuppliercode() + "-"
//						+ packMaster.getSupplierDetails().getSuppliername());
//		StringBuffer strBuff = new StringBuffer();
//		try {
//			strBuff = new StringBuffer("Dear Colleague,\r\n\n"
//					+ " For the following packaging sign off for the supplier ("
//					+ packMaster.getSupplierDetails().getSuppliercode()
//					+ "), the DICV in chennai has requested approval of the following document by "
//					+ LocalDate.parse(formatter1.format(formatter.parse(signOffStatus.getAssignedtime()))).plusDays(3)
//							.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
//					+ "\n\n" + "The Packaging Sign off Sheet for the Part Number "
//					+ packMaster.getPartDetails().getPartnumber() + " ("
//					+ packMaster.getSupplierDetails().getSuppliercode() + "-"
//					+ packMaster.getSupplierDetails().getSuppliername() + ") has been assigned to you for approval.");
//			strBuff.append("\n\n Document Reference Number: " + packMaster.getId());
//			strBuff.append("\n\n Supplier Name: " + packMaster.getSupplierDetails().getSuppliername());
//
//			strBuff.append("\n\n For the process of approval, kindly follow through the link -");
//			strBuff.append(" www.digitalsupplychain.bharatbenz.com");
//			strBuff.append("\n\n Once after login kindly enroute to Packaging Portal through Quality Module.");
//			strBuff.append(
//					"\n \nAny questions or problems related to access, kindly reach out to Srinivasan S or Logeswaran SG. \n\nFor Technical related queries, kindly reach out to Ranjith S of MHE Team.");
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		jsonObj.addProperty("message", strBuff.toString());
//		SevaelEmailAlerter.sendSignOffMail(jsonObj, packMaster);
//	}
}
