package com.settlengine.configuration;

import com.settlengine.repository.BalanceRepository;
import com.settlengine.repository.SequenceRepository;
import com.settlengine.settlement.SEParams;
import com.settlengine.settlement.SetlEngine;
import com.settlengine.settlement.SetlEngineCore;
import com.settlengine.web.LoggingService;
import com.settlengine.web.LoggingServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
	public SetlEngineCore setlEngineCore(ApplicationContext context, BalanceRepository balanceRepository,
	                                     SequenceRepository sequenceRepository, SEParams seParams) {
		final SetlEngineCore setlEngineCore = new SetlEngineCore(balanceRepository, sequenceRepository, seParams);

		ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");
		taskExecutor.execute(setlEngineCore);

		return setlEngineCore;
	}

	@Bean
	public SetlEngine setlEngine(BalanceRepository balanceRepository, SequenceRepository sequenceRepository,
	                             SetlEngineCore setlEngineCore, SEParams seParams) {
		return new SetlEngine(balanceRepository, sequenceRepository, setlEngineCore, seParams);
	}
}
