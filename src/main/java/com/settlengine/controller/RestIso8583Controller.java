package com.settlengine.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestIso8583Controller {
	@RequestMapping(
			method = RequestMethod.POST,
			value = "/settlengine/v1/pushpayment/advice/receive/oct",
			consumes = {MediaType.APPLICATION_JSON_VALUE}
	)
	public void accept(String body) {
	}
}
