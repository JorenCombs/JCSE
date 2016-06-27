package com.joren.jcse.ledger;

public abstract class LedgerUpdateListener {
	
	/**
	 * Notifies when a trade has occurred
	 * @param trade - The transaction that has taken place
	 */
	public abstract void notify(final IStockTradeUpdate trade);
}
