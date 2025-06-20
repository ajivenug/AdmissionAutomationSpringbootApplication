package com.harjit.admission.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.camunda.zeebe.client.ZeebeClient;

@Service
public class AdmissionLetterMessage {
	
	 private final ZeebeClient zeebeClient;
	 private final static Logger LOG = LoggerFactory.getLogger(AdmissionLetterMessage.class);

	    public AdmissionLetterMessage(ZeebeClient zeebeClient) {
	        this.zeebeClient = zeebeClient;
	    }

	    public void sendAdmissionLetterMessage(String applicationId) {
	        zeebeClient.newPublishMessageCommand()
	                .messageName("admissionLetter")  // 👈 Message name in BPMN
	                .correlationKey(applicationId)         // 👈 Must match process variable
	                .variables("{\"admission\": \"granted\"}") // 👈 Any payload to pass
	                .send()
	                .join();

	        LOG.info("Admission letter sent to process instance for studentId: " + applicationId);
	    }

}
