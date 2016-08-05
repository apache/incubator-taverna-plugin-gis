package org.apache.taverna.gis;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.taverna.gis.client.ComplexDataFormat;
import org.apache.taverna.gis.client.ComplexPortDataDescriptor;
import org.apache.taverna.gis.client.GisClientFactory;
import org.apache.taverna.gis.client.IGisClient;
import org.apache.taverna.gis.client.IPortDataDescriptor;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.processor.activity.AbstractAsynchronousActivity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;
import net.sf.taverna.t2.workflowmodel.processor.activity.AsynchronousActivity;
import net.sf.taverna.t2.workflowmodel.processor.activity.AsynchronousActivityCallback;

public class GisActivity extends AbstractAsynchronousActivity<GisActivityConfigurationBean>
		implements AsynchronousActivity<GisActivityConfigurationBean> {
	
	private GisActivityConfigurationBean configBean;

	private static Logger logger = Logger.getLogger(GisActivity.class);
	
	@Override
	public void configure(GisActivityConfigurationBean configBean) throws ActivityConfigurationException {

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
		for(IPortDataDescriptor inputPort : configBean.getInputPortDefinitions())
		{
			if (inputPort instanceof ComplexPortDataDescriptor)
			{
				addInput( inputPort.getName() + "_schema",0,true, null, null);
				addInput(inputPort.getName() + "_encoding",0,true, null, null);
				addInput(inputPort.getName() + "_mimetype",0,true, null, null);
			}
			
			addInput(inputPort.getName(),inputPort.getDepth(),inputPort.isAllowLiteralValues(),null, inputPort.getTranslatedElementType());
		}
		
		// Add output ports
		for(IPortDataDescriptor outputPort : configBean.getOutputPortDefinitions())
		{
			addOutput(outputPort.getName(),outputPort.getDepth());
			
			if (outputPort instanceof ComplexPortDataDescriptor)
			{
				addOutput(outputPort.getName() + "_schema",0);
				addOutput(outputPort.getName() + "_encoding",0);
				addOutput(outputPort.getName() + "_mimetype",0);
			}
			
		}
		
	}

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
					IGisClient gisClient = GisClientFactory.getInstance().getGisClient(
							configBean.getOgcServiceUri().toString());

					HashMap<String, IPortDataDescriptor> serviceInputs = new HashMap<String, IPortDataDescriptor>();
					
					for (IPortDataDescriptor activityInputPort : configBean.getInputPortDefinitions()) 
					{
						// Optional inputs are not stored in the map if no value is provided, hence they are skipped  
						if (inputs.containsKey(activityInputPort.getName())) 
						{
							Object inputValue = referenceService.renderIdentifier(inputs.get(activityInputPort.getName()), String.class, context);
						
							activityInputPort.setValue(inputValue);
							
							if (activityInputPort instanceof ComplexPortDataDescriptor)
							{
								//TODO: set format
								ComplexDataFormat complexFormat = new ComplexDataFormat();
								
								complexFormat.setEncoding(null);
								complexFormat.setMimeType("application/wkt");
								complexFormat.setSchema(null);
								
								((ComplexPortDataDescriptor) activityInputPort).setComplexFormat(complexFormat);
							}
							
							serviceInputs.put(activityInputPort.getName(), activityInputPort);
						}
						
					}
					
					// Execute process
					HashMap<String, String> serviceOutput = gisClient.executeProcess(configBean.getProcessIdentifier().toString(), serviceInputs);
					
					outputs = new HashMap<String, T2Reference>();
					T2Reference simpleRef = null;
					
					for (Map.Entry<String, String> entry : serviceOutput.entrySet()) {
					    String key = entry.getKey();
					    String value = entry.getValue();
					    
					    simpleRef = referenceService.register(value, 0, true, context);
						outputs.put(key, simpleRef);
					    
					}
					
//						
//					
//					Object responseObject = null;
//					
//					try {
//						// execute service
//						responseObject = wpsClient.execute(configBean.getOgcServiceUri().toString(), execute);
//					} catch (WPSClientException e) {
//						// if the an error return from service
//						callback.fail(e.getServerException().xmlText());
//					}
//
//					// Register outputs
//					outputs = new HashMap<String, T2Reference>();
//					T2Reference simpleRef = null;
//					
//					if (responseObject instanceof ExecuteResponseDocument) {
//			            ExecuteResponseDocument response = (ExecuteResponseDocument) responseObject;
//			            
//			            // analyser is used to get complex data
//			            ExecuteResponseAnalyser analyser = new ExecuteResponseAnalyser(
//			                    execute, response, processDescription);
//			            
//			            for(OutputDataType output : response.getExecuteResponse().getProcessOutputs().getOutputArray())
//						{
//			            	DataType data = output.getData();
//			            	
//			            	if (data.isSetLiteralData())
//							{
//			            		simpleRef = referenceService.register(data.getLiteralData().getStringValue(), 0, true, context);
//
//								outputs.put(output.getIdentifier().getStringValue(), simpleRef);
//							}
//			            	else
//			            	{
//			            	
//			            		simpleRef = referenceService.register(data.getComplexData().toString(), 0, true, context);
//			            		
//			            		outputs.put(output.getIdentifier().getStringValue(), simpleRef);
//			            		
//			            	}
//			            	
//						}
//			            
//			        }
//					
//					
//				} catch (WPSClientException e) {
//					callback.fail(e.getMessage());
//				} catch (IOException e) {
//					callback.fail(e.getMessage());
//				}
					
				} catch (Exception e) {
					logger.error("Error executing service/process: "
							+ configBean.getOgcServiceUri().toString() + "/" + configBean.getProcessIdentifier().toString(), e);
					callback.fail("Unable to execute service", e);
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
