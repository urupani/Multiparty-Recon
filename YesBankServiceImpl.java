package com.demo.MultipartyRecon;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import com.opencsv.CSVReader;

@Service
public class YesBankServiceImpl implements MultipartyReconServiceInterface{
	@Autowired
	MultipartyReconRepository multipartyReconRepository;
	
	@Autowired
	FileFailureRepository fileFailureRepository;
	
	@Autowired
	FailedTransactionsRepository failedTransactionsRepository;
	
	@SuppressWarnings("deprecation")
	public void readTransactionFile(File file, String username) throws IOException, ParseException {
		    
		String filename = file.getAbsolutePath();
		CSVReader reader;
			reader = new CSVReader(new FileReader(file), ',');
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
			Calendar cal = Calendar.getInstance();
			String dateTime = formatter.format(cal.getTime());
	        String[] line;
	        int linesToIgnore=2;
	        while(linesToIgnore>0)
	        {
	        	line=reader.readNext();
	        	linesToIgnore--;
	        }
	        line=reader.readNext();
	        String[] headerLine = {"TXN TYPE","TXN REF. NO.","AGENT SHOP NAME","TXN DATE","OPENING BALANCE","CREDIT","DEBIT","CLOSING BALANCE","UTILITY CODE","NARRATION TEXT"};
	        if(Arrays.equals(line, headerLine))
	        {
	        while((line=reader.readNext())!=null)
	        {
	        	String[] lineArr = line;
	        	if(lineArr[1].isEmpty() && lineArr[3].isEmpty())
	        	{
	        		break;
	        	}
	        	BankTransactionEntity bankTransactionEntity = new BankTransactionEntity();
	        	bankTransactionEntity.setTransactionId(lineArr[1]);
	        	String[] timestamp = lineArr[3].split(" ");
	        	Date txnDate = new SimpleDateFormat("yyyy-MM-dd").parse(timestamp[0]);
	        	bankTransactionEntity.setTransactionDate(txnDate);
	        	bankTransactionEntity.setValueDate(txnDate);
	        	
	        	if(lineArr[5].equals("-"))
	        	{	
	        		bankTransactionEntity.setTransactionAmount(Double.parseDouble(lineArr[6]));
	        		bankTransactionEntity.setCreditFlag("D");
	        	} else 
	        	{
	        		bankTransactionEntity.setTransactionAmount(Double.parseDouble(lineArr[5]));
	        		bankTransactionEntity.setCreditFlag("C");
	        	}
	        	
	        	bankTransactionEntity.setBalanceAfterTxn(Double.parseDouble(lineArr[7]));
	        	bankTransactionEntity.setBankCode("YES Bank");
	        	bankTransactionEntity.setRemarks(lineArr[9]);
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
	        else {
	        	int numberOfRecords=0;
	    		String[] nextLine;
	    		while((nextLine=reader.readNext())!=null)
	    		{
	    			String[] lineArr = nextLine;
		        	if(lineArr[1].isEmpty() && lineArr[3].isEmpty())
		        	{
		        		FileHistoryEntity fileHistoryEntity = new FileHistoryEntity();
		        		fileHistoryEntity.setTotalRecords(numberOfRecords);
		        		fileHistoryEntity.setFilename(filename);
		        		fileHistoryEntity.setTimestamp(dateTime);	
		        		fileHistoryEntity.setProcessedRecords(numberOfRecords);
		        		fileHistoryEntity.setUsername(username);
		        		fileFailureRepository.save(fileHistoryEntity);
		        		break;
		        	}
		        	numberOfRecords++;
	    			
	    		}
	        }
      reader.close();
	}
	
}
