package com.joren.jcse.orders.impl;

import com.joren.jcse.orders.ILimitOrder;

public class LimitOrder implements ILimitOrder {

	/** @see #getPrice() */
	private int price = 0;
	
	/** @see #getQuantity() */
	private int quantity = 0;
	
	/** @see #getTimeStamp() */
	private long timeStamp = 0;
	
	/** @see #getId() */
	private String id = "";
	
	/**
	 * @param price - The price shares are being ordered at.  Must be positive.
	 * @param quantity - The number of shares being ordered.  Positive
	 * indicates buy, negative indicates sell.  Must be non-zero.
	 * @param id - The unique ID to be given to this order.
	 */
	LimitOrder(int quantity, int price, String id) {
		if (price < 1) {
			throw new IllegalArgumentException("Price needs to be greater than 0");
		}
		if (quantity == 0) {
			throw new IllegalArgumentException("Quantity must be non-zero");
		}
		if (id == null || id.trim().isEmpty()) {
			throw new IllegalArgumentException("Order ID must not be empty");
		}
		this.price = price;
		this.quantity = quantity;
		this.timeStamp = System.currentTimeMillis();
		this.id = id;
	}
	
	@Override
	public int getQuantity() {
		return quantity;
	}
	
	@Override
	public void setQuantity(int newQuantity) {
		this.quantity = newQuantity;
	}

	@Override
	public int getPrice() {
		return price;
	}
	
	@Override
	public void setPrice(int newPrice) {
		this.price = newPrice;
	}
	
	@Override
	public long getTimeStamp() {
		return this.timeStamp;
	}

	@Override
	public String getId() {
		return id;
	}
}
