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
import com.sevael.lgtool.service.ClientsService;
import com.sevael.lgtool.utils.AppConstants;

@RestController
@RequestMapping(value = "/client")
public class ClientsController implements AppConstants {

	@Autowired
	ClientsService clientsService;

	@RequestMapping(value = "/save", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String save(HttpServletRequest request) {
		String METHOD_NAME = "[addClients]";
		System.out.println("entering in to " + METHOD_NAME);
		JsonParser jp = new JsonParser();
		return clientsService.save((JsonObject) jp.parse(request.getParameter("clientDetails")));
	}

	@RequestMapping(value = "/list", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public List<Document> list(HttpServletRequest request) {
		String METHOD_NAME = "[getAllClients]";
		System.out.println("entering in to " + METHOD_NAME);
		return clientsService.list(request.getParameter("searchstr"), Integer.parseInt(request.getParameter("page")));
	}

	@RequestMapping(value = "/get", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Document get(HttpServletRequest request) {
		return clientsService.get(request.getParameter("id"));
	}

	@RequestMapping(value = "/update", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String updateClient(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return clientsService.update((JsonObject) jp.parse(request.getParameter("clientDetails")));
	}

	@RequestMapping(value = "/getPaginationCount", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String getPaginationCount(HttpServletRequest request) {
		return clientsService.getPaginationCount(request.getParameter("searchstr"));
	}

}