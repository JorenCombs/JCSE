package com.joren.jcse.gateway.impl;

import com.joren.jcse.gateway.IStockOrderUpdate;
import com.joren.jcse.gateway.IStockOrderUpdate.UpdateType;

public class StockOrderUpdate extends StockUpdate implements IStockOrderUpdate {

	private IStockOrderUpdate.UpdateType updateType = null;
	
	/** @see #getPrice() */
	private int price = 0;
	
	/** @see #getQuantity() */
	private int quantity = 0;
	
	/** @see #getId() */
	private String id = "";
	
	/**
	 * @param updateType - Which {@link UpdateType update type} this is
	 * @param stock - The symbol of the stock this order is for.
	 * @param orderId - The order ID.
	 * @param quantity - The current quantity being ordered
	 * @param price - The current price of the order
	 * @param timeStamp - The timestamp for when the action took place 
	 */
	public StockOrderUpdate(IStockOrderUpdate.UpdateType updateType, String stock, String orderId, int quantity, int price, long timeStamp) {
		super(stock, timeStamp);
		this.updateType = updateType;
		this.quantity = quantity;
		this.price = price;
		this.id = orderId;
	}
	
	@Override
	public UpdateType getUpdateType() {
		return updateType;
	}

	@Override
	public int getQuantity() {
		return this.quantity;
	}

	@Override
	public int getPrice() {
		return this.price;
	}

	@Override
	public String getId() {
		return this.id;
	}
	
	@Override
	public String toString() {
		return "[" + getTimeStamp() + "] Order event " + this.updateType + " received for " + quantity + " shares of " + getStock() + " at " + price + " per share.  Order ID: " + id;
	}
}
