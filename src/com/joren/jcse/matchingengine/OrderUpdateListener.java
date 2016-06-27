package com.joren.jcse.matchingengine;

import com.joren.jcse.gateway.IStockOrderUpdate;

public abstract class OrderUpdateListener {
	
	/**
	 * Notifies when an update to an order has occurred.
	 * @param update - The order update
	 */
	public abstract void notify(final IStockOrderUpdate update);
}
