package com.joren.jcse.ledger;

public interface ILedger {
	/**
	 * Records a transaction
	 * @param trade - The trade being recorded.
	 */
	public void record(final IStockTradeUpdate trade);
	
	/**
	 * Registers a ledger update listener that will receive notifications
	 * for all trades
	 * @param listener
	 */
	public void registerListener(LedgerUpdateListener listener);
	
	/**
	 * Unregisters a ledger update listener that will no longer receive notifications
	 * @param listener
	 */
	public void unregisterListener(LedgerUpdateListener listener);
}
