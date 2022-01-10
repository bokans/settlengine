package com.settlengine.settlement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;

class SetlEngineTest {
	private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());

	@Test
	void createNetto() {
//		SETrans trans = new SETrans();
//		final SequenceRepository seq = SequenceRepository.get();
//		trans.id = seq.getTransId();
//		trans.docId = seq.getDocId();
//		trans.priority = 100;
//		trans.sttlmDt = LocalDate.now().atStartOfDay();
//		trans.state = "N";
//		trans.isQueueable = 'Y';
////		trans.details = null;
//
//		SEEntry entry = new SEEntry();
//		entry.id = seq.getEntryId();
//		entry.transId = trans.id;
//		entry.trans = trans;
//		entry.docId = trans.docId;
//		entry.dbtrAcctCust = "BD";
//		entry.dbtrAcct = "CL001A01";
//		entry.dbtrBalTp = "AVAI";
//		entry.cdtrAcctCust = "BD";
//		entry.cdtrAcct = "CL002A01";
//		entry.cdtrBalTp = "AVAI";
//		entry.instr = "USD";
//		entry.amount = BigDecimal.TEN;
//
//		final List<SENetto> lNetto = setlEngine.createNetto(trans, Arrays.asList(entry));
//
//		setlEngine.execute(trans, lNetto);
//
//		assertEquals("F", trans.state);
	}
}
