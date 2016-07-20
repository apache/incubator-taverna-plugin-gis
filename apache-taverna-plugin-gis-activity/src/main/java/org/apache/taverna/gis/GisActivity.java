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
			if (inputPort.getName()=="polygon")
			{
				addInput("polygon_schema",0,true, null, null);
				addInput("polygon_encoding",0,true, null, null);
				addInput("polygon_mimetype",0,true, null, null);
				
			}
			
			if (inputPort.getName()=="line")
			{
				addInput("line_schema",0,true, null, null);
				addInput("line_encoding",0,true, null, null);
				addInput("line_mimetype",0,true, null, null);
			}
			
			addInput(inputPort.getName(),inputPort.getDepth(),inputPort.getAllowsLiteralValues(),inputPort.getHandledReferenceSchemes(), inputPort.getTranslatedElementType());
		}
		
		// Add output ports
		for(ActivityOutputPortDefinitionBean outputPort : configBean.getOutputPortDefinitions())
		{
			addOutput(outputPort.getName(),outputPort.getDepth());
			
			if (outputPort.getName()=="result")
			{
				addOutput("result_schema",0);
				addOutput("result_encoding",0);
				addOutput("result_mimetype",0);
			}
			
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
						
						if (activityInputPort.getName().equals("polygon") || activityInputPort.getName().equals("line"))
						{
							String schema = "application/wkt";
							String encoding = null;
							String mimeType = "application/wkt";
							
							executeBuilder.addComplexData(activityInputPort.getName(),
									portValue, schema, encoding, mimeType);
							
						}else
						{
							executeBuilder.addLiteralData(activityInputPort.getName(), portValue);
						}
						
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
			            	else
			            	{
			            	
			            		simpleRef = referenceService.register(data.getComplexData().toString(), 0, true, context);
			            		
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
