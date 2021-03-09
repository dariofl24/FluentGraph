package com.tk.fluentGraph.model;

import java.util.Date;
import java.util.List;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "clusters", schemaVersion = "1.0")
public class Cluster
{
    @Id
    private Integer id;

    private List<NodeContent> contents;

    private List<NodeDefinition> nodeDefinitions;

    private List<EdgeDefinition> relations;

    private Date creationDate;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public List<NodeContent> getContents()
    {
        return contents;
    }

    public void setContents(final List<NodeContent> contents)
    {
        this.contents = contents;
    }

    public List<NodeDefinition> getNodeDefinitions()
    {
        return nodeDefinitions;
    }

    public void setNodeDefinitions(final List<NodeDefinition> nodeDefinitions)
    {
        this.nodeDefinitions = nodeDefinitions;
    }

    public List<EdgeDefinition> getRelations()
    {
        return relations;
    }

    public void setRelations(final List<EdgeDefinition> relations)
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
