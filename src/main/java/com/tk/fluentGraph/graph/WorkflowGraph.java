package com.tk.fluentGraph.graph;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tk.fluentGraph.model.Cluster;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

@Component
public class WorkflowGraph
{
    private final ClusterBuilderService clusterBuilderService;

    public WorkflowGraph(final ClusterBuilderService clusterBuilderService)
    {
        this.clusterBuilderService = clusterBuilderService;
    }

    public List<Cluster> buildClusters(final List<File> workflows) throws IOException
    {
        final List<Map<String, Object>> ruleSets = getRulesets(workflows);

        final List<Node> sortedGraphList = buildNodesList(ruleSets);

        return buildClusterModels(sortedGraphList);
    }

    private List<Map<String, Object>> getRulesets(final List<File> workflows) throws IOException
    {
        final ObjectMapper mapper = new ObjectMapper();

        final List<Map<String, Object>> allRulesets = Lists.newArrayList();

        for (final File workflow : workflows)
        {
            final Map<String, Object> map = mapper.readValue(workflow, Map.class);

            final Map<String, Object> collectionValues = map.entrySet().stream()
                .filter(ent -> ent.getValue() instanceof Collection)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            allRulesets.addAll((List<Map<String, Object>>) collectionValues.get("rulesets"));
        }

        return allRulesets;
    }

    private List<Cluster> buildClusterModels(final List<Node> sortedGraphList)
    {
        final Optional<Integer> max = sortedGraphList.stream()
            .map(Node::getVal)
            .max(Comparator.naturalOrder());

        if (max.isPresent())
        {
            final List<List<Node>> clusters = Lists.newArrayList();

            for (int i = 1; i <= max.get(); i++)
            {
                final List<Node> cluster = getCluster(i, sortedGraphList);

                clusters.add(cluster);
            }

            final List<List<Node>> sortedClusters = clusters.stream()
                .sorted(Comparator.comparingInt(List::size))
                .collect(Collectors.toList());

            final AtomicInteger clusterNum = new AtomicInteger(max.get());

            final List<Cluster> clusterModels = sortedClusters.stream()
                .map(nodes ->
                    clusterBuilderService.buildClusterModel(nodes, clusterNum.decrementAndGet())
                ).collect(Collectors.toList());

            return clusterModels;
        }

        return Lists.newArrayList();
    }

    private List<Node> buildNodesList(final List<Map<String, Object>> ruleSets)
    {
        final Map<String, List<Node>> name2NodeMap = Maps.newHashMap();

        ruleSets.stream()
            .map(this::ruleset2Node)
            .forEach(node ->
            {

                if (name2NodeMap.containsKey(node.getName()))
                {
                    name2NodeMap.get(node.getName()).add(node);
                }
                else
                {
                    name2NodeMap.put(node.getName(), Lists.newArrayList(node));
                }
            });

        name2NodeMap.values().stream()
            .flatMap(Collection::stream)
            .forEach(node -> relateNodes(node, name2NodeMap));

        final List<Node> nodeList = name2NodeMap.values().stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        addNodeClusterValues(nodeList);

        return nodeList.stream()
            .sorted(Comparator.comparing(Node::getVal))
            .collect(Collectors.toList());
    }

    private void addNodeClusterValues(final List<Node> nodeList)
    {
        int clusterValue = INTEGER_ZERO;

        for (final Node root : nodeList)
        {
            if (isNull(root.getVal()))
            {
                root.setVal(++clusterValue, INTEGER_ZERO);
            }
        }
    }

    private List<Node> getCluster(final Integer cluster, final List<Node> sortedNodeList)
    {
        return sortedNodeList.stream()
            .filter(node -> node.getVal().equals(cluster))
            .sorted(Comparator.comparing(Node::getLevel))
            .collect(Collectors.toList());
    }

    private void relateNodes(final Node node, final Map<String, List<Node>> name2NodeMap)
    {
        final List<String> stringProps = node.getStringProps();

        if (isNotEmpty(stringProps))
        {
            final List<Node> childs = name2NodeMap.entrySet().stream()
                .filter(ent -> stringProps.contains(ent.getKey()))
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

            if (isNotEmpty(childs))
            {
                childs.stream()
                    .forEach(child -> child.getParents().add(node));
                node.appendChildren(childs);
            }
        }
    }

    private Node ruleset2Node(final Map<String, Object> ruleSet)
    {
        final Node node = new Node();

        node.setName((String) ruleSet.get("name"));
        node.setType((String) ruleSet.get("type"));
        node.setSubtype((String) ruleSet.get("subtype"));
        node.setRuleSet(ruleSet);

        if (ruleSet.containsKey("rules"))
        {
            final List<Map> rules = (List<Map>) ruleSet.get("rules");

            for (final Map rule : rules)
            {

                if (Objects.nonNull(rule.get("props")))
                {
                    final Map<String, Object> props = (Map) rule.get("props");
                    final List<String> stringProps = extractStringProps(props);
                    node.appendStringProps(stringProps);
                }
            }
        }

        return node;
    }

    private List<String> extractStringProps(final Map<String, Object> props)
    {
        final List<String> stringProps = props.entrySet().stream()
            .filter(ent -> ent.getValue() instanceof String)
            .map(Map.Entry::getValue)
            .map(String.class::cast)
            .collect(Collectors.toList());

        final Stream<String> stream = props.values().stream()
            .filter(value -> value instanceof List)
            .map(List.class::cast)
            .flatMap(Collection::stream)
            .filter(obj -> obj instanceof String)
            .map(String.class::cast);

        stringProps.addAll(stream.collect(Collectors.toList()));

        props.entrySet().stream()
            .map(Map.Entry::getValue)
            .filter(value -> value instanceof Map)
            .<Map<String, Object>>map(Map.class::cast)
            .map(this::extractStringProps)
            .forEach(stringProps::addAll);

        return stringProps;
    }
}
