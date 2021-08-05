package com.sprint.interstack.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JOptionPane;

public class Model {
	
	public void runQuery(String parameter, List<ApigeeData> apigeeDataContainer) {
		ApigeeData apigeeData = new ApigeeData();
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conn =  
					DriverManager.getConnection("", "", "");
			
			//Archive
			PreparedStatement statement = conn.prepareStatement(
					"select " + 
					"ORDER_REQUEST_ID, INTERFACE_NAME, SOURCE_SYS, DESTINATION_SYS, CONC_REQ_ID, JMS_MESSAGE_DATE,  MESSAGE_ID, " + 
					"CREATION_DATE, LAST_UPDATE_DATE, substr(request,1,3989) ReqPart1, " + 
					"substr(request,186,20) Transaction_number, substr(request,3990,3989) ReqPart2, substr(request,7979) ReqPart3,substr(response,95,50), " + 
					"response,SERVER_NAME, trunc((LAST_UPDATE_DATE-JMS_MESSAGE_DATE)*24*60*60) as RespTime " + 
					"from SPRN.SPRN_RMS_RES_ADJIO_STG_ar " + 
					"where 1=1 " + 
					"AND INTERFACE_NAME in ('TMO_InventoryAdjustment') " + 
					"and (?" + 
					") " + 
					"AND creation_date >sysdate-6 "
			);
			
			ResultSet rs = statement.executeQuery();
			
			while (rs.next()) {
				apigeeData = new ApigeeData();
				
				apigeeData.setOrderRequestId(rs.getString(1));
				apigeeData.setInterfaceName(rs.getString(2));
				apigeeData.setSourceSys(rs.getString(3));
				apigeeData.setDestinationSys(rs.getString(4));
				apigeeData.setConcReqId(rs.getString(5));
				apigeeData.setJmsMessageDate(rs.getString(6));
				apigeeData.setMessageId(rs.getString(7));
				apigeeData.setCreationDate(rs.getString(8));
				apigeeData.setLastUpdateDate(rs.getString(9));
				apigeeData.setReqPart1(rs.getString(10));
				apigeeData.setTransactionNumber(rs.getString(11)); // change this
				apigeeData.setReqPart2(rs.getString(12)); // 
				apigeeData.setReqPart3(rs.getString(13));
				apigeeData.setSubStrResp(rs.getString(14));
				apigeeData.setResponse(rs.getString(15));
				apigeeData.setServerName(rs.getString(16));
				apigeeData.setRespTime(rs.getString(17));
				
				apigeeDataContainer.add(apigeeData);
			}
			
			
			// Base
			statement = conn.prepareStatement(
					"select " + 
					"ORDER_REQUEST_ID, INTERFACE_NAME, SOURCE_SYS, DESTINATION_SYS, CONC_REQ_ID, JMS_MESSAGE_DATE,  MESSAGE_ID, " + 
					"CREATION_DATE, LAST_UPDATE_DATE, substr(request,1,3989) ReqPart1, " + 
					"substr(request,186,20) Transaction_number, substr(request,3990,3989) ReqPart2, substr(request,7979) ReqPart3,substr(response,95,50), " + 
					"response,SERVER_NAME, trunc((LAST_UPDATE_DATE-JMS_MESSAGE_DATE)*24*60*60) as RespTime " + 
					"from SPRN.SPRN_RMS_RES_ADJIO_STG " + 
					"where 1=1 " + 
					"AND INTERFACE_NAME in ('TMO_InventoryAdjustment') " + 
					"and (?" + 
					") " + 
					"AND creation_date >sysdate-6 "	
			);
			
			rs = statement.executeQuery();
				
			while (rs.next()) {
				apigeeData = new ApigeeData();
					
				apigeeData.setOrderRequestId(rs.getString(1));
				apigeeData.setInterfaceName(rs.getString(2));
				apigeeData.setSourceSys(rs.getString(3));
				apigeeData.setDestinationSys(rs.getString(4));
				apigeeData.setConcReqId(rs.getString(5));
				apigeeData.setJmsMessageDate(rs.getString(6));
				apigeeData.setMessageId(rs.getString(7));
				apigeeData.setCreationDate(rs.getString(8));
				apigeeData.setLastUpdateDate(rs.getString(9));
				apigeeData.setReqPart1(rs.getString(10));
				apigeeData.setTransactionNumber(rs.getString(11)); // change this
				apigeeData.setReqPart2(rs.getString(12)); // 
				apigeeData.setReqPart3(rs.getString(13));
				apigeeData.setSubStrResp(rs.getString(14));
				apigeeData.setResponse(rs.getString(15));
				apigeeData.setServerName(rs.getString(16));
				apigeeData.setRespTime(rs.getString(17));
					
				apigeeDataContainer.add(apigeeData);
			}
			
			
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Driver error: " + e.getMessage());
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
		}
	}
	
}
