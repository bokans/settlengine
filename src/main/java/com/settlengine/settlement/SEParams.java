package com.settlengine.settlement;

import java.time.LocalDate;

public class SEParams {
	// TODO manage operdays
	public LocalDate getOperday() {
		return LocalDate.now();
	}
}
