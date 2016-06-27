package com.joren.jcse.matchingengine;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.joren.jcse.gateway.IStockOrderUpdate;
import com.joren.jcse.gateway.impl.StockOrderUpdate;
import com.joren.jcse.ledger.ILedger;
import com.joren.jcse.ledger.impl.Ledger;
import com.joren.jcse.orders.IOrderBook;
import com.joren.jcse.orders.impl.FIFOOrderBook;

public class MatchingEngine implements IMatchingEngine {
	private static MatchingEngine instance = null;
	
	/**
	 * A list of all the order books.
	 */
	private Map<String, IOrderBook> orderBooks = Collections.synchronizedMap(new HashMap<String, IOrderBook>());
	
	/**
	 * A ledger to track trades
	 */
	private ILedger ledger = new Ledger();
	
	/**
	 * @see #registerListener(OrderUpdateListener)
	 * @see #unregisterListener(OrderUpdateListener)
	 */
	private Set<OrderUpdateListener> listeners = Collections.synchronizedSet(new HashSet<OrderUpdateListener>());
	
	/** Hidden constructor */
	private MatchingEngine() {
		registerListener(new OrderUpdateListener() {

			@Override
			public void notify(IStockOrderUpdate update) {
				System.out.println(update);
			}
			
		});
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				synchronized (orderBooks) {
					for (IOrderBook orderBook : orderBooks.values()) {
						orderBook.balance();
					}
				}
			}
			
		}, 0, 1000);
	}
	
	public static MatchingEngine getInstance() {
		if (instance == null) {
			instance = new MatchingEngine();
		}
		
		return instance;
	}
	
	@Override
	public String order(String stock, int quantity, int price) {
		if (!orderBooks.containsKey(stock.toUpperCase())) {
			orderBooks.put(stock.toUpperCase(), new FIFOOrderBook(stock, this));
		}
		IOrderBook orderBook = orderBooks.get(stock.toUpperCase());
		if (orderBook != null) {
			return orderBook.order(quantity, price);
		}
		return null;
	}
	
	@Override
	public boolean cancel(String stock, String orderId) {
		IOrderBook orderBook = orderBooks.get(stock.toUpperCase());
		if (orderBook != null) {
			return orderBook.cancel(orderId);
		}
		return false;
	}
	
	@Override
	public boolean modify(String stock, String orderId, int quantity, int price) {
		IOrderBook orderBook = orderBooks.get(stock.toUpperCase());
		if (orderBook != null) {
			return orderBook.modify(orderId, quantity, price);
		}
		return false;
	}
	
	public void record(IStockOrderUpdate.UpdateType updateType, String stock, String orderId, int quantity, int price) {
		IStockOrderUpdate update = new StockOrderUpdate(updateType, stock, orderId, quantity, price, System.currentTimeMillis());
		synchronized(listeners) {
			for (OrderUpdateListener listener : listeners) {
				listener.notify(update);
			}
		}
	}

	@Override
	public ILedger getLedger() {
		return ledger;
	}

	@Override
	public void registerListener(OrderUpdateListener listener) {
		listeners.add(listener);
	}

	@Override
	public void unregisterListener(OrderUpdateListener listener) {
		listeners.remove(listener);
	}
}
