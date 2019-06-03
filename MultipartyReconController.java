package com.demo.MultipartyRecon;
import java.io.File;
import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.demo.MultipartyRecon.Entity.BankTxnEntity;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
public class MultipartyReconController {
	
	
	@Autowired
	BankTxnESServiceImpl bankTxnServiceImpl;
	
	@Autowired
	YesBankServiceImpl yesBankServiceImpl;
	@Autowired
	Yes876BankServiceImpl yes876BankServiceImpl;
	@Autowired
	PaytmBankServiceImpl paytmBankServiceImpl;
	@Autowired
	FederalBankServiceImpl federalBankServiceImpl;
	@Autowired
	AxisBankServiceImpl axisBankServiceImpl;
	@Autowired
	SBIBankServiceImpl sBIBankServiceImpl;
	@Autowired
	RBLBankServiceImpl rBLBankServiceImpl;
	@Autowired
	AndhraBankServiceImpl andhraBankServiceImpl;
	@Autowired
	RBL401ServiceImpl rBL401ServiceImpl;
	@Autowired
	RBL941ServiceImpl rBL941ServiceImpl;
	@Autowired
	IDFCBankServiceImpl idfcBankServiceImpl;
	
	@PostMapping(path="/yesbank", consumes=MediaType.MULTIPART_FORM_DATA)
	public void reconYesBank(@RequestPart("file") MultipartFile file1, String username) throws Exception
	{
		File file = MultipartyReconServiceInterface.convertMultipartFiletoFile(file1);
		yesBankServiceImpl.readTransactionFile(file, username);
	}
	
	@PostMapping(path="/yes876bank", consumes=MediaType.MULTIPART_FORM_DATA)
	public void reconYes876Bank(@RequestPart("file") MultipartFile file1, String username) throws Exception
	{
		File file = MultipartyReconServiceInterface.convertMultipartFiletoFile(file1);
		yes876BankServiceImpl.readTransactionFile(file, username);
	}
	
	@PostMapping(path="/paytmbank", consumes=MediaType.MULTIPART_FORM_DATA)
	public void reconPaytmBank(@RequestPart("file") MultipartFile file1, String username) throws Exception
	{
		File file = MultipartyReconServiceInterface.convertMultipartFiletoFile(file1);
		paytmBankServiceImpl.readTransactionFile(file,username);
	}
	
	@PostMapping(path="/federalbank", consumes=MediaType.MULTIPART_FORM_DATA)
	public void reconFederalBank(@RequestPart("file") MultipartFile file1, String username) throws Exception
	{
		File file = MultipartyReconServiceInterface.convertMultipartFiletoFile(file1);
		federalBankServiceImpl.readTransactionFile(file,username);
	}
	
	@PostMapping(path = "/axisbank", consumes= {MediaType.MULTIPART_FORM_DATA})
	public void reconAxisBank(@RequestPart("file") MultipartFile file1, String username) throws Exception
	{
		File file = MultipartyReconServiceInterface.convertMultipartFiletoFile(file1);
		axisBankServiceImpl.readTransactionFile(file, username);
	}
	
	@PostMapping(path="/sbibank", consumes=MediaType.MULTIPART_FORM_DATA)
	public void reconSBIBank(@RequestPart("file") MultipartFile file1, String username) throws Exception
	{
		File file = MultipartyReconServiceInterface.convertMultipartFiletoFile(file1);
		sBIBankServiceImpl.readTransactionFile(file,username);
	}
	
	@PostMapping(path="/rblbank", consumes=MediaType.MULTIPART_FORM_DATA)
	public void reconRBLBank(@RequestPart("file") MultipartFile file1, String username) throws Exception
	{
		File file = MultipartyReconServiceInterface.convertMultipartFiletoFile(file1);
		rBLBankServiceImpl.readTransactionFile(file, username);
	}
	
	@PostMapping(path="/andhrabank", consumes=MediaType.MULTIPART_FORM_DATA)
	public void reconAndhraBank(@RequestPart("file") MultipartFile file1, String username) throws Exception
	{
		File file = MultipartyReconServiceInterface.convertMultipartFiletoFile(file1);
		andhraBankServiceImpl.readTransactionFile(file,username);
	}
	
	@PostMapping(path="/rbl401bank", consumes=MediaType.MULTIPART_FORM_DATA)
	public void reconRBL401Bank(@RequestPart("file") MultipartFile file1, String username) throws Exception
	{
		File file = MultipartyReconServiceInterface.convertMultipartFiletoFile(file1);
		rBL401ServiceImpl.readTransactionFile(file,username);
	}
	
	@PostMapping(path="/rbl941bank", consumes=MediaType.MULTIPART_FORM_DATA)
	public void reconRBL941Bank(@RequestPart("file") MultipartFile file1, String username) throws Exception
	{
		File file = MultipartyReconServiceInterface.convertMultipartFiletoFile(file1);
		rBL941ServiceImpl.readTransactionFile(file,username);
	}
	
	@PostMapping(path="/idfcbank", consumes=MediaType.MULTIPART_FORM_DATA)
	public void reconIDFCBank(@RequestPart("file") MultipartFile file1, String username) throws Exception
	{
		File file = MultipartyReconServiceInterface.convertMultipartFiletoFile(file1);
		idfcBankServiceImpl.readTransactionFile(file,username);
	}
	
	
	
		
	@GetMapping(path="/getall")
	public void getBankTxnEntity() throws JsonGenerationException, JsonMappingException, IOException{
		bankTxnServiceImpl.fetchData();
		System.out.println("result in json");
	}

		
}
