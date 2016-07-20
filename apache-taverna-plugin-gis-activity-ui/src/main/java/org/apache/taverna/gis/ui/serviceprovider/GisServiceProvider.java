package org.apache.taverna.gis.ui.serviceprovider;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;

import org.apache.taverna.gis.client.*;
import org.apache.taverna.gis.client.impl.TypeDescriptor;

import net.sf.taverna.t2.servicedescriptions.AbstractConfigurableServiceProvider;
import net.sf.taverna.t2.servicedescriptions.ConfigurableServiceProvider;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;

public class GisServiceProvider extends AbstractConfigurableServiceProvider<GisServiceProviderConfig>
		implements ConfigurableServiceProvider<GisServiceProviderConfig> {

	public GisServiceProvider() {
		super(new GisServiceProviderConfig());
	}

	private static final URI providerId = URI
			.create("http://cs.man.ac.uk/2016/service-provider/apache-taverna2-plugin-gis");

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
		service.setOgcServiceUri(getConfiguration().getOgcServiceUri());
		service.setProcessIdentifier(getConfiguration().getProcessIdentifier());

		// TODO: Optional: set description (Set a better description
		service.setDescription(getConfiguration().getProcessIdentifier());

		// Get input ports
		
		IGisClient gisServiceClient = GisClientFactory.getInstance().getGisClient(getConfiguration().getOgcServiceUri().toASCIIString());
		 
		try {
			
			List<TypeDescriptor> inputList = gisServiceClient.getTaverna2InputPorts(getConfiguration().getProcessIdentifier());

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
	        
	        List<TypeDescriptor> outputList = gisServiceClient.getTaverna2OutputPorts(getConfiguration().getProcessIdentifier());
	        List<ActivityOutputPortDefinitionBean> outputPortDefinitions = new ArrayList<ActivityOutputPortDefinitionBean>();
	        
	        for( TypeDescriptor output : outputList )
	        {
	        	ActivityOutputPortDefinitionBean newOutputPort = new ActivityOutputPortDefinitionBean();
	        	newOutputPort.setName(output.getName());
	        	newOutputPort.setDepth(output.getDepth());
	        	
	        	outputPortDefinitions.add(newOutputPort);
	        	
	        }
		
	        service.setOutputPortDefinitions(outputPortDefinitions);
	        
		} catch (Exception e) {
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
		return "Geospatial Web Services " + getConfiguration().getProcessIdentifier();
	}

	public String getId() {
		return providerId.toASCIIString();
	}

	@Override
	protected List<? extends Object> getIdentifyingData() {
		return Arrays.asList(getConfiguration().getOgcServiceUri(), getConfiguration().getProcessIdentifier());
	}


	@Override
	public List<GisServiceProviderConfig> getDefaultConfigurations(){
		
		GisServiceProviderConfig myConfig = new GisServiceProviderConfig();
		
		myConfig.setOgcServiceUri(URI.create("http://localhost:8080/geoserver/ows"));
		myConfig.setProcessIdentifier("gs:StringConcatWPS");
		
		return Arrays.asList(myConfig);
		
		
	}
	
}
