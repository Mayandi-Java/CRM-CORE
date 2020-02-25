package com.sevael.lgtool.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sevael.lgtool.service.BusinessService;
import com.sevael.lgtool.utils.AppConstants;

@RestController
@RequestMapping(value = "/business")
public class BusinessController implements AppConstants {

	@Autowired
	BusinessService businessService;

	@RequestMapping(value = "/save", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String addBusinessContact(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return businessService.save((JsonObject) jp.parse(request.getParameter("businessDetails")));
	}

	@RequestMapping(value = "/update", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String update(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return businessService.update((JsonObject) jp.parse(request.getParameter("updateDetails")));
	}

	@RequestMapping(value = "/list", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public List<Document> list(HttpServletRequest request) {
		return businessService.list(request.getParameter("type"), request.getParameter("searchstr"),
				Integer.parseInt(request.getParameter("page")));
	}

	@RequestMapping(value = "/delete", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String delete(HttpServletRequest request) {
		return businessService.delete(request.getParameter("_id"), request.getParameter("serv_id"));
	}

	@RequestMapping(value = "/get", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Document get(HttpServletRequest request) {
		return businessService.get(request.getParameter("id"));
	}

	@RequestMapping(value = "/search", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String searchBusinessContact(HttpServletRequest request) {
		return businessService.searchBusinessContact(request.getParameter("searchstr"));
	}

	@RequestMapping(value = "/filter", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String filterBusinessContacts(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return businessService.filterBusinessContacts((JsonObject) jp.parse(request.getParameter("filterby")));
	}

	@RequestMapping(value = "/addActivity", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String addActivity(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return businessService.addActivity((JsonObject) jp.parse(request.getParameter("activity")),
				request.getParameter("id"));
	}

	@RequestMapping(value = "/download", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String download(HttpServletResponse response, HttpServletRequest request) {
		String excelName = "";
		try {
			excelName = businessService.download(request.getParameter("type"));
			Path path = Paths.get(excelName);
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "attachment; filename=" + excelName);
			Files.copy(path, response.getOutputStream());
			response.getOutputStream().flush();
		} catch (Exception e) {
			String message = "Error in download PDF file " + e.getMessage();
			System.out.println(message);
			// Console.log(message);
		}
		if (excelName.length() > 0) {
//                    retSts.setStatus("Success");
		}
		return "success";
	}

	@RequestMapping(value = "/getPaginationCount", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String getPaginationCount(HttpServletRequest request) {
		return businessService.getPaginationCount(request.getParameter("type"), request.getParameter("searchstr"));
	}
}
