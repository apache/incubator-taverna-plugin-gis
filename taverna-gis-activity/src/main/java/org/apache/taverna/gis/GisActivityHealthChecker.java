/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.apache.taverna.gis;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.taverna.visit.VisitReport;
import org.apache.taverna.visit.VisitReport.Status;
import org.apache.taverna.workflowmodel.health.HealthCheck;
import org.apache.taverna.workflowmodel.health.HealthChecker;

/**
 * Gis <code>HealthChecker</code>.
 */
public class GisActivityHealthChecker implements
		HealthChecker<GisActivity> {

	public boolean canVisit(Object o) {
		// Return True if we can visit the object. We could do
		// deeper (but not time consuming) checks here, for instance
		// if the health checker only deals with GisActivity where
		// a certain configuration option is enabled.
		return o instanceof GisActivity;
	}

	public boolean isTimeConsuming() {
		// Return true if the health checker does a network lookup
		// or similar time consuming checks, in which case
		// it would only be performed when using File->Validate workflow
		// or File->Run.
		return false;
	}

	public VisitReport visit(GisActivity activity, List<Object> ancestry) {
		JsonNode config = activity.getConfiguration();

		// We'll build a list of subreports
		List<VisitReport> subReports = new ArrayList<>();

		if (!URI.create(config.get("exampleUri").asText()).isAbsolute()) {
			// Report Severe problems we know won't work
			VisitReport report = new VisitReport(HealthCheck.getInstance(),
					activity, "Example URI must be absolute", HealthCheck.INVALID_URL,
					Status.SEVERE);
			subReports.add(report);
		}

		if (config.get("exampleString").asText().equals("")) {
			// Warning on possible problems
			subReports.add(new VisitReport(HealthCheck.getInstance(), activity,
					"Example string empty", HealthCheck.NO_CONFIGURATION,
					Status.WARNING));
		}

		// The default explanation here will be used if the subreports list is
		// empty
		return new VisitReport(HealthCheck.getInstance(), activity,
				"Gis service OK", HealthCheck.NO_PROBLEM, subReports);
	}

}
