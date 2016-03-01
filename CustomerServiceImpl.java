package com.rmstorewebservices.service;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rmstoreutil.dto.Invoice;
import com.rmstoreutil.dto.Response;
import com.rmstoreutil.dto.SearchCustomerParams;
import com.rmstoreutil.dto.SearchCustomerResults;
import com.rmstoreutil.util.JsonUtil;
import com.rmstoreutil.util.RmStoreConstants;
import com.rmstorewebservices.dao.AddressMasterDAO;
import com.rmstorewebservices.dao.CustomerPurchagesDAO;
import com.rmstorewebservices.dao.InvoiceDAO;
import com.rmstorewebservices.dao.StockMasterDAO;
import com.rmstorewebservices.dao.UserMasterDAO;

@Service
public class CustomerServiceImpl implements CustomerService {
    private static Logger logger=Logger.getLogger(CustomerServiceImpl.class);
	@Autowired
	private UserMasterDAO userMasterDao;
	@Autowired
    private AddressMasterDAO addressMasterDao;
	@Autowired
	private InvoiceDAO invoiceDAO;
	@Autowired
	private CustomerPurchagesDAO customerPurchagesDAO;
   @Autowired
	private StockMasterDAO stockMasterDAO;
	public String addCustomer(String customerName, String mobile,
			String addressLine1, String addressLine2) {
		String jsonResponseStr
	=RmStoreConstants.CONST_EMPTY_JSON;
		try{
Long userId=userMasterDao.addCustomer(customerName, mobile);
if(userId!=null && userId>0){
Response response=new Response();
response.setStatus((byte)1);
response.setData(userId.toString());
response.setMessage("Customer Details Saved Successfully With out Address Details ");
jsonResponseStr=JsonUtil.convertToJson(response);
if(addressLine1!=null &&
addressLine1.trim().length()>0 
|| addressLine2!=null &&
addressLine2.trim().length()>0){
Long addressId=addressMasterDao.addCustomer(addressLine1,addressLine2);
if(addressId!=null && addressId>0){
int count=userMasterDao.updateAddressId(userId,addressId);
	if(count>0){
		response.setStatus((byte)1);
		response.setData(userId.toString());
		response.setMessage("Customer Details Saved Successfully ! ");
	jsonResponseStr=JsonUtil.convertToJson(response);
	}
}
}
}
		}catch(Exception exception){
	logger.error("Exception Occured while  adding the customer Details () ::"+exception.getMessage());
		exception.printStackTrace();
		}
		
		return jsonResponseStr;
	}

	public String searchCustomer(String jsonSearchCustomerParams) {
		String jsonSearchCustomerResults=RmStoreConstants.CONST_EMPTY_JSON;
		try{
		
		SearchCustomerParams 
		searchCustomerParams=JsonUtil.convertToPojo(jsonSearchCustomerParams,SearchCustomerParams.class);
	if(searchCustomerParams.getMobile()!=null && searchCustomerParams.getMobile().trim().length()>0){
		List<SearchCustomerResults> searchCustomerResults=userMasterDao.searchCustomer(searchCustomerParams);
		jsonSearchCustomerResults=JsonUtil.convertToJson(searchCustomerResults);
	}	
	}catch(Exception exception){
		logger.error("Exception Occured while searching the customer ::"+exception.getMessage());
		}
		return jsonSearchCustomerResults;
	}

	public String saveInvoiceDetails(Long userId, Long patientId,
			String referedBy, List<Map<String,Object>> list) {
		String jsonResponse=RmStoreConstants.CONST_EMPTY_JSON;
		 try{
	Long billNumber=invoiceDAO.saveInvoiceDetails(userId,patientId,referedBy);
	if(billNumber!=null && billNumber>0){
for(Map<String,Object> map:list){
	int medicineId=(Integer)map.get("medicineId");
	int quantity=(Integer)map.get("quantity");
	long medicineId1=medicineId;
int count=customerPurchagesDAO.saveCustomerPurchages(billNumber,patientId,medicineId1,quantity);
if(count>0){

	int updateCount= stockMasterDAO.updateStock(medicineId1,quantity);
	 if(updateCount>0){
			Response response=new Response();
			response.setData(billNumber.toString());
response.setMessage("Invoice Details are Saved Successfully");
response.setStatus((byte)1);
jsonResponse=JsonUtil.convertToJson(response);
	 }
}
}
}
}catch(Exception exception){
logger.error("Exception occured while saving Invoice Details() "+exception.getMessage());
exception.printStackTrace();
}
		
		return jsonResponse;
	}
	

}
