package com.joren.jcse.gateway.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.joren.jcse.gateway.IMarketDataGateway;
import com.joren.jcse.gateway.IStockOrderUpdate;
import com.joren.jcse.gateway.IStockUpdate;
import com.joren.jcse.ledger.IStockTradeUpdate;
import com.joren.jcse.ledger.LedgerUpdateListener;
import com.joren.jcse.matchingengine.MatchingEngine;
import com.joren.jcse.matchingengine.OrderUpdateListener;

public class MarketDataGateway implements IMarketDataGateway {
	
	MatchingEngine engine = MatchingEngine.getInstance();

	/**
	 * A queue of pending stock updates sent to us
	 */
	private BlockingQueue<IStockUpdate> stockUpdates = new LinkedBlockingQueue<IStockUpdate>();

	private Set<String> stocks = Collections.synchronizedSet(new HashSet<String>());
	
	MarketDataGateway() {
		engine.getLedger().registerListener(new LedgerUpdateListener() {

			@Override
			public void notify(IStockTradeUpdate trade) {
				if (stocks.contains(trade.getStock())) {
					stockUpdates.add(trade);
				}
			}
		});
		
		engine.registerListener(new OrderUpdateListener() {

			@Override
			public void notify(IStockOrderUpdate update) {
				if (stocks.contains(update.getStock())) {
					stockUpdates.add(update);
				}
			}
			
		});
	}
	@Override
	public List<IStockUpdate> getStockUpdates() {
		ArrayList<IStockUpdate> updates = new ArrayList<IStockUpdate>();
		stockUpdates.drainTo(updates);
		return updates;
	}

	@Override
	public void subscribe(String stock) {
		stocks.add(stock);
	}

	@Override
	public void unsubscribe(String stock) {
		stocks.remove(stock);
	}

}
