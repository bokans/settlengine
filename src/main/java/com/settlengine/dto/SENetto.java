package com.settlengine.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

@DbTable(nm = "CS_NETTO")
public class SENetto extends SEEntity {
	private static final long serialVersionUID = -5459200045491280650L;

	@Override
	public long getId() {
		return id;
	}

	@DbField(nm = "netto_id")
	public long id;

	@DbField(nm = "trans_id")
	public long transId;

	@DbField(nm = "doc_id")
	public long docId;

	@DbField(nm = "bal_id")
	public long balId;
	public transient SEBalance balance;

	@DbField(nm = "acct_cust")
	public String acctCust;

	@DbField(nm = "acct")
	public String acct;

	@DbField(nm = "acct_bal_tp")
	public String acctBalTp;

	@DbField(nm = "instr")
	public String instr;

	@DbField(nm = "is_conditional")
	public boolean isNotConditional;

	@DbField(nm = "dbt_amt")
	public BigDecimal dbtAmt;

	@DbField(nm = "cdt_amt")
	public BigDecimal cdtAmt;

	@DbField(nm = "bal_dbt_amt")
	public BigDecimal balDbtAmt;

	@DbField(nm = "bal_cdt_amt")
	public BigDecimal balCdtAmt;

	@DbField(nm = "bal_dbt_amt_old")
	public BigDecimal balDbtAmtOld;

	@DbField(nm = "bal_cdt_amt_old")
	public BigDecimal balCdtAmtOld;

	@DbField(nm = "bal_in_amt_old")
	public BigDecimal balInAmtOld;

	@DbField(nm = "bal_in_dt_old")
	public LocalDate balInDtOld;
}
