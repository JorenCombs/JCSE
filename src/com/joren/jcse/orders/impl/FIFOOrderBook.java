package com.joren.jcse.orders.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.joren.jcse.gateway.IStockOrderUpdate.UpdateType;
import com.joren.jcse.ledger.ILedger;
import com.joren.jcse.ledger.impl.StockTradeUpdate;
import com.joren.jcse.matchingengine.IMatchingEngine;
import com.joren.jcse.orders.ILimitOrder;
import com.joren.jcse.orders.IOrderBook;

public class FIFOOrderBook implements IOrderBook {

	/** FIFO comparator.  Prioritizes orders first by lowest price, then by time received. */
	static FIFOSellComparator sellComparator = new FIFOSellComparator();

	/** FIFO queue for processing sell orders */
	PriorityBlockingQueue<ILimitOrder> sellQueue = new PriorityBlockingQueue<ILimitOrder>(11, sellComparator);
	
	/** FIFO comparator.  Prioritizes orders first by highest price, then by time received. */
	static FIFOBuyComparator buyComparator = new FIFOBuyComparator();

	/** FIFO queue for processing buy orders */
	PriorityBlockingQueue<ILimitOrder> buyQueue = new PriorityBlockingQueue<ILimitOrder>(11, buyComparator);
	
	Map<String, ILimitOrder> idToOrderMap = Collections.synchronizedMap(new HashMap<String, ILimitOrder>());
	
	/** @see #getStock() */
	String stock = "";

	AtomicBoolean balanceInProgress = new AtomicBoolean(false);

	/** Reference to the ledger that will record trades when this book is {@link #balance() balanced}*/
	private ILedger ledger;

	/** Reference to the matching engine*/
	private IMatchingEngine engine;

	/** Hidden constructor to prevent class being initialized w/o a ledger or symbol */
	@SuppressWarnings("unused")
	private FIFOOrderBook() {
	}
	
	/** 
	 * Initializes a new order book
	 * @param stockSymbol - The stock this order book is handling
	 * @param ledger - The ledger that should be updated when trades take place
	 */
	public FIFOOrderBook(String stockSymbol, IMatchingEngine engine) {
		if (stockSymbol == null || stockSymbol.isEmpty()) {
			throw new IllegalArgumentException("Non-empty stock symbol is required");
		}
		if (engine == null) {
			throw new IllegalArgumentException("Non-null engine is required");
		}
		this.stock = stockSymbol;
		this.engine = engine;
		this.ledger = engine.getLedger();
	}

