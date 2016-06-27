package com.joren.jcse.matchingengine;

import com.joren.jcse.gateway.IStockOrderUpdate.UpdateType;
import com.joren.jcse.ledger.ILedger;

public interface IMatchingEngine {
	
	/**
	 * Makes an order.
	 * @param stock - The trading symbol of the stock being ordered.
	 * @param quantity - The number of shares being ordered.  Positive
	 * indicates buy, negative indicates sell.  Must not be zero.
	 * @param price - The price per share.  Must be positive.
	 * @return String - an order ID that can be used to track the
	 * order, or null if not successful
	 */
	public String order(String stock, int quantity, int price);
	
	/**
	 * Cancels an order.  It is possible the order may be partially or
	 * completely fulfilled prior to being cancelled.
	 * @param stock - The trading symbol of the stock being ordered.
	 * @param orderId - The order to be removed
	 * @return True if able to cancel the order prior to being completely
	 * fulfilled.  This means this method will still return true if the order
	 * has been partially but not completely fulfilled prior to being
	 * cancelled.
	 */
	public boolean cancel(String stock, String orderId);
	
	/**
	 * Modifies an existing order to the provided parameters.  It is possible
	 * the order may be partially or completely fulfilled prior to being
	 * modified.
	 * @param stock - The trading symbol of the stock being ordered.
	 * @param orderId - The order to be modified
	 * @param quantity - The number of shares being ordered.  Positive
	 * indicates buy, negative indicates sell.  Must not be zero.
	 * @param price - The price per share.  Must be positive.
	 * @return True if able to modify the order prior to being completely
	 * fulfilled.  This means this method will still return true if the order
	 * has been partially but not completely fulfilled prior to being modified.
	 */
	public boolean modify(String stock, String orderId, int quantity, int price);

	/**
	 * Retrieves the ledger used by the matching engine to record transactions
	 * @return The ledger
	 */
	public ILedger getLedger();
	
	/**
	 * Registers an order update listener that will receive notifications
	 * for all trades
	 * @param listener
	 */
	public void registerListener(OrderUpdateListener listener);
	
	/**
	 * Unregisters an order update listener that will no longer receive notifications
	 * @param listener
	 */
	public void unregisterListener(OrderUpdateListener listener);

	/**
	 * Records an update to an order
	 * @param updateType - Which {@link UpdateType update type} this is
	 * @param stock - The trading symbol of the stock being ordered.
	 * @param orderId - The order to be modified
	 * @param quantity - The number of shares being ordered.  Positive
	 * indicates buy, negative indicates sell.  Must not be zero.
	 * @param price - The price per share.  Must be positive.
	 */
	public void record(UpdateType updateType, String stock, String orderId, int quantity, int price);
}
