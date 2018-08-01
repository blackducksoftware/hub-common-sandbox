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

import com.blackducksoftware.integration.exception.EncryptionException
import com.blackducksoftware.integration.exception.IntegrationException
import com.blackducksoftware.integration.hub.api.generated.component.ProjectRequest
import com.blackducksoftware.integration.hub.api.generated.discovery.ApiDiscovery
import com.blackducksoftware.integration.hub.api.generated.view.NotificationUserView
import com.blackducksoftware.integration.hub.api.generated.view.ProjectView
import com.blackducksoftware.integration.hub.api.generated.view.UserView
import com.blackducksoftware.integration.hub.api.generated.view.VersionBomPolicyStatusView
import com.blackducksoftware.integration.hub.api.view.MetaHandler
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.configuration.HubScanConfig
import com.blackducksoftware.integration.hub.configuration.HubScanConfigBuilder
import com.blackducksoftware.integration.hub.configuration.HubServerConfig
import com.blackducksoftware.integration.hub.configuration.HubServerConfigBuilder
import com.blackducksoftware.integration.hub.exception.DoesNotExistException
import com.blackducksoftware.integration.hub.rest.BlackduckRestConnection
import com.blackducksoftware.integration.hub.service.*
import com.blackducksoftware.integration.hub.service.model.PolicyStatusDescription
import com.blackducksoftware.integration.hub.service.model.ProjectRequestBuilder
import com.blackducksoftware.integration.hub.service.model.ProjectVersionWrapper
import com.blackducksoftware.integration.log.IntLogger
import com.blackducksoftware.integration.log.Slf4jIntLogger
import com.google.gson.Gson
import com.google.gson.JsonParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

import javax.annotation.PostConstruct
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@SpringBootApplication
class Application {
    private final Logger logger = LoggerFactory.getLogger(Application.class)

    public static final long FIVE_SECONDS = 5 * 1000;

    static void main(final String[] args) {
        new SpringApplicationBuilder(Application.class).logStartupInfo(false).run(args)
    }

    @PostConstruct
    void init() throws Exception {
        createPolicyViolation()
        checkPolicyStatus();
        checkPolicyStatus();
        checkPolicyStatus();
        checkPolicyStatus();
        checkPolicyStatus();
        checkPolicyStatus();
        checkPolicyStatus();
        checkPolicyStatus();
        checkPolicyStatus();
        checkPolicyStatus();
    }

    void createPolicyViolation() {
        IntLogger intLogger = createIntLogger();
        HubServerConfig hubServerConfig = createHubServerConfig(intLogger);
        HubServicesFactory hubServicesFactory = createHubServicesFactory(intLogger, hubServerConfig);

        ProjectService projectService = hubServicesFactory.createProjectService();
        PolicyRuleService policyRuleService = hubServicesFactory.createPolicyRuleService();
        ComponentService componentService = hubServicesFactory.createComponentService();
        MetaHandler metaHandler = new MetaHandler(intLogger);
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        CodeLocationService codeLocationService = hubServicesFactory.createCodeLocationService();
        HubService hubService = hubServicesFactory.createHubService();

        ProjectManager projectManager = new ProjectManager(projectService);
        PolicyRuleCreator policyRuleCreator = new PolicyRuleCreator(policyRuleService, componentService, metaHandler, externalIdFactory);
        BomUpdater bomUpdater = new BomUpdater(codeLocationService, hubService);

        bomUpdater.removeCommonsFileUpload();

        try {
            projectManager.deleteProject("ek-test-scan-speed");
        } catch (DoesNotExistException e) {
            System.out.println("project didn't exist - no need to delete");
        }

        ProjectVersionWrapper projectVersionWrapper = projectManager.createProject("ek-test-scan-speed", "4.8.0-test", "pleasework");

        policyRuleCreator.createNoCommonsFileUpload();

        bomUpdater.addCommonsFileUpload();

        VersionBomPolicyStatusView versionBomPolicyStatusView = projectService.getPolicyStatusForVersion(projectVersionWrapper.projectVersionView);
        PolicyStatusDescription policyStatusDescription = new PolicyStatusDescription(versionBomPolicyStatusView);
        System.out.println(policyStatusDescription.policyStatusMessage);
    }

    void checkPolicyStatus() {
        IntLogger intLogger = createIntLogger();
        HubServerConfig hubServerConfig = createHubServerConfig(intLogger);
        HubServicesFactory hubServicesFactory = createHubServicesFactory(intLogger, hubServerConfig);

        ProjectService projectService = hubServicesFactory.createProjectService();
        VersionBomPolicyStatusView versionBomPolicyStatusView = projectService.getPolicyStatusForProjectAndVersion("ek-test-scan-speed", "4.8.0-test");
        PolicyStatusDescription policyStatusDescription = new PolicyStatusDescription(versionBomPolicyStatusView);
        System.out.println(policyStatusDescription.policyStatusMessage);
    }

