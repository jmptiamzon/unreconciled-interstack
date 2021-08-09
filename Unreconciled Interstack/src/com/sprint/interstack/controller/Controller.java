package com.sprint.interstack.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
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
		Map<String, ApigeeData> apigeeDataContainer = new HashMap<>();
		List<FinalData> finalDataContainer = new ArrayList<>();
		createUnreconciledInterstack(filepath, finalDataContainer, apigeeDataContainer);
		createApigeeExcel(finalDataContainer, apigeeDataContainer);
		JOptionPane.showMessageDialog(null, "File creation done.");
	}
	
	public void createUnreconciledInterstack(String filepath, List<FinalData> finalDataContainer, Map<String, ApigeeData> apigeeDataContainer) {
		FinalData finalData = new FinalData();
		
		String parameter = "";
		
		try {
			FileInputStream xlsxFile = new FileInputStream(new File(filepath));
			Workbook workbook = new XSSFWorkbook(xlsxFile);
			Sheet sheet = workbook.getSheetAt(0);
			DataFormatter dataFormatter = new DataFormatter();
			
			Iterator<Row> itr = sheet.iterator();
			
			while (itr.hasNext()) {
				Row nextRow = itr.next();
				
				if (!dataFormatter.formatCellValue(nextRow.getCell(1)).trim().equalsIgnoreCase("txn_id")) {
					finalData = new FinalData();
					finalData.setTxnSource(dataFormatter.formatCellValue(nextRow.getCell(0)));
					finalData.setTxnId(dataFormatter.formatCellValue(nextRow.getCell(1)));
					finalData.setTxnDate(dataFormatter.formatCellValue(nextRow.getCell(2)));
					finalData.setTxnType(dataFormatter.formatCellValue(nextRow.getCell(3)));
					finalData.setSku(dataFormatter.formatCellValue(nextRow.getCell(4)));
					finalData.setDeviceAccessories(dataFormatter.formatCellValue(nextRow.getCell(5)));
					finalData.setTmoStoreId(dataFormatter.formatCellValue(nextRow.getCell(6)));
					finalData.setSprintStoreId(dataFormatter.formatCellValue(nextRow.getCell(7)));
					finalData.setTmoQuantity(dataFormatter.formatCellValue(nextRow.getCell(8)));
					finalData.setSprintQuantity(dataFormatter.formatCellValue(nextRow.getCell(9)));
					finalData.setSerialnumber(dataFormatter.formatCellValue(nextRow.getCell(10)));
					finalData.setComment(dataFormatter.formatCellValue(nextRow.getCell(11)));
					finalDataContainer.add(finalData);
				}
				
			}
			
			for (int ctr = 0; ctr < finalDataContainer.size(); ctr++) {
				if ("".equals(parameter)) parameter += "request like '%" + finalDataContainer.get(ctr).getTxnId() + "%' ";   
				else parameter += "or request like '%" + finalDataContainer.get(ctr).getTxnId() + "%' ";
				
				if ((ctr % 999) == 0 && ctr != 0) {
					model.runQuery(parameter, apigeeDataContainer);
					parameter = "";
				}
				
				if (ctr == finalDataContainer.size() - 1) {
					System.out.println(parameter);
					model.runQuery(parameter, apigeeDataContainer);
				}
			}
			
			String comment = "";
			String error = "";
			for (int ctr = 0; ctr < finalDataContainer.size(); ctr++) {
				comment = apigeeDataContainer.get(finalDataContainer.get(ctr).getTxnId()).getSubStrResp();
				
				System.out.println(finalDataContainer.get(ctr).getTxnId() + " - " + comment);
				if (comment.contains("Adjustment")) {
					finalDataContainer.get(ctr).setComment("Interstack Adjustment has been processed successfully fo");
					apigeeDataContainer.get(finalDataContainer.get(ctr).getTxnId()).setSubStrResp("Interstack Adjustment has been processed successfully fo");
				}
				
				if (comment.contains("null")) {
					finalDataContainer.get(ctr).setComment("Checking Apigee Team");
					apigeeDataContainer.get(finalDataContainer.get(ctr).getTxnId()).setSubStrResp("Checking Apigee Team");
				
				}
				
				if (comment.trim().isEmpty()) {
					error = model.getJbossError(finalDataContainer.get(ctr).getTxnId());
					if (error.split("-")[1].equals("200")) {
						finalDataContainer.get(ctr).setComment("Error code 200.");
						apigeeDataContainer.get(finalDataContainer.get(ctr).getTxnId()).setSubStrResp("Error code 200.");
					}
					
					else {
						if (error.split("-")[0].isEmpty()) {
							finalDataContainer.get(ctr).setComment("Comment not found in OMIM.");
							apigeeDataContainer.get(finalDataContainer.get(ctr).getTxnId()).setSubStrResp("Comment not found in OMIM.");
						}
						
					}

				}
				
				else {
					finalDataContainer.get(ctr).setComment(apigeeDataContainer.get(finalDataContainer.get(ctr).getTxnId()).getSubStrResp());
				}
			}
			
			
			//write
			Row row;
			Cell cell;
			
			row = sheet.getRow(0);
			cell = row.createCell(11);
			cell.setCellValue("COMMENT");
			
			for (int ctr = 0; ctr < finalDataContainer.size(); ctr++) {
				row = sheet.getRow(ctr + 1);
				cell = row.createCell(11);
				cell.setCellValue(finalDataContainer.get(ctr).getComment());

			}
			
			xlsxFile.close();
			
			FileOutputStream fileOutputStream = new FileOutputStream(filepath);
			workbook.write(fileOutputStream);
			workbook.close();
			fileOutputStream.close();
				
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "IO Exception");
		}
		
	}
	
	
	public void createApigeeExcel(List<FinalData> finalDataContainer, Map<String, ApigeeData> apigeeDataContainer) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM");
		Date date = new Date();
		String strDate = formatter.format(date);		
		String filepath = System.getProperty("user.home") + "\\Downloads\\Transaction_details_" + strDate + ".xlsx";
		System.out.println(filepath);
		
		try {
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Transaction Details");
			
			// create header for transaction details
			Row row = sheet.createRow(0);
			Cell cell = row.createCell(0);
			cell.setCellValue("TXN_SOURCE");
			
			cell = row.createCell(1);
			cell.setCellValue("TXN_ID");
			
			cell = row.createCell(2);
			cell.setCellValue("TXN_DATE");
			
			cell = row.createCell(3);
			cell.setCellValue("TXN_TYPE");
			
			cell = row.createCell(4);
			cell.setCellValue("SKU");
			
			cell = row.createCell(5);
			cell.setCellValue("DEVICE_ACCESSORIES");
			
			cell = row.createCell(6);
			cell.setCellValue("TMO_STOREID");
			
			cell = row.createCell(7);
			cell.setCellValue("SPRINT_STOREID");
			
			cell = row.createCell(8);
			cell.setCellValue("TMO_QUANTITY");
			
			cell = row.createCell(9);
			cell.setCellValue("SPRINT_QUANTITY");
			
			cell = row.createCell(10);
			cell.setCellValue("SERIAL_NUMBER");
			
			cell = row.createCell(11);
			cell.setCellValue("COMMENT");
			
			int inCtr = 1;
			for (int ctr = 0; ctr < finalDataContainer.size(); ctr++) {
				if (finalDataContainer.get(ctr).getComment().contains("Apigee")) {
					row = sheet.createRow(inCtr);
					cell = row.createCell(0);
					cell.setCellValue(finalDataContainer.get(ctr).getTxnSource());
						
					cell = row.createCell(1);
					cell.setCellValue(finalDataContainer.get(ctr).getTxnId());
						
					cell = row.createCell(2);
					cell.setCellValue(finalDataContainer.get(ctr).getTxnDate());
					
					cell = row.createCell(3);
					cell.setCellValue(finalDataContainer.get(ctr).getTxnType());
						
					cell = row.createCell(4);
					cell.setCellValue(finalDataContainer.get(ctr).getSku());
						
					cell = row.createCell(5);
					cell.setCellValue(finalDataContainer.get(ctr).getDeviceAccessories());
						
					cell = row.createCell(6);
					cell.setCellValue(finalDataContainer.get(ctr).getTmoStoreId());
						
					cell = row.createCell(7);
					cell.setCellValue(finalDataContainer.get(ctr).getSprintStoreId());
						
					cell = row.createCell(8);
					cell.setCellValue(finalDataContainer.get(ctr).getTmoQuantity());
						
					cell = row.createCell(9);
					cell.setCellValue(finalDataContainer.get(ctr).getSprintQuantity());
						
					cell = row.createCell(10);
					cell.setCellValue(finalDataContainer.get(ctr).getSerialnumber());
						
					cell = row.createCell(11);
					cell.setCellValue(finalDataContainer.get(ctr).getComment());
					
					inCtr++;
				}
			}
			
			
			//Request Response Details Sheet
			sheet = workbook.createSheet("Request Response Details");
			
			row = sheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue("ORDER_REQUEST_ID");
			cell = row.createCell(1);
			cell.setCellValue("INTERFACE_NAME");
			cell = row.createCell(2);
			cell.setCellValue("SOURCE_SYS");
			cell = row.createCell(3);
			cell.setCellValue("DESTINATION_SYS");
			cell = row.createCell(4);
			cell.setCellValue("CONC_REQ_ID");
			cell = row.createCell(5);
			cell.setCellValue("JMS_MESSAGE_DATE");
			cell = row.createCell(6);
			cell.setCellValue("MESSAGE_ID");
			cell = row.createCell(7);
			cell.setCellValue("CREATION_DATE");
			cell = row.createCell(8);
			cell.setCellValue("LAST_UPDATE_DATE");
			cell = row.createCell(9);
			cell.setCellValue("REQPART1");
			cell = row.createCell(10);
			cell.setCellValue("TRANSACTION_NUMBER");
			cell = row.createCell(11);
			cell.setCellValue("REQPART2");
			cell = row.createCell(12);
			cell.setCellValue("REQPART3");
			cell = row.createCell(13);
			cell.setCellValue("SUBSTR(RESPONSE,95,50)");
			cell = row.createCell(14);
			cell.setCellValue("RESPONSE");
			cell = row.createCell(15);
			cell.setCellValue("SERVER_NAME");
			cell = row.createCell(16);
			cell.setCellValue("RESPTIME");
			
			int ctr = 1;
			for (String key : apigeeDataContainer.keySet()) {
				if (apigeeDataContainer.get(key).getSubStrResp().contains("Apigee")) {
					row = sheet.createRow(ctr);
					cell = row.createCell(0);
					cell.setCellValue(apigeeDataContainer.get(key).getOrderRequestId());
					cell = row.createCell(1);
					cell.setCellValue(apigeeDataContainer.get(key).getInterfaceName());
					cell = row.createCell(2);
					cell.setCellValue(apigeeDataContainer.get(key).getSourceSys());
					cell = row.createCell(3);
					cell.setCellValue(apigeeDataContainer.get(key).getDestinationSys());
					cell = row.createCell(4);
					cell.setCellValue(apigeeDataContainer.get(key).getConcReqId());
					cell = row.createCell(5);
					cell.setCellValue(apigeeDataContainer.get(key).getJmsMessageDate());
					cell = row.createCell(6);
					cell.setCellValue(apigeeDataContainer.get(key).getMessageId());
					cell = row.createCell(7);
					cell.setCellValue(apigeeDataContainer.get(key).getCreationDate());
					cell = row.createCell(8);
					cell.setCellValue(apigeeDataContainer.get(key).getLastUpdateDate());
					cell = row.createCell(9);
					cell.setCellValue(apigeeDataContainer.get(key).getReqPart1());
					cell = row.createCell(10);
					cell.setCellValue(apigeeDataContainer.get(key).getTransactionNumber());
					cell = row.createCell(11);
					cell.setCellValue(apigeeDataContainer.get(key).getReqPart2());
					cell = row.createCell(12);
					cell.setCellValue(apigeeDataContainer.get(key).getReqPart3());
					cell = row.createCell(13);
					cell.setCellValue(apigeeDataContainer.get(key).getSubStrResp());
					cell = row.createCell(14);
					cell.setCellValue(apigeeDataContainer.get(key).getResponse());
					cell = row.createCell(15);
					cell.setCellValue(apigeeDataContainer.get(key).getServerName());
					cell = row.createCell(16);
					cell.setCellValue(apigeeDataContainer.get(key).getRespTime());
					
					ctr++;
				}
			}
			
			FileOutputStream fileOutputStream = new FileOutputStream(filepath);
			workbook.write(fileOutputStream);
			workbook.close();
			fileOutputStream.close();
			

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "IO Exception");
		}
	}

	
}
