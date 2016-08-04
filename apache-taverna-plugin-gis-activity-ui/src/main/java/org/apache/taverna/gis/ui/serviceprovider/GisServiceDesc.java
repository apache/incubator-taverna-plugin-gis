package org.apache.taverna.gis.ui.serviceprovider;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;

import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;

import org.apache.taverna.gis.GisActivity;
import org.apache.taverna.gis.GisActivityConfigurationBean;

public class GisServiceDesc extends ServiceDescription<GisActivityConfigurationBean> {

	/**
	 * The subclass of Activity which should be instantiated when adding a service
	 * for this description 
	 */
	@Override
	public Class<? extends Activity<GisActivityConfigurationBean>> getActivityClass() {
		return GisActivity.class;
	}

	/**
	 * The configuration bean which is to be used for configuring the instantiated activity.
	 * Making this bean will typically require some of the fields set on this service
	 * description, like an endpoint URL or method name. 
	 * 
	 */
	@Override
	public GisActivityConfigurationBean getActivityConfiguration() {
		GisActivityConfigurationBean bean = new GisActivityConfigurationBean();
		bean.setOgcServiceUri(ogcServiceUri);
		bean.setProcessIdentifier(processIdentifier);
		
		bean.setInputPortDefinitions(inputPortDefinitions);
		bean.setOutputPortDefinitions(outputPortDefinitions);
		
		return bean;
	}

	/**
	 * An icon to represent this service description in the service palette.
	 */
	@Override
	public Icon getIcon() {
		return GisServiceIcon.getIcon();
	}

	/**
	 * The display name that will be shown in service palette and will
	 * be used as a template for processor name when added to workflow.
	 */
	@Override
	public String getName() {
		return processIdentifier;
	}

	/**
	 * The path to this service description in the service palette. Folders
	 * will be created for each element of the returned path.
	 */
	@Override
	public List<String> getPath() {
		// For deeper paths you may return several strings
		return Arrays.asList("GIS", "WPS - " + getOgcServiceUri());
	}

	/**
	 * Return a list of data values uniquely identifying this service
	 * description (to avoid duplicates). Include only primary key like fields,
	 * ie. ignore descriptions, icons, etc.
	 */
	@Override
	protected List<? extends Object> getIdentifyingData() {
		// FIXME: Use your fields instead of example fields
		return Arrays.<Object>asList(ogcServiceUri, processIdentifier);
	}

	
	// All fields are searchable in the Service palette,
	// for instance try a search for exampleString:3
	private URI ogcServiceUri;
	private String processIdentifier;
	private List<ActivityInputPortDefinitionBean> inputPortDefinitions;
	private List<ActivityOutputPortDefinitionBean> outputPortDefinitions;
	
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

	public List<ActivityInputPortDefinitionBean> getInputPortDefinitions() {
		return inputPortDefinitions;
	}

	public void setInputPortDefinitions(List<ActivityInputPortDefinitionBean> inputPortDefinitions) {
		this.inputPortDefinitions = inputPortDefinitions;
	}

	public List<ActivityOutputPortDefinitionBean> getOutputPortDefinitions() {
		return outputPortDefinitions;
	}

	public void setOutputPortDefinitions(List<ActivityOutputPortDefinitionBean> outputPortDefinitions) {
		this.outputPortDefinitions = outputPortDefinitions;
	}
}
