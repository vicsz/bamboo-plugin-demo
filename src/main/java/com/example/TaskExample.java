package com.example;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;

public class TaskExample implements TaskType
{
    @Override
    public TaskResult execute(final TaskContext taskContext) throws TaskException
    {
        final BuildLogger buildLogger = taskContext.getBuildLogger();

        buildLogger.addBuildLogEntry("Hello, World!");

        return TaskResultBuilder.create(taskContext).success().build();
    }
}