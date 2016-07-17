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
package org.apache.taverna.gis;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;
import org.apache.taverna.gis.client.GisClientFactory;
import org.apache.taverna.gis.client.IGisClient;
import org.apache.taverna.gis.client.impl.NorthClientImpl;
import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.processor.activity.ActivityFactory;
import org.apache.taverna.workflowmodel.processor.activity.ActivityInputPort;
import org.apache.taverna.workflowmodel.processor.activity.ActivityOutputPort;

/**
 * Gis <code>ActivityFactory<code>.
 */
public class GisActivityFactory implements ActivityFactory {

	private static Logger logger = Logger.getLogger(GisActivityFactory.class);
	
	private Edits edits;

	@Override
	public GisActivity createActivity() {
		return new GisActivity();
	}

	@Override
	public URI getActivityType() {
		return URI.create(GisActivity.ACTIVITY_TYPE);
	}

	@Override
	public JsonNode getActivityConfigurationSchema() {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readTree(getClass().getResource("/schema.json"));
		} catch (IOException e) {
			return objectMapper.createObjectNode();
		}
	}

	@Override
	public Set<ActivityInputPort> getInputPorts(JsonNode configuration) {
		Map<String, ActivityInputPort> inputPorts = new HashMap<String, ActivityInputPort>();
		
		IGisClient gisServiceParser = new NorthClientImpl(configuration.get("service").textValue());
		
		try {
			// get ports name, depth 
			Map<String,Integer> inputPortDescriptions = gisServiceParser.GetProcessInputPorts(configuration.get("process").textValue());
			
			for (Map.Entry<String, Integer> entry : inputPortDescriptions.entrySet()) {
				inputPorts.put(entry.getKey(), edits.createActivityInputPort(
						entry.getKey(), entry.getValue(), true, null, String.class));
			} 
		} catch (Exception e) {
			logger.warn(
					"Unable to parse the GIS " + configuration.get("service").textValue(), e);
		}
		
		return new HashSet<ActivityInputPort>(inputPorts.values());
	}

	@Override
	public Set<ActivityOutputPort> getOutputPorts(JsonNode configuration) {
		Map<String, ActivityOutputPort> outputPorts = new HashMap<String, ActivityOutputPort>();
		
		IGisClient gisServiceParser = GisClientFactory.getInstance()
				.getGisClient(configuration.get("service").textValue());
		
		try {
			// get ports (name, depth) pairs 
			Map<String,Integer> outputPortDescriptions = gisServiceParser.GetProcessInputPorts(configuration.get("process").textValue());
			
			for (Map.Entry<String, Integer> outputPortIterator : outputPortDescriptions.entrySet()) {
				outputPorts.put(outputPortIterator.getKey(), 
						edits.createActivityOutputPort(outputPortIterator.getKey(),
								outputPortIterator.getValue(),outputPortIterator.getValue()));
			} 
		} catch (Exception e) {
			logger.warn(
					"Unable to parse the GIS " + configuration.get("service").textValue(), e);
		}
		
		return new HashSet<ActivityOutputPort>(outputPorts.values());
		
	}

	/**
	 * Sets the edits property.
	 * <p>
	 * This method is used by Spring. The property name must match the property specified
	 * in the Spring context file.
	 *
	 * @param edits the <code>Edits</code> used to create input/output ports
	 */
	public void setEdits(Edits edits) {
		this.edits = edits;
	}

}
