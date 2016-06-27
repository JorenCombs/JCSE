package com.joren.jcse.gateway;

import java.util.List;

import com.joren.jcse.ledger.IStockTradeUpdate;

/**
 * Client gateway for communicating with the matching engine.
 * 
 * @author Joren
 *
 */
public interface ITradingGateway {
	
	/**
	 * Requests that an order be added.
	 * @param stock - The trading symbol of the stock being ordered.
	 * @param quantity - The number of shares being ordered.  Positive indicates buy, negative indicates sell.  Must not be zero.
	 * @param price - The price per share.  Must be positive.
	 * @return String - an order ID that can be used to track the order, or null if not successful
	 */
	public String order(String stock, int quantity, int price);
	
	/**
	 * Requests that an order be cancelled.
	 * @param stock - The trading symbol of the stock being ordered.
	 * @param orderId - The order to be removed
	 * @return boolean - True if successful, false if not.
	 */
	public boolean cancel(String stock, String orderId);
	
	/**
	 * Requests that an order be modified.
	 * @param stock - The trading symbol of the stock being ordered.
	 * @param quantity - The number of shares being ordered.  Positive indicates buy, negative indicates sell.  Must not be zero.
	 * @param price - The price per share.  Must be positive.
	 * @param orderId - The order to be removed
	 * @return boolean - True if successful, false if not
	 */
	public boolean modify(String stock, String orderId, int quantity, int price);
	
	/**
	 * Retrieves a list of trades that have occurred against orders placed
	 * using this gateway since the last time this method was called.
	 * @return A list of trades.
	 */
	public List<IStockTradeUpdate> getOrderUpdates();
	
	/** Retrieves the name of the client */
	public String getClientName();

	/**
	 * Sets the name of the client.
	 * @param name - The client's name
	 */
	public void setClientName(String name);
}
