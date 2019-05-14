package com.demo.MultipartyRecon;

import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.demo.MultipartyRecon.Entity.BankTransactionEntity;
import com.demo.MultipartyRecon.Entity.FailedTransactionsEntity;
import com.demo.MultipartyRecon.Repository.FailedTransactionsRepository;
import com.demo.MultipartyRecon.Repository.MultipartyReconRepository;
import com.opencsv.CSVReader;

@Service
public class RBLBankServiceImpl implements MultipartyReconServiceInterface {

	@Autowired
	MultipartyReconRepository multipartyReconRepository;
	
	@Autowired
	FailedTransactionsRepository failedTransactionsRepository;
	
	@SuppressWarnings("deprecation")
	@Override
	public void readTransactionFile(File file, String username) throws Exception {
		String fileName = file.getAbsolutePath();
		CSVReader reader;
			reader = new CSVReader(new FileReader(file), ',');
	    	MessageDigest md = MessageDigest.getInstance("MD5");
	        String[] line;
	   
	        while((line=reader.readNext())!=null)
	        {
	        	String[] lineArr = line;
	        	BankTransactionEntity bankTransactionEntity = new BankTransactionEntity();
	        	Date txnDate = new SimpleDateFormat("dd-MMM-yy").parse(lineArr[1]);
	        	bankTransactionEntity.setTransactionDate(txnDate);
	        	bankTransactionEntity.setValueDate(txnDate);
	        	
	        	if(lineArr[6].equals("0"))
	        	{	
	        		bankTransactionEntity.setTransactionAmount(Double.parseDouble(lineArr[5]));
	        		bankTransactionEntity.setCreditFlag("D");
	        	} else 
	        	{
	        		bankTransactionEntity.setTransactionAmount(Double.parseDouble(lineArr[6]));
	        		bankTransactionEntity.setCreditFlag("C");
	        	}
	        	
	        	bankTransactionEntity.setBalanceAfterTxn(Double.parseDouble(lineArr[7]));
	        	bankTransactionEntity.setBankCode("RBL Bank");
	        	bankTransactionEntity.setRemarks(lineArr[3]);
	        	String txnId = fileName+"_"+lineArr[2];
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
	         }
reader.close();
	}

}
