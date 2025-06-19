package com.harjit.admission.service;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class ValidateApplication {

	public String validate(String name, int age, String departMent) {
		
		if(age <= 15 || age >= 25) {
			return null;
		}
		 String applicationId = UUID.randomUUID().toString();
		return applicationId;
		
	}
	
	

}
