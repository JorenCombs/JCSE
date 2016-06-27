package com.joren.jcse.ledger.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.joren.jcse.ledger.ILedger;
import com.joren.jcse.ledger.IStockTradeUpdate;
import com.joren.jcse.ledger.LedgerUpdateListener;

public class Ledger implements ILedger {

	/**
	 * A list of all the trades that have taken place.  This is of course
	 * not serialized anywhere and will run out when memory does :)
	 */
	private List<IStockTradeUpdate> trades = Collections.synchronizedList(new ArrayList<IStockTradeUpdate>());
	
	/**
	 * A queue of pending trade updates.  The idea is to make the order book balance calls
	 * complete as quickly as possible (i.e. balance should not be responsible for sending
	 * out notifications or recording the transactions).  No particular action is taken if
	 * this queue gets too large :(
	 */
	private BlockingQueue<IStockTradeUpdate> tradeUpdates = new LinkedBlockingQueue<IStockTradeUpdate>();
	
	/**
	 * @see #registerListener(LedgerUpdateListener)
	 * @see #unregisterListener(LedgerUpdateListener)
	 */
	private Set<LedgerUpdateListener> listeners = Collections.synchronizedSet(new HashSet<LedgerUpdateListener>());
	
	public Ledger() {
		registerListener(new LedgerUpdateListener() {
			@Override
			public void notify(IStockTradeUpdate trade) {
				System.out.println(trade);
			}
		});
		Thread queueDrainer = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						IStockTradeUpdate trade = tradeUpdates.take();
						
						// Trying to avoid issues if listener added/removed during drain
						synchronized (listeners) {
							for (LedgerUpdateListener listener : listeners) {
								listener.notify(trade);
							}
						}
					} catch (InterruptedException e) {
						System.out.println("Ledger queue draining process shut down");
					}
					
				}
			}
			
		});
		listeners.add(new LedgerUpdateListener() {
			@Override
			public void notify(IStockTradeUpdate trade) {
				trades.add(trade);
			}
		});
		queueDrainer.start();
	}
	
	@Override
	public void record(IStockTradeUpdate trade) {
		tradeUpdates.add(trade);
	}

	@Override
	public void registerListener(LedgerUpdateListener listener) {
		listeners.add(listener);
	}

	@Override
	public void unregisterListener(LedgerUpdateListener listener) {
		listeners.remove(listener);
	}
	
}
