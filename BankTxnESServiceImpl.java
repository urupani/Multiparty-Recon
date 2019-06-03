package com.demo.MultipartyRecon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.MultipartyRecon.Entity.BankTxnEntity;
import com.demo.MultipartyRecon.Repository.BankTxnRepository;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class BankTxnESServiceImpl {

	@Autowired
	BankTxnRepository bankTxnRepository;
	
	@SuppressWarnings("unchecked")
	public void fetchData() throws JsonGenerationException, JsonMappingException, IOException
	{
Iterator<BankTxnEntity> iterator = bankTxnRepository.findAll().iterator();
		
		ArrayList<BankTxnEntity> bankTxnEntityList = new ArrayList<BankTxnEntity>();
		while(iterator.hasNext())
		{
			BankTxnEntity entity = iterator.next();
			bankTxnEntityList.add(entity);
		}
		iterator = bankTxnEntityList.iterator();
		//JSONArray jsonObjList = new JSONArray();
		while(iterator.hasNext()) {
			try {
				BankTxnEntity bankTxnEntity = iterator.next();
				JSONObject jsonObj = new JSONObject();
				//jsonObj.put("_id", bankTxnEntity.getTxn_id());
				jsonObj.put("bal_after_txn", bankTxnEntity.getBal_after_txn());
				jsonObj.put("bank_code", bankTxnEntity.getBank_code());
				jsonObj.put("value_date", bankTxnEntity.getValue_date().getTime());
				jsonObj.put("credit_flag", bankTxnEntity.getCredit_flag());
				jsonObj.put("txn_amount", bankTxnEntity.getTxn_amount());
				jsonObj.put("txn_date", bankTxnEntity.getTxn_date().getTime());
				jsonObj.put("txn_id", bankTxnEntity.getTxn_id());
				jsonObj.put("partner",bankTxnEntity.getPartner());
				jsonObj.put("file_name", bankTxnEntity.getFile_name());
				jsonObj.put("txn_type", bankTxnEntity.getTxn_type());
				String JSONText = jsonObj.toJSONString();
				HttpClient httpClient = HttpClients.createDefault();
				String url = "http://localhost:9200/bank_txn/transactions/"+bankTxnEntity.getTxn_id();
				HttpPost httpPost = new HttpPost(url);

				// Add Request Header parameter
				httpPost.addHeader("Content-Type", "application/json");

				// Add Request Body parameters
				StringEntity sEntity = new StringEntity(JSONText);
				httpPost.setEntity(sEntity);
				// Execute HTTP Post Request
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String response = httpClient.execute(httpPost, responseHandler);
				System.out.println(response);
//				RestTemplate restTemplate = new RestTemplate();
//				restTemplate.postForEntity("http://localhost:9200/bank_txn/transaction", JSONText, ResponseEntity.class, bankTxnEntity.getId());
//				//jsonObjList.add(jsonObj);
			} catch(Exception e) {
				
			}
		}
		
	}

}
