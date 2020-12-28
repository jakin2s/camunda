package com.example.externalClient.HandlerConfig;

import java.time.LocalDateTime;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.*;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.impl.ExternalTaskImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.example.externalClient.Model.CamundaObject;

import org.camunda.bpm.client.backoff.ExponentialBackoffStrategy;
import org.json.JSONObject;

@Configuration
@Slf4j
public class HandlerConfiguration {
	@Bean
	public void createTopicSubscriberHandler() {
		ExponentialBackoffStrategy fetchTimer = new ExponentialBackoffStrategy(500L, 2, 500L);
		int maxTasksToFetchWithinOnRequest = 1;

		ExternalTaskClient externalTaskClient = ExternalTaskClient.create().baseUrl("http://localhost:8080/engine-rest")
				.maxTasks(3).backoffStrategy(fetchTimer).build();

		externalTaskClient.subscribe("Bearbeitungszeit_protokollieren").handler((externalTask, externalTaskService) -> {

			log.info("Bearbeitungszeit wird überprüft");
			try {
				Double l = ( externalTask.getVariable("orderTime"));
				Long orderTime = new Long(l.intValue());
				long endeBearbeitungszeit = System.currentTimeMillis() / 1000;
				long differenceTime = (long) (endeBearbeitungszeit - orderTime);
				long durchlaufzeit_sec = TimeUnit.SECONDS.convert(differenceTime, TimeUnit.SECONDS);
				long durchlaufzeit_ms = TimeUnit.SECONDS.convert(differenceTime, TimeUnit.MILLISECONDS);
				long durchlaufzeit_nano = TimeUnit.SECONDS.convert(differenceTime, TimeUnit.NANOSECONDS);




				LocalDateTime dateTime = LocalDateTime.ofEpochSecond(differenceTime, 0, ZoneOffset.UTC);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("DD,MM,YYYY h:mm,a", Locale.GERMAN);
				String finaleBearbeitungszeit = dateTime.format(DateTimeFormatter.ISO_DATE);

				Map<String, Object> variables = new HashMap<>();
				variables.put("differenceTimeValue", differenceTime);

				log.info("die Bearbeitungszeit beträgt " + differenceTime);

				RestTemplate restTemplate = new RestTemplate();
				CamundaObject camundaObjectClient = new CamundaObject(orderTime,endeBearbeitungszeit,durchlaufzeit_nano,durchlaufzeit_ms,durchlaufzeit_sec);
				JSONObject camundaJsonObject = new JSONObject(camundaObjectClient);
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);

				log.info("cumundaObject" + camundaJsonObject.toString());

				final String url = "http://localhost:8093/camundaObject/monitoring";
				HttpEntity<String> request = new HttpEntity<String>(camundaJsonObject.toString(), headers);
				restTemplate.postForObject(url, request, String.class);
				externalTaskService.complete(externalTask, variables);

			} catch (Exception e) {
				log.error("Fehler: ", e);
				externalTaskService.handleBpmnError(externalTask, externalTask.getId(), "Something went wrong!" + e);
				return;
			}
		}).open();

		externalTaskClient.subscribe("Startzeit_festlegen").handler((externalTask, externalTaskService) -> {
				externalTaskService.complete(externalTask);
		}).open();
	}
}
