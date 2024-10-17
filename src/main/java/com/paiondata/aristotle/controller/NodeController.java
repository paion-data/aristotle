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
import com.paiondata.aristotle.model.dto.GraphNodeDTO;
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
     * Retrieves a node by UUID.
     *
     * @param uuid the UUID of the node
     * @return the result containing the node or an error message if not found
     */
    @ApiOperation(value = "Retrieves a node by UUID")
    @GetMapping("/{uuid}")
    public Result<NodeVO> getNodeByUuid(
            @PathVariable @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK) final String uuid) {
        final Optional<NodeVO> optionalGraphNode = nodeService.getNodeByUuid(uuid);
        return optionalGraphNode.map(Result::ok).orElseGet(() -> Result.fail(Message.GRAPH_NODE_NULL + uuid));
    }

    /**
     * Creates and add relationships among several nodes.
     * One can create nodes without binding any relations.
     *
     * @param graphNodeCreateDTO the DTO containing the graph node creation information
     * @return the created graph
     */
    @ApiOperation(value = "Creates and binds nodes",
            notes = "The nodes could be created without binding any relations")
    @PostMapping
    public Result<List<NodeReturnDTO>> createAndBindNode(@RequestBody @Valid final NodeCreateDTO graphNodeCreateDTO) {
        return Result.ok(Message.CREATE_SUCCESS, nodeService.createAndBindGraphAndNode(graphNodeCreateDTO, null));
    }

    /**
     * Creates a graph and binds it with a graph node.
     * One can create a graph without adding any nodes, or add nodes without adding any relationships
     *
     * @param graphNodeCreateDTO the DTO containing the graph and node creation information
     * @return the created graph and nodes
     */
    @ApiOperation(value = "Creates a graph and binds it with nodes",
            notes = "You can create just graphs, or just graphs and nodes without binding any relations between nodes")
    @PostMapping("/graph")
    public Result<GraphNodeDTO> createGraphAndBindGraphAndNode(
            @RequestBody @Valid final GraphAndNodeCreateDTO graphNodeCreateDTO) {
        return Result.ok(nodeService.createGraphAndBindGraphAndNode(graphNodeCreateDTO, null));
    }

    /**
     * Binds multiple nodes.
     *
     * @param dtos the list of DTOs containing the binding information
     * @return the result indicating the success of the binding
     */
    @ApiOperation(value = "Binds multiple nodes")
    @PostMapping("/bind")
    public Result<String> bindNodes(@RequestBody @Valid final List<BindNodeDTO> dtos) {
        nodeService.bindNodes(dtos, null);
        return Result.ok(Message.BOUND_SUCCESS);
    }

    /**
     * Updates a graph node.
     * Please note that this is an overwrite operation, and the properties passed in will overwrite the existing ones.
     *
     * @param nodeUpdateDTO the DTO containing the updated node information
     * @return the result indicating the success of the update
     */
    @ApiOperation(value = "Updates a node")
    @PostMapping("/update")
    public Result<String> updateNode(@RequestBody @Valid final NodeUpdateDTO nodeUpdateDTO) {
        nodeService.updateNode(nodeUpdateDTO, null);
        return Result.ok(Message.UPDATE_SUCCESS);
    }

    /**
     * Updates a relation between graph nodes.
     *
     * @param relationUpdateDTO the DTO containing the relation update information
     * @return the result indicating the success of the update
     */
    @ApiOperation(value = "Updates a relation between nodes")
    @PutMapping("/relate")
    public Result<String> updateNodeRelation(@RequestBody @Valid final RelationUpdateDTO relationUpdateDTO) {
        nodeService.updateRelation(relationUpdateDTO);
        return Result.ok(Message.UPDATE_SUCCESS);
    }

    /**
     * Deletes graph nodes by their UUIDs.
     *
     * @param nodeDeleteDTO the list of UUIDs of the graph nodes to be deleted
     * @return the result indicating the success of the deletion
     */
    @ApiOperation(value = "Deletes nodes by their UUIDs")
    @DeleteMapping
    public Result<String> deleteNode(@RequestBody @Valid final NodeDeleteDTO nodeDeleteDTO) {
        nodeService.deleteByUuids(nodeDeleteDTO);
        return Result.ok(Message.DELETE_SUCCESS);
    }
}
