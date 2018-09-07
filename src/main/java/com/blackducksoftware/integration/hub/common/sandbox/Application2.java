package com.blackducksoftware.integration.hub.common.sandbox;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.service.HubService;
import com.synopsys.integration.blackduck.service.HubServicesFactory;
import com.synopsys.integration.blackduck.service.ProjectService;

public class Application2 {
    private final Logger logger = LoggerFactory.getLogger(Application2.class);

    private static final HubServices hubServices = new HubServices();

    public static void main(final String[] args) throws Exception {
        final Application2 application = new Application2();
        application.init();
    }

    void init() throws Exception {
        final HubServicesFactory hubServicesFactory = hubServices.createHubServicesFactory();

        final HubService hubService = hubServicesFactory.createHubService();
        final ProjectService projectService = hubServicesFactory.createProjectService();

        //        final ProjectView demo = projectService.getProjectByName("Black Ducky Demo");
        final List<ProjectView> allProjects = getAllProjects();
        System.out.println(allProjects.size());
        for (final ProjectView projectView : allProjects) {
            System.out.println(projectView.name + " " + projectView._meta.href);
            //            final List<ProjectVersionView> allVersions = hubService.getAllResponses(projectView, ProjectView.VERSIONS_LINK_RESPONSE);
            //            for (final ProjectVersionView projectVersionView : allVersions) {
            //                System.out.println(projectVersionView.versionName + " " + projectVersionView._meta.href);
            //                final List<VulnerableComponentView> vulnerableComponents = projectService.getVulnerableComponentsForProjectVersion(projectVersionView);
            //                System.out.println(vulnerableComponents.size());
            //            }
            System.out.println("");
            System.out.println("");
        }
    }

    public List<ProjectView> getAllProjects() throws Exception {
        final HubServicesFactory hubServicesFactory = hubServices.createHubServicesFactory();
        final HubService hubService = hubServicesFactory.createHubService();
        return hubService.getAllResponses(ApiDiscovery.PROJECTS_LINK_RESPONSE);
    }

}
