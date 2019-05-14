package com.demo.MultipartyRecon;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.demo.MultipartyRecon.Entity.BankTransactionEntity;
import com.demo.MultipartyRecon.Entity.FileHistoryEntity;
import com.demo.MultipartyRecon.Repository.FailedTransactionsRepository;
import com.demo.MultipartyRecon.Repository.FileFailureRepository;
import com.demo.MultipartyRecon.Repository.MultipartyReconRepository;
import com.demo.MultipartyRecon.XLSUtility;

@Service
public class FederalBankServiceImpl implements MultipartyReconServiceInterface {

	@Autowired
	MultipartyReconRepository multipartyReconRepository;
	
	@Autowired
	FileFailureRepository fileFailureRepository;
	
	@Autowired
	FailedTransactionsRepository failedTransactionsRepository;
	
	@Override
	public void readTransactionFile(File file, String username)throws Exception {
		String fileName = file.getAbsolutePath();
		String[] federalHeaders = {"Sl. No.",	"Tran Date"	,"Particulars", "",	"Value Date", "Tran Type", "Cheque Details", "Withdrawal", "Deposit", "Balance Amount"};
		MessageDigest md = MessageDigest.getInstance("MD5"); 
		
		ArrayList<HashMap<String, String>> xlsData = XLSUtility.readXLSXFile(file, federalHeaders, 10);
		
		if(xlsData.get(0)==null)
    	{
    		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
    		Calendar cal = Calendar.getInstance();
    		String dateTime = formatter.format(cal.getTime());
    		FileHistoryEntity fileHistoryEntity = new FileHistoryEntity();
    		fileHistoryEntity.setTotalRecords(xlsData.size()-1);
    		fileHistoryEntity.setFilename(fileName);
    		fileHistoryEntity.setTimestamp(dateTime);	
    		fileHistoryEntity.setProcessedRecords(xlsData.size()-1);
    		fileHistoryEntity.setUsername(username);
    		fileFailureRepository.save(fileHistoryEntity);
    	}
    	else {
			for(HashMap<String, String> entry:xlsData)
		{
			BankTransactionEntity bankTransactionEntity = new BankTransactionEntity();
			String[] line= new String[federalHeaders.length];
			int i=0;
			for(String key: entry.keySet()) {
				line[i]=entry.get(key);
				i++;
			}
		
		Date txnDate = new SimpleDateFormat("dd-MM-yyyy").parse(line[3]);
		bankTransactionEntity.setTransactionDate(txnDate);
		Date valDate = new SimpleDateFormat("dd-MM-yyyy").parse(line[9]);
		bankTransactionEntity.setValueDate(valDate);
		bankTransactionEntity.setRemarks(line[0]);
		bankTransactionEntity.setBankCode("Federal Bank");
		if(line[6].equalsIgnoreCase("0"))
		{
			bankTransactionEntity.setCreditFlag("C");
			String str = line[2].replaceAll(",", ""); 
			bankTransactionEntity.setTransactionAmount(Double.parseDouble(str));
			}
		else
		{
			bankTransactionEntity.setCreditFlag("D");
			String str = line[6].replaceAll(",", ""); 
			bankTransactionEntity.setTransactionAmount(Double.parseDouble(str));
		}
		String str = line[4].replaceAll(",", "");
		bankTransactionEntity.setBalanceAfterTxn(Double.parseDouble(str));
		String txnId = fileName+"_"+line[8];
		byte[] messageDigest = md.digest(txnId.getBytes());
		BigInteger no = new BigInteger(1, messageDigest);
		txnId = no.toString(16);
		bankTransactionEntity.setTransactionId(txnId);
		try {
			multipartyReconRepository.save(bankTransactionEntity);
			}
			catch(DataIntegrityViolationException e)
			{
				System.out.println(bankTransactionEntity.getTransactionId());
				continue;
			}
		}
    }
}
}
