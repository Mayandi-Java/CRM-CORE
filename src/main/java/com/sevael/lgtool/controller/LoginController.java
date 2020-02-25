package com.sevael.lgtool.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sevael.lgtool.service.LoginService;
import com.sevael.lgtool.utils.AppConstants;

@RestController
public class LoginController implements AppConstants{

	@Autowired
	LoginService loginService;
	
	@RequestMapping(value = "/login", produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public String login(HttpServletRequest request) {
		JsonParser jp = new JsonParser();
		return loginService.login((JsonObject) jp.parse(request.getParameter("loginDetails")));
	}
}
