package com.demo.MultipartyRecon;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.demo.MultipartyRecon.Entity.LoginCredentialsEntity;

@RestController
public class LoginController {
	
	@Autowired
	LoginServiceImpl loginServiceImpl;
	
	String answer;
	
	@PostMapping(path="/verifyLogin", consumes=MediaType.APPLICATION_JSON)
	@ResponseBody
	public String verifyCredentials(@RequestBody LoginCredentialsEntity login) {
		if(loginServiceImpl.checkCredentials(login.getUsername(),login.getPassword()))
		{
			answer="true";
			return answer;
		}
		else
		{
			answer="false";
			return answer;
		}
	}
}
