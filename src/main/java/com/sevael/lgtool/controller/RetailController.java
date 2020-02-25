package com.sevael.lgtool.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.gridfs.GridFSDBFile;
import com.sevael.lgtool.service.RetailService;
import com.sevael.lgtool.utils.AppConstants;

@RestController
@RequestMapping(value = "/retail")
public class RetailController implements AppConstants {

	@Autowired
	RetailService contactService;

	@RequestMapping(value = "/save", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String save(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return contactService.save((JsonObject) jp.parse(request.getParameter("contactDetails")));
	}

	@RequestMapping(value = "/uploadImage", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String uploadImage(HttpServletRequest request) throws IOException, ServletException {
		JsonParser jp = new JsonParser();
		Part filePart = request.getPart("file");
		return contactService.uploadImage(String.valueOf(request.getParameter("_id")), filePart.getInputStream());
	}

	@RequestMapping(value = "/update", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String update(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return contactService.update((JsonObject) jp.parse(request.getParameter("updateDetails")));
	}

	@RequestMapping(value = "/addActivity", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String addActivity(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return contactService.addActivity((JsonObject) jp.parse(request.getParameter("activity")),
				request.getParameter("id"));
	}

	@RequestMapping(value = "/list", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public List<Document> list(HttpServletRequest request) {
		return contactService.list(request.getParameter("type"), request.getParameter("searchstr"),
				Integer.parseInt(request.getParameter("page")));
	}

	@RequestMapping(value = "/get", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Document get(HttpServletRequest request) {
		return contactService.get(request.getParameter("id"));
	}

	@RequestMapping(value = "/search", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Map<String, List<Document>> search(HttpServletRequest request) {
		return contactService.search(request.getParameter("searchstr"));
	}

	@RequestMapping(value = "/filter", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String filter(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return contactService.filter((JsonArray) jp.parse(request.getParameter("filterby")));
	}

	@RequestMapping(value = "/updateServiceStatus", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String updateServiceStatus(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return contactService.updateServiceStatus(request.getParameter("_id"), request.getParameter("serv_id"),
				request.getParameter("status"));
	}

	@RequestMapping(value = "/uploadRetailLead", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String uploadRetail(HttpServletRequest request) throws IOException, ServletException {
		Part filePart = request.getPart("file");
		String returnstr = contactService.uploadRetail(filePart.getInputStream(), request.getParameter("filename"));
		return returnstr;
	}

	@RequestMapping(value = "/getContactImage", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public void getProfileImage(HttpServletRequest request, HttpServletResponse response) {
		try {
			contactService.getProfileImage(request.getParameter("_id").toString()).writeTo(response.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/download", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String downloadExcel(HttpServletResponse response, HttpServletRequest request) {
		String excelName = null;
		JsonObject retSts = new JsonObject();
		try {
			excelName = contactService.downloadExcel(request.getParameter("type"));
			Path path = Paths.get(excelName);
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "attachment; filename=" + excelName);
			Files.copy(path, response.getOutputStream());
			response.getOutputStream().flush();
		} catch (Exception e) {
			String message = "Error in download PDF file " + e.getMessage();
			// Console.log(message);
		}
		if (excelName.length() > 0) {
			retSts.addProperty("status", "Success");
		} else {
			retSts.addProperty("status", "Failed to download");
		}
		return retSts.toString();
	}

	@RequestMapping(value = "/downloadFilterExcel", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String downloadFilterExcel(HttpServletResponse response, HttpServletRequest request) {
		String excelName = null;
		JsonParser jp = new JsonParser();
		try {
			System.out.println("Controller downloadFilterExcel => "
					+ (JsonObject) jp.parse(request.getParameter("filterDetails")));
			excelName = contactService
					.downloadFilterExcel((JsonObject) jp.parse(request.getParameter("filterDetails")));
			System.out.println("downloadFilterExcel excelName => " + excelName);
			Path path = Paths.get(excelName);
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "attachment; filename=" + excelName);
			Files.copy(path, response.getOutputStream());
			response.getOutputStream().flush();
		} catch (Exception e) {
			String message = "Error in download PDF file " + e.getMessage();
			// Console.log(message);
		}

		return "success";
	}

	@RequestMapping(value = "/getPaginationCount", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String getPaginationCount(HttpServletRequest request) {
		return contactService.getPaginationCount(request.getParameter("type"), request.getParameter("searchstr"));
	}

}
