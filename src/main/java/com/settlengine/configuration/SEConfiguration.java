package com.settlengine.configuration;

import com.settlengine.repository.BalanceRepository;
import com.settlengine.repository.SequenceRepository;
import com.settlengine.settlement.SEParams;
import com.settlengine.settlement.SetlEngine;
import com.settlengine.web.LoggingService;
import com.settlengine.web.LoggingServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SEConfiguration {
	@Bean
	public LoggingService loggingService() {
		return new LoggingServiceImpl();
	}

	@Bean
	public SEParams seParams() {
		return new SEParams();
	}

	@Bean
	public SequenceRepository sequenceRepository() {
		return new SequenceRepository();
	}

	@Bean
	public BalanceRepository balanceRepository(SequenceRepository sequenceRepository, SEParams seParams) {
		return new BalanceRepository(sequenceRepository, seParams);
	}

	@Bean
	public SetlEngine setlEngine(BalanceRepository balanceRepository, SequenceRepository sequenceRepository, SEParams seParams) {
		return new SetlEngine(balanceRepository, sequenceRepository, seParams);
	}
}
