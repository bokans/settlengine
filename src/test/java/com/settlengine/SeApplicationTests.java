package com.settlengine;

import com.settlengine.dto.SEBalance;
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

	private SEBalance createPassiveAccount(final String cust, final String acct, final String balTp, final String instr, Integer inAmt) {
		final SEBalance balance = balanceRepository.find(BalanceRepository.BalanceKey.of(cust, acct, balTp, instr));
		balance.activity = 'P';
		balance.inAmt = balance.cdtAmt = BigDecimal.valueOf(inAmt);

		return balance;
	}

	@Test
	void settleActiveAccount() {
		SETrans trans = new SETrans();
		trans.id = sequenceRepository.getTransId();
		trans.docId = sequenceRepository.getDocId();
		trans.priority = 100;
		trans.sttlmDt = LocalDate.now().atStartOfDay();
		trans.state = "N";
		trans.isQueueable = 'Y';

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

		assertEquals(SetlEngine.TransStatus.SETL, setlEngine.execute(trans, lNetto));
	}

	@Test
	void settlePassiveAccount() {
		final SEBalance balC1A2 = createPassiveAccount("BD", "CL001A02", "AVAI", "USD", 7);

		SETrans trans10 = new SETrans();
		trans10.id = sequenceRepository.getTransId();
		trans10.docId = sequenceRepository.getDocId();
		trans10.priority = 100;
		trans10.sttlmDt = LocalDate.now().atStartOfDay();
		trans10.state = "N";
		trans10.isQueueable = 'N';

		SEEntry entry10 = new SEEntry();
		entry10.id = sequenceRepository.getEntryId();
		entry10.transId = trans10.id;
		entry10.trans = trans10;
		entry10.docId = trans10.docId;
		entry10.dbtrAcctCust = "BD";
		entry10.dbtrAcct = balC1A2.acct;
		entry10.dbtrBalTp = "AVAI";
		entry10.cdtrAcctCust = "BD";
		entry10.cdtrAcct = "CL002A01";
		entry10.cdtrBalTp = "AVAI";
		entry10.instr = "USD";
		entry10.amount = BigDecimal.TEN;

		final List<SENetto> lNetto10 = setlEngine.createNetto(trans10, Arrays.asList(entry10));

		assertEquals(SetlEngine.TransStatus.REJT, setlEngine.execute(trans10, lNetto10));


		SETrans trans5 = new SETrans();
		trans5.id = sequenceRepository.getTransId();
		trans5.docId = sequenceRepository.getDocId();
		trans5.priority = 100;
		trans5.sttlmDt = LocalDate.now().atStartOfDay();
		trans5.state = "N";
		trans5.isQueueable = 'Y';

		SEEntry entry5 = new SEEntry();
		entry5.id = sequenceRepository.getEntryId();
		entry5.transId = trans5.id;
		entry5.trans = trans5;
		entry5.docId = trans5.docId;
		entry5.dbtrAcctCust = "BD";
		entry5.dbtrAcct = "CL001A02";
		entry5.dbtrBalTp = "AVAI";
		entry5.cdtrAcctCust = "BD";
		entry5.cdtrAcct = "CL002A01";
		entry5.cdtrBalTp = "AVAI";
		entry5.instr = "USD";
		entry5.amount = BigDecimal.valueOf(5);

		final List<SENetto> lNetto5 = setlEngine.createNetto(trans5, Arrays.asList(entry5));

		assertEquals(SetlEngine.TransStatus.SETL, setlEngine.execute(trans5, lNetto5));
	}

	@Test
	void settlePassiveAccount2() {
		final SEBalance balC1A3 = createPassiveAccount("BD", "CL001A03", "AVAI", "USD", 10);

		SETrans trans10 = new SETrans();
		trans10.id = sequenceRepository.getTransId();
		trans10.docId = sequenceRepository.getDocId();
		trans10.priority = 100;
		trans10.sttlmDt = LocalDate.now().atStartOfDay();
		trans10.state = "N";
		trans10.isQueueable = 'Y';

		SEEntry entry10 = new SEEntry();
		entry10.id = sequenceRepository.getEntryId();
		entry10.transId = trans10.id;
		entry10.trans = trans10;
		entry10.docId = trans10.docId;
		entry10.dbtrAcctCust = "BD";
		entry10.dbtrAcct = "CL001A03";
		entry10.dbtrBalTp = "AVAI";
		entry10.cdtrAcctCust = "BD";
		entry10.cdtrAcct = "CL002A01";
		entry10.cdtrBalTp = "AVAI";
		entry10.instr = "USD";
		entry10.amount = BigDecimal.TEN;

		final List<SENetto> lNetto10 = setlEngine.createNetto(trans10, Arrays.asList(entry10));

		assertEquals(SetlEngine.TransStatus.SETL, setlEngine.execute(trans10, lNetto10));


		SETrans trans5 = new SETrans();
		trans5.id = sequenceRepository.getTransId();
		trans5.docId = sequenceRepository.getDocId();
		trans5.priority = 100;
		trans5.sttlmDt = LocalDate.now().atStartOfDay();
		trans5.state = "N";
		trans5.isQueueable = 'Y';

		SEEntry entry5 = new SEEntry();
		entry5.id = sequenceRepository.getEntryId();
		entry5.transId = trans5.id;
		entry5.trans = trans5;
		entry5.docId = trans5.docId;
		entry5.dbtrAcctCust = "BD";
		entry5.dbtrAcct = balC1A3.acct;
		entry5.dbtrBalTp = "AVAI";
		entry5.cdtrAcctCust = "BD";
		entry5.cdtrAcct = "CL002A01";
		entry5.cdtrBalTp = "AVAI";
		entry5.instr = "USD";
		entry5.amount = BigDecimal.valueOf(5);

		final List<SENetto> lNetto5 = setlEngine.createNetto(trans5, Arrays.asList(entry5));

		assertEquals(SetlEngine.TransStatus.QUED, setlEngine.execute(trans5, lNetto5));
	}
}
