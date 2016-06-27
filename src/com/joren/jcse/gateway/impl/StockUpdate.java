package com.joren.jcse.gateway.impl;

import com.joren.jcse.gateway.IStockUpdate;

public class StockUpdate implements IStockUpdate {

	/** @see #getStock() */
	private String stock = "";
	
	/** @see #getTimeStamp() */
	private long timeStamp;
	
	/**
	 * @param stock - The symbol of the stock this update is for.
	 * @param timeStamp - The timestamp for when the action took place 
	 */
	public StockUpdate(String stock, long timeStamp) {
		this.stock = stock;
		this.timeStamp = timeStamp;
	}
	
	@Override
	public String getStock() {
		return this.stock;
	}

	@Override
	public long getTimeStamp() {
		return timeStamp;
	}
}
