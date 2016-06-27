package com.joren.jcse.gateway.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.joren.jcse.gateway.ITradingGateway;
import com.joren.jcse.ledger.IStockTradeUpdate;
import com.joren.jcse.ledger.LedgerUpdateListener;
import com.joren.jcse.matchingengine.MatchingEngine;

/**
 * Client gateway for communicating with the matching engine.  Most of the API
 * here just forwards directly to the engine, which itself passes on to the
 * order books.  The books themselves have been designed to be thread-safe,
 * so hopefully just a straight pass-through is okay...?
 * 
 * @author Joren
 *
 */
public class TradingGateway implements ITradingGateway {
	
	private MatchingEngine engine = MatchingEngine.getInstance();
	
	/** A queue of pending trade updates sent to us */
	private BlockingQueue<IStockTradeUpdate> tradeUpdates = new LinkedBlockingQueue<IStockTradeUpdate>();
	
	/** The orders we are subscribed to receive updates for */
	private Set<String> orders = Collections.synchronizedSet(new HashSet<String>());

	/** The name of the client (user-specified) */
	private String clientName = "John Doe";
	
	/**
	 * Instantiates a new trading gateway.
	 */
	public TradingGateway() {
		engine.getLedger().registerListener(new LedgerUpdateListener() {

			@Override
			public void notify(IStockTradeUpdate trade) {
				if (orders.contains(trade.getBuyOrderId())
						|| orders.contains(trade.getSellOrderId())) {
					
					tradeUpdates.add(trade);
				}
			}
		});
	}
	
	@Override
	public String order(String stock, int quantity, int price) {
		String id = engine.order(stock, quantity, price);
		if (id != null && !id.isEmpty()) {
			orders.add(id);
		}
		return id;
	}
	
	@Override
	public boolean cancel(String stock, String orderId) {
		boolean success = engine.cancel(stock, orderId);
		if (success) {
			orders.remove(orderId);
		}
		return success;
	}
	
	@Override
	public boolean modify(String stock, String orderId, int quantity, int price) {
		return engine.modify(stock, orderId, quantity, price);
	}
	
	@Override
	public List<IStockTradeUpdate> getOrderUpdates() {
		ArrayList<IStockTradeUpdate> trades = new ArrayList<IStockTradeUpdate>();
		tradeUpdates.drainTo(trades);
		return trades;
	}

	@Override
	public String getClientName() {
		return clientName;
	}
	
	@Override
	public void setClientName(String name) {
	}
}
