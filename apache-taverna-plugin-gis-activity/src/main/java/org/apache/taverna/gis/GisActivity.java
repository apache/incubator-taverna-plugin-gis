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
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.processor.activity.AbstractAsynchronousActivity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;
import net.sf.taverna.t2.workflowmodel.processor.activity.AsynchronousActivity;
import net.sf.taverna.t2.workflowmodel.processor.activity.AsynchronousActivityCallback;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;

public class GisActivity extends AbstractAsynchronousActivity<GisActivityConfigurationBean>
		implements AsynchronousActivity<GisActivityConfigurationBean> {

	/*
	 * Best practice: Keep port names as constants to avoid misspelling. This
	 * would not apply if port names are looked up dynamically from the service
	 * operation, like done for WSDL services.
	 */
	
	private GisActivityConfigurationBean configBean;

	@Override
	public void configure(GisActivityConfigurationBean configBean) throws ActivityConfigurationException {

		// TODO: Should I call HealthChecker here??
		// Any pre-config sanity checks
		if (configBean.getOgcServiceUri().equals("")) {
			throw new ActivityConfigurationException("Geospatial web service URI can't be empty");
		}
		// Store for getConfiguration(), but you could also make
		// getConfiguration() return a new bean from other sources
		this.configBean = configBean;

		// OPTIONAL:
		// Do any server-side lookups and configuration, like resolving WSDLs

		// myClient = new MyClient(configBean.getExampleUri());
		// this.service = myClient.getService(configBean.getExampleString());

		// REQUIRED: (Re)create input/output ports depending on configuration
		configurePorts();
	}

	protected void configurePorts() {
		// In case we are being reconfigured - remove existing ports first
		// to avoid duplicates
		removeInputs();
		removeOutputs();

		// FIXME: Replace with your input and output port definitions

		for(ActivityInputPortDefinitionBean inputPort : configBean.getInputPortDefinitions())
		{
			addInput(inputPort.getName(),inputPort.getDepth(),inputPort.getAllowsLiteralValues(),inputPort.getHandledReferenceSchemes(), inputPort.getTranslatedElementType());
		}
		
//		// Hard coded input port, expecting a single String
//		addInput(IN_FIRSTNAME, 0, true, null, String.class);

		//
		// // Optional ports depending on configuration
		// if (configBean.getExampleString().equals("specialCase")) {
		// // depth 1, ie. list of binary byte[] arrays
		// addInput(IN_EXTRA_DATA, 1, true, null, byte[].class);
		// addOutput(OUT_REPORT, 0);
		// }

		for(ActivityOutputPortDefinitionBean outputPort : configBean.getOutputPortDefinitions())
		{
			addOutput(outputPort.getName(),outputPort.getDepth());
		}
		
//		// Single value output port (depth 0)
//		addOutput(OUT_FULLNAME, 0);
//		// // Output port with list of values (depth 1)
//		// addOutput(OUT_MORE_OUTPUTS, 1);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void executeAsynch(final Map<String, T2Reference> inputs, final AsynchronousActivityCallback callback) {
		// Don't execute service directly now, request to be run ask to be run
		// from thread pool and return asynchronously
		callback.requestRun(new Runnable() {

			public void run() {
				InvocationContext context = callback.getContext();
				ReferenceService referenceService = context.getReferenceService();
//				// Resolve inputs
//				String firstNameInput = (String) referenceService.renderIdentifier(inputs.get(IN_FIRSTNAME), String.class, context);
//				String lastNameInput = (String) referenceService.renderIdentifier(inputs.get(IN_LASTNAME), String.class, context);

				// // Support our configuration-dependendent input
				// boolean optionalPorts =
				// configBean.getExampleString().equals("specialCase");
				//
				// List<byte[]> special = null;
				// // We'll also allow IN_EXTRA_DATA to be optionally not
				// provided
				// if (optionalPorts && inputs.containsKey(IN_EXTRA_DATA)) {
				// // Resolve as a list of byte[]
				// special = (List<byte[]>) referenceService.renderIdentifier(
				// inputs.get(IN_EXTRA_DATA), byte[].class, context);
				// }

				// TODO: Do the actual service invocation
				// try {
				// results = this.service.invoke(firstInput, special)
				// } catch (ServiceException ex) {
				// callback.fail("Could not invoke Gis service " +
				// configBean.getExampleUri(),
				// ex);
				// // Make sure we don't call callback.receiveResult later
				// return;
				// }

//				String wpsURL = "http://localhost:8080/geoserver/ows";
//
//				String processID = "gs:StringConcatWPS";

				// Register outputs
				Map<String, T2Reference> outputs = null;
				
				try {

					WPSClientSession wpsClient = WPSClientSession.getInstance();

					ProcessDescriptionType processDescription = wpsClient.getProcessDescription(configBean.getOgcServiceUri().toString(), configBean.getProcessIdentifier());
					// configBean.getWpsUri().toString(),
					// configBean.getProcessBrief().getIdentifier().getStringValue());

					ExecuteRequestBuilder executeBuilder = new ExecuteRequestBuilder(processDescription);

					for (ActivityInputPortDefinitionBean activityInputPort : configBean.getInputPortDefinitions()) {
						String portValue = (String) referenceService.renderIdentifier(inputs.get(activityInputPort.getName()), String.class, context);
						executeBuilder.addLiteralData(activityInputPort.getName(), portValue);
					}
					
//					executeBuilder.addLiteralData(IN_FIRSTNAME, (String) firstNameInput);
//					executeBuilder.addLiteralData(IN_LASTNAME, (String) lastNameInput);

					// execute request
					ExecuteDocument execute = executeBuilder.getExecute();
					// wpsClient.retrieveExecuteResponseViaPOST(configBean.getWpsUri().toString(),
					// execute, false);
					execute.getExecute().setService("WPS");
					Object responseObject = null;
					try {
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
//				} catch (ServiceException e) {
//					callback.fail(e.getMessage());
				} catch (IOException e) {
					callback.fail(e.getMessage());
				}

//				// Register outputs
//				Map<String, T2Reference> outputs = new HashMap<String, T2Reference>();
//				String simpleValue = "simple";
//				T2Reference simpleRef = referenceService.register(simpleValue, 0, true, context);
//				outputs.put(OUT_SIMPLE_OUTPUT, simpleRef);

				// // For list outputs, only need to register the top level list
				// List<String> moreValues = new ArrayList<String>();
				// moreValues.add("Value 1");
				// moreValues.add("Value 2");
				// T2Reference moreRef = referenceService.register(moreValues,
				// 1, true, context);
				// outputs.put(OUT_MORE_OUTPUTS, moreRef);
				//
				// if (optionalPorts) {
				// // Populate our optional output port
				// // NOTE: Need to return output values for all defined output
				// ports
				// String report = "Everything OK";
				// outputs.put(OUT_REPORT, referenceService.register(report,
				// 0, true, context));
				// }

				// return map of output data, with empty index array as this is
				// the only and final result (this index parameter is used if
				// pipelining output)
				callback.receiveResult(outputs, new int[0]);
			}
		});
	}

	@Override
	public GisActivityConfigurationBean getConfiguration() {
		return this.configBean;
	}

}
