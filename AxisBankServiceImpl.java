package com.demo.MultipartyRecon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
public class AxisBankServiceImpl implements MultipartyReconServiceInterface{
	
	@Autowired
	MultipartyReconRepository multipartyReconRepository;
	
	@Autowired
	FileFailureRepository fileFailureRepository;
	
	@Autowired
	FailedTransactionsRepository failedTransactionsRepository;
	
	@Override
	public void readTransactionFile(File file, String username) throws IOException, NoSuchAlgorithmException, ParseException {
	String fileName = file.getAbsolutePath();
    FileReader file1 = new FileReader(file);
	MessageDigest md = MessageDigest.getInstance("MD5"); 
    BufferedReader br = new BufferedReader(file1);
  	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
	Calendar cal = Calendar.getInstance();
	String dateTime = formatter.format(cal.getTime());
    int id = 1,failedRecords = 0;
    boolean failure=false;
    String line;
    int linesToIgnore=9;
    while(linesToIgnore>0)
    {
    	line=br.readLine();
    	linesToIgnore--;
    }
	
    line=br.readLine();
    String headerLine = "Tran Date,Value Date,CHQNO,Transaction Particulars,Amount(INR),DR|CR,Balance(INR),Branch Name";
    String headerLine2 = "Tran Date,Value Date,CHQNO,Transaction Particulars,Amount(INR),DR|CR,Balance(INR),Branch Name,,";
    if(line.equalsIgnoreCase(headerLine) || line.equalsIgnoreCase(headerLine2))
    {
    	while(!(line=br.readLine()).isEmpty())
    	{
    		String[] lineArr = line.split(",");
    		if(line.contains(",,,,,,,,"))
    		{
    			break;
    		}
           	BankTransactionEntity bankTransactionEntity = new BankTransactionEntity();
        	Date txnDate = new SimpleDateFormat("dd-MM-yyyy").parse(lineArr[0]);
        	bankTransactionEntity.setTransactionDate(txnDate);
        	Date valDate = new SimpleDateFormat("dd-MM-yyyy").parse(lineArr[1]);
        	bankTransactionEntity.setValueDate(valDate);
        	bankTransactionEntity.setRemarks(lineArr[3]);
        	bankTransactionEntity.setTransactionAmount(Double.parseDouble(lineArr[4]));
        	bankTransactionEntity.setBalanceAfterTxn(Double.parseDouble(lineArr[6]));
        	bankTransactionEntity.setBankCode("Axis Bank");
        	if(lineArr[5].equalsIgnoreCase("CR"))
        	{
        		bankTransactionEntity.setCreditFlag("C");
        	}
        	else {
        		bankTransactionEntity.setCreditFlag("D");
        	}
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
    				failedRecords++;
    				failure = true;
    				continue;
    			}
			id++;
	}
}
    else
    {
    	int numberOfRecords=0;
		String nextLine;
		while((nextLine=br.readLine())!=null)
		{
			if(nextLine.isEmpty()){				
	        		FileHistoryEntity fileHistoryEntity = new FileHistoryEntity();
	        		fileHistoryEntity.setTotalRecords(numberOfRecords);
	        		fileHistoryEntity.setFilename(fileName);
	        		fileHistoryEntity.setTimestamp(dateTime);	
	        		fileHistoryEntity.setProcessedRecords(0);
	        		fileHistoryEntity.setUsername(username);
	        		fileFailureRepository.save(fileHistoryEntity);
	        		break;
	        	}
			numberOfRecords++;
			}			
		}
    if(failure)
    {
    	FileHistoryEntity fileHistoryEntity = new FileHistoryEntity();
		fileHistoryEntity.setTotalRecords(id);
		fileHistoryEntity.setFilename(fileName);
		fileHistoryEntity.setTimestamp(dateTime);	
		fileHistoryEntity.setProcessedRecords(id-failedRecords);
		fileHistoryEntity.setUsername(username);
		fileFailureRepository.save(fileHistoryEntity);
    }
    br.close();
  }
}