	@Override
	public boolean balance() {
		boolean completed = false;
		if (balanceInProgress.compareAndSet(false, true)) {
		
			try {
				// Only one thing should modify this map at a time
				synchronized (idToOrderMap) {
					while (!sellQueue.isEmpty() && !buyQueue.isEmpty()
							&& sellQueue.peek().getPrice() <= buyQueue.peek().getPrice()) {
						ILimitOrder sell = sellQueue.peek();
						ILimitOrder buy = buyQueue.peek();
						int difference = buy.getQuantity() + sell.getQuantity();
						if (difference == 0) {
							processTrade(buy.getId(), sell.getId(), 0 - sell.getQuantity(), sell.getPrice());
							idToOrderMap.remove(buyQueue.remove().getId());
							idToOrderMap.remove(sellQueue.remove().getId());
						} else if (difference < 0) {
							processTrade(buy.getId(), sell.getId(), buy.getQuantity(), sell.getPrice());
							idToOrderMap.remove(buyQueue.remove().getId());
							sell.setQuantity(difference);
						} else {
							processTrade(buy.getId(), sell.getId(), 0 - sell.getQuantity(), sell.getPrice());
							idToOrderMap.remove(sellQueue.remove().getId());
							buy.setQuantity(difference);
						}
						completed = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				balanceInProgress.compareAndSet(true, false);
			}
		}
		return completed;
	}
	
	/** Adds a completed trade to the ledger
	 * 
	 * @param price - The price the trade was executed at
	 * @param quantity - The quantity traded
	 */
	private void processTrade(String buyOrderId, String sellOrderId, int quantity, int price) {
		ledger.record(new StockTradeUpdate(stock, buyOrderId, sellOrderId, quantity, price, System.currentTimeMillis()));
	}
	
	private void processOrder(UpdateType updateType, String orderId, int quantity, int price) {
		engine.record(updateType, this.stock, orderId, quantity, price);
	}

	@Override
	public String getStock() {
		return stock;
	}
	
	/**
	 * Changes the stock this order is for
	 * @param stock
	 */
	public void setStock(String newStock) {
		this.stock = newStock;
	}

	@Override
	public String order(int quantity, int price) {
		ILimitOrder order = order(quantity, price, UUID.randomUUID().toString());
		processOrder(UpdateType.NEW, order.getId(), quantity, price);
		return order.getId();
	}

	/**
	 * Private ordering method that takes a UUID to make it easier to replace an existing order.
	 * Does not notify.
	 * @param quantity - The quantity being ordered
	 * @param price - The price per share being ordered
	 * @param orderId - The ID that should be used to create the order
	 */
	private ILimitOrder order(int quantity, int price, String orderId) {
		if (price <= 0) {
			throw new IllegalArgumentException("Price must be greater than 0");
		} else if (quantity == 0) {
			throw new IllegalArgumentException("Quantity must be non-zero");
		} else {
			
			synchronized (idToOrderMap) {
				LimitOrder order = new LimitOrder(quantity, price, orderId);
				if (quantity > 0) {
					buyQueue.add(order);
				} else {
					sellQueue.add(order);
				}
				this.idToOrderMap.put(orderId, order);
				return order;
			}
		}
	}

	@Override
	public boolean cancel(String orderId) {
		return cancel(orderId, true);
	}
	
	/**
	 * Private canceling method that allows a toggle to avoid sending notifications.
	 * @param orderId - The ID that should be used to create the order
	 * @param notify - True if notifications should be sent, false if not.
	 */
	private boolean cancel(String orderId, boolean notify) {
		synchronized (idToOrderMap) {
			ILimitOrder order = idToOrderMap.get(orderId);
			if (order != null) {
				if (order.getQuantity() > 0) {
					buyQueue.remove(order);
				} else {
					sellQueue.remove(order);
				}
				idToOrderMap.remove(orderId);
				if (notify) {
					processOrder(UpdateType.CANCEL, order.getId(), order.getQuantity(), order.getPrice());
				}
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean modify(String orderId, int quantity, int price) {
		if (cancel(orderId, false)) {
			// Does not preserve timestamp, so modifying an existing order puts you at the back of the line.
			order(quantity, price, orderId);
			processOrder(UpdateType.MODIFY, orderId, quantity, price);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Comparator to prioritize orders first by lowest price, then by earliest time.
	 * @author Joren
	 */
	static class FIFOSellComparator implements Comparator<ILimitOrder> {
		@Override
		public int compare(ILimitOrder order, ILimitOrder otherOrder) {
			if (order.getPrice() == otherOrder.getPrice()) {
				return Long.compare(order.getTimeStamp(), otherOrder.getTimeStamp());
			}
			return order.getPrice() - otherOrder.getPrice();

		}
	}

	/**
	 * Comparator to prioritize orders first by highest price, then by earliest time.
	 * @author Joren
	 */
	static class FIFOBuyComparator implements Comparator<ILimitOrder> {
		@Override
		public int compare(ILimitOrder order, ILimitOrder otherOrder) {
			if (order.getPrice() == otherOrder.getPrice()) {
				return Long.compare(order.getTimeStamp(), otherOrder.getTimeStamp());
			}
			return otherOrder.getPrice() - order.getPrice();

		}
	}
}
