package com.sevael.lgtool.service;

import java.util.List;
import java.util.Map;

import com.sevael.lgtool.model.DashboardDealTrendModel;
import com.sevael.lgtool.model.DashboardLeadTrendModel;
import com.sevael.lgtool.model.DashboardModel;
import com.sevael.lgtool.model.DashboardTopServiceModel;
import com.sevael.lgtool.model.DashboardTopUserModel;

public interface DashboardService {

	DashboardModel getDashboard(String userid);

	List<DashboardTopServiceModel> getTopTrainingDashboard(String userid, int type);

	List<DashboardTopServiceModel> getTopCertDashboard(String userid, int type);

	List<DashboardTopUserModel> getTopUserDashboard(String userid, int type);
	
	List<DashboardDealTrendModel> getDealTrend(String userid, int type);

	List<DashboardLeadTrendModel> getLeadTypeTrend();


}
