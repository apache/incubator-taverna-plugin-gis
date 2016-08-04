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
import org.apache.taverna.gis.client.ComplexDataTypeDescriptor;
import org.apache.taverna.gis.client.IGisClient;
import org.apache.taverna.gis.client.PortDataType;
import org.apache.taverna.gis.client.PortTypeDescriptor;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;

import net.opengis.ows.x11.LanguageStringType;
import net.opengis.wps.x100.CRSsType;
import net.opengis.wps.x100.CapabilitiesDocument;
import net.opengis.wps.x100.ComplexDataCombinationType;
import net.opengis.wps.x100.ComplexDataCombinationsType;
import net.opengis.wps.x100.ComplexDataDescriptionType;
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
	
	public List<PortTypeDescriptor> getTaverna2InputPorts(String processID)
	{
        
		List<PortTypeDescriptor> inputPorts = new ArrayList<PortTypeDescriptor>();
		
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
			PortTypeDescriptor myNewInputPort = new PortTypeDescriptor();
			
			myNewInputPort.setName(input.getIdentifier().getStringValue());
			myNewInputPort.setDepth(getInputPortDepth(input));
			myNewInputPort.setAllowLiteralValues(true);
			myNewInputPort.setHandledReferenceSchemes(null); // is not used in Taverna
			myNewInputPort.setTranslatedElementType(String.class);
			myNewInputPort.setPortDataType(getPortDataType(input));
			myNewInputPort.setRequired(input.getMinOccurs().compareTo(BigInteger.valueOf(1))>0?true:false);
			myNewInputPort.setSupportedComplexFormats(getInputPortSupportedComplexFormats(input));
			myNewInputPort.setDefaultComplexFormat(getInputPortDefaultComplexFormat(input));
			myNewInputPort.setSupportedBoundingBoxFormats(getInputPortSupportedBoundingBoxFormats(input));
			myNewInputPort.setDefaultBoundingBoxFormat(getInputPortDefaultBoundingBoxFormats(input));
			
			inputPorts.add(myNewInputPort);
		}
	
		return inputPorts;
	}

	@Override
	public List<PortTypeDescriptor> getTaverna2OutputPorts(String processID) {
		List<PortTypeDescriptor> outputPorts = new ArrayList<PortTypeDescriptor>();

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
			PortTypeDescriptor myNewOutputPort = new PortTypeDescriptor();

			myNewOutputPort.setName(output.getIdentifier().getStringValue());
			myNewOutputPort.setDepth(0); // output port depth is always 1
			
			myNewOutputPort.setPortDataType(getPortDataType(output));
			myNewOutputPort.setRequired(false);
			myNewOutputPort.setSupportedComplexFormats(getOutputPortSupportedComplexFormats(output));
			myNewOutputPort.setDefaultComplexFormat(getOutputPortDefaultComplexFormat(output));
			myNewOutputPort.setSupportedBoundingBoxFormats(getOutputPortSupportedBoundingBoxFormats(output));
			myNewOutputPort.setDefaultBoundingBoxFormat(getOutputPortDefaultBoundingBoxFormats(output));
			
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

	private PortDataType getPortDataType(InputDescriptionType inputPort)
	{
		// set default dataType to literal data
		PortDataType portDataType = PortDataType.LITERAL_DATA;
		
		if (inputPort.getLiteralData()!=null)
			return portDataType;
		
		if(inputPort.getComplexData()!=null)
			return PortDataType.COMPLEX_DATA;
		
		if(inputPort.getBoundingBoxData()!=null)
			return PortDataType.BOUNDING_BOX_DATA;
		
		return portDataType;
		
	}
	
	private PortDataType getPortDataType(OutputDescriptionType outputPort)
	{
		// set default dataType to literal data
		PortDataType portDataType = PortDataType.LITERAL_DATA;
		
		if (outputPort.getLiteralOutput()!=null)
			return portDataType;
		
		if(outputPort.getComplexOutput()!=null)
			return PortDataType.COMPLEX_DATA;
		
		if(outputPort.getBoundingBoxOutput()!=null)
			return PortDataType.BOUNDING_BOX_DATA;
		
		return portDataType;
		
	}
	
	/**
	 * @param input port
	 * @return List of supported formats
	 */
	private List<ComplexDataTypeDescriptor> getInputPortSupportedComplexFormats(InputDescriptionType inputPort)
	{
		List<ComplexDataTypeDescriptor> supportedComplexFormats = new ArrayList<ComplexDataTypeDescriptor>();
		
		if (inputPort.getComplexData()==null)
			return supportedComplexFormats;
		else
		{
			ComplexDataCombinationsType complexDataSupportedTypes = inputPort.getComplexData().getSupported();
			
			if (complexDataSupportedTypes.sizeOfFormatArray()==0)
				return supportedComplexFormats;
			
			for(ComplexDataDescriptionType format : complexDataSupportedTypes.getFormatArray())
			{
				supportedComplexFormats.add(new ComplexDataTypeDescriptor(format.getMimeType(),format.getEncoding(), format.getSchema()));
			}
		}
		
		return supportedComplexFormats;
	}
	
	private ComplexDataTypeDescriptor getInputPortDefaultComplexFormat(InputDescriptionType inputPort)
	{
		ComplexDataTypeDescriptor defaultFormat = null;
		
		if (inputPort.getComplexData()==null)
			if (inputPort.getComplexData().getDefault()!=null)
				if(inputPort.getComplexData().getDefault().getFormat()!=null)
				{
					ComplexDataDescriptionType outputDefaultFormat = inputPort.getComplexData().getDefault().getFormat();
					defaultFormat = new ComplexDataTypeDescriptor(outputDefaultFormat.getMimeType(),outputDefaultFormat.getEncoding(),outputDefaultFormat.getSchema());
				}
					
		return defaultFormat;
		
	}
	
	private List<String> getInputPortSupportedBoundingBoxFormats(InputDescriptionType inputPort)
	{
		List<String> supportedBoundingBoxFormats = new ArrayList<String>();
		
		if (inputPort.getBoundingBoxData()==null)
			return supportedBoundingBoxFormats;
		else
		{
			CRSsType boundingBoxDataSupportedTypes = inputPort.getBoundingBoxData().getSupported();
			
			if (boundingBoxDataSupportedTypes.sizeOfCRSArray()==0)
				return supportedBoundingBoxFormats;
			
			for(String format : boundingBoxDataSupportedTypes.getCRSArray())
			{
				supportedBoundingBoxFormats.add(format);
			}
			
		}
		
		return supportedBoundingBoxFormats;

	}
	
	private String getInputPortDefaultBoundingBoxFormats(InputDescriptionType inputPort)
	{
		String defaultFormat = null;
		
		if (inputPort.getBoundingBoxData()==null)
			if (inputPort.getBoundingBoxData().getDefault()!=null)
				if(inputPort.getBoundingBoxData().getDefault().getCRS()!=null)
				{
					defaultFormat = inputPort.getBoundingBoxData().getDefault().getCRS();
				}
					
		return defaultFormat;
		
	}
	
	/**
	 * @param input port
	 * @return List of supported formats
	 */
	private List<ComplexDataTypeDescriptor> getOutputPortSupportedComplexFormats(OutputDescriptionType outputPort)
	{
		List<ComplexDataTypeDescriptor> supportedComplexFormats = new ArrayList<ComplexDataTypeDescriptor>();
		
		if (outputPort.getComplexOutput()==null)
			return supportedComplexFormats;
		else
		{
			ComplexDataCombinationsType complexDataSupportedTypes = outputPort.getComplexOutput().getSupported();
			
			if (complexDataSupportedTypes.sizeOfFormatArray()==0)
				return supportedComplexFormats;
			
			for(ComplexDataDescriptionType format : complexDataSupportedTypes.getFormatArray())
			{
				supportedComplexFormats.add(new ComplexDataTypeDescriptor(format.getMimeType(),format.getEncoding(), format.getSchema()));
			}
		}
		
		return supportedComplexFormats;
	}
	
	private ComplexDataTypeDescriptor getOutputPortDefaultComplexFormat(OutputDescriptionType outputPort)
	{
		ComplexDataTypeDescriptor defaultFormat = null;
		
		if (outputPort.getComplexOutput()==null)
			if (outputPort.getComplexOutput().getDefault()!=null)
				if(outputPort.getComplexOutput().getDefault().getFormat()!=null)
				{
					ComplexDataDescriptionType outputDefaultFormat = outputPort.getComplexOutput().getDefault().getFormat();
					defaultFormat = new ComplexDataTypeDescriptor(outputDefaultFormat.getMimeType(),outputDefaultFormat.getEncoding(),outputDefaultFormat.getSchema());
				}
					
		return defaultFormat;
		
	}
	
	private List<String> getOutputPortSupportedBoundingBoxFormats(OutputDescriptionType outputPort)
	{
		List<String> supportedBoundingBoxFormats = new ArrayList<String>();
		
		if (outputPort.getBoundingBoxOutput()==null)
			return supportedBoundingBoxFormats;
		else
		{
			CRSsType boundingBoxDataSupportedTypes = outputPort.getBoundingBoxOutput().getSupported();
			
			if (boundingBoxDataSupportedTypes.sizeOfCRSArray()==0)
				return supportedBoundingBoxFormats;
			
			for(String format : boundingBoxDataSupportedTypes.getCRSArray())
			{
				supportedBoundingBoxFormats.add(format);
			}
			
		}
		
		return supportedBoundingBoxFormats;

	}
	
	private String getOutputPortDefaultBoundingBoxFormats(OutputDescriptionType outputPort)
	{
		String defaultFormat = null;
		
		if (outputPort.getBoundingBoxOutput()==null)
			if (outputPort.getBoundingBoxOutput().getDefault()!=null)
				if(outputPort.getBoundingBoxOutput().getDefault().getCRS()!=null)
				{
					defaultFormat = outputPort.getBoundingBoxOutput().getDefault().getCRS();
				}
					
		return defaultFormat;
		
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
	
}
