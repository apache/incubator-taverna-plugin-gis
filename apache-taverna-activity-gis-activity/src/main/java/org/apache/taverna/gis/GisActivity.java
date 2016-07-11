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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.geotools.ows.ServiceException;
import org.n52.wps.client.ExecuteRequestBuilder;
import org.n52.wps.client.ExecuteResponseAnalyser;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

import net.opengis.wps.x100.DataType;
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.OutputDataType;
import net.opengis.wps.x100.ProcessDescriptionType;
import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.processor.activity.AbstractAsynchronousActivity;
import org.apache.taverna.workflowmodel.processor.activity.ActivityConfigurationException;
import org.apache.taverna.workflowmodel.processor.activity.AsynchronousActivity;
import org.apache.taverna.workflowmodel.processor.activity.AsynchronousActivityCallback;
import org.apache.taverna.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import org.apache.taverna.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;

public class GisActivity extends AbstractAsynchronousActivity<GisActivityConfigurationBean>
		implements AsynchronousActivity<GisActivityConfigurationBean> {

	private GisActivityConfigurationBean configBean;

	@Override
	public void configure(GisActivityConfigurationBean configBean) throws ActivityConfigurationException {

		// TODO: Should I call HealthChecker here??
		// Any pre-config sanity checks
		if (configBean.getOgcServiceUri().equals("")) {
			throw new ActivityConfigurationException("Geospatial web service URI can't be empty");
		}
		// Store for getConfiguration()
		this.configBean = configBean;

		// REQUIRED: (Re)create input/output ports depending on configuration
		configurePorts();
	}

	protected void configurePorts() {
		// In case we are being reconfigured - remove existing ports first
		// to avoid duplicates
		removeInputs();
		removeOutputs();

		// Add input ports
		for(ActivityInputPortDefinitionBean inputPort : configBean.getInputPortDefinitions())
		{
			addInput(inputPort.getName(),inputPort.getDepth(),inputPort.getAllowsLiteralValues(),inputPort.getHandledReferenceSchemes(), inputPort.getTranslatedElementType());
		}

		// Add output ports
		for(ActivityOutputPortDefinitionBean outputPort : configBean.getOutputPortDefinitions())
		{
			addOutput(outputPort.getName(),outputPort.getDepth());
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void executeAsynch(final Map<String, T2Reference> inputs, final AsynchronousActivityCallback callback) {
		// Execute service asynchronously
		callback.requestRun(new Runnable() {

			public void run() {
				InvocationContext context = callback.getContext();
				ReferenceService referenceService = context.getReferenceService();

				// Declare outputs variable
				Map<String, T2Reference> outputs = null;

				try {

					// prepare the execute object
					WPSClientSession wpsClient = WPSClientSession.getInstance();

					ProcessDescriptionType processDescription = wpsClient.getProcessDescription(configBean.getOgcServiceUri().toString(), configBean.getProcessIdentifier());

					ExecuteRequestBuilder executeBuilder = new ExecuteRequestBuilder(processDescription);

					for (ActivityInputPortDefinitionBean activityInputPort : configBean.getInputPortDefinitions()) {
						String portValue = (String) referenceService.renderIdentifier(inputs.get(activityInputPort.getName()), String.class, context);
						executeBuilder.addLiteralData(activityInputPort.getName(), portValue);
					}

					ExecuteDocument execute = executeBuilder.getExecute();

					execute.getExecute().setService("WPS");

					Object responseObject = null;

					try {
						// execute service
						responseObject = wpsClient.execute(configBean.getOgcServiceUri().toString(), execute);
					} catch (WPSClientException e) {
						// if the an error return from service
						callback.fail(e.getServerException().xmlText());
					}

					// Register outputs
					outputs = new HashMap<String, T2Reference>();
					T2Reference simpleRef = null;

					if (responseObject instanceof ExecuteResponseDocument) {
			            ExecuteResponseDocument response = (ExecuteResponseDocument) responseObject;

			            // analyser is used to get complex data
			            ExecuteResponseAnalyser analyser = new ExecuteResponseAnalyser(
			                    execute, response, processDescription);

			            for(OutputDataType output : response.getExecuteResponse().getProcessOutputs().getOutputArray())
						{
			            	DataType data = output.getData();

			            	if (data.isSetLiteralData())
							{
			            		simpleRef = referenceService.register(data.getLiteralData().getStringValue(), 0, true, context);

								outputs.put(output.getIdentifier().getStringValue(), simpleRef);
							}

						}

			        }


				} catch (WPSClientException e) {
					callback.fail(e.getMessage());
				} catch (IOException e) {
					callback.fail(e.getMessage());
				}

				callback.receiveResult(outputs, new int[0]);
			}
		});
	}

	@Override
	public GisActivityConfigurationBean getConfiguration() {
		return this.configBean;
	}

}
