package com.tk.fluentGraph.controller.rest;

import java.util.List;
import java.util.stream.Collectors;

import com.tk.fluentGraph.dto.ClusterDto;
import com.tk.fluentGraph.model.Cluster;
import com.tk.fluentGraph.service.WorkflowGraphFacade;
import io.jsondb.JsonDBOperations;
import ma.glasnost.orika.MapperFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@RestController
@RequestMapping(value = "/cluster")
public class ClusterController
{
    private final JsonDBOperations jsonDBOperations;

    private final WorkflowGraphFacade workflowGraphFacade;

    private final MapperFacade mapperFacade;

    public ClusterController(final JsonDBOperations jsonDBOperations, final WorkflowGraphFacade workflowGraphFacade,
        final MapperFacade mapperFacade)
    {
        this.jsonDBOperations = jsonDBOperations;
        this.workflowGraphFacade = workflowGraphFacade;
        this.mapperFacade = mapperFacade;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createClusters()
    {
        try
        {
            workflowGraphFacade.buildNewGraph();
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (final Exception e)
        {
            System.err.println(e.toString());
            e.printStackTrace();
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<ClusterDto> getClusterById(@RequestParam(name = "id") final Integer id)
    {
        final Cluster byId = jsonDBOperations.findById(id, Cluster.class);

        if (nonNull(byId))
        {
            final ClusterDto clusterDto = mapperFacade.map(byId, ClusterDto.class);
            return new ResponseEntity<>(clusterDto, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/all")
    public void deleteClusters()
    {
        jsonDBOperations.dropCollection(Cluster.class);

        if (!jsonDBOperations.collectionExists(Cluster.class))
        {
            jsonDBOperations.createCollection(Cluster.class);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<ClusterDto>> getAll()
    {
        final List<Cluster> all = jsonDBOperations.findAll(Cluster.class);

        if (isNotEmpty(all))
        {
            final List<ClusterDto> clusterDtos = all.stream()
                .map(cluster -> mapperFacade.map(cluster, ClusterDto.class))
                .collect(Collectors.toList());

            return new ResponseEntity<>(clusterDtos, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(newArrayList(), HttpStatus.OK);
        }
    }
}
