package com.joren.jcse.gateway;

public interface IStockOrderUpdate extends IStockUpdate {
	/**
	 * Enumerates the update types that can take place:
	 * 
	 * <li><b>ORDER</b> - A new order has been placed
	 * 
	 * <li><b>CANCEL</b> - An order has been cancelled
	 * 
	 * <li><b>MODIFY</b> - An order has been modified
	 */
	enum UpdateType {
		/** ORDER - A new order has been placed */
		NEW,
		/** CANCEL - An order has been cancelled */
		CANCEL,
		/** MODIFY - An order has been modified */
		MODIFY;
	}
	
	/**
	 * Retrieves the quantity being offered/ordered.  Negative indicates sell, positive indicates buy.
	 * @return The quantity being ordered, as an int.
	 */
	public int getQuantity();
	
	/**
	 * Retrieves the price being ordered or offered at.
	 * @return The price, as an int.
	 */
	public int getPrice();

	/**
	 * Retrieves the ID of the order.
	 * @return String - the unique identifier that can be used to track this order.
	 */
	public String getId();

	/**
	 * Retrieves the {@link UpdateType type of update} taking place.
	 * @return
	 */
	UpdateType getUpdateType();
}
