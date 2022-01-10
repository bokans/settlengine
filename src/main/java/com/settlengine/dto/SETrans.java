package com.settlengine.dto;

import java.time.LocalDateTime;

@DbTable(nm = "CS_TRANS")
public class SETrans extends SEEntity {
	private static final long serialVersionUID = -8059407840410370695L;

	@Override
	public long getId() {
		return id;
	}

	@DbField(nm = "trans_id")
	public long id;

	@DbField(nm = "doc_id")
	public long docId;

	@DbField(nm = "priority")
	public int priority;

	@DbField(nm = "settlement_date")
	public LocalDateTime sttlmDt;

	@DbField(nm = "state")
	public String state;

	@DbField(nm = "is_queueable")
	public char isQueueable;

	@DbField(nm = "details")
	public String details;
}
