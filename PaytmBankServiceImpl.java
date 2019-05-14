package com.demo.MultipartyRecon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
public class PaytmBankServiceImpl implements MultipartyReconServiceInterface{
	
	@Autowired
	MultipartyReconRepository multipartyReconRepository;
	
	@Autowired
	FileFailureRepository fileFailureRepository;
	
	@Autowired
	FailedTransactionsRepository failedTransactionsRepository;
	
	@Override
	public void readTransactionFile(File file, String username) throws IOException, ParseException {
	String fileName = file.getAbsolutePath();
	FileReader file1 = new FileReader(file);
	BufferedReader br = new BufferedReader(file1);
	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
	Calendar cal = Calendar.getInstance();
	String dateTime = formatter.format(cal.getTime());
	String line;
	int metaDataLines = 5;
	while(metaDataLines>0)
	{
		line=br.readLine();
		metaDataLines--;
	}
	line=br.readLine();
	String headerLine = "ID,TRANSACTION DATE,MODE,AMOUNT,DR/CR,AVAILABLE BALANCE,RRN,TRANSACTION REQUEST ID,BENEFICIARY A/C NO,BENEFICIARY NAME,REMITTER NAME,REMITTER MOBILE NUMBER,FEE";
	if(headerLine.equalsIgnoreCase(line)) {
		 while((line=br.readLine())!=null)
	        {
	        	String[] lineArr = line.split(",");
	        	if(lineArr[0]==null && lineArr[1]==null && lineArr[2]==null)
	        	{
	        	   System.out.println("File has ended!");
	        	   break;
	        	}
	        	else {
	        	BankTransactionEntity bankTransactionEntity = new BankTransactionEntity();
	        	bankTransactionEntity.setTransactionId(lineArr[0]);
	        	Date txnDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(lineArr[1]);
	        	bankTransactionEntity.setTransactionDate(txnDate);
	        	bankTransactionEntity.setValueDate(txnDate);
		        bankTransactionEntity.setRemarks(lineArr[2]);
		        bankTransactionEntity.setTransactionAmount(Double.parseDouble(lineArr[3]));
		        if(lineArr[4].equalsIgnoreCase("C"))
		        {
		        	bankTransactionEntity.setCreditFlag("C");
		        }
		        else {
		        	bankTransactionEntity.setCreditFlag("D");
		        }
		        bankTransactionEntity.setBankCode("Paytm Bank");
		        bankTransactionEntity.setBalanceAfterTxn(Double.parseDouble(lineArr[5]));
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
	else {
		int numberOfRecords=0;
		String nextLine;
		while((nextLine=br.readLine())!=null)
		{
			String[] lineArr = nextLine.split(",");
        	if(lineArr[1].isEmpty() && lineArr[3].isEmpty())
        	{
        		FileHistoryEntity fileHistoryEntity = new FileHistoryEntity();
        		fileHistoryEntity.setTotalRecords(numberOfRecords);
        		fileHistoryEntity.setFilename(fileName);
        		fileHistoryEntity.setTimestamp(dateTime);	
        		fileHistoryEntity.setProcessedRecords(numberOfRecords);
        		fileHistoryEntity.setUsername(username);
        		fileFailureRepository.save(fileHistoryEntity);
        		break;
        	}
        	numberOfRecords++;
			
		}
	}
	br.close();	
  }
}