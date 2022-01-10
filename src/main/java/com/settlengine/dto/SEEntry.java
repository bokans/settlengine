package com.settlengine.dto;

import java.math.BigDecimal;

@DbTable(nm = "CS_ENTRY")
public class SEEntry extends SEEntity {
	private static final long serialVersionUID = 2932615097149668779L;

	@Override
	public long getId() {
		return id;
	}

	@DbField(nm = "entry_id")
	public long id;

	@DbField(nm = "trans_id")
	public long transId;
	public transient SETrans trans;

	@DbField(nm = "doc_id")
	public long docId;

	/**
	 * debitor custodian
	 */
	@DbField(nm = "dbtr_acct_cust")
	public String dbtrAcctCust;

	/**
	 * debitor account
	 */
	@DbField(nm = "dbtr_acc")
	public String dbtrAcct;

	/**
	 * debitor balance type
	 */
	@DbField(nm = "dbtr_bal_tp")
	public String dbtrBalTp;

	/**
	 * creditor custodian
	 */
	@DbField(nm = "cdtr_acct_cust")
	public String cdtrAcctCust;

	/**
	 * creditor account
	 */
	@DbField(nm = "cdtr_acct")
	public String cdtrAcct;

	/**
	 * creditor balance type
	 */
	@DbField(nm = "cdtr_bal_tp")
	public String cdtrBalTp;

	/**
	 * instrument
	 */
	@DbField(nm = "instr")
	public String instr;

	@DbField(nm = "amount")
	public BigDecimal amount;
}
