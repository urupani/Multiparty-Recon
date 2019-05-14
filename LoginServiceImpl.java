package com.demo.MultipartyRecon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.MultipartyRecon.Repository.LoginCredentialsRepository;

@Service
public class LoginServiceImpl {
	
	@Autowired
	LoginCredentialsRepository loginCredentialsRepository;

	public boolean checkCredentials(String uname, String pass)
	{
		return loginCredentialsRepository.existsById(uname, pass) != null;
	}
}
