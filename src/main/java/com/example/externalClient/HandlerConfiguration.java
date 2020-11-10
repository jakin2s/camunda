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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.example.externalClient.Model.CamundaObject;

import org.camunda.bpm.client.backoff.ExponentialBackoffStrategy;

@Configuration
public class HandlerConfiguration {
	private final Logger logger = LoggerFactory.getLogger(HandlerConfiguration.class);

	@Bean
	public void createTopicSubscriberHandler() {
		ExponentialBackoffStrategy fetchTimer = new ExponentialBackoffStrategy(1000L,2, 1000L);
		int maxTasksToFetchWithinOnRequest = 1;
		
		
		ExternalTaskClient externalTaskClient = ExternalTaskClient
				.create()
				.baseUrl("http://localhost:8080/engine-rest")
				.maxTasks(3)
				.backoffStrategy(fetchTimer)
				.maxTasks(maxTasksToFetchWithinOnRequest)
				.build();
		

		externalTaskClient
		 .subscribe("Bearbeitungszeit_protokollieren")
		 .handler((externalTask, externalTaskService) -> {
	
			logger.info("Bearbeitungszeit wird überprüft");
			System.out.println("xxx");
			try {
				int moneyValue = externalTask.getVariable("moneyValue");
				boolean insureOrder = externalTask.getVariable("insureOrder");
				int orderSize = externalTask.getVariable("orderSize");
				int orderTime = externalTask.getVariable("orderTime");
				long endeBearbeitungszeit = System.currentTimeMillis()/1000;
				long differenceTime = endeBearbeitungszeit - orderTime;
				
				
				
				LocalDateTime dateTime = LocalDateTime.ofEpochSecond(differenceTime, 0, ZoneOffset.UTC);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("DD,MM,YYYY h:mm,a", Locale.GERMAN);
				String finaleBearbeitungszeit = dateTime.format(DateTimeFormatter.ISO_DATE);
				
				 Map<String, Object> variables = new HashMap<>();
				 variables.put("differenceTimeValue", differenceTime);
				 
					 logger.info("die Bearbeitungszeit beträgt " + differenceTime );
					 
					 RestTemplate restTemplate = new RestTemplate();
					 camundaJsonObject = new JSONObject();
					    personJsonObject.put("id", 1);
					    personJsonObject.put("name", "John");
					
					 HttpEntity<String> request = 
						      new HttpEntity<String>(CamundaObject.toString(), headers);
					 
					 String personResultAsJsonStr = 
						      restTemplate.postForObject(createPersonUrl, request, String.class);
						    JsonNode root = objectMapper.readTree(personResultAsJsonStr);
						    
							return restTemplate.getForObject("http://localhost:8085", HandlerConfiguration.class);
						
					 
					 externalTaskService.complete(externalTask, variables);
				 
				
			
			}catch(Exception e) {
				externalTaskService.handleBpmnError(externalTask, externalTask.getId(), "Something went wrong!" +e);
				return;
			}
		}).open();
		
		
	}
}
