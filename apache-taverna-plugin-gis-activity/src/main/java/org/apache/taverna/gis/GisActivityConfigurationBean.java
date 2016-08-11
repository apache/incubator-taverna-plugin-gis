package org.apache.taverna.gis;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import org.apache.taverna.gis.client.IPortDataDescriptor;

/**
 * Gis activity configuration bean.
 * 
 */
@SuppressWarnings("serial")
public class GisActivityConfigurationBean implements Serializable {

	private URI ogcServiceUri;
	private String processIdentifier;
	private List<IPortDataDescriptor> inputPortDefinitions;
	private List<IPortDataDescriptor> outputPortDefinitions;
	
	public URI getOgcServiceUri() {	
		return ogcServiceUri;
	}
	public void setOgcServiceUri(URI ogcServiceUri) {
		this.ogcServiceUri = ogcServiceUri;
	}
	public String getProcessIdentifier() {
		return processIdentifier;
	}
	public void setProcessIdentifier(String processIdentifier) {
		this.processIdentifier = processIdentifier;
	}
	public List<IPortDataDescriptor> getInputPortDefinitions() {
		return inputPortDefinitions;
	}
	public void setInputPortDefinitions(List<IPortDataDescriptor> inputPortDefinitions) {
		this.inputPortDefinitions = inputPortDefinitions;
	}
	public List<IPortDataDescriptor> getOutputPortDefinitions() {
		return outputPortDefinitions;
	}
	public void setOutputPortDefinitions(List<IPortDataDescriptor> outputPortDefinitions) {
		this.outputPortDefinitions = outputPortDefinitions;
	}
}
