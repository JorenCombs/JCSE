package com.joren.jcse.ledger;

import com.joren.jcse.gateway.IStockUpdate;

/**
 * A record of a trade that was executed - the stock involved, the quantity, the price, and the timestamp.
 * 
 * These records are intended to be read-only once constructed, as they represent a transaction that has already taken place.
 * @author Joren
 *
 */
public interface IStockTradeUpdate extends IStockUpdate{

	/**
	 * Retrieves the quantity traded.
	 * @return The quantity of the transaction, as an int.
	 */
	public int getQuantity();
	
	/**
	 * Retrieves the price the trade was executed at.
	 * @return The per-share price of the transaction, as an int.
	 */
	public int getPrice();
	
	/**
	 * Retrieves the order ID for which shares were purchased
	 * @return The order ID, as a String
	 */
	public String getBuyOrderId();

	/**
	 * Retrieves the order ID for which shares were sold
	 * @return The order ID, as a String
	 */
	public String getSellOrderId();
}
