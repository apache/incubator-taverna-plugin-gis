package org.apache.taverna.gis.ui.serviceprovider;

import java.net.URI;

import net.sf.taverna.t2.lang.beans.PropertyAnnotated;
import net.sf.taverna.t2.lang.beans.PropertyAnnotation;

public class GisServiceProviderConfig extends PropertyAnnotated {
	private URI ogcServiceUri = URI.create("http://localhost:8080/geoserver/ows");
	private String processIdentifier = "gs:StringConcatWPS";
	
	@PropertyAnnotation(displayName="OGC Web Service URI", preferred=true)
	public URI getOgcServiceUri() {
		return ogcServiceUri;
	}
	public void setOgcServiceUri(URI ogcServiceUri) {
		this.ogcServiceUri = ogcServiceUri;
	}
	
	@PropertyAnnotation(displayName="Process Identifier")
	public String getProcessIdentifier() {
		return processIdentifier;
	}
	public void setProcessIdentifier(String processIdentifier) {
		this.processIdentifier = processIdentifier;
	}
	
}
