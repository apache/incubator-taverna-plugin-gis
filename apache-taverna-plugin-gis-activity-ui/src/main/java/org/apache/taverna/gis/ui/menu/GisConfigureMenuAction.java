package org.apache.taverna.gis.ui.menu;

import javax.swing.Action;

import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;
import org.apache.taverna.gis.GisActivity;
import org.apache.taverna.gis.ui.config.GisConfigureAction;

public class GisConfigureMenuAction extends
		AbstractConfigureActivityMenuAction<GisActivity> {

	public GisConfigureMenuAction() {
		super(GisActivity.class);
	}

	@Override
	protected Action createAction() {
		GisActivity a = findActivity();
		Action result = null;
		result = new GisConfigureAction(findActivity(),
				getParentFrame());
		result.putValue(Action.NAME, "Configure example service");
		addMenuDots(result);
		return result;
	}

}
