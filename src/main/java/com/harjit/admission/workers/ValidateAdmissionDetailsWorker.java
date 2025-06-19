package com.harjit.admission.workers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harjit.admission.service.ValidateApplication;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError;

@Component
public class ValidateAdmissionDetailsWorker {

	private final static Logger LOG = LoggerFactory.getLogger(ValidateAdmissionDetailsWorker.class);
	
	@Autowired
	private ValidateApplication validateApplication;

	@JobWorker(type = "validate-admission-details", fetchVariables = { "name", "age", "department" })
	public Map<String, Object> validateAdmissionDetails(final JobClient client, final ActivatedJob job) {

		try {
			String name = (String) job.getVariablesAsMap().get("name");
			int age = (Integer) job.getVariablesAsMap().get("age");
			String departMent = (String) job.getVariablesAsMap().get("department");
			LOG.info("Name: {}", name);
			LOG.info("Age: {}", age);
			LOG.info("Department: {}", departMent);
			String applicationId=validateApplication.validate(name,age,departMent);
			return Map.ofEntries(
				    Map.entry("validationStatus", true),
				    Map.entry("applicationId", applicationId));
		
		} catch (Exception e) {
			throw new ZeebeBpmnError("VALIDATON_FAILED", "Admission details invalid!", null);
		}
	}
	
	

}
