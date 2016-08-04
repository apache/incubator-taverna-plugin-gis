/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.apache.taverna.gis.client.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.taverna.gis.client.IGisClient;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;

import net.opengis.ows.x11.LanguageStringType;
import net.opengis.wps.x100.CapabilitiesDocument;
import net.opengis.wps.x100.ComplexDataCombinationType;
import net.opengis.wps.x100.ComplexDataCombinationsType;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessBriefType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType.DataInputs;
import net.opengis.wps.x100.WPSCapabilitiesType;

public class GisClientNorthImpl implements IGisClient {

	private Logger logger = Logger.getLogger(GisClientNorthImpl.class);
	
	private URI serviceURI = null;
	private WPSClientSession wpsClient;
	
	public GisClientNorthImpl(String serviceURL) {
		this.serviceURI = URI.create(serviceURL);
		wpsClient = WPSClientSession.getInstance();
		
		try {
			wpsClient.connect(serviceURI.toString());
		} catch (WPSClientException ex) {
			logger.error("Failed to connect to service: " + serviceURI, ex);
		}
		
	}
	
	@Override
	public String getServiceCapabilities(URI serviceURI) {

		CapabilitiesDocument capabilities = wpsClient.getWPSCaps(serviceURI.toString());

		LanguageStringType[] serviceAbstract = capabilities.getCapabilities().getServiceIdentification()
				.getTitleArray();
	
		if (serviceAbstract != null && serviceAbstract.length > 0)
			return serviceAbstract[0].getStringValue();
		else
			return null;
	}

	@Override
	public HashMap<String, Integer> getProcessInputPorts(String processID) {
		HashMap<String, Integer> inputPorts = new HashMap<String, Integer>();
		
		ProcessDescriptionType processDescription = null;
		
		try {
			processDescription = wpsClient.getProcessDescription(serviceURI.toString(), processID);
		} catch (IOException ex) {
			logger.error("Failed to list input ports for process: " + processID, ex);
		}

		if (processDescription==null)
			return inputPorts;
		
		DataInputs dataInputs = processDescription.getDataInputs();
		
		if (dataInputs == null)
			return inputPorts;
		
		InputDescriptionType[] inputList = dataInputs.getInputArray();

		for (InputDescriptionType input : inputList) {

			// if compareTo returns 1 then first value is greater than 1. it means that there is more than one occurrence therefore the depth is more than 0
			int depth = ((input.getMaxOccurs().compareTo(BigInteger.valueOf(1))==1) ? 1 : 0);
			
			inputPorts.put(input.getIdentifier().getStringValue(), depth);
		}
		
		return inputPorts;
		
	}

	@Override
	public HashMap<String, Integer> getProcessOutputPorts(String processID) {
		HashMap<String, Integer> outputPorts = new HashMap<String, Integer>();
		
		ProcessDescriptionType processDescription = null;
		
		try {
			processDescription = wpsClient.getProcessDescription(serviceURI.toString(), processID);
		} catch (IOException ex) {
			logger.error("Failed to list processe output port for process: " + processID, ex);
		}

		if (processDescription==null)
			return outputPorts;
		
		OutputDescriptionType[] outputList = processDescription.getProcessOutputs().getOutputArray();

		for (OutputDescriptionType output : outputList) {

			// TODO: Calculate output depth
			int depth = 0;
			
			outputPorts.put(output.getIdentifier().getStringValue(), depth);
		}
		
		return outputPorts;
	}
	
