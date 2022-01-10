package com.settlengine.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;

public class LoggingServiceImpl implements LoggingService {
	private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public void logRequest(HttpServletRequest request, Object body) {
		log.trace(() -> "Request. Headers:" + headers(Collections.list(request.getHeaderNames()).iterator(), request::getHeader) + "\nBody: " + body);
	}

	@Override
	public void logResponse(HttpServletRequest request, HttpServletResponse response, Object body) {
		log.trace(() -> "Response. Headers:" + headers(response.getHeaderNames().iterator(), response::getHeader) + "\nBody: " + body);
	}

	private String headers(Iterator<String> headerNames, Function<String, String> headerExtractor) {
		StringBuilder sb = new StringBuilder();
		headerNames.forEachRemaining(s -> {
			sb.append("\n");
			sb.append(s);
			sb.append(": ");
			sb.append(headerExtractor.apply(s));
		});
		return sb.toString();
	}
}
