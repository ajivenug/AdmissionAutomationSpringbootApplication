package com.harjit.admission.workers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harjit.admission.service.ExamResultsMessage;
import com.harjit.admission.service.ValidateApplication;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError;

@Component
public class MessageWorker {

	private final static Logger LOG = LoggerFactory.getLogger(MessageWorker.class);

	@Autowired
	private ExamResultsMessage examResultsMessage;

	@JobWorker(type = "send-message")
	public void validateAdmissionDetails(final JobClient client, final ActivatedJob job, @Variable(name="applicationId") String applicationId) {
		try {
			LOG.info("ApplicationId: {}", applicationId);
			examResultsMessage.sendAdmissionApprovedMessage(applicationId);

		} catch (Exception e) {
			throw new ZeebeBpmnError("SEND_FAILED", "Message Send failed!", null);
		}
	}

}
