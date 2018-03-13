/**
 * hub-common-sandbox
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.common.sandbox;

import java.net.URL;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.core.HubPath;
import com.blackducksoftware.integration.hub.service.model.UriCombiner;

public class CustomUriCombiner extends UriCombiner {

	@Override
	public String pieceTogetherUri(URL baseUrl, HubPath hubPath) throws IntegrationException {
		System.out.println("this should only print when custom is used (HubPath hubPath)");
		return super.pieceTogetherUri(baseUrl, hubPath);
	}

	@Override
	public String pieceTogetherUri(URL baseUrl, String path) throws IntegrationException {
		System.out.println("this should only print when custom is used (String path)");
		return super.pieceTogetherUri(baseUrl, path);
	}

}
