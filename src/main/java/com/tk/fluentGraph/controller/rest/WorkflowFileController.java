package com.tk.fluentGraph.controller.rest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.tk.fluentGraph.dto.WorkflowFileDto;
import com.tk.fluentGraph.model.WorkflowFile;
import io.jsondb.JsonDBOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@RestController
@RequestMapping(value = "/wffile")
public class WorkflowFileController
{
    private final JsonDBOperations jsonDBOperations;

    public WorkflowFileController(final JsonDBOperations jsonDBOperations)
    {
        this.jsonDBOperations = jsonDBOperations;
    }

    @PostMapping
    public ResponseEntity<WorkflowFileDto> upsert(@RequestBody final WorkflowFileDto dto)
    {
        if (isNotEmpty(dto.getId()) && isNotEmpty(dto.getLocation()))
        {
            final WorkflowFile model = getModel(dto);

            jsonDBOperations.upsert(model);

            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/pathupdate")
    public ResponseEntity updateLocationPaths(@RequestBody final List<WorkflowFileDto> dtos)
    {
        if (isNotEmpty(dtos))
        {
            for (final WorkflowFileDto dto : dtos)
            {
                final WorkflowFile model = jsonDBOperations.findById(dto.getId(), WorkflowFile.class);

                model.setLocation(dto.getLocation());

                jsonDBOperations.save(model, WorkflowFile.class);
            }

            return new ResponseEntity(HttpStatus.ACCEPTED);
        }

        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<WorkflowFileDto> getById(@RequestParam(name = "id") final String id)
    {
        final WorkflowFile byId = jsonDBOperations.findById(id, WorkflowFile.class);

        if (Objects.nonNull(byId))
        {
            return new ResponseEntity<>(getDto(byId), HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/enable")
    public ResponseEntity<WorkflowFileDto> enable(@RequestParam(name = "id") final String id,
        @RequestParam(name = "enabled") final String enabled)
    {
        final WorkflowFile byId = jsonDBOperations.findById(id, WorkflowFile.class);

        if (Objects.nonNull(byId))
        {
            byId.setEnabled(Boolean.valueOf(enabled));

            jsonDBOperations.upsert(byId);

            return new ResponseEntity<>(getDto(byId), HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<WorkflowFileDto>> getAll()
    {
        final List<WorkflowFile> all = jsonDBOperations.findAll(WorkflowFile.class);

        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(all))
        {
            final List<WorkflowFileDto> fileDtos = all.stream()
                .map(this::getDto)
                .collect(Collectors.toList());
            return new ResponseEntity<>(fileDtos, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(Lists.newArrayList(), HttpStatus.OK);
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity removeWorkflowFile(@RequestParam(name = "id") final String id)
    {
        final WorkflowFile byId = jsonDBOperations.findById(id, WorkflowFile.class);

        if (Objects.nonNull(byId))
        {
            jsonDBOperations.remove(byId, WorkflowFile.class);
            return new ResponseEntity(HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    private WorkflowFile getModel(@RequestBody final WorkflowFileDto dto)
    {
        final WorkflowFile model = new WorkflowFile();

        model.setId(dto.getId());
        model.setLocation(dto.getLocation());
        model.setEnabled(dto.isEnabled());
        return model;
    }

    private WorkflowFileDto getDto(final WorkflowFile model)
    {
        return new WorkflowFileDto(model.getId(), model.getLocation(), model.isEnabled());
    }
}
