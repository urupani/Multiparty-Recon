package com.demo.MultipartyRecon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSUtility {

	private static final Log LOGGER = LogFactory.getLog(XLSUtility.class);
	
	public static void writeToXLSFile(String[] headerTokens, ArrayList<?> rowsList,String fileName, String fileDir) throws IOException{
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(fileName);

		HSSFRow row = sheet.createRow(0);
		int cellnum = 0;
		for (String header : headerTokens) {
			HSSFCell cell = row.createCell(cellnum);
			cell.setCellValue(headerTokens[cellnum++]);
		}

		for (int rownum=1;rownum<=rowsList.size();rownum++) {
			row = sheet.createRow(rownum);
			cellnum = 0;
			for (String header : headerTokens) {
				HSSFCell cell = row.createCell(cellnum++);
				HashMap rowMap = (HashMap) rowsList.get(rownum-1);
				if(rowMap.get(header)!=null)
					cell.setCellValue((String)rowMap.get(header));
				else
					cell.setCellValue("");
			}
		}

		FileOutputStream out = 
					new FileOutputStream(new File(fileDir+fileName));
			workbook.write(out);
			out.close();
			LOGGER.info(fileName+" written successfully..");
	}

	public static ArrayList<HashMap<String, String>> readXLSFile(File fileName, String[] headerTokens, int headerRows){

		boolean match=false;
		ArrayList<HashMap<String, String>> rowsList = new ArrayList<HashMap<String, String>>();

		try{
			FileInputStream file = new FileInputStream(fileName);

			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			boolean endOfRecords = false;

			int rowCount = 0;
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();
				HashMap<String, String> rowMap = new HashMap<String, String>();
				int columnCount = 0;        	         
				Iterator<Cell> cellIterator = row.cellIterator();
				while(cellIterator.hasNext()) {
					Cell cell = cellIterator.next();					
					if(rowCount>headerRows){
						if(cell.getCellType() == Cell.CELL_TYPE_STRING){
							String string = "**This is a computer generated statement and does not require a signature";
							if(cell.toString().equalsIgnoreCase(string))
						{
								if(match==false)
								{
									rowsList.add(0, null);
								}
							return rowsList;
						}
							rowMap.put(headerTokens[columnCount], cell.getStringCellValue().trim());
						}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								Double dvalue = cell.getNumericCellValue();
								Date date = HSSFDateUtil.getJavaDate(dvalue);
								String df = cell.getCellStyle().getDataFormatString();
						        rowMap.put(headerTokens[columnCount], new CellDateFormatter(df).format(date));
						    } else {							
							Double dValue = cell.getNumericCellValue();							
							if(dValue-Math.floor(dValue)==0)
								rowMap.put(headerTokens[columnCount], String.valueOf((long)cell.getNumericCellValue()).trim());
							else
								rowMap.put(headerTokens[columnCount], dValue.toString());
						    }
						}else if(cell.getCellType() == Cell.CELL_TYPE_BLANK){
							if(columnCount==0){
								endOfRecords = true;
								break;
							}								
						}
					}
					else if(rowCount==headerRows)
					{
						int headerCount=0;
						String[] headerRow = new String[headerTokens.length];
						headerRow[headerCount] = cell.toString();
						headerCount++;
						while(cellIterator.hasNext())
						{
							Cell cell2 = cellIterator.next();
							headerRow[headerCount] = cell2.toString().trim();
							headerCount++;
						}
						if(Arrays.equals(headerRow,headerTokens))
						{
							match=true;
						}
					}
					else
						break;
					columnCount++;
				}
				if(endOfRecords)
					break;
				if(rowCount>headerRows)
					rowsList.add(rowMap);
				rowCount++;
			}
			
			file.close();
		 

		} catch (FileNotFoundException e) {
			LOGGER.error(fileName+" file not found...",e);
		} catch (IOException e) {
			LOGGER.error("Error while reading file: "+fileName,e);
		}
		return rowsList;
	}
	
	
	
	public static ArrayList<HashMap<String, String>> readRBL401XLSFile(File fileName, String[] headerTokens, int headerRows){
		
		boolean match = true;
		ArrayList<HashMap<String, String>> rowsList = new ArrayList<HashMap<String, String>>();

		try{
			FileInputStream file = new FileInputStream(fileName);

			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			boolean endOfRecords = false;

			int rowCount = 0;
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();
				HashMap<String, String> rowMap = new HashMap<String, String>();
				int columnCount = 0;        	         
				Iterator<Cell> cellIterator = row.cellIterator();
				while(cellIterator.hasNext()) {
					Cell cell = cellIterator.next();					
					if(rowCount>headerRows){
						if(cell.getCellType() == Cell.CELL_TYPE_STRING){
							if(cell.toString().equalsIgnoreCase("Statement Summary")){
								endOfRecords = true;
								if(match==false)
								{
									rowsList.add(0, null);
								}
							 return rowsList;
							}
							rowMap.put(headerTokens[columnCount], cell.getStringCellValue().trim());
						}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								Double dvalue = cell.getNumericCellValue();
								Date date = HSSFDateUtil.getJavaDate(dvalue);
								String df = cell.getCellStyle().getDataFormatString();
						        rowMap.put(headerTokens[columnCount], new CellDateFormatter(df).format(date));
						    } else {							
							Double dValue = cell.getNumericCellValue();							
							if(dValue-Math.floor(dValue)==0)
								rowMap.put(headerTokens[columnCount], String.valueOf((long)cell.getNumericCellValue()).trim());
							else
								rowMap.put(headerTokens[columnCount], dValue.toString());
						    }
						}else if(cell.getCellType() == Cell.CELL_TYPE_BLANK){
							if(columnCount>=headerTokens.length)								
							{
								break;
							}
							else
							{
								rowMap.put(headerTokens[columnCount],"0");
							}
						}
					}
					else if(rowCount==headerRows)
					{
						int headerCount=0;
						String[] headerRow = new String[headerTokens.length];
						headerRow[headerCount] = cell.toString();
						headerCount++;
						while(cellIterator.hasNext())
						{
							Cell cell2 = cellIterator.next();
							headerRow[headerCount] = cell2.toString();
							headerCount++;
						}
						if(!Arrays.equals(headerTokens, headerRow))
						{
							match=false;
						}
					}
					else
						break;
					columnCount++;
				}
				if(endOfRecords)
					break;
				if(rowCount>headerRows)
					rowsList.add(rowMap);
				rowCount++;
			}
			file.close();
		 

		} catch (FileNotFoundException e) {
			LOGGER.error(fileName+" file not found...",e);
		} catch (IOException e) {
			LOGGER.error("Error while reading file: "+fileName,e);
		}
		return rowsList;
	}
	
	
	public static ArrayList<HashMap<String, String>> readAndhraXLSFile(File fileName, String[] headerTokens, int headerRows){

		boolean match = true;
		ArrayList<HashMap<String, String>> rowsList = new ArrayList<HashMap<String, String>>();

		try{
			FileInputStream file = new FileInputStream(fileName);

			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			boolean endOfRecords = false;		
		    Iterator<Row> itr = sheet.iterator();
		// Iterating over Excel file in Java
		    int rowCount = 0;
		    while (itr.hasNext()) { //row iterator
			Row row = itr.next(); 
			HashMap<String, String> rowMap = new HashMap<String, String>();
			int columnCount = 0;
			// Iterating over each column of Excel file
			//Iterator<Cell> cellIterator = row.cellIterator();
			while (columnCount<headerTokens.length) { 
				Cell cell = row.getCell(columnCount);
				if(rowCount>headerRows)
				{
					if(cell!=null) {
				switch (cell.getCellType()) { 
				case Cell.CELL_TYPE_STRING: rowMap.put(headerTokens[columnCount], cell.getStringCellValue().trim());
				break;
				case Cell.CELL_TYPE_NUMERIC: if (HSSFDateUtil.isCellDateFormatted(cell)) {
					Double dvalue = cell.getNumericCellValue();
					Date date = HSSFDateUtil.getJavaDate(dvalue);
					String df = cell.getCellStyle().getDataFormatString();
			        rowMap.put(headerTokens[columnCount], new CellDateFormatter(df).format(date));
			    } else {							
				Double dValue = cell.getNumericCellValue();							
				if(dValue-Math.floor(dValue)==0)
					rowMap.put(headerTokens[columnCount], String.valueOf((long)cell.getNumericCellValue()).trim());
				else
					rowMap.put(headerTokens[columnCount], dValue.toString());
			    }
				break;
				case Cell.CELL_TYPE_BOOLEAN:;
				break;
				case Cell.CELL_TYPE_BLANK:
					{
						if(columnCount>=headerTokens.length)								
						{
							if(match==false)
							{
								rowsList.add(0, null);
							}
							return rowsList;
						}
						else
						{
							rowMap.put(headerTokens[columnCount],"0");
							break;
						}
					}
				default:
					break;
			}
				}
			else
			{
				rowMap.put(headerTokens[columnCount],"0");
			}
				}
				else if(rowCount==headerRows)
				{
					columnCount=0;
					String[] headerRow = new String[headerTokens.length];
					while(columnCount<headerTokens.length)
					{
						Cell cell2 =row.getCell(columnCount);
						headerRow[columnCount] = cell2.toString();
						columnCount++;
					}
					if(!Arrays.equals(headerTokens, headerRow))
					{
						match=false;
					}
				}
				else
					break;
				columnCount++;
		} 
			if(endOfRecords)
				break;
			if(rowCount>headerRows)
				rowsList.add(rowMap);
			rowCount++;
	  }
	  file.close();
		 

		} catch (FileNotFoundException e) {
			LOGGER.error(fileName+" file not found...",e);
		} catch (IOException e) {
			LOGGER.error("Error while reading file: "+fileName,e);
		}
		return rowsList;
	}
	
	
	
