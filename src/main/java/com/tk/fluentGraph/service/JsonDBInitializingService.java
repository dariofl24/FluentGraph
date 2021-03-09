package com.tk.fluentGraph.service;

import com.tk.fluentGraph.model.Cluster;
import com.tk.fluentGraph.model.WorkflowFile;
import io.jsondb.JsonDBOperations;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class JsonDBInitializingService implements InitializingBean
{
    private final JsonDBOperations jsonDBOperations;

    public JsonDBInitializingService(final JsonDBOperations jsonDBOperations)
    {
        this.jsonDBOperations = jsonDBOperations;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        if (!jsonDBOperations.collectionExists(WorkflowFile.class))
        {
            jsonDBOperations.createCollection(WorkflowFile.class);
        }

        if (!jsonDBOperations.collectionExists(Cluster.class))
        {
            jsonDBOperations.createCollection(Cluster.class);
        }
    }
}
