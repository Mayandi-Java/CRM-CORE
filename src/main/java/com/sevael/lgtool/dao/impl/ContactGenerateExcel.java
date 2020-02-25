package com.sevael.lgtool.dao.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.client.model.Filters;
import com.sevael.lgtool.utils.AppConstants;

@Repository
public class ContactGenerateExcel implements AppConstants {

	public String getRetailExcel(List<Document> contacts) throws Exception {

		System.out.println("enter to excel");
		XSSFWorkbook workbook = new XSSFWorkbook();

		String file = filePath + LocalDate.now().toEpochDay() + ".xlsx";

		XSSFSheet spreadsheet = workbook.createSheet("Contacts");

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setColor(IndexedColors.BLACK.getIndex());

		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setFillBackgroundColor(IndexedColors.BLUE.getIndex());

		XSSFRow headerRow = spreadsheet.createRow(0);

		headerRow.createCell(0).setCellValue("S.No");
		headerRow.getCell(0).setCellStyle(headerCellStyle);

		headerRow.createCell(1).setCellValue("First Name");
		headerRow.getCell(1).setCellStyle(headerCellStyle);

		headerRow.createCell(2).setCellValue("Middle Name");
		headerRow.getCell(2).setCellStyle(headerCellStyle);

		headerRow.createCell(3).setCellValue("Last Name");
		headerRow.getCell(3).setCellStyle(headerCellStyle);

		headerRow.createCell(4).setCellValue("Email Id");
		headerRow.getCell(4).setCellStyle(headerCellStyle);

		headerRow.createCell(5).setCellValue("Phone Number");
		headerRow.getCell(5).setCellStyle(headerCellStyle);

		headerRow.createCell(6).setCellValue("Alternate Number");
		headerRow.getCell(6).setCellStyle(headerCellStyle);

		headerRow.createCell(7).setCellValue("Gender");
		headerRow.getCell(7).setCellStyle(headerCellStyle);

		headerRow.createCell(8).setCellValue("Date of Birth");
		headerRow.getCell(8).setCellStyle(headerCellStyle);

		headerRow.createCell(9).setCellValue("Place of Birth");
		headerRow.getCell(9).setCellStyle(headerCellStyle);

		headerRow.createCell(10).setCellValue("User Type");
		headerRow.getCell(10).setCellStyle(headerCellStyle);

		headerRow.createCell(11).setCellValue("Organization Name");
		headerRow.getCell(11).setCellStyle(headerCellStyle);

		headerRow.createCell(12).setCellValue("Lead Type");
		headerRow.getCell(12).setCellStyle(headerCellStyle);

		headerRow.createCell(13).setCellValue("Additional Information");
		headerRow.getCell(13).setCellStyle(headerCellStyle);

		headerRow.createCell(14).setCellValue("Door NO");
		headerRow.getCell(14).setCellStyle(headerCellStyle);

		headerRow.createCell(15).setCellValue("Street");
		headerRow.getCell(15).setCellStyle(headerCellStyle);

		headerRow.createCell(16).setCellValue("City");
		headerRow.getCell(16).setCellStyle(headerCellStyle);

		headerRow.createCell(17).setCellValue("State");
		headerRow.getCell(17).setCellStyle(headerCellStyle);

		headerRow.createCell(18).setCellValue("Country");
		headerRow.getCell(18).setCellStyle(headerCellStyle);

		headerRow.createCell(19).setCellValue("Pincode");
		headerRow.getCell(19).setCellStyle(headerCellStyle);

		headerRow.createCell(20).setCellValue("Type");
		headerRow.getCell(20).setCellStyle(headerCellStyle);

		int rowCount = 1;

		String[] fields = { "firstname", "middlename", "lastname", "email", "phonenumber", "alterphonenum", "gender",
				"dob", "placeofbirth", "userrole", "orgname", "leadtype", "additionalinfo", "doorno", "street", "city",
				"state", "country", "pincode", "type" };

		for (int i = 0; i < contacts.size(); i++) {
			XSSFRow listrow = spreadsheet.createRow(rowCount);

			listrow.createCell(0).setCellValue(rowCount);
			for (int j = 0; j < fields.length; j++) {
				listrow.createCell(j + 1).setCellValue(contacts.get(i).getString((fields[j])));
			}
			rowCount++;
		}
		FileOutputStream fos = new FileOutputStream(new File(file));
		workbook.write(fos);
		fos.close();

		return file;
	}

	public String getSingleContactExcel(List<Document> contacts) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("enter to excel");
		XSSFWorkbook workbook = new XSSFWorkbook();
		String file = excelfilePath + "Contacts_" + LocalDate.now().toEpochDay() + ".xlsx";

		FileOutputStream fos = new FileOutputStream(new File(file));
		workbook.write(fos);
		fos.close();
		return null;
	}

	public String getBusinessExcel(List<Document> businesscontacts) throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook();

		String file = excelfilePath + "AllBusinessContacts" + LocalDate.now().toEpochDay() + ".xlsx";

		XSSFSheet spreadsheet = workbook.createSheet("Business Contacts");

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setColor(IndexedColors.BLACK.getIndex());

		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setFillBackgroundColor(IndexedColors.BLUE.getIndex());

		XSSFRow headerRow = spreadsheet.createRow(0);

		headerRow.createCell(0).setCellValue("S.No");
		headerRow.getCell(0).setCellStyle(headerCellStyle);

		headerRow.createCell(1).setCellValue("Company Name");
		headerRow.getCell(1).setCellStyle(headerCellStyle);

		headerRow.createCell(2).setCellValue("Email Id");
		headerRow.getCell(2).setCellStyle(headerCellStyle);

		headerRow.createCell(3).setCellValue("GST Number");
		headerRow.getCell(3).setCellStyle(headerCellStyle);

		headerRow.createCell(4).setCellValue("Door No");
		headerRow.getCell(4).setCellStyle(headerCellStyle);

		headerRow.createCell(5).setCellValue("Street");
		headerRow.getCell(5).setCellStyle(headerCellStyle);

		headerRow.createCell(6).setCellValue("City");
		headerRow.getCell(6).setCellStyle(headerCellStyle);

		headerRow.createCell(7).setCellValue("State");
		headerRow.getCell(7).setCellStyle(headerCellStyle);

		headerRow.createCell(8).setCellValue("Country");
		headerRow.getCell(8).setCellStyle(headerCellStyle);

		headerRow.createCell(9).setCellValue("Pincode");
		headerRow.getCell(9).setCellStyle(headerCellStyle);

		headerRow.createCell(10).setCellValue("Lead Type");
		headerRow.getCell(10).setCellStyle(headerCellStyle);

		headerRow.createCell(11).setCellValue("Lead Name");
		headerRow.getCell(11).setCellStyle(headerCellStyle);

		headerRow.createCell(12).setCellValue("Type");
		headerRow.getCell(12).setCellStyle(headerCellStyle);

		int rowCount = 1;

		String[] fields = { "companyname", "companyemailid", "gst", "doorno", "street", "city", "state", "country",
				"pincode", "leadtype", "leadname", "type" };

		for (int i = 0; i < businesscontacts.size(); i++) {
			XSSFRow listrow = spreadsheet.createRow(rowCount);
			listrow.createCell(0).setCellValue(rowCount);
			for (int j = 0; j < fields.length; j++) {
				listrow.createCell(j + 1).setCellValue(businesscontacts.get(i).getString((fields[j])));
			}
			rowCount++;
		}

		FileOutputStream fos = new FileOutputStream(new File(file));
		workbook.write(fos);
		fos.close();

		return file;

	}

}
