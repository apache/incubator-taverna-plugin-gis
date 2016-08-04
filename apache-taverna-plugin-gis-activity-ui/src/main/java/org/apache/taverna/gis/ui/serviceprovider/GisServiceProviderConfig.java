package org.apache.taverna.gis.ui.serviceprovider;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import net.sf.taverna.t2.lang.beans.PropertyAnnotated;
import net.sf.taverna.t2.lang.beans.PropertyAnnotation;

public class GisServiceProviderConfig extends PropertyAnnotated {
	private URI ogcServiceUri = URI.create("");
	private List<String> processIdentifiers = Arrays.asList("");
	
	public GisServiceProviderConfig() {
		
	}
	
	public GisServiceProviderConfig(String serviceURL, List<String> processIdentifiers ) {
		this.ogcServiceUri = URI.create(serviceURL);
		this.processIdentifiers = processIdentifiers;
	}
	
	@PropertyAnnotation(displayName="OGC Web Service URI", preferred=true)
	public URI getOgcServiceUri() {
		return ogcServiceUri;
	}
	public void setOgcServiceUri(URI ogcServiceUri) {
		this.ogcServiceUri = ogcServiceUri;
	}
	
	@PropertyAnnotation(displayName="Process Identifier")
	public List<String> getProcessIdentifiers() {
		return processIdentifiers;
	}
	public void setProcessIdentifiers(List<String> processIdentifiers) {
		this.processIdentifiers = processIdentifiers;
	}
	
}
