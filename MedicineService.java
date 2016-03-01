package com.rmstorewebservices.service;


public interface MedicineService {
public String addMedicine(String medicine);

public String checkBatchNumber(String batchNumber);

public String searchMedicine(String medicineName, String medicineType);

public String medicineNameAutoComplete(String medicineName);

}

