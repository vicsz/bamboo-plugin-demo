package com.example;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.plan.PlanManager;
import com.atlassian.bamboo.task.*;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

@Scanned
public class TaskExample implements TaskType
{
    @ComponentImport
    private final PlanManager planManager;

    public TaskExample(final PlanManager planManager)
    {
        this.planManager = planManager;
    }

    @Override
    public TaskResult execute(final TaskContext taskContext) throws TaskException
    {
        final BuildLogger buildLogger = taskContext.getBuildLogger();

        buildLogger.addBuildLogEntry("Hello, World!!!!");
        buildLogger.addBuildLogEntry("Number of plans:" + planManager.getPlanCount());

        return TaskResultBuilder.create(taskContext).success().build();
    }
}