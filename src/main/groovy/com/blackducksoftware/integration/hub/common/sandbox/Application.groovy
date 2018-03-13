/*
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
package com.blackducksoftware.integration.hub.common.sandbox

import javax.annotation.PostConstruct

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

import com.blackducksoftware.integration.hub.api.generated.discovery.ApiDiscovery
import com.blackducksoftware.integration.hub.configuration.HubServerConfig
import com.blackducksoftware.integration.hub.configuration.HubServerConfigBuilder
import com.blackducksoftware.integration.hub.configuration.HubServerConfigValidator
import com.blackducksoftware.integration.hub.rest.RestConnection
import com.blackducksoftware.integration.hub.service.HubService
import com.blackducksoftware.integration.hub.service.HubServicesFactory
import com.blackducksoftware.integration.hub.service.model.HubServerVerifier
import com.blackducksoftware.integration.hub.service.model.UriCombiner
import com.blackducksoftware.integration.log.IntLogger
import com.blackducksoftware.integration.log.Slf4jIntLogger

@SpringBootApplication
class Application {
	private final Logger logger = LoggerFactory.getLogger(Application.class)

	static void main(final String[] args) {
		new SpringApplicationBuilder(Application.class).logStartupInfo(false).run(args)
	}

	@PostConstruct
	void init() {
		getAllProjectsDefault()
		println '----------------------------------------------'
		println '-----The custom stuff should print now--------'
		println '----------------------------------------------'
		getAllProjectsCustom()
	}

	void getAllProjectsDefault() {
		IntLogger slf4jIntLogger = new Slf4jIntLogger(logger)

		HubServerConfigBuilder hubServerConfigBuilder = new HubServerConfigBuilder()
		hubServerConfigBuilder.setHubUrl(System.getenv().get('BLACKDUCK_HUB_URL'))
		hubServerConfigBuilder.setApiToken(System.getenv().get('BLACKDUCK_HUB_API_TOKEN'))
		hubServerConfigBuilder.setTimeout(120)
		hubServerConfigBuilder.setAlwaysTrustServerCertificate(true)
		hubServerConfigBuilder.setLogger(slf4jIntLogger)

		HubServerConfig hubServerConfig = hubServerConfigBuilder.build()
		RestConnection restConnection = hubServerConfig.createRestConnection(slf4jIntLogger)
		HubServicesFactory hubServicesFactory = new HubServicesFactory(restConnection);
		HubService hubService = hubServicesFactory.createHubService()
		hubService.getAllResponses(ApiDiscovery.PROJECTS_LINK_RESPONSE).each { println it.name }
	}

	void getAllProjectsCustom() {
		IntLogger slf4jIntLogger = new Slf4jIntLogger(logger)

		UriCombiner uriCombiner = new CustomUriCombiner()
		HubServerVerifier hubServerVerifier = new HubServerVerifier(uriCombiner)
		HubServerConfigValidator hubServerConfigValidator = new HubServerConfigValidator(hubServerVerifier)
		HubServerConfigBuilder hubServerConfigBuilder = new HubServerConfigBuilder(hubServerConfigValidator)
		hubServerConfigBuilder.setHubUrl(System.getenv().get('BLACKDUCK_HUB_URL'))
		hubServerConfigBuilder.setApiToken(System.getenv().get('BLACKDUCK_HUB_API_TOKEN'))
		hubServerConfigBuilder.setTimeout(120)
		hubServerConfigBuilder.setAlwaysTrustServerCertificate(true)
		hubServerConfigBuilder.setLogger(slf4jIntLogger)

		HubServerConfig hubServerConfig = hubServerConfigBuilder.build()
		RestConnection restConnection = hubServerConfig.createRestConnection(slf4jIntLogger)
		HubServicesFactory hubServicesFactory = new HubServicesFactory(restConnection);
		HubService hubService = hubServicesFactory.createHubService(uriCombiner)
		hubService.getAllResponses(ApiDiscovery.PROJECTS_LINK_RESPONSE).each { println it.name }
	}
}
