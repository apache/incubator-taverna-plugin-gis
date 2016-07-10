package org.apache.taverna.gis.ui.view;

import java.util.Arrays;
import java.util.List;

import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ContextualViewFactory;

import org.apache.taverna.gis.GisActivity;

public class GisActivityContextViewFactory implements
		ContextualViewFactory<GisActivity> {

	public boolean canHandle(Object selection) {
		return selection instanceof GisActivity;
	}

	public List<ContextualView> getViews(GisActivity selection) {
		return Arrays.<ContextualView>asList(new GisContextualView(selection));
	}
	
}
