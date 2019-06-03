package com.demo.MultipartyRecon;

import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DateFormat;
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
public class RBL941ServiceImpl implements MultipartyReconServiceInterface {

	@Autowired
	MultipartyReconRepository multipartyReconRepository;
	
	@Autowired
	FileFailureRepository fileFailureRepository;
	
	@Autowired
	FailedTransactionsRepository failedTransactionsRepository;
	
	@SuppressWarnings("deprecation")
	@Override
	public void readTransactionFile(File file, String username) throws Exception {
		String fileName = file.getAbsolutePath();
		MessageDigest md = MessageDigest.getInstance("MD5"); 
		int id=1;
		
		CSVReader reader;
			reader = new CSVReader(new FileReader(file), ',');
			DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");  
			Calendar cal = Calendar.getInstance();
			String dateTime = formatter.format(cal.getTime());
	        String[] line;
	        int linesToIgnore=12;
	        while(linesToIgnore>0)
	        {
	        	line=reader.readNext();
	        	linesToIgnore--;
	        }
	        line=reader.readNext();
	        String[] headerLine = {"Transaction Date",	"Value Date",	"Transaction Remarks",	"Instrument Id",	"Withdrawl Amt(INR)",	"Deposit Amt(INR)",	"Transaction Balance(INR)",""};
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
	        	String txnId = fileName+"_"+Integer.toString(id);
				byte[] messageDigest = md.digest(txnId.getBytes());
				BigInteger no = new BigInteger(1, messageDigest);
				txnId = no.toString(16);
				bankTransactionEntity.setTransactionId(txnId);
	        	String[] timestamp = lineArr[0].split(" ");
	        	Date txnDate = new SimpleDateFormat("dd-MM-yyyy").parse(timestamp[0]);
	        	bankTransactionEntity.setTransactionDate(txnDate);
	        	Date valDate = new SimpleDateFormat("dd-MM-yyyy").parse(lineArr[1]);
	        	bankTransactionEntity.setValueDate(valDate);
	        	
	        	if(lineArr[5].startsWith("   "))
	        	{	
	        		bankTransactionEntity.setTransactionAmount(Double.parseDouble(lineArr[4]));
	        		bankTransactionEntity.setCreditFlag("D");
	        	} else 
	        	{
	        		bankTransactionEntity.setTransactionAmount(Double.parseDouble(lineArr[5]));
	        		bankTransactionEntity.setCreditFlag("C");
	        	}
	        	
	        	bankTransactionEntity.setBalanceAfterTxn(Double.parseDouble(lineArr[6]));
	        	bankTransactionEntity.setBankCode("RBL 941 Bank");
	        	bankTransactionEntity.setRemarks(lineArr[2]);
	        	bankTransactionEntity.setFileName(fileName);
	        	bankTransactionEntity.setPartner("RBL");
	        	bankTransactionEntity.setTxnType("AEPS");
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
	        else {
	        	int numberOfRecords=1;
	    		String[] nextLine;
	    		while((nextLine=reader.readNext())!=null)
	    		{
	    			String[] lineArr = nextLine;
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
      reader.close();

		
		
	}

}
