/**
 * Copyright (c) 2015,  RMStore and/or its affiliates. All rights reserved.
 */
package com.rmstorewebservices.service;
	
import java.sql.SQLException;
	
/**
 * @author sathish.Bandi
 * @since 10-Feb-2015		
 * @version 1.0
 *@purpose It is a UserService interface, it is responsible for user related actio		ns.
 */
public interface UserService {

	String login(String jsonLoginStr);

	String getMedicalStoreDetails();
	/**
	 * This method is used to check given username is available or not (if available returns true as 1 else returns false as 0)
	 * 
	 * @param userName	 
	 */
	public String checkUserNameAvailability(String userName);
	/**
	 * This method is used change the UserName and Password
	*@param userName
	 * @param oldPassword
	 * @param newPassword
	 * @return
      */
	public String changePassword(Long userId,String userName,
			String oldPassword, String newPassword);
			
}
