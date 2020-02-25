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
import com.sevael.lgtool.utils.AppConstants;

@RestController
@RequestMapping(value = "/category")
public class CategoryController implements AppConstants {

	@Autowired
	CategoryService categoryService;

	@RequestMapping(value = "/save", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String save(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return categoryService.save((JsonObject) jp.parse(request.getParameter("catDetails")));
	}

	@RequestMapping(value = "/list", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public List<Document> list(HttpServletRequest request) {
		String METHOD_NAME = "[getAllTrainingCategory]";
		System.out.println("entering in to " + METHOD_NAME);
		return categoryService.list(request.getParameter("searchstr"), Integer.parseInt(request.getParameter("page")));
	}

	@RequestMapping(value = "/get", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Document get(HttpServletRequest request) {
		return categoryService.get(request.getParameter("id"));
	}

	@RequestMapping(value = "/update", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String update(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return categoryService.update((JsonObject) jp.parse(request.getParameter("catDetails")));
	}

	@RequestMapping(value = "/getPaginationCount", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String getPaginationCount(HttpServletRequest request) {
		return categoryService.getPaginationCount(request.getParameter("searchstr"));
	}

}
