package com.sevael.lgtool.configuration;

import java.io.File;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class MyWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	private int maxUploadSizeInMb = 5 * 1024 * 1024; // 5 MB

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] {};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { WebConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected Filter[] getServletFilters() {
		return new Filter[] { new HiddenHttpMethodFilter(), new MultipartFilter() };
	}

	@Override
	protected void registerDispatcherServlet(ServletContext servletContext) {
		super.registerDispatcherServlet(servletContext);
		// servletContext.addListener(new HttpSessionEventPublisher());
	}

	@Override
	protected void customizeRegistration(ServletRegistration.Dynamic registration) {
//		File uploadDirectory = new File("c:\\tmp");
		File uploadDirectory = new File("/opt/tomcat/temp");
		MultipartConfigElement multipartConfigElement = new MultipartConfigElement(uploadDirectory.getAbsolutePath(),
				maxUploadSizeInMb, maxUploadSizeInMb * 2, maxUploadSizeInMb / 2);
		registration.setMultipartConfig(multipartConfigElement);
	}
}