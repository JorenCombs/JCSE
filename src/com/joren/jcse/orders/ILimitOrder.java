package com.joren.jcse.orders;

public interface ILimitOrder {
	/**
	 * Retrieves the quantity being offered/ordered.  Negative indicates sell, positive indicates buy.
	 * @return The quantity being ordered, as an int.
	 */
	public int getQuantity();
	
	/**
	 * Changes the quantity of the order
	 * @param quantity
	 */
	public void setQuantity(int newQuantity);
	
	/**
	 * Retrieves the price being ordered or offered at.
	 * @return The price, as an int.
	 */
	public int getPrice();

	/**
	 * Changes the price of the order
	 * @param price - The new price
	 */
	public void setPrice(int newPrice);
	
	/**
	 * Retrieves the time at which the order was placed
	 * @return The time of the order, in milliseconds
	 */
	public long getTimeStamp();
	
	/**
	 * Retrieves the ID of the order.
	 * @return String - the unique identifier that can be used to track this order.
	 */
	public String getId();
}
