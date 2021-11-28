package edu.csupomona.fall2021.cs4800.CPP_Navigation_Web_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CppNavigationWebServiceApplication {

	public static String key = "the key has not been initialized";
	
	public static void main(String[] args) {
		key = args[0];
		SpringApplication.run(CppNavigationWebServiceApplication.class, args);
	}

}