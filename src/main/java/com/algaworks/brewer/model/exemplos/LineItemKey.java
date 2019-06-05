package com.algaworks.brewer.model.exemplos;

import java.io.Serializable;

public class LineItemKey implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public Integer orderId;
	public int itemId;

	public LineItemKey() {
	}

	public LineItemKey(Integer orderId, int itemId) {
		this.orderId = orderId;
		this.itemId = itemId;
	}

	public boolean equals(Object otherOb) {
		if (this == otherOb) {
			return true;
		}
		if (!(otherOb instanceof LineItemKey)) {
			return false;
		}
		LineItemKey other = (LineItemKey) otherOb;
		return ((orderId == null ? other.orderId == null : orderId.equals(other.orderId)) && (itemId == other.itemId));
	}

	public int hashCode() {
		return ((orderId == null ? 0 : orderId.hashCode()) ^ ((int) itemId));
	}

	public String toString() {
		return "" + orderId + "-" + itemId;
	}

}
