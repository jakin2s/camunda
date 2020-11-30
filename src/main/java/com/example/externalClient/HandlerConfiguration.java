package com.example.externalClient;

import java.time.LocalDateTime;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.camunda.bpm.client.ExternalTaskClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.example.externalClient.Model.CamundaObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.camunda.bpm.client.backoff.ExponentialBackoffStrategy;
import org.json.JSONObject;

@Configuration
public class HandlerConfiguration {
	private final Logger logger = LoggerFactory.getLogger(HandlerConfiguration.class);

	@Bean
	public void createTopicSubscriberHandler() {
		ExponentialBackoffStrategy fetchTimer = new ExponentialBackoffStrategy(500L, 2, 500L);
		int maxTasksToFetchWithinOnRequest = 1;

		ExternalTaskClient externalTaskClient = ExternalTaskClient.create().baseUrl("http://localhost:8080/engine-rest")
				.maxTasks(3).backoffStrategy(fetchTimer).build();

		externalTaskClient.subscribe("Bearbeitungszeit_protokollieren").handler((externalTask, externalTaskService) -> {

			logger.info("Bearbeitungszeit wird überprüft");
			System.out.println("xxx");
			try {
				int moneyValue = externalTask.getVariable("moneyValue");
				boolean insureOrder = externalTask.getVariable("insureOrder");
				int orderSize = externalTask.getVariable("orderSize");
				int l = externalTask.getVariable("orderTime");
				Long orderTime = new Long(l);
				long endeBearbeitungszeit = System.currentTimeMillis() / 1000;
				long differenceTime = endeBearbeitungszeit - orderTime;

				LocalDateTime dateTime = LocalDateTime.ofEpochSecond(differenceTime, 0, ZoneOffset.UTC);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("DD,MM,YYYY h:mm,a", Locale.GERMAN);
				String finaleBearbeitungszeit = dateTime.format(DateTimeFormatter.ISO_DATE);

				Map<String, Object> variables = new HashMap<>();
				variables.put("differenceTimeValue", differenceTime);

				logger.info("die Bearbeitungszeit beträgt " + differenceTime);

				RestTemplate restTemplate = new RestTemplate();
				CamundaObject camundaObjectClient = new CamundaObject(orderTime,endeBearbeitungszeit,differenceTime);
				JSONObject camundaJsonObject = new JSONObject(camundaObjectClient);
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				
				logger.info("cumundaObject" + camundaJsonObject.toString());

				final String url = "http://localhost:8093/camundaObject/monitoring";
				HttpEntity<String> request = new HttpEntity<String>(camundaJsonObject.toString(), headers);
				restTemplate.postForObject(url, request, String.class);
				externalTaskService.complete(externalTask, variables);

			} catch (Exception e) {
				logger.error("Fehler: ", e);
				externalTaskService.handleBpmnError(externalTask, externalTask.getId(), "Something went wrong!" + e);
				return;
			}
		}).open();

		externalTaskClient.subscribe("Startzeit_festlegen").handler((externalTask, externalTaskService) -> {
				externalTaskService.complete(externalTask);

		}).open();
	}
}
