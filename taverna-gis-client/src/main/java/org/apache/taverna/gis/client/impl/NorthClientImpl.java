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
import java.util.HashMap;

import org.apache.taverna.gis.client.IGisClient;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;

import net.opengis.ows.x11.LanguageStringType;
import net.opengis.wps.x100.CapabilitiesDocument;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;

public class NorthClientImpl implements IGisClient {

	@Override
	public String GetServiceCapabilities(URI serviceURI) {
		WPSClientSession wpsClient = WPSClientSession.getInstance();

		try {
			wpsClient.connect(serviceURI.toString());
		} catch (WPSClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CapabilitiesDocument capabilities = wpsClient.getWPSCaps(serviceURI.toString());

		LanguageStringType[] serviceAbstract = capabilities.getCapabilities().getServiceIdentification()
				.getTitleArray();
		//
		// ProcessBriefType[] processList = capabilities.getCapabilities()
		// .getProcessOfferings().getProcessArray();
		//
		// for (ProcessBriefType process : processList) {
		// System.out.println(process.getIdentifier().getStringValue());
		// }
		// return capabilities;
		if (serviceAbstract != null && serviceAbstract.length > 0)
			return serviceAbstract[0].getStringValue();
		else
			return null;
	}

	@Override
	public HashMap<String, Integer> GetProcessInputPorts(URI serviceURI, String processID) {
		HashMap<String, Integer> inputPorts = new HashMap<String, Integer>();
		
		WPSClientSession wpsClient = WPSClientSession.getInstance();

		ProcessDescriptionType processDescription = null;
		
		try {
			processDescription = wpsClient.getProcessDescription(serviceURI.toString(), processID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (processDescription==null)
			return null;
		
		InputDescriptionType[] inputList = processDescription.getDataInputs().getInputArray();

		for (InputDescriptionType input : inputList) {

			// if compareTo returns 1 then first value is greater than 1. it means that there is more than one occurrence therefore the depth is more than 0
			int depth = ((input.getMaxOccurs().compareTo(BigInteger.valueOf(1))==1) ? 1 : 0);
			
			inputPorts.put(input.getIdentifier().getStringValue(), depth);
		}
		
		return inputPorts;
		
	}

	@Override
	public HashMap<String, Integer> GetProcessOutputPorts(URI serviceURI, String processID) {
		HashMap<String, Integer> outputPorts = new HashMap<String, Integer>();
		
		WPSClientSession wpsClient = WPSClientSession.getInstance();

		ProcessDescriptionType processDescription = null;
		
		try {
			processDescription = wpsClient.getProcessDescription(serviceURI.toString(), processID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (processDescription==null)
			return null;
		
		OutputDescriptionType[] outputList = processDescription.getProcessOutputs().getOutputArray();

		for (OutputDescriptionType output : outputList) {

			// TODO: Calculate output depth
			int depth = 0;
			
			outputPorts.put(output.getIdentifier().getStringValue(), depth);
		}
		
		return outputPorts;
	}

}
