package com.example;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.deployments.environments.Environment;
import com.atlassian.bamboo.deployments.execution.service.DeploymentExecutionService;
import com.atlassian.bamboo.deployments.projects.DeploymentProject;
import com.atlassian.bamboo.deployments.projects.service.DeploymentProjectService;
import com.atlassian.bamboo.deployments.versions.DeploymentVersion;
import com.atlassian.bamboo.deployments.versions.service.DeploymentVersionService;
import com.atlassian.bamboo.task.*;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import java.util.List;

@Scanned
public class TaskExample implements TaskType
{
    @ComponentImport
    private final DeploymentVersionService deploymentVersionService;

    @ComponentImport
    private final DeploymentProjectService deploymentProjectService;

    @ComponentImport
    private final DeploymentExecutionService deploymentExecutionService;

    public TaskExample(DeploymentVersionService deploymentVersionService, DeploymentProjectService deploymentProjectService, DeploymentExecutionService deploymentExecutionService)
    {
        this.deploymentVersionService = deploymentVersionService;
        this.deploymentProjectService = deploymentProjectService;
        this.deploymentExecutionService = deploymentExecutionService;
    }

    @Override
    public TaskResult execute(final TaskContext taskContext) throws TaskException
    {

        final BuildLogger buildLogger = taskContext.getBuildLogger();

        try {

            final String environmetName = taskContext.getConfigurationMap().get("environment");

            DeploymentProject deploymentProject = getMatchingDeploymentProject("DeploymentProject");

            DeploymentVersion deploymentVersion = deploymentVersionService.getOrCreateDeploymentVersion(deploymentProject.getId(), taskContext.getBuildContext().getParentBuildContext().getPlanResultKey());

            Environment environment = getMatchingEnvironment(deploymentProject, environmetName);

            deploymentExecutionService.prepareDeploymentContext(environment, deploymentVersion, taskContext.getBuildContext().getTriggerReason());

            buildLogger.addBuildLogEntry(deploymentVersion.getName() + " deployment to " + environment + " triggered.");

            return TaskResultBuilder.create(taskContext).success().build();

        }
        catch (Exception exception){

            buildLogger.addErrorLogEntry(exception.getMessage());

            return TaskResultBuilder.create(taskContext).failed().build();

        }

    }

    private DeploymentProject getMatchingDeploymentProject(String name){

        List<DeploymentProject> allDeploymentProjects = deploymentProjectService.getAllDeploymentProjects();

        for (DeploymentProject deploymentProject : allDeploymentProjects) {
            if(deploymentProject.getName().equals(name))
                return deploymentProject;
        }

        throw new RuntimeException("Unable to find deployment project: " + name);
    }

    private Environment getMatchingEnvironment(DeploymentProject deploymentProject, String name) {

        for (Environment environment : deploymentProject.getEnvironments()) {
            if(environment.getName().equals(name))
                return environment;
        }

        throw new RuntimeException("Unable to find environment: " + name);
    }

}