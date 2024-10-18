/*
 * Copyright 2024 Paion Data
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.paiondata.aristotle.controller;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.base.Result;
import com.paiondata.aristotle.model.dto.FilterQueryGraphDTO;
import com.paiondata.aristotle.model.dto.GraphDeleteDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.vo.GraphVO;
import com.paiondata.aristotle.service.GraphService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * Controller for handling graph-related operations.
 */
@Api(tags = "Graph controller for handling graph-related operations")
@RestController
@RequestMapping("/graph")
public class GraphController {

    @Autowired
    private GraphService graphService;

    /**
     * Retrieves a graph by its UUID and filter parameters.
     * <p>
     * This method handles a POST request to retrieve a graph based on the provided UUID and filter parameters.
     * It validates the input DTO and calls the graph service to fetch the graph data.
     * The result is wrapped in a {@link Result} object and returned.
     *
     * @param dto the {@link FilterQueryGraphDTO} containing the UUID and filter parameters
     * @return a {@link Result} object containing the graph data as a {@link GraphVO}
     */
    @ApiOperation(value = "Retrieve the graph by uuid and filter parameters")
    @PostMapping
    public Result<GraphVO> getGraphByUuidAndFilterParams(@RequestBody @Valid final FilterQueryGraphDTO dto) {
        return Result.ok(graphService.getGraphVOByUuid(dto));
    }

    /**
     * Updates a graph.
     *
     * <p>
     * This method handles a PUT request to update a graph based on the provided update DTO.
     * It validates the input DTO and calls the graph service to perform the update.
     * The result is wrapped in a {@link Result} object with a success message.
     *
     * @param graphUpdateDTO the {@link GraphUpdateDTO} containing the updated graph information
     * @return a {@link Result} object containing a success message
     */
    @ApiOperation(value = "Updates a graph")
    @PutMapping
    public Result<String> updateGraph(@RequestBody @Valid final GraphUpdateDTO graphUpdateDTO) {
        graphService.updateGraph(graphUpdateDTO, null);
        return Result.ok(Message.UPDATE_SUCCESS);
    }

    /**
     * Deletes graphs by their UUIDs.
     *
     * <p>
     * This method handles a DELETE request to delete graphs based on the provided UUIDs.
     * It validates the input DTO and calls the graph service to perform the deletion.
     * The result is wrapped in a {@link Result} object with a success message.
     *
     * @param graphDeleteDTO the {@link GraphDeleteDTO} containing the UUIDs of the graphs to delete
     * @return a {@link Result} object containing a success message
     */
    @ApiOperation(value = "Deletes graphs by their UUIDs")
    @DeleteMapping
    public Result<String> deleteGraph(@RequestBody @Valid final GraphDeleteDTO graphDeleteDTO) {
        graphService.deleteByUuids(graphDeleteDTO);
        return Result.ok(Message.DELETE_SUCCESS);
    }
}
