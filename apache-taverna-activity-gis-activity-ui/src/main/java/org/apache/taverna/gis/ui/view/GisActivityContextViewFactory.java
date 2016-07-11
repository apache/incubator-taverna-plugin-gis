package org.apache.taverna.gis.ui.view;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.taverna.services.ServiceRegistry;
import org.apache.taverna.scufl2.api.activity.Activity;

import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.ui.views.contextualviews.ContextualView;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ContextualViewFactory;

public class GisActivityContextViewFactory implements ContextualViewFactory<Activity> {

	private static final URI ACTIVITY_TYPE = URI
			.create("http://example.com/2013/activity/apache-taverna-activity-gis");

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;
	private ServiceDescriptionRegistry serviceDescriptionRegistry;
	private ServiceRegistry serviceRegistry;

	@Override
	public boolean canHandle(Object object) {
		return object instanceof Activity && ((Activity) object).getType().equals(ACTIVITY_TYPE);
	}

	@Override
	public List<ContextualView> getViews(Activity selection) {
		return Arrays.<ContextualView>asList(new GisContextualView(selection, editManager,
				fileManager, activityIconManager, serviceDescriptionRegistry, serviceRegistry));
	}

	public void setEditManager(EditManager editManager) {
		this.editManager = editManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setActivityIconManager(ActivityIconManager activityIconManager) {
		this.activityIconManager = activityIconManager;
	}

	public void setServiceDescriptionRegistry(ServiceDescriptionRegistry serviceDescriptionRegistry) {
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

}
