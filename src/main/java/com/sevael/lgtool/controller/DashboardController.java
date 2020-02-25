package com.sevael.lgtool.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sevael.lgtool.model.DashboardDealTrendModel;
import com.sevael.lgtool.model.DashboardLeadTrendModel;
import com.sevael.lgtool.model.DashboardModel;
import com.sevael.lgtool.model.DashboardTopServiceModel;
import com.sevael.lgtool.model.DashboardTopUserModel;
import com.sevael.lgtool.service.DashboardService;
import com.sevael.lgtool.utils.AppConstants;

@RestController
@RequestMapping(value = "/dashboard")
public class DashboardController implements AppConstants {

	/** The ServeType service. */
	@Autowired
	DashboardService dashboardService;

	@RequestMapping(value = "/get", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public ResponseEntity<DashboardModel> getDashboard(HttpServletRequest request)
			throws IOException, ServletException {
		DashboardModel dashboardModel = dashboardService.getDashboard(request.getParameter("id"));
		return ResponseEntity.ok().body(dashboardModel);
	}

	@RequestMapping(value = "/getTopUser", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public ResponseEntity<List<DashboardTopUserModel>> countBySuppCode(HttpServletRequest request)
			throws IOException, ServletException {
		List<DashboardTopUserModel> dashboardModel = dashboardService.getTopUserDashboard(request.getParameter("id"),
				1);
		return ResponseEntity.ok().body(dashboardModel);
	}

	@RequestMapping(value = "/getTopTraining", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public ResponseEntity<List<DashboardTopServiceModel>> getTopTrainingDashboard(HttpServletRequest request)
			throws IOException, ServletException {
		List<DashboardTopServiceModel> dashboardModel = dashboardService
				.getTopTrainingDashboard(request.getParameter("id"), 1);
		return ResponseEntity.ok().body(dashboardModel);
	}

	@RequestMapping(value = "/getTopCert", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public ResponseEntity<List<DashboardTopServiceModel>> getTopCertDashboard(HttpServletRequest request)
			throws IOException, ServletException {
		List<DashboardTopServiceModel> dashboardModel = dashboardService.getTopCertDashboard(request.getParameter("id"),
				1);
		return ResponseEntity.ok().body(dashboardModel);
	}

	@RequestMapping(value = "/getDealTrend", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public ResponseEntity<List<DashboardDealTrendModel>> getDealTrend(HttpServletRequest request)
			throws IOException, ServletException {
		List<DashboardDealTrendModel> dashboardModel = dashboardService.getDealTrend(request.getParameter("id"),
				1);
		return ResponseEntity.ok().body(dashboardModel);
	}

	@RequestMapping(value = "/getLeadTrend", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public ResponseEntity<List<DashboardLeadTrendModel>> getLeadTrend(HttpServletRequest request)
			throws IOException, ServletException {
		List<DashboardLeadTrendModel> dashboardModel = dashboardService.getLeadTypeTrend();
		return ResponseEntity.ok().body(dashboardModel);
	}

}
