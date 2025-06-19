package com.harjit.admission.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.harjit.admission.workers.MessageWorker;

import io.camunda.zeebe.client.ZeebeClient;

@Service
public class ExamResultsMessage {
	
	 private final ZeebeClient zeebeClient;
	 private final static Logger LOG = LoggerFactory.getLogger(ExamResultsMessage.class);

	    public ExamResultsMessage(ZeebeClient zeebeClient) {
	        this.zeebeClient = zeebeClient;
	    }

	    public void sendAdmissionApprovedMessage(String applicationId) {
	        zeebeClient.newPublishMessageCommand()
	                .messageName("submitExamConfirmation")  // 👈 Message name in BPMN
	                .correlationKey(applicationId)         // 👈 Must match process variable
	                .variables("{\"passed\": true}") // 👈 Any payload to pass
	                .send()
	                .join();

	        LOG.info("Message sent to process instance for studentId: " + applicationId);
	    }

}
