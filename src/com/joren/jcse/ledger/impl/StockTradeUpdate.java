package com.joren.jcse.ledger.impl;

import com.joren.jcse.gateway.impl.StockUpdate;
import com.joren.jcse.ledger.IStockTradeUpdate;

/**
 * Implementation of {@link IStockTradeUpdate}.
 * @author Joren
 *
 */
public class StockTradeUpdate extends StockUpdate implements IStockTradeUpdate {
	/** @see #getPrice() */
	private int price = 0;
	
	/** @see #getQuantity() */
	private int quantity = 0;
	
	/** @see #getBuyOrderId() */
	private String buyOrderId = "";
	
	/** @see #getSellOrderId() */
	private String sellOrderId = "";

	/**
	 * Initializes a new transaction record.
	 * @param stock - The stock being traded
	 * @param buyOrderId - The order ID for which shares were purchased
	 * @param sellOrderId - The order ID for which shares were sold
	 * @param quantity - The quantity being traded
	 * @param price - The per-share price
	 * @param timeStamp - The timestamp for when the action took place 
	 */
	public StockTradeUpdate(String stock, String buyOrderId, String sellOrderId, int quantity, int price, long timeStamp) {
		super(stock, timeStamp);
		this.buyOrderId = buyOrderId;
		this.sellOrderId = sellOrderId;
		this.quantity = quantity;
		this.price = price;
	}

	@Override
	public int getQuantity() {
		return quantity;
	}

	@Override
	public int getPrice() {
		return price;
	}

	@Override
	public String getBuyOrderId() {
		return buyOrderId;
	}

	@Override
	public String getSellOrderId() {
		return sellOrderId;
	}
	
	@Override
	public String toString() {
		return "[" + getTimeStamp() + "] Trade executed for " + quantity + " shares of " + getStock() + " at " + price + " per share.  Buy order: " + buyOrderId + "  Sell order: " + sellOrderId;
	}
}
