package org.apache.taverna.gis.ui.serviceprovider;

import java.net.URI;

import net.sf.taverna.t2.lang.beans.PropertyAnnotation;

public class GisServiceProviderConfig {
	private URI ogcServiceUri = URI.create("http://localhost:8080/geoserver/ows");
	private String processIdentifier = "gs:StringConcatWPS";
	
	// TODO: Display name not working, hide getClass
	
	@PropertyAnnotation(displayName="OGC Web Service URI", preferred=true)
	public URI getOgcServiceUri() {
		return ogcServiceUri;
	}
	public void setOgcServiceUri(URI ogcServiceUri) {
		this.ogcServiceUri = ogcServiceUri;
	}
	
	// TODO: Display name not working
	
	@PropertyAnnotation(displayName="Process Identifier")
	public String getProcessIdentifier() {
		return processIdentifier;
	}
	public void setProcessIdentifier(String processIdentifier) {
		this.processIdentifier = processIdentifier;
	}
	
}
