package com.joren.jcse.gateway;

import java.util.List;

public interface IMarketDataGateway {
	
	/**
	 * Retrieves a list of updates that have occurred for subscribed stocks
	 * since the last time this method was called.
	 * @return A list of updates.
	 */
	public List<IStockUpdate> getStockUpdates();
	
	/**
	 * Subscribes to receive updates on trades so they can be retrieved using
	 * {@link #getStockUpdates()}
	 */
	public void subscribe(String stock);
	
	/**
	 * Unsubscribes from receiving updates on trades
	 */
	public void unsubscribe(String stock);
}