	public List<TypeDescriptor> getTaverna2InputPorts(String processID)
	{
        
		List<TypeDescriptor> inputPorts = new ArrayList<TypeDescriptor>();
		
		ProcessDescriptionType processDescription = null;
		
		try {
			processDescription = wpsClient.getProcessDescription(serviceURI.toString(), processID);
		} catch (IOException ex) {
			logger.error("Failed to get process description for process: " + processID, ex);
		}

		if (processDescription==null)
			return inputPorts;
		
		DataInputs dataInputs = processDescription.getDataInputs();
		
		if (dataInputs == null)
			return inputPorts;
					
		InputDescriptionType[] inputList = dataInputs.getInputArray();

		for (InputDescriptionType input : inputList) {
			TypeDescriptor myNewInputPort = new TypeDescriptor();
			
			myNewInputPort.setName(input.getIdentifier().getStringValue());
			myNewInputPort.setDepth(getInputPortDepth(input));
			myNewInputPort.setAllowLiteralValues(true);
			myNewInputPort.setHandledReferenceSchemes(null); // is not used in Taverna
			myNewInputPort.setTranslatedElementType(String.class);
			myNewInputPort.setRequired(input.getMinOccurs().compareTo(BigInteger.valueOf(1))>0?true:false);
			myNewInputPort.setSupportedFormats(getPortSupportedFormats(input));
			
			
			
			inputPorts.add(myNewInputPort);
		}
	
		return inputPorts;
	}

	/**
	 * @param input port
	 * @return List of supported formats
	 */
	private List<String> getPortSupportedFormats(InputDescriptionType inputPort)
	{
		List<String> supportedFormats = new ArrayList<String>();
		
		if (inputPort.getLiteralData() == null)
			return supportedFormats;
		
		if (inputPort.getComplexData()==null)
			return supportedFormats;
		else
		{
			
			inputPort.getBoundingBoxData().getSupported().getCRSArray();
			inputPort.getBoundingBoxData().getDefault().getCRS();
			
			
			ComplexDataCombinationsType complexDataType = inputPort.getComplexData().getSupported();
			inputPort.getComplexData().getDefault().getFormat();
			complexDataType.getFormatArray(0).getEncoding();
			complexDataType.getFormatArray(0).getMimeType();
			complexDataType.getFormatArray(0).getSchema();
			
			
		}
		
		
		 
		
		return supportedFormats;
	}
	
	/**
	 * @param inputPort
	 * @return
	 */
	private int getInputPortDepth(InputDescriptionType inputPort)
	{
		// The input has cardinality (Min/Max Occurs) of 1 when it returns 1 value and greater than 1  when it 
		// returns multiple values 
		// if compareTo returns 1 then first value (MaxOccurs) is greater than 1. it means that there is more than one occurrence 
		// therefore the depth is greater than 0
		int depth = ((inputPort.getMaxOccurs().compareTo(BigInteger.valueOf(1))==1) ? 1 : 0);
		
		return depth;
	}

	@Override
	public List<TypeDescriptor> getTaverna2OutputPorts(String processID) {
		List<TypeDescriptor> outputPorts = new ArrayList<TypeDescriptor>();

		ProcessDescriptionType processDescription = null;

		try {
			processDescription = wpsClient.getProcessDescription(serviceURI.toString(), processID);
		} catch (IOException ex) {
			logger.error("Failed to list output ports for process: " + processID, ex);
		}

		if (processDescription == null)
			return outputPorts;

		OutputDescriptionType[] outputList = processDescription.getProcessOutputs().getOutputArray();

		for (OutputDescriptionType output : outputList) {
			TypeDescriptor myNewOutputPort = new TypeDescriptor();

			myNewOutputPort.setName(output.getIdentifier().getStringValue());
			myNewOutputPort.setDepth(0); // output port depth is always 1
			
			outputPorts.add(myNewOutputPort);
		}

		return outputPorts;
	}

	@Override
	public List<String> getProcessList() {
		List<String> results = new ArrayList<String>();

		WPSCapabilitiesType wpsCapabilities = wpsClient.getWPSCaps(serviceURI.toString()).getCapabilities();
		
		ProcessBriefType[] processList = wpsCapabilities.getProcessOfferings().getProcessArray();
		
		for( ProcessBriefType process: processList)
		{
			results.add(process.getIdentifier().getStringValue());
		}
		
		return results;
		
	}

}
