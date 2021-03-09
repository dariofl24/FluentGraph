package com.tk.fluentGraph.dto;

import java.util.Date;
import java.util.List;

public class ClusterDto
{
    private Integer id;

    private List<NodeContentDto> contents;

    private List<NodeDefinitionDto> nodeDefinitions;

    private List<EdgeDefinitionDto> relations;

    private Date creationDate;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public List<NodeContentDto> getContents()
    {
        return contents;
    }

    public void setContents(final List<NodeContentDto> contents)
    {
        this.contents = contents;
    }

    public List<NodeDefinitionDto> getNodeDefinitions()
    {
        return nodeDefinitions;
    }

    public void setNodeDefinitions(final List<NodeDefinitionDto> nodeDefinitions)
    {
        this.nodeDefinitions = nodeDefinitions;
    }

    public List<EdgeDefinitionDto> getRelations()
    {
        return relations;
    }

    public void setRelations(final List<EdgeDefinitionDto> relations)
    {
        this.relations = relations;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(final Date creationDate)
    {
        this.creationDate = creationDate;
    }
}
