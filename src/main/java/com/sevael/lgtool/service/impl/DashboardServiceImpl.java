package com.sevael.lgtool.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sevael.lgtool.dao.DashboardDao;
import com.sevael.lgtool.model.DashboardDealTrendModel;
import com.sevael.lgtool.model.DashboardLeadTrendModel;
import com.sevael.lgtool.model.DashboardModel;
import com.sevael.lgtool.model.DashboardTopServiceModel;
import com.sevael.lgtool.model.DashboardTopUserModel;
import com.sevael.lgtool.service.DashboardService;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	public DashboardDao dashboardDao;

	@Override
	public DashboardModel getDashboard(String userid) {
		return dashboardDao.getDashboard(userid);
	}

	@Override
	public List<DashboardTopServiceModel> getTopTrainingDashboard(String userid, int type) {
		return dashboardDao.getTopTrainingDashboard(userid, type);
	}

	@Override
	public List<DashboardTopServiceModel> getTopCertDashboard(String userid, int type) {
		return dashboardDao.getTopCertDashboard(userid, type);
	}

	@Override
	public List<DashboardTopUserModel> getTopUserDashboard(String userid, int type) {
		return dashboardDao.getTopUserDashboard(userid, type);
	}

	@Override
	public List<DashboardDealTrendModel> getDealTrend(String userid, int type) {
		return dashboardDao.getDealTrend(userid, type);
	}

	@Override
	public List<DashboardLeadTrendModel> getLeadTypeTrend() {
		return dashboardDao.getLeadTypeTrend();
	}

}
