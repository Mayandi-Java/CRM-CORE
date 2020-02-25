package com.sevael.lgtool.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sevael.lgtool.service.DealsService;
import com.sevael.lgtool.utils.AppConstants;

@RestController
@RequestMapping(value = "/deal")
public class DealsController implements AppConstants {

	@Autowired
	DealsService dealsService;

	@RequestMapping(value = "/save", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String save(HttpServletRequest request) {
		String METHOD_NAME = "[addClients]";
		System.out.println("entering in to " + METHOD_NAME);
		JsonParser jp = new JsonParser();
		return dealsService.save((JsonObject) jp.parse(request.getParameter("dealDetails")));
	}

	@RequestMapping(value = "/list", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public List<Document> list(HttpServletRequest request) {
		String METHOD_NAME = "[getAllClients]";
		System.out.println("entering in to " + METHOD_NAME);
		return dealsService.list(request.getParameter("searchstr"), Integer.parseInt(request.getParameter("page")));
	}

	

	@RequestMapping(value = "/get", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Document get(HttpServletRequest request) {
		return dealsService.get(request.getParameter("id"));
	}

	@RequestMapping(value = "/addActivity", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String addActivity(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return dealsService.addActivity((JsonObject) jp.parse(request.getParameter("activity")),
				request.getParameter("id"));
	}

	@RequestMapping(value = "/updateDeal", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String updateDeal(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return dealsService.updateDeal(request.getParameter("dealid"), request.getParameter("serviceid"),
				request.getParameter("status"));
	}

	@RequestMapping(value = "/updateDueDate", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String updateDueDate(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return dealsService.updateDueDate(request.getParameter("dealid"), request.getParameter("duedate"),
				request.getParameter("justification"));
	}

	@RequestMapping(value = "/getPaginationCount", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String getPaginationCount(HttpServletRequest request) {
		return dealsService.getPaginationCount(request.getParameter("searchstr"));
	}

}