package com.tk.fluentGraph.dto;

public class WorkflowFileDto
{
    private String id;

    private String location;

    private boolean enabled;

    public WorkflowFileDto()
    {

    }

    public WorkflowFileDto(final String id, final String location, final boolean enabled)
    {
        this.id = id;
        this.location = location;
        this.enabled = enabled;
    }

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(final String location)
    {
        this.location = location;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(final boolean enabled)
    {
        this.enabled = enabled;
    }
}
