package com.tk.fluentGraph.model;

public class NodeDefinition
{
    private Integer id;

    private String list_label;

    private String label;

    private String color;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public String getList_label()
    {
        return list_label;
    }

    public void setList_label(final String list_label)
    {
        this.list_label = list_label;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(final String label)
    {
        this.label = label;
    }

    public String getColor()
    {
        return color;
    }

    public void setColor(final String color)
    {
        this.color = color;
    }
}
