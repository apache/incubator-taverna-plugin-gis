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

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;
import org.apache.taverna.workflowmodel.processor.activity.config.ActivityPortsDefinitionBean;

/**
 * Gis activity configuration bean.
 *
 */
public class GisActivityConfigurationBean extends ActivityPortsDefinitionBean implements Serializable {

	/*
	 * TODO: Remove this comment. Should the jackson ojbect be managed in this class?
	 *
	 * The configuration specifies the variable options and configurations for
	 * an activity that has been added to a workflow. For instance for a WSDL
	 * activity, the configuration contains the URL for the WSDL together with
	 * the method name. String constant configurations contain the string that
	 * is to be returned, while Beanshell script configurations contain both the
	 * scripts and the input/output ports (by subclassing
	 * ActivityPortsDefinitionBean).
	 *
	 * Configuration beans are serialised as XML (currently by using XMLBeans)
	 * when Taverna is saving the workflow definitions. Therefore the
	 * configuration beans need to follow the JavaBeans style and only have
	 * fields of 'simple' types such as Strings, integers, etc. Other beans can
	 * be referenced as well, as long as they are part of the same plugin.
	 */


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
