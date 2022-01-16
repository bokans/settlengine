package com.settlengine.settlement;

import com.settlengine.dto.SEBalance;
import com.settlengine.dto.SEEntry;
import com.settlengine.dto.SENetto;
import com.settlengine.dto.SETrans;
import com.settlengine.repository.BalanceRepository;
import com.settlengine.repository.SequenceRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SetlEngine {
	public SetlEngine(BalanceRepository balanceRepository, SequenceRepository sequenceRepository,
	                  SetlEngineCore setlEngineCore, SEParams seParams) {
		this.balanceRepository = balanceRepository;
		this.sequenceRepository = sequenceRepository;
		this.setlEngineCore = setlEngineCore;
		this.seParams = seParams;
	}

	private final BalanceRepository balanceRepository;
	private final SequenceRepository sequenceRepository;
	private final SetlEngineCore setlEngineCore;
	private final SEParams seParams;

	public enum TransStatus {SETL, REJT, QUED}

	public List<SENetto> createNetto(final SETrans trans, final List<SEEntry> lEntry) {
		Map<BalanceRepository.BalanceKey, SENetto> map = new HashMap<>();

		// TODO remove ?
		int countDebitNetto = 0;
		int countCreditNetto = 0;

		for (final SEEntry e : lEntry) {
			final BalanceRepository.BalanceKey key = BalanceRepository.BalanceKey.of(e.dbtrAcctCust, e.dbtrAcct, e.dbtrBalTp, e.instr);
			final SENetto debitNetto = map.computeIfAbsent(key, k -> {
				final SENetto nn = new SENetto();
				nn.id = sequenceRepository.getNettoId();

				nn.transId = e.transId;
				nn.docId = e.docId;
				nn.acctCust = e.dbtrAcctCust;
				nn.acct = e.dbtrAcct;
				nn.acctBalTp = e.dbtrBalTp;
				nn.instr = e.instr;
				nn.dbtAmt = e.amount;
				nn.cdtAmt = BigDecimal.ZERO;

				nn.balance = balanceRepository.find(key);
				nn.balId = nn.balance.id;

				return nn;
			});
			debitNetto.dbtAmt = debitNetto.dbtAmt.add(e.amount);
			countDebitNetto++;

			final SENetto creditNetto = map.computeIfAbsent(key, k -> {
				final SENetto nn = new SENetto();
				nn.id = sequenceRepository.getNettoId();

				nn.transId = e.transId;
				nn.docId = e.docId;
				nn.acctCust = e.dbtrAcctCust;
				nn.acct = e.dbtrAcct;
				nn.acctBalTp = e.dbtrBalTp;
				nn.instr = e.instr;
				nn.dbtAmt = e.amount;
				nn.cdtAmt = BigDecimal.ZERO;

				nn.balance = balanceRepository.find(key);
				nn.balId = nn.balance.id;

				return nn;
			});
			creditNetto.cdtAmt = creditNetto.cdtAmt.add(e.amount);
			countCreditNetto++;
		}

		return new ArrayList<>(map.values());
	}

	// TODO async execution
	public TransStatus execute(SETrans trans, List<SENetto> lNetto) {
		try {
			final TransStatus transStatus = setlEngineCore.execQueueAdd(trans, lNetto).get();
			trans.state = transStatus.name();
			return transStatus;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