    void getAllProjects() throws IntegrationException {
        HubServicesFactory hubServicesFactory = createHubServicesFactory();

        HubService hubService = hubServicesFactory.createHubService();
        List<ProjectView> allProjects = hubService.getAllResponses(ApiDiscovery.PROJECTS_LINK_RESPONSE);
        allProjects.each {
            println it.name
        }
    }

    void scanAPath() {
        IntLogger intLogger = createIntLogger();
        HubServerConfig hubServerConfig = createHubServerConfig(intLogger);
        HubServicesFactory hubServicesFactory = createHubServicesFactory(intLogger, hubServerConfig);

        SignatureScannerService signatureScannerService = hubServicesFactory.createSignatureScannerService();
        HubScanConfigBuilder hubScanConfigBuilder = new HubScanConfigBuilder();
        hubScanConfigBuilder.toolsDir = new File("/Users/ekerwin/working/java_scanner");
        hubScanConfigBuilder.workingDirectory = new File("/Users/ekerwin/working/scan_output");
        hubScanConfigBuilder.addScanTargetPath("/Users/ekerwin/Downloads/winzipmacedition60.dmg");
        hubScanConfigBuilder.setScanMemory(4096);
        HubScanConfig hubScanConfig = hubScanConfigBuilder.build()

        ProjectRequestBuilder projectRequestBuilder = new ProjectRequestBuilder();
        projectRequestBuilder.setProjectName("ek-winzip");
        projectRequestBuilder.setVersionName("0.0.1-SNAPSHOT");
        ProjectRequest projectRequest = projectRequestBuilder.build();

        signatureScannerService.executeScans(hubServerConfig, hubScanConfig, projectRequest);
    }

    public void getAllNotificationsForTheLastHour() throws IntegrationException {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime oneHourAgo = now.minus(1, ChronoUnit.HOURS);

        Date start = Date.from(oneHourAgo.toInstant());
        Date end = Date.from(now.toInstant());

        System.out.println(start);
        System.out.println(end);

        HubServicesFactory hubServicesFactory = createHubServicesFactory();
        UserGroupService userGroupService = hubServicesFactory.createUserGroupService();
        NotificationService notificationService = hubServicesFactory.createNotificationService();

        UserView userView = userGroupService.getUserByUserName("sysadmin");
        List<NotificationUserView> notificationUserViews = notificationService.getAllUserNotifications(userView, start, end);

        notificationUserViews.each {
            println it.json
        }
    }

    public HubServicesFactory createHubServicesFactory() {
        IntLogger intLogger = createIntLogger();
        HubServerConfig hubServerConfig = createHubServerConfig(intLogger);
        return createHubServicesFactory(intLogger, hubServerConfig);
    }

    public IntLogger createIntLogger() {
        final IntLogger intLogger = new Slf4jIntLogger(logger);
        return intLogger;
    }

    public HubServerConfig createHubServerConfig(IntLogger intLogger) {
        final HubServerConfigBuilder hubServerConfigBuilder = new HubServerConfigBuilder();
        //        hubServerConfigBuilder.setUrl(System.getenv().get("HUB_QA_URL"));
        hubServerConfigBuilder.setUrl(System.getenv().get("INT-HUB02_URL"));
        //        hubServerConfigBuilder.setUrl(System.getenv().get("INT-HUB04_URL"));
        //        hubServerConfigBuilder.setUrl(System.getenv().get("BLACKDUCK_HUB_URL"));
        hubServerConfigBuilder.setApiToken(System.getenv().get("BLACKDUCK_HUB_API_TOKEN"));
        hubServerConfigBuilder.setUsername(System.getenv().get("BLACKDUCK_HUB_USERNAME"));
        hubServerConfigBuilder.setPassword(System.getenv().get("BLACKDUCK_HUB_PASSWORD"));
        hubServerConfigBuilder.setTimeout(120);
        hubServerConfigBuilder.setTrustCert(true);
        hubServerConfigBuilder.setLogger(intLogger);

        final HubServerConfig hubServerConfig = hubServerConfigBuilder.build();
        return hubServerConfig;
    }

    public HubServicesFactory createHubServicesFactory(IntLogger intLogger, HubServerConfig hubServerConfig) throws EncryptionException {
        final BlackduckRestConnection blackduckRestConnection = hubServerConfig.createRestConnection(intLogger);

        final Gson gson = HubServicesFactory.createDefaultGson();
        final JsonParser jsonParser = HubServicesFactory.createDefaultJsonParser();
        final HubServicesFactory hubServicesFactory = new HubServicesFactory(gson, jsonParser, blackduckRestConnection, intLogger);

        return hubServicesFactory;
    }
}
