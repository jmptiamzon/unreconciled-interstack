package com.sprint.interstack.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sprint.interstack.model.ApigeeData;
import com.sprint.interstack.model.FinalData;
import com.sprint.interstack.model.Model;

public class Controller {
	private Model model;
	
	public Controller() {
		model = new Model();
	}
	
	
	public void runApp(String filepath) {
		readExcelAndProcess(filepath);
	}
	
	public void readExcelAndProcess(String filepath) {
		FinalData finalData = new FinalData();
		ApigeeData apigeeData = new ApigeeData();
		//List<String> txnIdTemp = new ArrayList<>();
		List<FinalData> finalDataContainer = new ArrayList<>();
		List<ApigeeData> apigeeDataContainer = new ArrayList<>();
		
		String parameter = "";
		
		try {
			FileInputStream xlsxFile = new FileInputStream(new File(filepath));
			Workbook workbook = new XSSFWorkbook(xlsxFile);
			Sheet sheet = workbook.getSheetAt(0);
			
			Iterator<Row> itr = sheet.iterator();
			
			while (itr.hasNext()) {
				Row nextRow = itr.next();
				
				if (!nextRow.getCell(1).getStringCellValue().trim().equalsIgnoreCase("txn_id")) {
					finalData = new FinalData();
					finalData.setTxnSource(nextRow.getCell(0).getStringCellValue().trim());
					finalData.setTxnId(nextRow.getCell(1).getStringCellValue().trim());
					finalData.setTxnDate(nextRow.getCell(2).getStringCellValue().trim());
					finalData.setTxnType(nextRow.getCell(3).getStringCellValue().trim());
					finalData.setSku(nextRow.getCell(4).getStringCellValue().trim());
					finalData.setDeviceAccessories(nextRow.getCell(5).getStringCellValue().trim());
					finalData.setTmoStoreId(nextRow.getCell(6).getStringCellValue().trim());
					finalData.setSprintStoreId(nextRow.getCell(7).getStringCellValue().trim());
					finalData.setTmoQuantity(nextRow.getCell(8).getStringCellValue().trim());
					finalData.setSprintQuantity(nextRow.getCell(9).getStringCellValue().trim());
					finalData.setSerialnumber(nextRow.getCell(10).getStringCellValue().trim());
					finalData.setComment(nextRow.getCell(11).getStringCellValue().trim());
					finalDataContainer.add(finalData);
					//txnIdTemp.add(nextRow.getCell(1).getStringCellValue().trim());
				}
				
			}
				
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
		
		for (int ctr = 0; ctr < finalDataContainer.size(); ctr++) {
			if ("".equals(parameter)) parameter += "request like '%" + finalDataContainer.get(ctr).getTxnId() + "%' ";   
			else parameter += "or request like '%" + finalDataContainer.get(ctr).getTxnId() + "%' ";
			
			if ((ctr % 999) == 0 && ctr != 0) {
				model.runQuery(parameter, apigeeDataContainer);
				parameter = "";
			}
		}
		
		
		
		
		
		
		
		
		
	}
	
	
}
