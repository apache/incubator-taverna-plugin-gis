package org.apache.taverna.gis.client.impl;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.junit.Test;

public class NorthClientImplTest {

	@Test
	public void testGetServiceCapabilities() throws URISyntaxException {

		URI uri = new URI("http://localhost:8080/geoserver/ows");
	
		String result = new NorthClientImpl(uri.toASCIIString()).GetServiceCapabilities(uri);
		
		assertEquals("Incorrect service title",result, "Prototype GeoServer WPS");
		
	}
	
	@Test
	public void testGetProcessInputPorts() throws URISyntaxException {

		URI uri = new URI("http://localhost:8080/geoserver/ows");
		
		String processID = "gs:StringConcatWPS";
	
		HashMap<String, Integer> expectedInputResult = new HashMap<String, Integer>();
		expectedInputResult.put("name", 0);
		expectedInputResult.put("surname", 0);
		
		HashMap<String,Integer> result = new NorthClientImpl(uri.toASCIIString()).GetProcessInputPorts(processID);
		
		assertEquals("Incorrect input ports result",result, expectedInputResult);
		
	}
	
	@Test
	public void testGetProcessOutputPorts() throws URISyntaxException {

		URI uri = new URI("http://localhost:8080/geoserver/ows");
		
		String processID = "gs:StringConcatWPS";
	
		// check if the ports match
		HashMap<String, Integer> expectedOutputResult = new HashMap<String, Integer>();
		expectedOutputResult.put("result", 0);
		
		HashMap<String,Integer> result = new NorthClientImpl(uri.toASCIIString()).GetProcessOutputPorts(processID);
		
		// check if the ports do not match
		assertEquals("Incorrect output ports result",result, expectedOutputResult);
		
		expectedOutputResult.clear();
		expectedOutputResult.put("result1", 0);
		
		assertNotEquals("The output ports result should not match", result, expectedOutputResult);
		
	}
	

}
