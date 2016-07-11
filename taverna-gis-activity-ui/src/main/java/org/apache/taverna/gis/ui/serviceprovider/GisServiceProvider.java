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
package org.apache.taverna.gis.ui.serviceprovider;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;

import org.apache.taverna.scufl2.api.common.Visitor;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.servicedescriptions.AbstractConfigurableServiceProvider;
import org.apache.taverna.servicedescriptions.ConfigurableServiceProvider;
import org.apache.taverna.servicedescriptions.ServiceDescription;
import org.apache.taverna.servicedescriptions.ServiceDescriptionProvider;
import org.apache.taverna.servicedescriptions.ServiceDescriptionProvider.FindServiceDescriptionsCallBack;
import org.apache.taverna.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import org.apache.taverna.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;
import org.n52.wps.client.WPSClientSession;

import com.fasterxml.jackson.databind.node.ObjectNode;

import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;

public class GisServiceProvider extends AbstractConfigurableServiceProvider
		implements ConfigurableServiceProvider {


	public GisServiceProvider() {
		super(defaultConfig());

	}

	private static Configuration defaultConfig() {
		Configuration c = new Configuration();
		ObjectNode conf = c.getJsonAsObjectNode();
		conf.put("osgiServiceUri", "http://localhost:8080/geoserver/ows");
		conf.put("processIdentifier", "gs:StringConcatWPS");
		return c;
	}

	private static final URI providerId = GisServiceDesc.ACTIVITY_TYPE.resolve("#provider");

	/**
	 * Do the actual search for services. Return using the callBack parameter.
	 */
	@SuppressWarnings("unchecked")
	public void findServiceDescriptionsAsync(FindServiceDescriptionsCallBack callBack) {
		// Use callback.status() for long-running searches
		callBack.status("Resolving GIS services");

		List<ServiceDescription> results = new ArrayList<ServiceDescription>();

		// FIXME: Implement the actual service search/lookup instead
		// of dummy for-loop

		GisServiceDesc service = new GisServiceDesc();
		// Populate the service description bean
		ObjectNode conf = getConfiguration().getJsonAsObjectNode();
		String serviceUri = conf.get("osgiServiceUri").asText();
		service.setOgcServiceUri(URI.create(serviceUri));
		String processIdentifier = conf.get("processIdentifier").asText();
		service.setProcessIdentifier(processIdentifier);

		// TODO: Optional: set description (Set a better description
		service.setDescription(processIdentifier);

		// TODO: Exctract in a separate method
		// Get input ports

		WPSClientSession wpsClient = WPSClientSession.getInstance();

        ProcessDescriptionType processDescription;
		try {
			processDescription = wpsClient
			        .getProcessDescription(serviceUri, processIdentifier);

			InputDescriptionType[] inputList = processDescription.getDataInputs()
	                .getInputArray();

	        List<ActivityInputPortDefinitionBean> inputPortDefinitions = new ArrayList<ActivityInputPortDefinitionBean>();

	        for (InputDescriptionType input : inputList) {
	    		ActivityInputPortDefinitionBean newInputPort = new ActivityInputPortDefinitionBean();
	    		newInputPort.setName(input.getIdentifier().getStringValue());
	    		newInputPort.setDepth(0);
	    		newInputPort.setAllowsLiteralValues(true);
	    		newInputPort.setHandledReferenceSchemes(null);
	    		newInputPort.setTranslatedElementType(String.class);

	    		inputPortDefinitions.add(newInputPort);

	        }

	       // service.setInputPortDefinitions(inputPortDefinitions);


	        // Get output ports

	        OutputDescriptionType[] outputList = processDescription.getProcessOutputs().getOutputArray();
	        List<ActivityOutputPortDefinitionBean> outputPortDefinitions = new ArrayList<ActivityOutputPortDefinitionBean>();

	        for( OutputDescriptionType output : outputList )
	        {
	        	ActivityOutputPortDefinitionBean newOutputPort = new ActivityOutputPortDefinitionBean();
	        	newOutputPort.setName(output.getIdentifier().getStringValue());
	        	newOutputPort.setDepth(0);

	        	outputPortDefinitions.add(newOutputPort);

	        }

	        //service.setOutputPortDefinitions(outputPortDefinitions);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		results.add(service);

		// partialResults() can also be called several times from inside
		// for-loop if the full search takes a long time
		callBack.partialResults(results);

		// No more results will be coming
		callBack.finished();
	}

	/**
	 * Icon for service provider
	 */
	public Icon getIcon() {
		return GisServiceIcon.getIcon();
	}

	/**
	 * Name of service provider, appears in right click for 'Remove service
	 * provider'
	 */
	public String getName() {
		return "Geospatial Web Services";
	}

	@Override
	public String toString() {
		return "Geospatial Web Services " + getConfiguration().getJson().get("osgiServiceUri");
	}

	public String getId() {
		return providerId.toASCIIString();
	}

	@Override
	protected List<? extends Object> getIdentifyingData() {
		return Arrays.asList(getConfiguration().getJson().get("osgiServiceUri"),
				getConfiguration().getJson().get("processIdentifier"));
	}

	@Override
	public ServiceDescriptionProvider newInstance() {
		return new GisServiceProvider();
	}

	@Override
	public URI getType() {
		return providerId;
	}

	@Override
	public void setType(URI type) {
	}

	@Override
	public boolean accept(Visitor visitor) {
		return false;
	}


}
