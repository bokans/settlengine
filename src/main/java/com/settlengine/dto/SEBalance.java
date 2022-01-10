package com.settlengine.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

@DbTable(nm = "CS_BALANCE")
public class SEBalance extends SEEntity {
	private static final long serialVersionUID = 6084296470000105651L;

	@Override
	public long getId() {
		return id;
	}

	@DbField(nm = "bal_id")
	public long id;

	@DbField(nm = "cust")
	public String cust;

	@DbField(nm = "acct")
	public String acct;

	@DbField(nm = "instr")
	public String instr;

	@DbField(nm = "bal_tp")
	public String balTp;

	/**
	 * P - passive
	 * A - active
	 * N - active+passive
	 */
	@DbField(nm = "activity")
	public char activity;

	@DbField(nm = "type")
	public String type;

	/**
	 * Income amount
	 */
	@DbField(nm = "in_amt")
	public BigDecimal inAmt;

	@DbField(nm = "dbt_amt")
	/** Debit amount */
	public BigDecimal dbtAmt;

	@DbField(nm = "cdt_amt")
	/** Credit amount */
	public BigDecimal cdtAmt;

	@DbField(nm = "dt")
	public LocalDate dt;

	@DbField(nm = "open_dt")
	public LocalDate openDt;

	@DbField(nm = "close_dt")
	public LocalDate closeDt;
}