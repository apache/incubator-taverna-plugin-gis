package org.apache.taverna.gis;

import java.io.Serializable;
import java.net.URI;

import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityPortsDefinitionBean;

/**
 * Gis activity configuration bean.
 * 
 */
@SuppressWarnings("serial")
public class GisActivityConfigurationBean extends ActivityPortsDefinitionBean implements Serializable {

	private URI ogcServiceUri;
	private String processIdentifier;
	
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

}
