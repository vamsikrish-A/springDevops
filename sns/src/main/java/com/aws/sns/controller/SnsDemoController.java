package com.aws.sns.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aws.sns.dto.MessageEvents;
import com.aws.sns.service.SnsDemoService;

@RestController
@RequestMapping("/sns")
public class SnsDemoController {
	
	@Autowired
	private SnsDemoService snsDemoService;
	
	@GetMapping("/subscribe")
	public String enableSubScription(@RequestParam String protocol, String endpoint) {
		return snsDemoService.autoSubscription(protocol, endpoint);
	}
	
	@PostMapping
	public String publishMessageToTopic(@RequestBody MessageEvents messageEvents) {
		return snsDemoService.publishMessage(messageEvents.getEvent());
	}

}
