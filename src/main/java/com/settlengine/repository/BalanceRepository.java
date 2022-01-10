package com.settlengine.repository;

import com.settlengine.dto.SEBalance;
import com.settlengine.settlement.SEParams;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class BalanceRepository {
	public BalanceRepository(SequenceRepository sequenceRepository, SEParams seParams) {
		this.sequenceRepository = sequenceRepository;
		this.seParams = seParams;
	}

	private final SequenceRepository sequenceRepository;
	private final SEParams seParams;

	public static class BalanceKey {
		public BalanceKey(final String cust, final String acct, final String balTp, final String instr) {
			this.cust = cust;
			this.acct = acct;
			this.balTp = balTp;
			this.instr = instr;
			this.hashcode = Objects.hash(cust, acct, balTp, instr);
		}

		public static BalanceKey of(final String cust, final String acct, final String balTp, final String instr) {
			return new BalanceKey(cust, acct, balTp, instr);
		}

		final String cust;
		final String acct;
		final String balTp;
		final String instr;

		private final int hashcode;

		@Override
		public boolean equals(final Object o) {
			return o instanceof BalanceKey key && Objects.equals(this.cust, key.cust) && Objects.equals(this.acct, key.acct)
					&& Objects.equals(this.balTp, key.balTp) && Objects.equals(this.instr, key.instr);
		}

		@Override
		public int hashCode() {
			return hashcode;
		}

		@Override
		public String toString() {
			return "BalanceKey{" +
					"cust='" + cust + '\'' +
					", acct='" + acct + '\'' +
					", balTp='" + balTp + '\'' +
					", instr='" + instr + '\'' +
					", hashcode=" + hashcode +
					'}';
		}
	}


	final Map<BalanceKey, SEBalance> map = new ConcurrentHashMap<>();

	public SEBalance find(final BalanceKey key) {
		final SEBalance bal = map.get(key);
		if (bal != null)
			return bal;

		synchronized (map) {
			return map.computeIfAbsent(key, k -> {
				final SEBalance b = new SEBalance();
				b.id = sequenceRepository.getBalanceId();
				b.cust = key.cust;
				b.acct = key.acct;
				b.balTp = key.balTp;
				b.instr = key.instr;

				b.activity = 'N';
				b.type = "SA";
				b.inAmt = BigDecimal.ZERO;
				b.dbtAmt = BigDecimal.ZERO;
				b.cdtAmt = BigDecimal.ZERO;

				b.dt = b.openDt = seParams.getOperday();

				return b;
			});
		}
	}
}
