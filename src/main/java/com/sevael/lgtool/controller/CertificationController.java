package com.sevael.lgtool.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sevael.lgtool.service.CertificationService;
import com.sevael.lgtool.utils.AppConstants;

@RestController
@RequestMapping(value = "/certificate")
public class CertificationController implements AppConstants {

	@Autowired
	CertificationService certificationService;

	@RequestMapping(value = "/save", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String save(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return certificationService.save((JsonObject) jp.parse(request.getParameter("certificationDetails")));
	}

	@RequestMapping(value = "/list", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public List<Document> list(HttpServletRequest request) {
		String METHOD_NAME = "[getAllCertifications]";
		System.out.println("entering in to " + METHOD_NAME);
		return certificationService.list(request.getParameter("searchstr"),
				Integer.parseInt(request.getParameter("page")));
	}

	@RequestMapping(value = "/search", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String searchCertifications(HttpServletRequest request) {
		return certificationService.searchCertifications(request.getParameter("searchstr"));
	}

	@RequestMapping(value = "/update", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String update(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return certificationService.update((JsonObject) jp.parse(request.getParameter("updateCertificationDetails")));
	}

	@RequestMapping(value = "/filter", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String filterCertification(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		System.out.println("CertificationController --> filterCertification-->  "
				+ (JsonObject) jp.parse(request.getParameter("filterCertby")));
		return certificationService.filterCertification((JsonObject) jp.parse(request.getParameter("filterCertby")));
	}

	@RequestMapping(value = "/get", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Document get(HttpServletRequest request) {
		return certificationService.get(request.getParameter("id"));
	}

	@RequestMapping(value = "/getPaginationCount", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String getPaginationCount(HttpServletRequest request) {
		return certificationService.getPaginationCount(request.getParameter("searchstr"));
	}

}
