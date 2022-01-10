package com.settlengine.dto;

import java.io.Serializable;
import java.time.Instant;

public abstract class SEEntity implements Serializable, Comparable<SEEntity> {
	public abstract long getId();

	public Instant getRowDate() {
		return rdate;
	}

	public long getRowRev() {
		return rrev;
	}

	@DbField(nm = "row_date")
	public Instant rdate;

	@DbField(nm = "row_rev")
	public long rrev;

	@Override
	public boolean equals(Object o) {
		return this == o || (o instanceof SEEntity entity) && this.getId() == entity.getId()
				&& this.getRowRev() == entity.getRowRev() && this.getClass().equals(o.getClass());
	}

	@Override
	public int hashCode() {
		return (int) ((getClass().hashCode() << 5) + Long.hashCode(getId()));
	}

	@Override
	public int compareTo(final SEEntity o) {
		return Long.compare(getId(), o.getId());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + '{' + getId() + '}';
	}
}
