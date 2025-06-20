package com.harjit.admission;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;

@SpringBootApplication
@Deployment(resources = { "classpath:AdmissionProcess.bpmn", "classpath:GetAdmission.bpmn", "classpath:admissionForm.form",
		"classpath:Collect More Details.form", "classpath:Enter Interview result.form" })
public class AdmissionApplication implements CommandLineRunner {

	@Autowired
	private ZeebeClient zeebeClient;

	private static final Logger LOG = LoggerFactory.getLogger(AdmissionApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AdmissionApplication.class, args);
	}

	/*
	 * @Override public void run(String... args) throws Exception {
	 * 
	 * 
	 * }
	 */

	@Override
	public void run(final String... args) {
		var bpmnProcessId = "Process_1pfarrz";
		var event = zeebeClient.newCreateInstanceCommand().bpmnProcessId(bpmnProcessId).latestVersion()
				.variables(Map.of("startedFrom", "Springboot")).send().join();
		LOG.info("started a process instance: {}", event.getProcessInstanceKey());
	}

}
