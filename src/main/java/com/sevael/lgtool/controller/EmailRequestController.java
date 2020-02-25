package com.sevael.lgtool.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.Part;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sevael.lgtool.service.EmailRequestService;
import com.sevael.lgtool.utils.AppConstants;

@RestController
public class EmailRequestController implements AppConstants {

	@Autowired
	EmailRequestService emailRequestservice;

	@RequestMapping(value = "/emailRequest", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String emailRequest(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return emailRequestservice.emailRequest((JsonObject) jp.parse(request.getParameter("requestDetails")));
	}

	@RequestMapping(value = "/getAllRequeter", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public List<Document> getAllRequeter() {
		return emailRequestservice.getAllRequeter();
	}

	@RequestMapping(value = "/uploadPdf", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String uploadPdfFile(HttpServletRequest request) throws IOException, ServletException {
		Part filePart = request.getPart("file");
		String result = emailRequestservice.uploadPdfFile(filePart.getInputStream(), request.getParameter("filename"),
				request.getParameter("certificationid"));
		
		return result;
	}
}
