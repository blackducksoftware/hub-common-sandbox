package com.blackducksoftware.integration.hub.common.sandbox;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.generated.component.ProjectRequest;
import com.blackducksoftware.integration.hub.api.generated.enumeration.ProjectVersionDistributionType;
import com.blackducksoftware.integration.hub.api.generated.enumeration.ProjectVersionPhaseType;
import com.blackducksoftware.integration.hub.api.generated.view.ProjectView;
import com.blackducksoftware.integration.hub.service.ProjectService;
import com.blackducksoftware.integration.hub.service.model.ProjectRequestBuilder;
import com.blackducksoftware.integration.hub.service.model.ProjectVersionWrapper;

public class ProjectManager {
    private final ProjectService projectService;

    public ProjectManager(final ProjectService projectService) {
        this.projectService = projectService;
    }

    public ProjectVersionWrapper createProject(final String projectName, final String projectVersionName) throws IntegrationException {
        final ProjectRequestBuilder projectRequestBuilder = new ProjectRequestBuilder();

        return populateAndSubmit(projectRequestBuilder, projectName, projectVersionName);
    }

    public ProjectVersionWrapper createProject(final String projectName, final String projectVersionName, final String nickname) throws IntegrationException {
        final ProjectRequestBuilder projectRequestBuilder = new ProjectRequestBuilder();
        projectRequestBuilder.setVersionNickname(nickname);

        return populateAndSubmit(projectRequestBuilder, projectName, projectVersionName);
    }

    public void deleteProject(final String projectName) throws IntegrationException {
        final ProjectView createdProject = projectService.getProjectByName(projectName);
        projectService.deleteHubProject(createdProject);
    }

    private ProjectVersionWrapper populateAndSubmit(final ProjectRequestBuilder projectRequestBuilder, final String projectName, final String projectVersionName) throws IntegrationException {
        projectRequestBuilder.setProjectName(projectName);
        projectRequestBuilder.setVersionName(projectVersionName);
        projectRequestBuilder.setPhase(ProjectVersionPhaseType.DEVELOPMENT.name());
        projectRequestBuilder.setDistribution(ProjectVersionDistributionType.OPENSOURCE.name());

        final ProjectRequest projectRequest = projectRequestBuilder.build();
        final ProjectVersionWrapper projectVersionWrapper = projectService.getProjectVersionAndCreateIfNeeded(projectRequest);
        return projectVersionWrapper;
    }

}
