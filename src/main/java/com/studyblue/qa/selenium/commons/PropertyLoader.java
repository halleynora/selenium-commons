package com.studyblue.qa.selenium.commons;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertyLoader {
	private static final Logger log = Logger.getLogger(PropertyLoader.class.getName());
	Properties prop = new Properties();
	
	public PropertyLoader(String filePath){
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(filePath);
		try {
			prop.load(in);
			in.close();
		} catch (IOException e) {
			log.info("File at "+filePath+" not found.");
		}
	}
	
	public String getProperty(String propertyName){
		
		String property = System.getProperty(propertyName);
		if (property == null){
			property = new String(prop.getProperty(propertyName));
		}
		return property;
	}
}
