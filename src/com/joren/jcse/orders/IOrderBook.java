package com.joren.jcse.orders;

public interface IOrderBook {
	
	/**
	 * Will cause the book to balance itself, matching buys and sells.
	 * 
	 * @return True if the book is balanced as of the end of this call
	 * False if the balance could not be run (may happen if the last call
	 * to balance() is still ongoing)
	 * 
	 */
	public boolean balance();
	
	/**
	 * Returns the stock that this order book is tracking buys/sells for
	 * @return The trading symbol for the stock
	 */
	public String getStock();
	
	/**
	 * Makes an order.
	 * @param quantity - The number of shares being ordered.  Positive
	 * indicates buy, negative indicates sell.  Must not be zero.
	 * @param price - The price per share.  Must be positive.
	 * @return String - an order ID that can be used to track the
	 * order, or null if not successful
	 */
	String order(int quantity, int price);
	
	/**
	 * Cancels an order.  It is possible the order may be partially or
	 * completely fulfilled prior to being cancelled.
	 * @param orderId - The order to be removed
	 * @return True if able to cancel the order prior to being completely
	 * fulfilled.  This means this method will still return true if the order
	 * has been partially but not completely fulfilled prior to being
	 * cancelled.
	 */
	public boolean cancel(String orderId);
	
	/**
	 * Modifies an existing order to the provided parameters.  It is possible
	 * the order may be partially or completely fulfilled prior to being
	 * modified.
	 * @param orderId - The order to be modified
	 * @param quantity - The number of shares being ordered.  Positive
	 * indicates buy, negative indicates sell.  Must not be zero.
	 * @param price - The price per share.  Must be positive.
	 * @return True if able to modify the order prior to being completely
	 * fulfilled.  This means this method will still return true if the order
	 * has been partially but not completely fulfilled prior to being modified.
	 */
	public boolean modify(String order, int quantity, int price);

}
