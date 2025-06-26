package com.harjit.admission.workers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harjit.admission.service.AdmissionLetterMessage;
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

	@Autowired
	private AdmissionLetterMessage admissionLetterMessage;

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

	
	@SuppressWarnings("unchecked")
	@JobWorker(type = "create-milestone")
	public Map<String, Object> createMilestones(final JobClient client, final ActivatedJob job,
			@Variable(name = "name") String name, @Variable(name = "milestone") String milestone,
			@Variable(name = "milestoneStatus") Object milestoneStatus) {
		Map<String, Object> milestoneStatusMap;

		if (milestoneStatus == null) {
			milestoneStatusMap = new HashMap<>();
		} else {
			milestoneStatusMap = (Map<String, Object>) milestoneStatus;
		}
		LOG.info(milestone + "Milestones Created for " + name);
		milestoneStatusMap.put(milestone, "Init");
		LOG.info("Milestone Status: " + milestoneStatusMap);
		return Map.ofEntries(Map.entry("MilestoneName", milestone), Map.entry("milestoneStatus", milestoneStatusMap));

	}

	@JobWorker(type = "approve-first-level")
	public Map<String, Map<String, String>> approveFirstLevel(final JobClient client, final ActivatedJob job,
			@Variable(name = "milestoneListArray") List<String> milestones,
			@Variable(name = "milestoneStatus") Map<String, String> milestoneStatus) {
		LOG.info("Approved First Level");
		String milestoneName = milestones.get(0);
		milestoneStatus.put(milestoneName, "Approved");
		return Map.of("milestoneStatus", milestoneStatus);
	}

	@JobWorker(type = "reject-first-level")
	public Map<String, Map<String, String>> rejectFirstLevel(final JobClient client, final ActivatedJob job,
			@Variable(name = "milestoneListArray") List<String> milestones,
			@Variable(name = "milestoneStatus") Map<String, String> milestoneStatus) {
		LOG.info("Rejected First Level");
		String milestoneName = milestones.get(0);
		milestoneStatus.put(milestoneName, "Rejected");
		return Map.of("milestoneStatus", milestoneStatus);
	}

	@JobWorker(type = "schedule-exam")
	public void scheduleExam(final JobClient client, final ActivatedJob job, @Variable(name = "name") String name,
			@Variable(name = "applicationId") String applicationId) {
		LOG.info("Exam has been Scheduled for " + name + " with Application Id " + applicationId);
	}

	@JobWorker(type = "exam-failed-notification")
	public void examFailedNotif(final JobClient client, final ActivatedJob job, @Variable(name = "name") String name) {
		LOG.info("Sorry " + name + " You have failed to qualify for next round!");
	}

	@JobWorker(type = "exam-time-expired-notification")
	public void examTimeExpiredNotif(final JobClient client, final ActivatedJob job,
			@Variable(name = "name") String name) {
		LOG.info("Sorry " + name + " Time Expired to take the exam!");
	}

	@JobWorker(type = "approve-second-level")
	public Map<String, Map<String, String>> approveSecondLevel(final JobClient client, final ActivatedJob job,
			@Variable(name = "milestoneListArray") List<String> milestones,
			@Variable(name = "milestoneStatus") Map<String, String> milestoneStatus) {
		LOG.info("Approved Second Level");
		String milestoneName = milestones.get(1);
		milestoneStatus.put(milestoneName, "Approved");
		return Map.of("milestoneStatus", milestoneStatus);

	}

	@JobWorker(type = "reject-second-level")
	public Map<String, Map<String, String>> rejectSecondLevel(final JobClient client, final ActivatedJob job,
			@Variable(name = "milestones") List<String> milestones,
			@Variable(name = "milestoneStatus") Map<String, String> milestoneStatus) {
		LOG.info("Rejected Second Level");
		String milestoneName = milestones.get(1);
		milestoneStatus.put(milestoneName, "Approved");
		return Map.of("milestoneStatus", milestoneStatus);
	}

	@JobWorker(type = "schedule-interview")
	public void scheduleInterview(final JobClient client, final ActivatedJob job, @Variable(name = "name") String name,
			@Variable(name = "applicationId") String applicationId) {
		LOG.info("Interview has been Scheduled for " + name + " with Application Id " + applicationId);
	}

	@JobWorker(type = "conduct-bgc")
	public Map<String, Boolean> conductBGC(final JobClient client, final ActivatedJob job,
			@Variable(name = "name") String name, @Variable(name = "applicationId") String applicationId) {
		LOG.info("BGC has been completed for " + name + " with Application Id " + applicationId);
		return Map.of("bgc", true);
	}

	@JobWorker(type = "reject-third-level")
	public Map<String, Map<String, String>> rejectThirdLevel(final JobClient client, final ActivatedJob job,
			@Variable(name = "milestones") List<String> milestones,
			@Variable(name = "milestoneStatus") Map<String, String> milestoneStatus) {
		LOG.info("Rejected Third Level");
		String milestoneName = milestones.get(2);
		milestoneStatus.put(milestoneName, "Rejected");
		return Map.of("milestoneStatus", milestoneStatus);
	}

	@JobWorker(type = "send-admission-letter")
	public void sendAdmissionLetter(final JobClient client, final ActivatedJob job,
			@Variable(name = "applicationId") String applicationId) {
		try {
			LOG.info("ApplicationId: {}", applicationId);
			admissionLetterMessage.sendAdmissionLetterMessage(applicationId);

		} catch (Exception e) {
			throw new ZeebeBpmnError("SEND_FAILED", "Message Send failed!", null);
		}
	}

}
