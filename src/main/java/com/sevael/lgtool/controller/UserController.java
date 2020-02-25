package com.sevael.lgtool.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sevael.lgtool.service.UserService;
import com.sevael.lgtool.utils.AppConstants;

@RestController
@RequestMapping(value = "/user")
public class UserController implements AppConstants {

	@Autowired
	UserService userService;

	@RequestMapping(value = "/save", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String save(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return userService.save((JsonObject) jp.parse(request.getParameter("userDetails")));
	}

	@RequestMapping(value = "/list", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public List<Document> list(HttpServletRequest request) {
		String METHOD_NAME = "[getAllEmployees]";
		System.out.println("entering in to " + METHOD_NAME);
		return userService.list(request.getParameter("searchstr"), Integer.parseInt(request.getParameter("page")));
	}

	

	@RequestMapping(value = "/get", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public Document get(HttpServletRequest request) {
		return userService.get(request.getParameter("id"));
	}

	@RequestMapping(value = "/searchEmployee", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String searchEmployee(HttpServletRequest request) {
		String METHOD_NAME = "[searchEmployee]";
		System.out.println("entering in to " + METHOD_NAME);
		return userService.searchEmployee(request.getParameter("searchstr"));
	}

	@RequestMapping(value = "/update", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String update(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return userService.update((JsonObject) jp.parse(request.getParameter("userDetails")));
	}

	@RequestMapping(value = "/saveImage", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String saveImage(HttpServletRequest request) throws IOException, ServletException {
		Part filePart = request.getPart("file");
		return userService.saveImage(filePart.getInputStream(), request.getParameter("filename"),
				request.getParameter("id"));
	}

	@GetMapping(value = "/getImage", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public void getImage(HttpServletRequest request, HttpServletResponse response) {
		try {
			String fileName = userService.getImage(request.getParameter("filename"));
			Path path = Paths.get(fileName);
			response.setContentType("image/png");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			Files.copy(path, response.getOutputStream());
			response.getOutputStream().flush();
		} catch (Exception e) {
			String message = "Error in download PDF file " + e.getMessage();
			// Console.log(message);
		}
	}

	@RequestMapping(value = "/getPaginationCount", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String getPaginationCount(HttpServletRequest request) {
		return userService.getPaginationCount(request.getParameter("searchstr"));
	}

}
