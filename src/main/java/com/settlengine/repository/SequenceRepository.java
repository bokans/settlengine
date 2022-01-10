package com.settlengine.repository;

import java.util.concurrent.atomic.AtomicLong;

public class SequenceRepository {
	private final AtomicLong docId = new AtomicLong();

	public long getDocId() {
		return docId.incrementAndGet();
	}

	private final AtomicLong balId = new AtomicLong();

	public long getBalanceId() {
		return balId.incrementAndGet();
	}

	private final AtomicLong transId = new AtomicLong();

	public long getTransId() {
		return transId.incrementAndGet();
	}

	private final AtomicLong entryId = new AtomicLong();

	public long getEntryId() {
		return entryId.incrementAndGet();
	}

	private final AtomicLong nettoId = new AtomicLong();

	public long getNettoId() {
		return nettoId.incrementAndGet();
	}
}
