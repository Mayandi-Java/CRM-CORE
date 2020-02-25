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
import com.sevael.lgtool.service.CategoryService;
import com.sevael.lgtool.service.LevelService;
import com.sevael.lgtool.utils.AppConstants;

@RestController
@RequestMapping(value = "/level")
public class LevelController implements AppConstants {

	@Autowired
	LevelService levelService;

	@RequestMapping(value = "/save", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String save(HttpServletRequest request) {
		String METHOD_NAME = "[addTrainingCategory]";
		System.out.println("entering in to " + METHOD_NAME);
		JsonParser jp = new JsonParser();
		return levelService.save((JsonObject) jp.parse(request.getParameter("levelDetails")));
	}

	@RequestMapping(value = "/list", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public List<Document> list(HttpServletRequest request) {
		String METHOD_NAME = "[getAllTrainingCategory]";
		System.out.println("entering in to " + METHOD_NAME);
		return levelService.list(request.getParameter("searchstr"), Integer.parseInt(request.getParameter("page")));
	}

	

	@RequestMapping(value = "/update", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String update(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return levelService.update((JsonObject) jp.parse(request.getParameter("levelDetails")));
	}

	@RequestMapping(value = "/get", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Document get(HttpServletRequest request) {
		return levelService.get(request.getParameter("id"));
	}

	@RequestMapping(value = "/getPaginationCount", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String getPaginationCount(HttpServletRequest request) {
		return levelService.getPaginationCount(request.getParameter("searchstr"));
	}

}
