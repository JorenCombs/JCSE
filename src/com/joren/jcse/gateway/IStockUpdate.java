package com.joren.jcse.gateway;

public interface IStockUpdate {

	/**
	 * Retrieves the time at which the action took place
	 * @return The time of the order, in milliseconds
	 */
	public long getTimeStamp();
	
	/**
	 * Retrieves the stock this update is for
	 * @return The stock symbol
	 */
	String getStock();

}
