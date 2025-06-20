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
import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError;

@Component
public class AdmissionWorker {

	private final static Logger LOG = LoggerFactory.getLogger(AdmissionWorker.class);

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
			String applicationId = validateApplication.validate(name, age, departMent);
			return Map.ofEntries(Map.entry("validationStatus", true), Map.entry("applicationId", applicationId));

		} catch (Exception e) {
			throw new ZeebeBpmnError("VALIDATON_FAILED", "Admission details invalid!", null);
		}
	}

	@JobWorker(type = "approve-first-level")
	public Map<String, Boolean> approveFirstLevel(final JobClient client, final ActivatedJob job) {
		LOG.info("Approved First Level");
		return Map.of("firstLevelApproval", true);
	}

	@JobWorker(type = "reject-first-level")
	public Map<String, Boolean> rejectFirstLevel(final JobClient client, final ActivatedJob job) {
		LOG.info("Rejected First Level");
		return Map.of("firstLevelApproval", false);
	}

	@JobWorker(type = "schedule-exam")
	public void scheduleExam(final JobClient client, final ActivatedJob job,
			@Variable(name = "name") String name, @Variable(name = "applicationId") String applicationId) {
		LOG.info("Exam has been Scheduled for "+name+" with Application Id "+ applicationId);	
	}
	
	@JobWorker(type = "exam-failed-notification")
	public void examFailedNotif(final JobClient client, final ActivatedJob job,
			@Variable(name = "name") String name) {
		LOG.info("Sorry "+name+" You have failed to qualify for next round!");	
	}

	
	@JobWorker(type = "exam-time-expired-notification")
	public void examTimeExpiredNotif(final JobClient client, final ActivatedJob job,
			@Variable(name = "name") String name) {
		LOG.info("Sorry "+name+" Time Expired to take the exam!");	
	}
}
