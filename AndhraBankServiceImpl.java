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
import com.demo.MultipartyRecon.Entity.FailedTransactionsEntity;
import com.demo.MultipartyRecon.Entity.FileHistoryEntity;
import com.demo.MultipartyRecon.Repository.FailedTransactionsRepository;
import com.demo.MultipartyRecon.Repository.FileFailureRepository;
import com.demo.MultipartyRecon.Repository.MultipartyReconRepository;

@Service
public class AndhraBankServiceImpl implements MultipartyReconServiceInterface {
	
	@Autowired
	MultipartyReconRepository multipartyReconRepository;
	
	@Autowired
	FileFailureRepository fileFailureRepository;
	
	@Autowired
	FailedTransactionsRepository failedTransactionsRepository;

	@Override
	public void readTransactionFile(File file, String username) throws Exception {
		String fileName = file.getAbsolutePath();
		String[] andhraHeaders = {"","","Tran Date","","",	"Cheque No","","", "Transaction Description",	"Withdrawal (INR)","",	"Deposits (INR)", "Balance (INR)"};
    	MessageDigest md = MessageDigest.getInstance("MD5"); 
    	
    	
    	ArrayList<HashMap<String, String>> xlsData = XLSUtility.readAndhraXLSFile(file, andhraHeaders, 12);
    	
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
		int id=1;	
    	for(HashMap<String, String> entry:xlsData)
    		{
    			BankTransactionEntity bankTransactionEntity = new BankTransactionEntity();
    			String[] line= new String[andhraHeaders.length];
    			int i=0;
    			for(String key: entry.keySet()) {
    				line[i]=entry.get(key);
    				i++;
    			}
    			
    			Date txnDate = new SimpleDateFormat("dd/MM/yyyy").parse(line[5]);
    			bankTransactionEntity.setTransactionDate(txnDate);
    			bankTransactionEntity.setValueDate(txnDate);
    			bankTransactionEntity.setRemarks(line[6]);
    			bankTransactionEntity.setBankCode("Andhra Bank");
    			if(line[1].isEmpty())
    			{
    				bankTransactionEntity.setCreditFlag("C");
    				String str = line[4].replaceAll(",", ""); 
    				bankTransactionEntity.setTransactionAmount(Double.parseDouble(str));
       			}
    			else
    			{
    				bankTransactionEntity.setCreditFlag("D");
    				String str = line[1].replaceAll(",", ""); 
    				bankTransactionEntity.setTransactionAmount(Double.parseDouble(str));
    			}
    			String str = line[2].replaceAll(",", ""); 
    			bankTransactionEntity.setBalanceAfterTxn(Double.parseDouble(str));
    			String txnId = fileName+"_"+Integer.toString(id);
    			byte[] messageDigest = md.digest(txnId.getBytes());
    			BigInteger no = new BigInteger(1, messageDigest);
    			txnId = no.toString(16);
    			bankTransactionEntity.setTransactionId(txnId);
    			id++;
    			try {
        			multipartyReconRepository.save(bankTransactionEntity);
        			}
        			catch(DataIntegrityViolationException e)
        			{
        				FailedTransactionsEntity failedTransactionsEntity = new FailedTransactionsEntity();
        				failedTransactionsEntity.setBalanceAfterTxn(bankTransactionEntity.getBalanceAfterTxn());
        				failedTransactionsEntity.setBankCode(bankTransactionEntity.getBankCode());
        				failedTransactionsEntity.setCreditFlag(bankTransactionEntity.isCreditFlag());
        				failedTransactionsEntity.setFailureReason(e.getMostSpecificCause().toString());
        				failedTransactionsEntity.setRemarks(bankTransactionEntity.getRemarks());
        				failedTransactionsEntity.setTransactionAmount(bankTransactionEntity.getTransactionAmount());
        				failedTransactionsEntity.setTransactionDate(bankTransactionEntity.getTransactionDate());
        				failedTransactionsEntity.setTransactionId(bankTransactionEntity.getTransactionId());
        				failedTransactionsEntity.setValueDate(bankTransactionEntity.getValueDate());
        				failedTransactionsRepository.save(failedTransactionsEntity);
        				continue;
        			} 

	}
   }
  }
}
