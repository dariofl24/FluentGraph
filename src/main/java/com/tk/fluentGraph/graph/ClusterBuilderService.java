package com.tk.fluentGraph.graph;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.tk.fluentGraph.model.Cluster;
import com.tk.fluentGraph.model.EdgeDefinition;
import com.tk.fluentGraph.model.NodeContent;
import com.tk.fluentGraph.model.NodeDefinition;
import org.springframework.stereotype.Component;

@Component
public class ClusterBuilderService
{
    public Cluster buildClusterModel(final List<Node> sortedGraphList, final Integer cluster)
    {
        final Cluster clusterObj = new Cluster();

        clusterObj.setId(cluster);

        final List<NodeContent> nodeContents = sortedGraphList.stream()
            .sorted(Comparator.comparing(node -> node.getName().toLowerCase()))
            .map(Node::nodeContent)
            .collect(Collectors.toList());

        clusterObj.setContents(nodeContents);

        final List<NodeDefinition> nodeDefinitions = sortedGraphList.stream()
            .sorted(Comparator.comparing(node -> node.getName().toLowerCase()))
            .map(Node::nodeDefinition)
            .collect(Collectors.toList());

        clusterObj.setNodeDefinitions(nodeDefinitions);

        final List<EdgeDefinition> relations = sortedGraphList.stream()
            .map(Node::edgeDefinition)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        clusterObj.setRelations(relations);

        clusterObj.setCreationDate(new Date());

        return clusterObj;
    }
}
