package org.apache.taverna.gis.ui.serviceprovider;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;

import org.n52.wps.client.WPSClientSession;

import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.sf.taverna.t2.servicedescriptions.AbstractConfigurableServiceProvider;
import net.sf.taverna.t2.servicedescriptions.ConfigurableServiceProvider;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionProvider;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;

public class GisServiceProvider extends AbstractConfigurableServiceProvider<GisServiceProviderConfig>
		implements ConfigurableServiceProvider<GisServiceProviderConfig> {

	public GisServiceProvider() {
		super(new GisServiceProviderConfig());
	}

	private static final URI providerId = URI
			.create("http://cs.man.ac.uk/2016/service-provider/apache-taverna-plugin-gis");

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

		// TODO: Exctract in a separate method 
		// Get input ports
		
		WPSClientSession wpsClient = WPSClientSession.getInstance();

        ProcessDescriptionType processDescription;
		try {
			processDescription = wpsClient
			        .getProcessDescription(getConfiguration().getOgcServiceUri().toString(), getConfiguration().getProcessIdentifier());
		
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
	        
	        service.setInputPortDefinitions(inputPortDefinitions);
			
	        
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
		
	        service.setOutputPortDefinitions(outputPortDefinitions);
	        
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
