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
import com.paiondata.aristotle.model.vo.GraphAndNodeVO;
import com.paiondata.aristotle.model.dto.NodeCreateDTO;
import com.paiondata.aristotle.model.dto.GraphAndNodeCreateDTO;
import com.paiondata.aristotle.model.dto.NodeDeleteDTO;
import com.paiondata.aristotle.model.dto.NodeReturnDTO;
import com.paiondata.aristotle.model.dto.NodeUpdateDTO;
import com.paiondata.aristotle.model.dto.RelationUpdateDTO;
import com.paiondata.aristotle.model.dto.BindNodeDTO;
import com.paiondata.aristotle.model.vo.NodeVO;
import com.paiondata.aristotle.service.NodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

/**
 * Controller for handling graph node-related operations.
 */
@Api(tags = "Node controller for handling graph node-related operations")
@RestController
@RequestMapping("/node")
public class NodeController {

    @Autowired
    private NodeService nodeService;

    /**
     * Retrieves a node by its UUID.
     *
     * <p>
     * This method handles a GET request to retrieve a node based on the provided UUID.
     * It validates the UUID and calls the node service to fetch the node data.
     * If the node is found, it is wrapped in a {@link Result} object and returned.
     * If the node is not found, a failure result with an appropriate message is returned.
     *
     * @param uuid the UUID of the node to retrieve
     * @return a {@link Result} object containing the node data as a {@link NodeVO},
     * or a failure message if the node is not found
     */
    @ApiOperation(value = "Retrieves a node by UUID")
    @GetMapping("/{uuid}")
    public Result<NodeVO> getNodeByUuid(
            @PathVariable @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK) final String uuid) {
        final Optional<NodeVO> optionalGraphNode = nodeService.getNodeByUuid(uuid);
        return optionalGraphNode.map(Result::ok).orElseGet(() -> Result.fail(Message.GRAPH_NODE_NULL + uuid));
    }

    /**
     * Creates and binds nodes.
     *
     * <p>
     * This method handles a POST request to create and optionally bind nodes.
     * It validates the input DTO and calls the node service to create the nodes.
     * If specified, it also binds the nodes with relationships.
     * The result is wrapped in a {@link Result} object with a success message and the list of created nodes.
     *
     * @param graphNodeCreateDTO the {@link NodeCreateDTO} containing the node creation and binding information
     * @return a {@link Result} object containing a success message and a list of created nodes as {@link NodeReturnDTO}
     * @notes The nodes could be created without binding any relations
     */
    @ApiOperation(value = "Creates and binds nodes",
            notes = "The nodes could be created without binding any relations")
    @PostMapping
    public Result<List<NodeReturnDTO>> createAndBindNode(@RequestBody @Valid final NodeCreateDTO graphNodeCreateDTO) {
        return Result.ok(Message.CREATE_SUCCESS, nodeService.createAndBindGraphAndNode(graphNodeCreateDTO, null));
    }

    /**
     * Creates a graph and binds it with nodes.
     *
     * <p>
     * This method handles a POST request to create a graph and optionally bind it with nodes.
     * It validates the input DTO and calls the node service to create the graph and nodes.
     * If specified, it also binds the nodes with relationships.
     * The result is wrapped in a {@link Result} object with a success message and the created graph data.
     *
     * @param graphNodeCreateDTO the {@link GraphAndNodeCreateDTO} containing the graph and node creation information
     * @return a {@link Result} object containing a success message and the created graph data as {@link GraphAndNodeVO}
     * @notes You can create just graphs, or just graphs and nodes without binding any relations between nodes
     */
    @ApiOperation(value = "Creates a graph and binds it with nodes",
            notes = "You can create just graphs, or just graphs and nodes without binding any relations between nodes")
    @PostMapping("/graph")
    public Result<GraphAndNodeVO> createGraphAndBindGraphAndNode(
            @RequestBody @Valid final GraphAndNodeCreateDTO graphNodeCreateDTO) {
        return Result.ok(nodeService.createGraphAndBindGraphAndNode(graphNodeCreateDTO, null));
    }

    /**
     * Binds multiple nodes.
     *
     * <p>
     * This method handles a POST request to bind multiple nodes.
     * It validates the input DTOs and calls the node service to perform the binding.
     * The result is wrapped in a {@link Result} object with a success message.
     *
     * @param dtos a list of {@link BindNodeDTO} objects containing the binding information for the nodes
     * @return a {@link Result} object containing a success message
     */
    @ApiOperation(value = "Binds multiple nodes")
    @PostMapping("/bind")
    public Result<String> bindNodes(@RequestBody @Valid final List<BindNodeDTO> dtos) {
        nodeService.bindNodes(dtos, null);
        return Result.ok(Message.BOUND_SUCCESS);
    }

    /**
     * Updates a node.
     *
     * <p>
     * This method handles a POST request to update a node based on the provided update DTO.
     * It validates the input DTO and calls the node service to perform the update.
     * The result is wrapped in a {@link Result} object with a success message.
     *
     * @param nodeUpdateDTO the {@link NodeUpdateDTO} containing the updated node information
     * @return a {@link Result} object containing a success message
     */
    @ApiOperation(value = "Updates a node")
    @PostMapping("/update")
    public Result<String> updateNode(@RequestBody @Valid final NodeUpdateDTO nodeUpdateDTO) {
        nodeService.updateNode(nodeUpdateDTO, null);
        return Result.ok(Message.UPDATE_SUCCESS);
    }

    /**
     * Updates a relation between nodes.
     *
     * <p>
     * This method handles a PUT request to update a relation between nodes based on the provided update DTO.
     * It validates the input DTO and calls the node service to perform the relation update.
     * The result is wrapped in a {@link Result} object with a success message.
     *
     * @param relationUpdateDTO the {@link RelationUpdateDTO} containing the updated relation information
     * @return a {@link Result} object containing a success message
     */
    @ApiOperation(value = "Updates a relation between nodes")
    @PutMapping("/relate")
    public Result<String> updateNodeRelation(@RequestBody @Valid final RelationUpdateDTO relationUpdateDTO) {
        nodeService.updateRelation(relationUpdateDTO);
        return Result.ok(Message.UPDATE_SUCCESS);
    }

    /**
     * Deletes nodes by their UUIDs.
     *
     * <p>
     * This method handles a DELETE request to delete nodes based on the provided UUIDs.
     * It validates the input DTO and calls the node service to perform the deletion.
     * The result is wrapped in a {@link Result} object with a success message.
     *
     * @param nodeDeleteDTO the {@link NodeDeleteDTO} containing the UUIDs of the nodes to delete
     * @return a {@link Result} object containing a success message
     */
    @ApiOperation(value = "Deletes nodes by their UUIDs")
    @DeleteMapping
    public Result<String> deleteNode(@RequestBody @Valid final NodeDeleteDTO nodeDeleteDTO) {
        nodeService.deleteByUuids(nodeDeleteDTO);
        return Result.ok(Message.DELETE_SUCCESS);
    }
}
