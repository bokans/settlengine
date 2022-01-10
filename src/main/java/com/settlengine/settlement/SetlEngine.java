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

public class SetlEngine {
	public SetlEngine(BalanceRepository balanceRepository, SequenceRepository sequenceRepository, SEParams seParams) {
		this.balanceRepository = balanceRepository;
		this.sequenceRepository = sequenceRepository;
		this.seParams = seParams;
	}

	private final BalanceRepository balanceRepository;
	private final SequenceRepository sequenceRepository;
	private final SEParams seParams;

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
	public boolean execute(SETrans trans, List<SENetto> lNetto) {
		List<SENetto> lNettoFail = executeNettoDebit(lNetto);

		if (lNettoFail != null) {
			trans.state = "X";
			return false;
		}

		trans.state = "F";
		return false;
	}

	private List<SENetto> executeNettoDebit(List<SENetto> lNetto) {
		List<SENetto> lNettoFail = null;
		int i = 0;

		try {
			lNetto.stream().forEach(n -> saveBalance(n));

			for (; i < lNetto.size(); i++) {
				SENetto n = lNetto.get(i);

				if (!updateBalance(n)) {
					if (lNettoFail == null)
						lNettoFail = new ArrayList<>();

					lNettoFail.add(n);
				}
			}
		} finally {
			if (lNettoFail != null)
				for (int r = 0; r < i; r++) {
					SENetto n = lNetto.get(r);
					revertBalance(n);
				}
		}

		return lNettoFail;
	}

	private void saveBalance(SENetto n) {
		final SEBalance balance = n.balance;
		n.balInDtOld = balance.dt;
		n.balInAmtOld = balance.inAmt;
		n.balDbtAmtOld = balance.dbtAmt;
		n.balCdtAmtOld = balance.cdtAmt;

		final LocalDate operday = seParams.getOperday();
		if (!balance.dt.equals(operday)) {
			balance.dt = operday;
			balance.inAmt = balance.cdtAmt.subtract(n.balDbtAmt);

			if (balance.inAmt.signum() >= 0) {
				balance.dbtAmt = BigDecimal.ZERO;
				balance.cdtAmt = balance.inAmt;
			} else {
				balance.dbtAmt = balance.inAmt;
				balance.cdtAmt = BigDecimal.ZERO;
				;
			}
		}
	}

	// TODO get limits
	private BigDecimal getLimits(SENetto n) {
		return BigDecimal.ZERO;
	}

	// TODO get overdrafts
	private BigDecimal getOverdrafts(SENetto n) {
		return BigDecimal.ZERO;
	}

	// TODO check balances
	private boolean updateBalance(SENetto n) {
		final SEBalance balance = n.balance;
		balance.dbtAmt = balance.dbtAmt.add(n.dbtAmt);
		balance.cdtAmt = balance.cdtAmt.add(n.cdtAmt);

		return balance.activity != 'P'
				|| balance.cdtAmt.subtract(balance.dbtAmt).subtract(getLimits(n)).add(getOverdrafts(n)).signum() >= 0;
	}

	private void revertBalance(SENetto n) {
		final SEBalance balance = n.balance;
		balance.dt = n.balInDtOld;
		balance.inAmt = n.balInAmtOld;
		balance.dbtAmt = n.balDbtAmtOld;
		balance.cdtAmt = n.balCdtAmtOld;
	}
}
