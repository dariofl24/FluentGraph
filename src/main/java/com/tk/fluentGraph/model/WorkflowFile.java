package com.tk.fluentGraph.model;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "workflowFiles", schemaVersion = "1.0")
public class WorkflowFile
{
    @Id
    private String id;

    private String location;

    private boolean enabled;

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(final boolean enabled)
    {
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
}
