package org.apache.taverna.gis.ui.config;

import java.awt.GridLayout;
import java.net.URI;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationPanel;

import org.apache.taverna.gis.GisActivity;
import org.apache.taverna.gis.GisActivityConfigurationBean;


@SuppressWarnings("serial")
public class GisConfigurationPanel
		extends
		ActivityConfigurationPanel<GisActivity, 
        GisActivityConfigurationBean> {

	private GisActivity activity;
	private GisActivityConfigurationBean configBean;
	
	private JTextField fieldServiceURI;
	private JTextField fieldProcessIdentifier;

	public GisConfigurationPanel(GisActivity activity) {
		this.activity = activity;
		initGui();
	}

	protected void initGui() {
		removeAll();
		setLayout(new GridLayout(0, 2));

		// FIXME: Create GUI depending on activity configuration bean
		JLabel labelServiceURI = new JLabel("Service URI:");
		add(labelServiceURI);
		fieldServiceURI = new JTextField(100);
		add(fieldServiceURI);
		labelServiceURI.setLabelFor(fieldServiceURI);

		JLabel labelProcessIdentifier = new JLabel("Process Identifier:");
		add(labelProcessIdentifier);
		fieldProcessIdentifier = new JTextField(100);
		add(fieldProcessIdentifier);
		labelProcessIdentifier.setLabelFor(fieldProcessIdentifier);

		// Populate fields from activity configuration bean
		refreshConfiguration();
	}

	/**
	 * Check that user values in UI are valid
	 */
	@Override
	public boolean checkValues() {
		try {
			URI.create(fieldServiceURI.getText());
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(this, ex.getCause().getMessage(),
					"Invalid URI", JOptionPane.ERROR_MESSAGE);
			// Not valid, return false
			return false;
		}
		
		// TODO: Should check if process exists
		
		// All valid, return true
		return true;
	}

	/**
	 * Return configuration bean generated from user interface last time
	 * noteConfiguration() was called.
	 */
	@Override
	public GisActivityConfigurationBean getConfiguration() {
		// Should already have been made by noteConfiguration()
		return configBean;
	}

	/**
	 * Check if the user has changed the configuration from the original
	 */
	@Override
	public boolean isConfigurationChanged() {
		String originalServiceUri = configBean.getOgcServiceUri().toASCIIString();
		String originalProcessIdentifier = configBean.getProcessIdentifier();
		
		// true (changed) unless all fields match the originals
		return ! (originalServiceUri.equals(fieldServiceURI.getText())
				&& originalProcessIdentifier.equals(fieldProcessIdentifier.getText()));
	}

	/**
	 * Prepare a new configuration bean from the UI, to be returned with
	 * getConfiguration()
	 */
	@Override
	public void noteConfiguration() {
		configBean = new GisActivityConfigurationBean();
		
		// FIXME: Update bean fields from your UI elements
		configBean.setOgcServiceUri(URI.create(fieldServiceURI.getText()));
		configBean.setProcessIdentifier(fieldProcessIdentifier.getText());
		
	}

	/**
	 * Update GUI from a changed configuration bean (perhaps by undo/redo).
	 * 
	 */
	@Override
	public void refreshConfiguration() {
		configBean = activity.getConfiguration();
		
		// FIXME: Update UI elements from your bean fields
		fieldServiceURI.setText(configBean.getOgcServiceUri().toASCIIString());
		fieldProcessIdentifier.setText(configBean.getProcessIdentifier());
		
	}
}
