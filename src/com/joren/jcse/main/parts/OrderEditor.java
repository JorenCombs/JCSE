package com.joren.jcse.main.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.joren.jcse.gateway.ITradingGateway;
import com.joren.jcse.gateway.impl.TradingGateway;

public class OrderEditor {

	private Label stockLabel, priceLabel, quantityLabel;
	private Text stockText, priceText, quantityText;
	private TableViewer orders;
	private Button placeOrder, modifyOrder, cancelOrder;
	private ArrayList<String> orderIds = new ArrayList<String>();
	private Map<String, String> orderIdToStockMap = new HashMap<String, String>();
	private ITradingGateway tradingGateway = new TradingGateway();

	@Inject
	private MDirtyable dirty;

	@PostConstruct
	public void createComposite(Composite parent) {
		parent.setLayout(new GridLayout(2, false));

		stockLabel = new Label(parent, SWT.NONE);
		stockLabel.setText("Stock: ");
		
		stockText = new Text(parent, SWT.BORDER);
		stockText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		quantityLabel = new Label(parent, SWT.NONE);
		quantityLabel.setText("Quantity: ");
		
		quantityText = new Text(parent, SWT.BORDER);
		quantityText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		priceLabel = new Label(parent, SWT.NONE);
		priceLabel.setText("Price: ");
		
		priceText = new Text(parent, SWT.BORDER);
		priceText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		orders = new TableViewer(parent);

		orders.setContentProvider(ArrayContentProvider.getInstance());;
		orders.setInput(orderIds);
		orders.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		orders.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection structuredSel = (IStructuredSelection) event.getSelection();
					String orderId = structuredSel.getFirstElement().toString();
					String stockSymbol = orderIdToStockMap.get(orderId);
					stockText.setText(stockSymbol);
					priceText.setText("");
					quantityText.setText("");
				}
			}
		});
		
		placeOrder = new Button(parent, SWT.NONE);
		placeOrder.setText("Place Order");
		placeOrder.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				try {
					String orderId = tradingGateway.order(stockText.getText(), Integer.parseInt(quantityText.getText()), Integer.parseInt(priceText.getText()));

					orderIds.add(orderId);					
					orderIdToStockMap.put(orderId, stockText.getText().toUpperCase());

					orders.refresh();
				} catch (NumberFormatException e) {
					//Eat the exception... mmm... delicious
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//No-op
			}
		});

		modifyOrder = new Button(parent, SWT.NONE);
		modifyOrder.setText("Modify Order");
		modifyOrder.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				try {
					tradingGateway.modify(stockText.getText(), orders.getStructuredSelection().getFirstElement().toString(), Integer.parseInt(quantityText.getText()), Integer.parseInt(priceText.getText()));
				} catch (NumberFormatException e) {
					//Eat the exception... mmm... delicious
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//No-op
			}
		});

		cancelOrder = new Button(parent, SWT.NONE);
		cancelOrder.setText("Cancel Order");
		cancelOrder.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				String orderId = orders.getStructuredSelection().getFirstElement().toString();
				tradingGateway.cancel(stockText.getText(), orderId);
				orderIds.remove(orderId);
				orderIdToStockMap.remove(orderId);
				orders.refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//No-op
			}
		});
}

	@Focus
	public void setFocus() {
		stockText.setFocus();
	}

	@Persist
	public void save() {
		dirty.setDirty(false);
	}
}