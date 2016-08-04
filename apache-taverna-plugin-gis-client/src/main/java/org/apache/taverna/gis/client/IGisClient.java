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
package org.apache.taverna.gis.client;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

public interface IGisClient {
	
	// TODO: The interface is specific to WPS services. It should be generic so that it can handle different 
	// geospatial web services
	
	
	/**
	 * @param serviceURI - the URI of the GWS that will return the capabilities
	 * @return 
	 */
	public String getServiceCapabilities(URI serviceURI);
	
	public List<String> getProcessList();
	
	public HashMap<String, Integer> getProcessInputPorts(String processID);
	
	public HashMap<String, Integer> getProcessOutputPorts(String processID);
	
	public List<PortTypeDescriptor> getTaverna2InputPorts(String processID);
	
	public List<PortTypeDescriptor> getTaverna2OutputPorts(String processID);
	
}
