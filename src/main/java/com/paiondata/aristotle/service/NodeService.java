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
package com.paiondata.aristotle.service;

import com.paiondata.aristotle.model.vo.GraphAndNodeVO;
import com.paiondata.aristotle.model.dto.NodeDeleteDTO;
import com.paiondata.aristotle.model.dto.NodeReturnDTO;
import com.paiondata.aristotle.model.dto.NodeUpdateDTO;
import com.paiondata.aristotle.model.dto.BindNodeDTO;
import com.paiondata.aristotle.model.dto.GraphAndNodeCreateDTO;
import com.paiondata.aristotle.model.dto.NodeCreateDTO;
import com.paiondata.aristotle.model.dto.RelationUpdateDTO;
import com.paiondata.aristotle.model.vo.NodeVO;

import org.neo4j.driver.Transaction;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing graph nodes.
 *
 * This class provides methods for CRUD operations on graph nodes and their relationships.
 */
public interface NodeService {

    /**
     * Retrieves a graph node by its UUID.
     *
     * @param uuid the UUID of the graph node
     * @return an {@code Optional} containing the graph node if found
     */
    Optional<NodeVO> getNodeByUuid(String uuid);

    /**
     * Creates and binds a graph and a node based on the provided DTO.
     *
     * @param nodeCreateDTO the DTO containing information for creating the graph and node
     * @param tx the Neo4j transaction
     * @return the list of created nodes
     */
    List<NodeReturnDTO> createAndBindGraphAndNode(NodeCreateDTO nodeCreateDTO, Transaction tx);

    /**
     * Creates a graph and binds it with a node based on the provided DTO.
     * @param graphNodeCreateDTO the DTO containing information for creating the graph and node
     * @param tx the Neo4j transaction
     * @return the created graph node
     */
    GraphAndNodeVO createGraphAndBindGraphAndNode(GraphAndNodeCreateDTO graphNodeCreateDTO, Transaction tx);

    /**
     * Binds nodes based on the provided DTOs.
     *
     * @param dtos the list of DTOs for binding nodes
     * @param tx   the Neo4j transaction
     */
    void bindNodes(List<BindNodeDTO> dtos, Transaction tx);

    /**
     * Deletes graph nodes by their UUIDs.
     *
     * @param nodeDeleteDTO the list of UUIDs of the graph nodes to be deleted
     */
    void deleteByUuids(NodeDeleteDTO nodeDeleteDTO);

    /**
     * Updates a graph node based on the provided DTO.
     *
     * @param nodeUpdateDTO the DTO containing information for updating the node
     * @param tx   the Neo4j transaction
     */
    void updateNode(NodeUpdateDTO nodeUpdateDTO, Transaction tx);

    /**
     * Updates graph node relations based on the provided DTO.
     *
     * @param relationUpdateDTO the DTO containing information for updating the graph node relations
     */
    void updateRelation(RelationUpdateDTO relationUpdateDTO);
}
