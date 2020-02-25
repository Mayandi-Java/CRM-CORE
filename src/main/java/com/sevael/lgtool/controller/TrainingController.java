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
import com.sevael.lgtool.service.TrainingService;
import com.sevael.lgtool.utils.AppConstants;

@RestController
@RequestMapping(value = "/training")
public class TrainingController implements AppConstants {

	@Autowired
	TrainingService trainingService;

	@RequestMapping(value = "/save", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String addTraining(HttpServletRequest request) {
		String METHOD_NAME = "[addTraining]";
		System.out.println("entering in to " + METHOD_NAME);
		JsonParser jp = new JsonParser();
		return trainingService.save((JsonObject) jp.parse(request.getParameter("trainingDetails")));
	}

	@RequestMapping(value = "/list", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public List<Document> list(HttpServletRequest request) {
		String METHOD_NAME = "[getAllTraining]";
		System.out.println("entering in to " + METHOD_NAME);
		return trainingService.list(request.getParameter("searchstr"), Integer.parseInt(request.getParameter("page")));
	}

	@RequestMapping(value = "/get", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Document get(HttpServletRequest request) {
		return trainingService.get(request.getParameter("id"));
	}

	@RequestMapping(value = "/searchTraining", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String searchTraining(HttpServletRequest request) {
		return trainingService.searchTraining(request.getParameter("searchstr"));
	}

	@RequestMapping(value = "/update", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String updateTraining(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return trainingService.update((JsonObject) jp.parse(request.getParameter("trainingDetails")));
	}

	@RequestMapping(value = "/getPaginationCount", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String getPaginationCount(HttpServletRequest request) {
		return trainingService.getPaginationCount(request.getParameter("searchstr"));
	}
}
