package com.sevael.lgtool.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sevael.lgtool.service.ServeTypeService;
import com.sevael.lgtool.utils.AppConstants;

@RestController
public class ServiceController implements AppConstants {

	/** The ServeType service. */
	@Autowired
	ServeTypeService servetypeService;

	@RequestMapping(value = "/addServiceType", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String addServiceType(HttpServletRequest request) {
		String METHOD_NAME = "[addServiceType]";
		System.out.println("entering in to " + METHOD_NAME);
		return servetypeService.addServiceType(request.getParameter("servicename"), request.getParameter("usertype"));
	}

	@RequestMapping(value = "/getAllServices", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public List<Document> getAllServices() {
		String METHOD_NAME = "[getAllServices]";
		System.out.println("entering in to " + METHOD_NAME);
		return servetypeService.getAllServices();
	}

	@RequestMapping(value = "/deleteServices", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String deleteServices(HttpServletRequest request) {
		String METHOD_NAME = "[deleteServices]";
		System.out.println("entering in to " + METHOD_NAME);
		return servetypeService.deleteServices(request.getParameter("_id"));
	}
	
	@RequestMapping(value = "/saveServiceImage", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String saveServiceImage(HttpServletRequest request) throws IOException, ServletException {
		String METHOD_NAME = "[saveServiceImage]:";
		Part filePart = request.getPart("file");
		System.out.println("entering in to line 56" + METHOD_NAME);
		String fileName = String.valueOf(request.getParameter("filename"));
		return servetypeService.saveServiceImage(filePart.getInputStream(), fileName);
	}
}
