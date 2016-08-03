package org.apache.taverna.gis.ui.serviceprovider;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.apache.taverna.gis.client.*;
import org.apache.taverna.gis.client.impl.TypeDescriptor;

import net.sf.taverna.t2.servicedescriptions.AbstractConfigurableServiceProvider;
import net.sf.taverna.t2.servicedescriptions.ConfigurableServiceProvider;
import net.sf.taverna.t2.servicedescriptions.CustomizedConfigurePanelProvider;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;

public class GisServiceProvider extends AbstractConfigurableServiceProvider<GisServiceProviderConfig>
		implements ConfigurableServiceProvider<GisServiceProviderConfig>,
		CustomizedConfigurePanelProvider<GisServiceProviderConfig> {

	public GisServiceProvider() {
		super(new GisServiceProviderConfig("", new ArrayList<String>()));
	}

	private static final URI providerId = URI
			.create("http://cs.man.ac.uk/2016/service-provider/apache-taverna2-plugin-gis");
	
	private Logger logger = Logger.getLogger(AddGisServiceDialog.class);
	
	
	/**
	 * Do the actual search for services. Return using the callBack parameter.
	 */
	public void findServiceDescriptionsAsync(FindServiceDescriptionsCallBack callBack) {
		// Use callback.status() for long-running searches

		URI serviceURI = serviceProviderConfig.getOgcServiceUri();

		callBack.status("Resolving service: " + serviceURI);
		
		List<GisServiceDesc> results = new ArrayList<GisServiceDesc>();

		IGisClient gisServiceClient = GisClientFactory.getInstance().getGisClient(getConfiguration().getOgcServiceUri().toASCIIString());
		 
		List<String> processIdentifiers = serviceProviderConfig.getProcessIdentifiers();
		
		try {
			
			for (String processID : processIdentifiers)
			{
				GisServiceDesc service = new GisServiceDesc();

				// Populate the service description bean
				service.setOgcServiceUri(getConfiguration().getOgcServiceUri());
				service.setProcessIdentifier(processID);
		
				// TODO: Optional: set description (Set a better description)
				service.setDescription(processID);
				
				// Get input ports
				List<TypeDescriptor> inputList = gisServiceClient.getTaverna2InputPorts(processID);

		        List<ActivityInputPortDefinitionBean> inputPortDefinitions = new ArrayList<ActivityInputPortDefinitionBean>();

		        for (TypeDescriptor input : inputList) {
		    		ActivityInputPortDefinitionBean newInputPort = new ActivityInputPortDefinitionBean();
		    		newInputPort.setName(input.getName());
		    		newInputPort.setDepth(input.getDepth());
		    		newInputPort.setAllowsLiteralValues(input.isAllowLiteralValues());
		    		newInputPort.setHandledReferenceSchemes(null);
		    		newInputPort.setTranslatedElementType(input.getTranslatedElementType());
		    		
		    		inputPortDefinitions.add(newInputPort);
		    		
		        }
		        
		        service.setInputPortDefinitions(inputPortDefinitions);
				
		        // Get output ports
		        
		        List<TypeDescriptor> outputList = gisServiceClient.getTaverna2OutputPorts(processID);
		        List<ActivityOutputPortDefinitionBean> outputPortDefinitions = new ArrayList<ActivityOutputPortDefinitionBean>();
		        
		        for( TypeDescriptor output : outputList )
		        {
		        	ActivityOutputPortDefinitionBean newOutputPort = new ActivityOutputPortDefinitionBean();
		        	newOutputPort.setName(output.getName());
		        	newOutputPort.setDepth(output.getDepth());
		        	
		        	outputPortDefinitions.add(newOutputPort);
		        	
		        }
			
		        service.setOutputPortDefinitions(outputPortDefinitions);
		        
		        results.add(service);

				// partialResults() can also be called several times from inside
				// for-loop if the full search takes a long time
				callBack.partialResults(results);
				
			}
			
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null,
					"Could not read the service definition from "
							+ serviceURI + ":\n" + ex,
					"Could not add service service",
					JOptionPane.ERROR_MESSAGE);

			logger.error(
					"Failed to list GWS processes for service: "
							+ serviceURI, ex);
		}
        
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
		return "Geospatial Web Services " + getConfiguration().getOgcServiceUri();
	}

	public String getId() {
		return providerId.toASCIIString();
	}

	@Override
	protected List<? extends Object> getIdentifyingData() {
		List<String> result = new ArrayList<String>();
		
		List<String> processIdentifiers = getConfiguration().getProcessIdentifiers();
		
		for (String processID : processIdentifiers)
		{
			result.add(getConfiguration().getOgcServiceUri() + processID);
			
		}
		
		//return Arrays.asList(getConfiguration().getOgcServiceUri(), getConfiguration().getProcessIdentifier());
		//return Arrays.asList(getConfiguration().getOgcServiceUri());
		return result;
		
	}


	@Override
	public List<GisServiceProviderConfig> getDefaultConfigurations(){
		
		List<GisServiceProviderConfig> myDefaultConfigs = new ArrayList<GisServiceProviderConfig>();
		
		myDefaultConfigs.add(new GisServiceProviderConfig("http://localhost:8080/geoserver/ows", 
				Arrays.asList("gs:StringConcatWPS")));
		
		return myDefaultConfigs;
		
	}

	@Override
	public void createCustomizedConfigurePanel(
			final net.sf.taverna.t2.servicedescriptions.CustomizedConfigurePanelProvider.CustomizedConfigureCallBack<GisServiceProviderConfig> callBack) {

		@SuppressWarnings("serial")
		AddGisServiceDialog addGISServiceDialog = new AddGisServiceDialog(null) {

			@Override
			protected void addRegistry(String serviceURL, List<String> processIdentifiers) {
				GisServiceProviderConfig providerConfig = new GisServiceProviderConfig(serviceURL, processIdentifiers);					
				callBack.newProviderConfiguration(providerConfig);
				
			}
			
		};

		addGISServiceDialog.setVisible(true);
		
	}
	
}
