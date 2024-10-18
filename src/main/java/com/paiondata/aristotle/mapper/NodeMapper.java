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
package com.paiondata.aristotle.mapper;

import com.paiondata.aristotle.model.dto.GetRelationDTO;
import com.paiondata.aristotle.model.dto.NodeDTO;
import com.paiondata.aristotle.model.dto.NodeUpdateDTO;
import com.paiondata.aristotle.model.vo.NodeVO;

import org.neo4j.driver.Transaction;
import java.util.Map;

/**
 * Mapper interface for NodeMapper.
 */
public interface NodeMapper {

    /**
     * Retrieves a graph node by its UUID.
     *
     * @param uuid the UUID of the graph node
     * @return the graph node
     */
    NodeVO getNodeByUuid(String uuid);

    /**
     * Creates a node in the Neo4j database.
     * @param graphUuid the UUID of the graph
     * @param nodeUuid the UUID of the node
     * @param relationUuid the UUID of the link between the created node and the graph this node belongs to
     * @param currentTime the current time
     * @param nodeDTO the NodeDTO object containing the node properties
     * @param tx the Neo4j transaction
     * @return the created Node object
     */
    NodeVO createNode(String graphUuid, String nodeUuid, String relationUuid,
                                String currentTime, NodeDTO nodeDTO, Transaction tx);

    /**
     * Retrieves all relationships by graph uuid.
     * @param uuid the UUID of the graph
     * @param properties the filter properties of the node
     * @return Data Transfer Object (DTO) contains relations and nodes
     */
    GetRelationDTO getRelationByGraphUuid(String uuid, Map<String, String> properties);

    /**
     * Binds two graph nodes with a specified relationship.
     *
     * @param uuid1        the UUID of the first graph node
     * @param uuid2        the UUID of the second graph node
     * @param relation     the name of the relationship
     * @param relationUuid the UUID of the link between the created node and the graph this node belongs to
     * @param currentTime  the current timestamp
     * @param tx the Neo4j transaction
     */
    void bindGraphNodeToGraphNode(String uuid1, String uuid2, String relation,
                                  String relationUuid, String currentTime, Transaction tx);

    /**
     * Updates a graph node by its UUID.
     *
     * @param nodeUpdateDTO the NodeUpdateDTO object containing the updated node properties
     * @param currentTime the current time for update
     * @param tx the Neo4j transaction
     */
    void updateNodeByUuid(NodeUpdateDTO nodeUpdateDTO, String currentTime, Transaction tx);
}
