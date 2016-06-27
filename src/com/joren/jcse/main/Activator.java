package com.joren.jcse.main;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.joren.jcse.matchingengine.MatchingEngine;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		MatchingEngine.getInstance(); // Gentlemen, start your engines!
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
}
