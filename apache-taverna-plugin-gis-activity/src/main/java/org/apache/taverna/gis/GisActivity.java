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
	
	private final static String ENCODING_PORT_POSTFIX = "_encoding";
	private final static String MIMETYPE_PORT_POSTFIX = "_mimeType";
	private final static String SCHEMA_PORT_POSTFIX = "_schema";
	

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
				// Depth is 0 as it only gets 1 value 
				addInput( inputPort.getName() + SCHEMA_PORT_POSTFIX,0,true, null, null);
				addInput(inputPort.getName() + ENCODING_PORT_POSTFIX,0,true, null, null);
				addInput(inputPort.getName() + MIMETYPE_PORT_POSTFIX,0,true, null, null);
			}
			
			addInput(inputPort.getName(),inputPort.getDepth(),inputPort.isAllowLiteralValues(),null, inputPort.getTranslatedElementType());
		}
		
		// Add output ports
		for(IPortDataDescriptor outputPort : configBean.getOutputPortDefinitions())
		{
			addOutput(outputPort.getName(),outputPort.getDepth());
			
			if (outputPort instanceof ComplexPortDataDescriptor)
			{
				addOutput(outputPort.getName() + SCHEMA_PORT_POSTFIX,0);
				addOutput(outputPort.getName() + ENCODING_PORT_POSTFIX,0);
				addOutput(outputPort.getName() + MIMETYPE_PORT_POSTFIX,0);
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
					
					// Get client instance
					IGisClient gisClient = GisClientFactory.getInstance().getGisClient(
							configBean.getOgcServiceUri().toString());

					// Prepare inputs
					HashMap<String, IPortDataDescriptor> serviceInputs = prepareInputs(inputs, context,	referenceService);
					
					// Prepare outputs
					HashMap<String, IPortDataDescriptor> serviceOutputs = prepareOutputs(inputs, context, referenceService);
					
					// Execute process
					HashMap<String, String> serviceOutput = gisClient.executeProcess(
							configBean.getProcessIdentifier().toString(), serviceInputs, serviceOutputs);
					
					// Retrieve output
					outputs = retrieveResponseOutput(context, referenceService, serviceOutput);
					
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
	
	private Map<String, T2Reference> retrieveResponseOutput(InvocationContext context,
			ReferenceService referenceService, HashMap<String, String> serviceOutput) 
	{
		Map<String, T2Reference> outputs;
		outputs = new HashMap<String, T2Reference>();
		T2Reference simpleRef = null;
		
		for (Map.Entry<String, String> entry : serviceOutput.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    
		    simpleRef = referenceService.register(value, 0, true, context);
			outputs.put(key, simpleRef);
		    
		}
		return outputs;
	}

	// Checks if input ports have defined the execution output format
	private HashMap<String, IPortDataDescriptor> prepareOutputs(final Map<String, T2Reference> inputs,
			InvocationContext context, ReferenceService referenceService) 
	{
		
		HashMap<String, IPortDataDescriptor> serviceOutputs = new HashMap<String, IPortDataDescriptor>();
		
		for (IPortDataDescriptor activityOutputPort : configBean.getOutputPortDefinitions()) 
		{
			if (activityOutputPort instanceof ComplexPortDataDescriptor)
			{
				ComplexDataFormat complexFormat = getComplexDataFormat(
						inputs, referenceService, context, activityOutputPort.getName());
				
				((ComplexPortDataDescriptor) activityOutputPort).setComplexFormat(complexFormat);
				
//				// test format
//				ComplexDataFormat complexFormat = new ComplexDataFormat();
//				
//				complexFormat.setMimeType("text/xml");
//				complexFormat.setSchema("http://schemas.opengis.net/gml/2.1.2/feature.xsd");
//				
//				((ComplexPortDataDescriptor) activityOutputPort).setComplexFormat(complexFormat);
				
			}
			
			serviceOutputs.put(activityOutputPort.getName(), activityOutputPort);
			
		}
		
		return serviceOutputs;
	}

	private HashMap<String, IPortDataDescriptor> prepareInputs(final Map<String, T2Reference> inputs,
			InvocationContext context, ReferenceService referenceService) 
	{
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
					ComplexDataFormat complexFormat = getComplexDataFormat(
							inputs, referenceService, context, activityInputPort.getName());
					
					((ComplexPortDataDescriptor) activityInputPort).setComplexFormat(complexFormat);
					
//					ComplexDataFormat complexFormat = new ComplexDataFormat();
//					
//					complexFormat.setMimeType("text/XML");
//					complexFormat.setSchema("http://schemas.opengis.net/gml/2.1.2/feature.xsd");
//					
//					((ComplexPortDataDescriptor) activityInputPort).setComplexFormat(complexFormat);
					
				}
				
				serviceInputs.put(activityInputPort.getName(), activityInputPort);
			}
			
		}
		return serviceInputs;
	}
	
	private ComplexDataFormat getComplexDataFormat(Map<String, T2Reference> inputs, 
			ReferenceService referenceService, InvocationContext context, String activityPortName)
	{
		ComplexDataFormat complexFormat = new ComplexDataFormat();
		
		complexFormat.setEncoding(getComplexDataPortValue(inputs, referenceService, context,
				activityPortName, ENCODING_PORT_POSTFIX));
		
		complexFormat.setMimeType(getComplexDataPortValue(inputs, referenceService, context,
				activityPortName, MIMETYPE_PORT_POSTFIX));
		
		complexFormat.setSchema(getComplexDataPortValue(inputs, referenceService, context,
				activityPortName, SCHEMA_PORT_POSTFIX));
		
		return complexFormat;
		
	}
	
	private String getComplexDataPortValue(Map<String, T2Reference> inputs, ReferenceService referenceService, 
			InvocationContext context,  String portName, String portPostFix)
	{
		String value = null;
		if (inputs.containsKey(portName + portPostFix))
			value = (String) referenceService.renderIdentifier(
					inputs.get(portName + portPostFix), String.class, context);
		
		return value;
		
	}

}
