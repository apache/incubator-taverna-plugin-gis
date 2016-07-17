package org.apache.taverna.gis.client;

import org.apache.taverna.gis.client.impl.NorthClientImpl;

public class GisClientFactory {
	
	private static GisClientFactory instance = null;
	
	private GisClientFactory()
	{
		// private constructor
	}

	public static GisClientFactory getInstance()
	{
		if (instance == null)
			return new GisClientFactory();
		else
			return instance;
		
	}
	
	public IGisClient getGisClient(String serviceURL)
	{
		return new NorthClientImpl(serviceURL);
	}
	
}
