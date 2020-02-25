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
import com.sevael.lgtool.service.ServProviderService;
import com.sevael.lgtool.utils.AppConstants;

@RestController
@RequestMapping(value = "/serviceprovider")
public class ServiceProviderController implements AppConstants {

	@Autowired
	ServProviderService servProviderService;

	@RequestMapping(value = "/save", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String addServiceProvider(HttpServletRequest request) {
		String METHOD_NAME = "[addServiceProvider]";
		System.out.println("entering in to " + METHOD_NAME);
		JsonParser jp = new JsonParser();
		return servProviderService.save((JsonObject) jp.parse(request.getParameter("servproviderDetails")));
	}

	@RequestMapping(value = "/list", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public List<Document> getAllServiceProvider(HttpServletRequest request) {
		String METHOD_NAME = "[getAllServiceProvider]";
		System.out.println("entering in to " + METHOD_NAME);
		return servProviderService.list(request.getParameter("searchstr"),
				Integer.parseInt(request.getParameter("page")));
	}

	

	@RequestMapping(value = "/get", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Document get(HttpServletRequest request) {
		return servProviderService.get(request.getParameter("id"));
	}

	@RequestMapping(value = "/update", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String updateServiceProvider(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return servProviderService.update((JsonObject) jp.parse(request.getParameter("servproviderDetails")));
	}

	@RequestMapping(value = "/getPaginationCount", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String getPaginationCount(HttpServletRequest request) {
		return servProviderService.getPaginationCount(request.getParameter("searchstr"));
	}

}
