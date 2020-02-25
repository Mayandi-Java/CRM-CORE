package com.sevael.lgtool.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.sevael.lgtool.dao.LoginDao;
import com.sevael.lgtool.service.LoginService;

@Service
@Transactional(readOnly = true)
public class LoginServiceImpl implements LoginService {

	@Autowired 
	LoginDao loginDao;
	
	@Override
	public String login(JsonObject loginDetails) {
		return loginDao.login(loginDetails);
	}

}
