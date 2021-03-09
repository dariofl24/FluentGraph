package com.tk.fluentGraph.service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import com.tk.fluentGraph.graph.WorkflowGraph;
import com.tk.fluentGraph.model.Cluster;
import com.tk.fluentGraph.model.WorkflowFile;
import io.jsondb.JsonDBOperations;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class WorkflowGraphFacade
{
    private final WorkflowGraph workflowGraph;

    private final JsonDBOperations jsonDBOperations;

    public WorkflowGraphFacade(final WorkflowGraph workflowGraph, final JsonDBOperations jsonDBOperations)
    {
        this.workflowGraph = workflowGraph;
        this.jsonDBOperations = jsonDBOperations;
    }

    public void buildNewGraph()
    {
        final List<File> activeWorkflows = getActiveWorkflows();

        try
        {
            final List<Cluster> clusters = workflowGraph.buildClusters(activeWorkflows);

            refreshCollection();

            if (CollectionUtils.isNotEmpty(clusters))
            {
                clusters.forEach(jsonDBOperations::insert);
            }
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private void refreshCollection()
    {
        jsonDBOperations.dropCollection(Cluster.class);

        if (!jsonDBOperations.collectionExists(Cluster.class))
        {
            jsonDBOperations.createCollection(Cluster.class);
        }
    }

    private List<File> getActiveWorkflows()
    {
        final List<WorkflowFile> files = jsonDBOperations.findAll(WorkflowFile.class);

        if (CollectionUtils.isNotEmpty(files))
        {
            return files.stream()
                .filter(WorkflowFile::isEnabled)
                .map(WorkflowFile::getLocation)
                .map(File::new)
                .collect(Collectors.toList());
        }

        return newArrayList();
    }
}