public static ArrayList<HashMap<String, String>> readXLSXFile(File fileName, String[] headerTokens, int headerRows) throws IOException{
		
	boolean match = true;	
	ArrayList<HashMap<String, String>> rowsList = new ArrayList<HashMap<String, String>>();
		
	try { 
		FileInputStream file = new FileInputStream(fileName);
		XSSFWorkbook book = new XSSFWorkbook(file);
		XSSFSheet sheet = book.getSheetAt(0);
		boolean endOfRecords = false;		
		Iterator<Row> itr = sheet.iterator();
		// Iterating over Excel file in Java
		int rowCount = 0;
		while (itr.hasNext()) { //row iterator
			Row row = itr.next(); 
			HashMap<String, String> rowMap = new HashMap<String, String>();
			int columnCount = 0;
			// Iterating over each column of Excel file
			//Iterator<Cell> cellIterator = row.cellIterator();
			while (columnCount<headerTokens.length) { 
				Cell cell = row.getCell(columnCount);
				if(rowCount>headerRows)
				{
					if(cell!=null) {
				switch (cell.getCellType()) { 
				case Cell.CELL_TYPE_STRING: 
					String string = "This is a computer generated statement which need not normally be signed. Contents of this statement will be considered correct if no error is reported within 21 days of the statement date.";
					if(cell.toString().equalsIgnoreCase(string))
				{
						if(match==false)
						{
							rowsList.add(0, null);
						}
						return rowsList;
				}
				rowMap.put(headerTokens[columnCount], cell.getStringCellValue().trim());
				break;
				case Cell.CELL_TYPE_NUMERIC: if (HSSFDateUtil.isCellDateFormatted(cell)) {
					Double dvalue = cell.getNumericCellValue();
					Date date = HSSFDateUtil.getJavaDate(dvalue);
					String df = cell.getCellStyle().getDataFormatString();
			        rowMap.put(headerTokens[columnCount], new CellDateFormatter(df).format(date));
			    } else {							
				Double dValue = cell.getNumericCellValue();							
				if(dValue-Math.floor(dValue)==0)
					rowMap.put(headerTokens[columnCount], String.valueOf((long)cell.getNumericCellValue()).trim());
				else
					rowMap.put(headerTokens[columnCount], dValue.toString());
			    }
				break;
				case Cell.CELL_TYPE_BOOLEAN:;
				break;
				case Cell.CELL_TYPE_BLANK:
					{
						if(columnCount>=headerTokens.length)								
						{
							break;
						}
						else
						{
							rowMap.put(headerTokens[columnCount],"0");
						}
					}
				default:
					break;
			}
				}
			else
			{
				rowMap.put(headerTokens[columnCount],"0");
			}
					columnCount++;
				}
				else if(rowCount==headerRows)
				{
					columnCount=0;
					String[] headerRow = new String[headerTokens.length];
					headerRow[columnCount] = cell.toString();
					columnCount++;
					while((row.getCell(columnCount))!=null || columnCount==3)
					{
						if(columnCount==3)
						{
							headerRow[columnCount]="";
							columnCount++;
							continue;
						}
						Cell cell2 =row.getCell(columnCount);
						headerRow[columnCount] = cell2.toString();
						columnCount++;
					}
					if(!Arrays.equals(headerTokens, headerRow))
					{
						match=false;
					}
				}
				else
					break;
				
		} 
			if(endOfRecords)
				break;
			if(rowCount>headerRows)
				rowsList.add(rowMap);
			rowCount++;
	  }
		file.close();
	}
	catch(FileNotFoundException e)
	{
		e.printStackTrace();
	}
	return rowsList;
  }




	
	@SuppressWarnings("unchecked")
	public static ArrayList<HashMap<String, String>> readXLSFile2(File fileName, String[] headerTokens, int headerRows){

		ArrayList<HashMap<String, String>> rowsList = new ArrayList<HashMap<String, String>>();

		try{
			FileInputStream file = new FileInputStream(fileName);

			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
	
			int columns = headerTokens.length;
			int startRow = headerRows;
			int lastRow = sheet.getLastRowNum();
			
			for (int r = startRow; r < lastRow; r++) {

				Row row = sheet.getRow(r);
				HashMap<String, String> rowMap = new HashMap<String, String>();

				for (int c = 0; c < columns; c++) {
					Cell cell = row.getCell(c, Row.RETURN_BLANK_AS_NULL);

					if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
						rowMap.put(headerTokens[c], "");

					} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
						rowMap.put(headerTokens[c], cell.getStringCellValue().trim());

					} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {

						Double dValue = cell.getNumericCellValue();
						try{
						if (dValue - Math.floor(dValue) == 0)
							rowMap.put(headerTokens[c], String.valueOf((long) cell.getNumericCellValue()).trim());
						else
							rowMap.put(	headerTokens[c],
										dValue.toString());
						} catch(Exception e){
							rowMap.put(headerTokens[c], dValue.toString());
						}
					}

				}

				rowsList.add(rowMap);
			}
			file.close();
			

		} catch (FileNotFoundException e) {
			LOGGER.error(fileName+" file not found...",e);
		} catch (IOException e) {
			LOGGER.error("Error while reading file: "+fileName,e);
		}
		return rowsList;
	}
	
	
	public static ArrayList<HashMap<String, String>> readXLSFileWithBlankCellHandling(File fileName, String[] headerTokens, int headerRows) throws Exception {

		ArrayList<HashMap<String, String>> rowsList = new ArrayList<HashMap<String, String>>();

		try {
			FileInputStream file = new FileInputStream(fileName);

			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(0);

			boolean endOfRecords = false;

			int firstRowNum = sheet.getFirstRowNum();
			int lastRowNum = sheet.getLastRowNum();
			for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {
				HashMap<String, String> rowMap = new HashMap<String, String>();
				Row row = sheet.getRow(rowNum);
				short firstCellNum = row.getFirstCellNum();
				int lastCellNum = row.getLastCellNum();
				if (lastCellNum > headerTokens.length) {
					lastCellNum = headerTokens.length;
				}
				for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
					Cell cell = row.getCell(cellNum, Row.CREATE_NULL_AS_BLANK);
					if (rowNum >= headerRows) {
						if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
							rowMap.put(headerTokens[cellNum], cell.getStringCellValue().trim());
						} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
							rowMap.put(headerTokens[cellNum], String.valueOf((long) cell.getNumericCellValue()).trim());
						} else if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
							if (cellNum == firstCellNum) {
								endOfRecords = true;
								break;
							}
						}
					} else {
						if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
							if (cellNum>=13) {
								endOfRecords = true;
								break;
							}
						} else {
							if (!headerTokens[cellNum].equals(cell.getStringCellValue().trim()) && rowNum>headerRows) {
								throw new Exception("Invalid Column, Expected Column Name: " + headerTokens[cellNum] + ", Actual Column Name: " + cell.getStringCellValue().trim());
							}
						}
					}
				}
				if (firstCellNum == -1 || endOfRecords)
					break;
				if (rowNum >= headerRows)
					rowsList.add(rowMap);
			}
			file.close();

		} catch (FileNotFoundException e) {
			LOGGER.error(fileName + " file not found...", e);
		} catch (IOException e) {
			LOGGER.error("Error while reading file: " + fileName, e);
		}
		return rowsList;
	}

	public static ArrayList<HashMap<String, String>> readXLSXFileWithBlankCellHandling(File fileName, String[] headerTokens, int headerRows) throws Exception {

		ArrayList<HashMap<String, String>> rowsList = new ArrayList<HashMap<String, String>>();

		try {
			FileInputStream file = new FileInputStream(fileName);
			XSSFWorkbook book = new XSSFWorkbook(file);
			XSSFSheet sheet = book.getSheetAt(0);
			boolean endOfRecords = false;		
			Iterator<Row> itr = sheet.iterator();
			int firstRowNum = sheet.getFirstRowNum();
			int lastRowNum = sheet.getLastRowNum();
			for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {
				int columnCount=0;
				HashMap<String, String> rowMap = new HashMap<String, String>();
				Row row = sheet.getRow(rowNum);
				short firstCellNum = row.getFirstCellNum();
				int lastCellNum = row.getLastCellNum();
				if (lastCellNum > headerTokens.length) {
					lastCellNum = headerTokens.length;
				}
				for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
					Cell cell = row.getCell(cellNum, Row.CREATE_NULL_AS_BLANK);
					if (rowNum >= headerRows) {
						if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
							rowMap.put(headerTokens[cellNum], cell.getStringCellValue().trim());
						} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
							rowMap.put(headerTokens[cellNum], String.valueOf((long) cell.getNumericCellValue()).trim());
						} else if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
//							if (cellNum == firstCellNum) {
//								endOfRecords = true;
//								break;
//							}
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								Double dvalue = cell.getNumericCellValue();
								Date date = HSSFDateUtil.getJavaDate(dvalue);
								String df = cell.getCellStyle().getDataFormatString();
						        rowMap.put(headerTokens[columnCount], new CellDateFormatter(df).format(date));
						    } else {							
							Double dValue = cell.getNumericCellValue();							
							if(dValue-Math.floor(dValue)==0)
								rowMap.put(headerTokens[columnCount], String.valueOf((long)cell.getNumericCellValue()).trim());
							else
								rowMap.put(headerTokens[columnCount], dValue.toString());
						    }
							
						}
					} else {
						if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
							if (cellNum == firstCellNum) {
								endOfRecords = true;
								break;
							}
						} else {
							if (!headerTokens[cellNum].equals(cell.getStringCellValue().trim())) {
								throw new Exception("Invalid Column, Expected Column Name: " + headerTokens[cellNum] + ", Actual Column Name: " + cell.getStringCellValue().trim());
							}
						}
					}
					columnCount++;
				}
				if (firstCellNum == -1 || endOfRecords)
					break;
				if (rowNum >= headerRows)
					rowsList.add(rowMap);
			}
			file.close();

		} catch (FileNotFoundException e) {
			LOGGER.error(fileName + " file not found...", e);
		} catch (IOException e) {
			LOGGER.error("Error while reading file: " + fileName, e);
		}
		return rowsList;
			
		}
	}

