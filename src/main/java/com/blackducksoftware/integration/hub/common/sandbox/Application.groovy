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

import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery
import com.synopsys.integration.blackduck.api.generated.view.*
import com.synopsys.integration.blackduck.api.view.MetaHandler
import com.synopsys.integration.blackduck.configuration.HubServerConfig
import com.synopsys.integration.blackduck.exception.DoesNotExistException
import com.synopsys.integration.blackduck.service.*
import com.synopsys.integration.blackduck.service.model.PolicyStatusDescription
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper
import com.synopsys.integration.exception.IntegrationException
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.synopsys.integration.log.IntLogger
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

    private static HubServices hubServices = new HubServices();

    static void main(final String[] args) {
        new SpringApplicationBuilder(Application.class).logStartupInfo(false).run(args)
    }

    @PostConstruct
    void init() throws Exception {
        HubServicesFactory hubServicesFactory = hubServices.createHubServicesFactory();

        HubService hubService = hubServicesFactory.createHubService();
        ProjectService projectService = hubServicesFactory.createProjectService();
        ComponentService componentService = hubServicesFactory.createComponentService();

        ProjectView demo = projectService.getProjectByName("Black Ducky Demo");
        List<ProjectVersionView> versions = hubService.getAllResponses(demo, ProjectView.VERSIONS_LINK_RESPONSE);

        versions.each {
            println it.versionName + " " + it._meta.href
            List<VulnerableComponentView> vulnerableComponents = projectService.getVulnerableComponentsForProjectVersion(it);
            println vulnerableComponents.size();
        }
    }

    void createPolicyViolation() {
        IntLogger intLogger = hubServices.createIntLogger();
        HubServerConfig hubServerConfig = hubServices.createHubServerConfig(intLogger);
        HubServicesFactory hubServicesFactory = hubServices.createHubServicesFactory(intLogger, hubServerConfig);

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
        IntLogger intLogger = hubServices.createIntLogger();
        HubServerConfig hubServerConfig = hubServices.createHubServerConfig(intLogger);
        HubServicesFactory hubServicesFactory = hubServices.createHubServicesFactory(intLogger, hubServerConfig);

        ProjectService projectService = hubServicesFactory.createProjectService();
        VersionBomPolicyStatusView versionBomPolicyStatusView = projectService.getPolicyStatusForProjectAndVersion("ek-test-scan-speed", "4.8.0-test");
        PolicyStatusDescription policyStatusDescription = new PolicyStatusDescription(versionBomPolicyStatusView);
        System.out.println(policyStatusDescription.policyStatusMessage);
    }

    void getAllProjects() throws IntegrationException {
        HubServicesFactory hubServicesFactory = hubServices.createHubServicesFactory();

        HubService hubService = hubServicesFactory.createHubService();
        List<ProjectView> allProjects = hubService.getAllResponses(ApiDiscovery.PROJECTS_LINK_RESPONSE);
        allProjects.each {
            println it.name + " " + it._meta.href
        }
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

}
