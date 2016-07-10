package org.apache.taverna.gis.ui.view;

import java.awt.Frame;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;

import org.apache.taverna.gis.GisActivity;
import org.apache.taverna.gis.GisActivityConfigurationBean;
import org.apache.taverna.gis.ui.config.GisConfigureAction;

@SuppressWarnings("serial")
public class GisContextualView extends ContextualView {
	private final GisActivity activity;
	private JLabel description = new JLabel("ads");

	public GisContextualView(GisActivity activity) {
		this.activity = activity;
		initView();
	}

	@Override
	public JComponent getMainFrame() {
		JPanel jPanel = new JPanel();
		jPanel.add(description);
		refreshView();
		return jPanel;
	}

	@Override
	public String getViewTitle() {
		GisActivityConfigurationBean configuration = activity
				.getConfiguration();
		return "Gis service " + configuration.getProcessIdentifier();
	}

	/**
	 * Typically called when the activity configuration has changed.
	 */
	@Override
	public void refreshView() {
		GisActivityConfigurationBean configuration = activity
				.getConfiguration();
		description.setText("Gis service " + configuration.getOgcServiceUri()
				+ " - " + configuration.getProcessIdentifier());
		// TODO: Might also show extra service information looked
		// up dynamically from endpoint/registry
	}

	/**
	 * View position hint
	 */
	@Override
	public int getPreferredPosition() {
		// We want to be on top
		return 100;
	}
	
	@Override
	public Action getConfigureAction(final Frame owner) {
		return new GisConfigureAction(activity, owner);
	}

}
