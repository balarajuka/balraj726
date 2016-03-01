/**
 * Copyright (c) 2015,  RMStore and/or its affiliates. All rights reserved.
 */
package com.rmstorewebservices.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rmstoreutil.dto.Login;
import com.rmstoreutil.dto.MedicalStore;
import com.rmstoreutil.dto.Response;
import com.rmstoreutil.util.JsonUtil;
import com.rmstoreutil.util.RmStoreConstants;
import com.rmstorewebservices.dao.MedicalStoreDAO;
import com.rmstorewebservices.dao.UserAuthenticationDAO;
import com.rmstorewebservices.dao.UserMasterDAO;

/**
 * @author sathish.Bandi
 * @since 10-Feb-2015
 * @version 1.
*@purpose It is a UserServiceImpl class, it is responsible for user related actions.

 */
@Service
public class UserServiceImpl implements UserService {
private static final Logger logger=Logger.getLogger(UserServiceImpl.class);
@Value("${senderid}")
String senderid;
@Value("${apikey}")
String apikey;
@Value("${type}")
String type;
@Value("${username}")
String username;

@Value("${mainurl}")
String mainurl;	


@Autowired
	private UserAuthenticationDAO userAuthenticationDao;
    @Autowired
	private MedicalStoreDAO medicalStoreDao;
	public String login(String jsonLoginStr) {
		String jsonLoginRes=RmStoreConstants.CONST_EMPTY_JSON;
		try{
Login login=JsonUtil.convertToPojo(jsonLoginStr,Login.class);
      login=userAuthenticationDao.login(login);
      if(login!=null){
    	jsonLoginRes=JsonUtil.convertToJson(login);
      
      }

		}catch(Exception exception){
			exception.printStackTrace();
	logger.error("Exception Occured while login ::"+exception.getMessage());		
		}
		return jsonLoginRes;
	}

	public String getMedicalStoreDetails() {
		String jsonMedicalStore=RmStoreConstants.CONST_EMPTY_JSON;
		
		try{
			logger.info("entered into getMedicalStoreDetails");
			
		MedicalStore medicalStore=medicalStoreDao.getMedicalStoreDetails();
		
		jsonMedicalStore=JsonUtil.convertToJson(medicalStore);
		logger.info("response of getMedicalStoreDetails"+jsonMedicalStore);
		}catch(Exception exception){
	logger.error("Exception Occured while getting the MedicalStoreDetails () ::"+exception.getMessage());
		}
		return jsonMedicalStore;
	}
	/**
	 * This method is used to check given username is available or not (if available returns true as 1 else returns false as 0)
	 * 
	 * @param userName	 
	 */
	
	public String checkUserNameAvailability(String userName) {
		boolean isAvailable = false;
		try{
			isAvailable = userAuthenticationDao.checkUserNameAvailability(userName);			
		} catch (SQLException sqlException) {
			logger.error("SQLException occured while checking username availability(closing the connection, roll back) --->" + sqlException.getMessage());
		}catch (Exception exception){
			logger.error("Exception(GenericException) occured while checking username availability --->" + exception.getMessage());
		}
		Response response = new Response();
		if (isAvailable) {
			response.setStatus((byte)1);
			response.setMessage("User name is available!");
		} else {
			response.setStatus((byte)0);
			response.setMessage("User name not available!");
		}
		return JsonUtil.convertToJson(response);
	}
	/**
	 * This method is used change the Password
	 * @param userName
	 * @param oldPassword
	 * @param newPassword
	 * @return
      */
	public String changePassword(Long userId, String userName,
			String oldPassword, String newPassword)  {
String jsonResponseStr=RmStoreConstants.CONST_EMPTY_JSON;
		try{
		String phoneNumber = 
userAuthenticationDao.changePassword(userId,userName,oldPassword,newPassword);
		Response response = new Response();
		if (phoneNumber!=null &&
			phoneNumber.trim().length()>0) {
	String message ="Dear ,You have successfully changed your login details, your login details are UserName :"+userName+" , Password : "+newPassword+" ";
		sendSmsToUser(phoneNumber, message);
			response.setStatus((byte) 1);
			response.setMessage("You have successfully changed your login details and  has been successfully sent to your registered mobile.");
            
		} else {
			response.setStatus((byte) 0);
			response.setMessage("Password Change Operation Failure. Please try again.");
		}
	jsonResponseStr = JsonUtil.convertToJson(response);
		}catch(Exception exception){
	logger.info("Exception Occured while changing the password ::"+exception.getMessage());
		}
		return jsonResponseStr;
	}
	/**
	 * This method is used to send the SMS to User 
	 * @param phoneNumber
	 * @param message
	 * return status
	 */
	public String sendSmsToUser
	(String phoneNumber,String message){
		String status="";

		logger.info("Entered into sendSmsToUser "+"sender Id "+senderid+"apiKey :"+apikey+"type :"+type+"mainurl :"+mainurl);

		logger.info(message);

		URLConnection myURLConnection=null;
		URL myURL=null;
		BufferedReader reader=null;
String encoded_message=URLEncoder.encode(message);
StringBuilder sbPostData= new StringBuilder(mainurl);
		sbPostData.append("user="+username); 
		sbPostData.append("&apikey="+apikey);
		sbPostData.append("&message="+encoded_message);
		sbPostData.append("&mobile="+phoneNumber);
		sbPostData.append("&senderid="+senderid);
		sbPostData.append("&type="+type);
		
		logger.info("user : " + username + " apikey : " + apikey + " senderid : " + senderid + " type : " + type);
		mainurl = sbPostData.toString();
		try
		{
			myURL = new URL(mainurl);
			myURLConnection = myURL.openConnection();
			myURLConnection.connect();
	reader= new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
			String response;
			while ((response = reader.readLine()) != null) 
				logger.info("sms response :"+response);
			status="Sms sended successfully";
			reader.close();
		} 
		catch (IOException ie) 
		{ 
			logger.info("Exception occured while sending the Sms to User :"+ie.getMessage());
		}

		return status;
	}

	
}


