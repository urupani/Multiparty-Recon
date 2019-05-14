package com.demo.MultipartyRecon;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public interface MultipartyReconServiceInterface {
	public void readTransactionFile(File file, String username) throws Exception;	
	public static File convertMultipartFiletoFile(MultipartFile file) throws IOException {
		 
		    File convFile = new File(file.getOriginalFilename());
		    convFile.createNewFile(); 
		    FileOutputStream fos = new FileOutputStream(convFile); 
		    fos.write(file.getBytes());
		    fos.close(); 
		    return convFile;
	}
}