package com.demo.MultipartyRecon;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
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
public class SBIBankServiceImpl implements MultipartyReconServiceInterface {

	@Autowired
	MultipartyReconRepository multipartyReconRepository;
	
	@Autowired
	FileFailureRepository fileFailureRepository;
	
	@Autowired
	FailedTransactionsRepository failedTransactionsRepository;
	
	@Override
	public void readTransactionFile(File file, String username) throws IOException, NoSuchAlgorithmException, ParseException {
		String fileName = file.getAbsolutePath();
		String[] sbiHeaders = {"Txn Date",	"Value Date", "Description",	"Ref No./Cheque No.",	"Branch Code", "Debit",	"Credit",	"Balance"};
    	MessageDigest md = MessageDigest.getInstance("MD5"); 
    	
    	
    	
    	ArrayList<HashMap<String, String>> xlsData = XLSUtility.readXLSFile(file, sbiHeaders, 19);
    	
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
    			String[] line= new String[sbiHeaders.length];
    			int i=0;
    			for(String key: entry.keySet()) {
    				line[i]=entry.get(key);
    				i++;
    			}
    			
    			Date txnDate = new SimpleDateFormat("dd/MM/yyyy").parse(line[1]);
    			bankTransactionEntity.setTransactionDate(txnDate);
    			Date valDate = new SimpleDateFormat("dd/MM/yyyy").parse(line[2]);
    			bankTransactionEntity.setValueDate(valDate);
    			bankTransactionEntity.setRemarks(line[3]);
    			bankTransactionEntity.setBankCode("SBI Bank");
    			if(line[0].isEmpty())
    			{
    				bankTransactionEntity.setCreditFlag("C");
    				bankTransactionEntity.setTransactionAmount(Double.parseDouble(line[4]));
       			}
    			else
    			{
    				bankTransactionEntity.setCreditFlag("D");
    				bankTransactionEntity.setTransactionAmount(Double.parseDouble(line[0]));
    			}
    			bankTransactionEntity.setBalanceAfterTxn(Double.parseDouble(line[7]));
    			String txnId = fileName+"_"+Integer.toString(id);
    			byte[] messageDigest = md.digest(txnId.getBytes());
    			BigInteger no = new BigInteger(1, messageDigest);
    			txnId = no.toString(16);
    			bankTransactionEntity.setTransactionId(txnId);
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
    			id++;
    		}
    	}

	}

}
