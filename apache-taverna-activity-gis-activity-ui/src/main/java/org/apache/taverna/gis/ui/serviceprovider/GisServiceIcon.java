package org.apache.taverna.gis.ui.serviceprovider;

import java.net.URI;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.taverna.workbench.activityicons.ActivityIconSPI;

public class GisServiceIcon implements ActivityIconSPI {

	private static final URI ACTIVITY_TYPE = URI
			.create("http://example.com/2013/activity/apache-taverna-activity-gis");

	private static Icon icon;

	@Override
	public int canProvideIconScore(URI activityType) {
		if (ACTIVITY_TYPE.equals(activityType)) {
			return DEFAULT_ICON + 1;
		}
		return NO_ICON;
	}

	@Override
	public Icon getIcon(URI activityType) {
		return getIcon();
	}

	public static Icon getIcon() {
		if (icon == null) {
			icon = new ImageIcon(GisServiceIcon.class.getResource("/exampleIcon.png"));
		}
		return icon;
	}

}
