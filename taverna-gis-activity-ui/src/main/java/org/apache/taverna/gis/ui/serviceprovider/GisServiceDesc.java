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
package org.apache.taverna.gis.ui.serviceprovider;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;

import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.servicedescriptions.ServiceDescription;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class GisServiceDesc extends ServiceDescription {
	public static final URI ACTIVITY_TYPE = URI
			.create("http://ns.taverna.org.uk/2016/service/gis");


	/**
	 * The type of Activity which should be instantiated when adding a service
	 * for this description
	 */
	@Override
	public URI getActivityType() {
		return ACTIVITY_TYPE;
	}

	@Override
	public Configuration getActivityConfiguration() {
		Configuration configuration = new Configuration();
		configuration.setType(ACTIVITY_TYPE.resolve("#Config"));
		ObjectNode json = configuration.getJsonAsObjectNode();
		json.put("ogcServiceUri", ogcServiceUri.toASCIIString());
		json.put("processIdentifier", processIdentifier);
		return configuration;
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
		return Arrays.asList("GIS", "WPS " + getOgcServiceUri());
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

}
