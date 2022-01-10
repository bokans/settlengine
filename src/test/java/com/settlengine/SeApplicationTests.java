package com.settlengine;

import com.settlengine.dto.SEEntry;
import com.settlengine.dto.SENetto;
import com.settlengine.dto.SETrans;
import com.settlengine.repository.BalanceRepository;
import com.settlengine.repository.SequenceRepository;
import com.settlengine.settlement.SetlEngine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
//@EmbeddedKafka(partitions = 1, ports = {9093})
class SeApplicationTests {
	@Autowired
	private BalanceRepository balanceRepository;

	@Autowired
	private SequenceRepository sequenceRepository;

	@Autowired
	private SetlEngine setlEngine;

	@Test
	void contextLoads() {
	}

	@Test
	void createNetto() {
		SETrans trans = new SETrans();
//		final SequenceRepository seq = SequenceRepository.get();
		trans.id = sequenceRepository.getTransId();
		trans.docId = sequenceRepository.getDocId();
		trans.priority = 100;
		trans.sttlmDt = LocalDate.now().atStartOfDay();
		trans.state = "N";
		trans.isQueueable = 'Y';
//		trans.details = null;

		SEEntry entry = new SEEntry();
		entry.id = sequenceRepository.getEntryId();
		entry.transId = trans.id;
		entry.trans = trans;
		entry.docId = trans.docId;
		entry.dbtrAcctCust = "BD";
		entry.dbtrAcct = "CL001A01";
		entry.dbtrBalTp = "AVAI";
		entry.cdtrAcctCust = "BD";
		entry.cdtrAcct = "CL002A01";
		entry.cdtrBalTp = "AVAI";
		entry.instr = "USD";
		entry.amount = BigDecimal.TEN;

		final List<SENetto> lNetto = setlEngine.createNetto(trans, Arrays.asList(entry));

		setlEngine.execute(trans, lNetto);

		assertEquals("F", trans.state);
	}

}
