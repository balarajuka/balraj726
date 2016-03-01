package com.rmstorewebservices.service;

import java.util.List;
import java.util.Map;

import com.rmstoreutil.dto.Invoice;

public interface CustomerService {

	String addCustomer(String customerName, String mobile, String addressLine1,
			String addressLine2);

	String searchCustomer(String jsonSearchCustomerParams);

	String saveInvoiceDetails(Long userId, Long patientId, String referedBy,
			List<Map<String,Object>> list);

}
