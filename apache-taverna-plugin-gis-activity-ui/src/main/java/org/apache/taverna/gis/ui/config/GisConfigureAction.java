package org.apache.taverna.gis.ui.config;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;

import org.apache.taverna.gis.GisActivity;
import org.apache.taverna.gis.GisActivityConfigurationBean;

@SuppressWarnings("serial")
public class GisConfigureAction
		extends
		ActivityConfigurationAction<GisActivity,
        GisActivityConfigurationBean> {

	public GisConfigureAction(GisActivity activity, Frame owner) {
		super(activity);
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		ActivityConfigurationDialog<GisActivity, GisActivityConfigurationBean> currentDialog = ActivityConfigurationAction
				.getDialog(getActivity());
		if (currentDialog != null) {
			currentDialog.toFront();
			return;
		}
		GisConfigurationPanel panel = new GisConfigurationPanel(
				getActivity());
		ActivityConfigurationDialog<GisActivity,
        GisActivityConfigurationBean> dialog = new ActivityConfigurationDialog<GisActivity, GisActivityConfigurationBean>(
				getActivity(), panel);

		ActivityConfigurationAction.setDialog(getActivity(), dialog);

	}

}
