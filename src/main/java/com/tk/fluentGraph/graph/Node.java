package com.tk.fluentGraph.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Lists;
import com.tk.fluentGraph.model.EdgeDefinition;
import com.tk.fluentGraph.model.NodeContent;
import com.tk.fluentGraph.model.NodeDefinition;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class Node
{
    private static int ID = 0;

    private String name;

    private String type;

    private String subtype;

    private Integer val;

    private Integer uuid = ++ID;

    private Integer level;

    private Map<String, Object> ruleSet;

    private final List<Node> children = Lists.newArrayList();

    private final List<Node> parents = Lists.newArrayList();

    private final List<String> stringProps = Lists.newArrayList();

    private static final ArrayList<String> EDGES_COLORS = Lists.newArrayList(
        "247, 37, 133",
        "181, 23, 158",
        "86, 11, 173",
        "58, 12, 163",
        "67, 97, 238",
        "76, 201, 240",
        "129, 178, 154",
        "224, 122, 95",
        "242, 152, 21",
        "152, 160, 147",
        "0, 168, 150",
        "91, 142, 125",
        "10, 147, 150",
        "238, 155, 0",
        "174, 32, 18"
    );

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public List<String> getStringProps()
    {
        return stringProps;
    }

    public void appendStringProps(final List<String> stringProps)
    {
        this.stringProps.addAll(stringProps);
    }

    public List<Node> getChildren()
    {
        return children;
    }

    public void appendChildren(final Node child)
    {
        this.children.add(child);
    }

    public void appendChildren(final List<Node> child)
    {
        this.children.addAll(child);
    }

    public List<Node> getParents()
    {
        return parents;
    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    public void setVal(final Integer val, final Integer level)
    {
        if (this.val == null)
        {
            this.val = val;
            this.level = level;

            children.forEach(node -> node.setVal(val, level + 1));
            parents.forEach(node -> node.setVal(val, level - 1));
        }
    }

    public Integer getVal()
    {
        return val;
    }

    public Integer getLevel()
    {
        return level;
    }

    public String getSubtype()
    {
        return subtype;
    }

    public void setSubtype(final String subtype)
    {
        this.subtype = subtype;
    }

    public NodeDefinition nodeDefinition()
    {
        final String subtypeLabel = StringUtils.isBlank(this.subtype) ? "" : " : " + this.subtype;

        final String label = StringUtils.isBlank(this.type) ? this.name : this.name + " ("
            + this.type + subtypeLabel + ")";

        final NodeDefinition nodeDefinition = new NodeDefinition();

        nodeDefinition.setId(this.uuid);
        nodeDefinition.setList_label(label);
        nodeDefinition.setLabel(this.name);
        nodeDefinition.setColor(getNodeColor());

        return nodeDefinition;
    }

    public NodeContent nodeContent()
    {
        try
        {
            final ObjectWriter objectWriter = getObjectWriter();
            final String json = objectWriter.writeValueAsString(this.ruleSet);

            final NodeContent nodeContent = new NodeContent();

            nodeContent.setId(this.uuid);
            nodeContent.setContent(json);

            return nodeContent;
        }
        catch (final JsonProcessingException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private ObjectWriter getObjectWriter()
    {
        final ObjectMapper mapper = new ObjectMapper();
        final DefaultPrettyPrinter base = new DefaultPrettyPrinter();

        final DefaultPrettyPrinter prettyPrinter = base.withArrayIndenter(new DefaultIndenter());

        return mapper.writer(prettyPrinter);
    }

    private String getNodeColor()
    {
        if (this.type.equalsIgnoreCase("ORDER"))
        {
            return "rgb(126, 214, 221)";
        }
        else if (this.type.equalsIgnoreCase("FULFILMENT"))
        {
            return "rgb(183, 183, 164)";
        }
        else if (this.type.equalsIgnoreCase("RETURN_ORDER"))
        {
            return "rgb(255,153,200)";
        }
        else if (this.type.equalsIgnoreCase("WAVE"))
        {
            return "rgb(249,132,74)";
        }
        else if (this.type.equalsIgnoreCase("LOCATION"))
        {
            return "rgb(255,198,255)";
        }
        else if (this.type.equalsIgnoreCase("INVENTORY_CATALOGUE"))
        {
            return "rgb(215, 138, 118)";
        }
        else if (this.type.equalsIgnoreCase("INVENTORY_POSITION"))
        {
            return "rgb(203,192,211)";
        }
        else if (this.type.equalsIgnoreCase("INVENTORY_QUANTITY"))
        {
            return "rgb(239,211,215)";
        }
        else if (this.type.equalsIgnoreCase("CREDIT_MEMO"))
        {
            return "rgb(224,251,252)";
        }
        else if (this.type.equalsIgnoreCase("BILLING_ACCOUNT"))
        {
            return "rgb(254,200,154)";
        }
        else if (this.type.equalsIgnoreCase("INVOICE"))
        {
            return "rgb(216,226,220)";
        }

        else if (this.type.equalsIgnoreCase("FULFILMENT_PLAN"))
        {
            return "rgb(114,239,221)";
        }
        else if (this.type.equalsIgnoreCase("FULFILMENT_OPTIONS"))
        {
            return "rgb(78,168,222)";
        }
        else if (this.type.equalsIgnoreCase("FULFILMENT_CHOICE"))
        {
            return "rgb(242, 165, 65)";
        }
        else if (this.type.equalsIgnoreCase("ARTICLE"))
        {
            return "rgb(244, 241, 222)";
        }
        else if (this.type.equalsIgnoreCase("CONSIGNMENT"))
        {
            return "rgb(224, 122, 95)";
        }

        return "rgb(219, 239, 148)";
    }

    public List<EdgeDefinition> edgeDefinition()
    {
        if (CollectionUtils.isNotEmpty(this.children))
        {
            return children.stream()
                .map(Node::getUuid)
                .map(uuid ->
                {
                    final EdgeDefinition edgeDefinition = new EdgeDefinition();

                    edgeDefinition.setFrom(this.uuid);
                    edgeDefinition.setTo(uuid);
                    edgeDefinition.setColor(String.format("rgb(%s)", getEdgeColor()));

                    return edgeDefinition;
                }).collect(Collectors.toList());
        }

        return Lists.newArrayList();
    }

    private String getEdgeColor()
    {
        Collections.shuffle(EDGES_COLORS);
        return EDGES_COLORS.stream().findFirst().get();
    }

    @Override
    public String toString()
    {
        return "val='" + val + '\'' + " (" + level + ")";
    }

    public Map<String, Object> getRuleSet()
    {
        return ruleSet;
    }

    public void setRuleSet(final Map<String, Object> ruleSet)
    {
        this.ruleSet = ruleSet;
    }

    public Integer getUuid()
    {
        return uuid;
    }
}
