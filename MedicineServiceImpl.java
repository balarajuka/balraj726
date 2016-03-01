package com.rmstorewebservices.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import antlr.StringUtils;

import com.rmstoreutil.dto.Medicine;
import com.rmstoreutil.dto.Response;
import com.rmstoreutil.dto.SearchMedicineResults;
import com.rmstoreutil.util.JsonUtil;
import com.rmstoreutil.util.RmStoreConstants;
import com.rmstorewebservices.dao.MedicineDAO;

@Service
public class MedicineServiceImpl implements MedicineService {
	private static final Logger logger = Logger
			.getLogger(MedicineServiceImpl.class);
	@Autowired
	private MedicineDAO medicineDao;

	public String addMedicine(String jsonMedicineStr) {
		String jsonResponseStr = RmStoreConstants.CONST_EMPTY_JSON;
		try {
			Response response = new Response();
			response.setStatus((byte) 0);
			response.setMessage("Medicine Not Added !Try Again...");
			Medicine medicine = JsonUtil.convertToPojo(jsonMedicineStr,
					Medicine.class);
			Long medicineId = medicineDao.addMedicine(medicine);
			if (medicineId != null && medicineId > 0) {
				response.setStatus((byte) 1);
				response.setMessage("Medicine Added successfully");
				response.setData(medicineId.toString());
			}
			jsonResponseStr = JsonUtil.convertToJson(response);
		} catch (Exception exception) {
			exception.printStackTrace();
			logger.error("Exception Occured while adding the Medicine ::"
					+ exception.getMessage());
		}
		return jsonResponseStr;
	}

	public String checkBatchNumber(String batchNumber) {
		String jsonResponseStr = RmStoreConstants.CONST_EMPTY_JSON;

		if (batchNumber != null && batchNumber.trim().length() > 0) {
			try {
				Response response = new Response();
				Long count = medicineDao.checkBatchNumber(batchNumber);
				if (count != null && count > 0) {
					response.setMessage("Medicine Existed In Stock,Please Update Medicine Stock");
					response.setStatus((byte) 1);
				} else {
					response.setStatus((byte) 0);
					response.setMessage("Medicine Not Existed in Stock !Ok ");
				}
				jsonResponseStr = JsonUtil.convertToJson(response);
			} catch (Exception exception) {
				exception.printStackTrace();
				logger.error("Exception Occured while checking BatchNumber() ::"
						+ exception.getMessage());
				jsonResponseStr = RmStoreConstants.CONST_EMPTY_JSON;
			}

		}
		return jsonResponseStr;
	}

	public String
searchMedicine(String medicineName, String medicineType) {
String jsonSearchMedicineResults=
RmStoreConstants.CONST_EMPTY_JSON;
if(medicineName!=null && 
medicineName.trim().length()>0 &&
medicineType!=null && 
medicineType.trim().length()>0){
	try{
  List<SearchMedicineResults> searchMedicineResultsList
  =medicineDao.searchMedicine(medicineName,medicineType);	
jsonSearchMedicineResults=JsonUtil.convertToJson(searchMedicineResultsList);
	}catch(Exception exception){
logger.error("Exception Occured while searching the medicines ::"+exception.getMessage());
	}
}	
return jsonSearchMedicineResults;
	}

	public String medicineNameAutoComplete(String medicineName) {
		String jsonResponseStr=RmStoreConstants.CONST_EMPTY_JSON;
		if(medicineName!=null &&
				medicineName.trim().length()>0){
		try{
		List<String> medicineNamesList=medicineDao.medicineNameAutoComplete(medicineName);
jsonResponseStr=JsonUtil.convertToJson(medicineNamesList);
		  
			
		}catch(Exception exception){
logger.error("Exception Occured while searching MedicineName with AutoComplete ::"+exception.getMessage());
		}
			
		}
		return jsonResponseStr;
	}
}
