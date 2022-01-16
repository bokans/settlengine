package com.settlengine.settlement;

import com.settlengine.dto.SEBalance;
import com.settlengine.dto.SENetto;
import com.settlengine.dto.SETrans;
import com.settlengine.repository.BalanceRepository;
import com.settlengine.repository.SequenceRepository;
import io.swagger.models.auth.In;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;

public class SetlEngineCore implements  Runnable {
	public SetlEngineCore(BalanceRepository balanceRepository, SequenceRepository sequenceRepository, SEParams seParams) {
		this.balanceRepository = balanceRepository;
		this.sequenceRepository = sequenceRepository;
		this.seParams = seParams;
	}

	private final BalanceRepository balanceRepository;
	private final SequenceRepository sequenceRepository;
	private final SEParams seParams;

	public record QueueData(SETrans trans, List<SENetto> lNetto, CompletableFuture<SetlEngine.TransStatus> result) {}

	private final ArrayBlockingQueue<QueueData> execQueue = new ArrayBlockingQueue<>(1000);

	private final TreeMap<Integer, TreeSet<SETrans>> queue = new TreeMap<>();

	public CompletableFuture<SetlEngine.TransStatus> execQueueAdd(SETrans trans, List<SENetto> lNetto) {
		final CompletableFuture<SetlEngine.TransStatus> result  = new CompletableFuture<>();
		execQueue.add(new QueueData(trans, lNetto, result));
		return result;
	}

	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				final QueueData qd = execQueue.take();

				qd.result.complete(executeNettoDebit(qd.trans, qd.lNetto));
			} catch (InterruptedException e) {
				return;
			} catch (Exception e) {
			}
		}
	}

	private SetlEngine.TransStatus executeNettoDebit(SETrans trans, List<SENetto> lNetto) {
		List<SENetto> lNettoFail = null;
		int i = 0;

		try {
			lNetto.stream().forEach(n -> saveBalance(n));

			final Set<SETrans> queue = getQueue(trans.priority);

			for (; i < lNetto.size(); i++) {
				SENetto n = lNetto.get(i);

				if (queue != null && n.dbtAmt.compareTo(n.cdtAmt) >= 0 && findQueuedAcc(queue, n))
					lNettoFail.add(n);
				else if (!updateBalance(n)) {
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
			else
				return SetlEngine.TransStatus.SETL;
		}

		if (trans.isQueueable == 'Y') {
			addQueued(trans);
			return SetlEngine.TransStatus.QUED;
		}

		return SetlEngine.TransStatus.REJT;
	}

	private boolean findQueuedAcc(Set<SETrans> set, SENetto n) {
		//set.forEach(t -> t.lNetto);
		return false;
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

	private Set<SETrans> getQueue(Integer priority) {
		final Map.Entry<Integer, TreeSet<SETrans>> entry = queue.floorEntry(priority);
		return entry != null ? entry.getValue() : Collections.emptySet();
	}

	private void addQueued(SETrans trans) {
		final TreeSet<SETrans> set = queue.computeIfAbsent(trans.priority, k -> new TreeSet<>());
		set.add(trans);
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
