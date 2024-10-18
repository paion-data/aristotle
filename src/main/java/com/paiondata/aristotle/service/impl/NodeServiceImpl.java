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
package com.paiondata.aristotle.service.impl;

import cn.hutool.core.lang.UUID;

import com.paiondata.aristotle.common.annotion.Neo4jTransactional;
import com.paiondata.aristotle.common.base.Constants;
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.DeleteException;
import com.paiondata.aristotle.common.exception.NodeNullException;
import com.paiondata.aristotle.common.exception.NodeRelationException;
import com.paiondata.aristotle.common.exception.GraphNullException;
import com.paiondata.aristotle.common.exception.TemporaryKeyException;
import com.paiondata.aristotle.common.exception.TransactionException;
import com.paiondata.aristotle.mapper.NodeMapper;
import com.paiondata.aristotle.model.dto.BindNodeDTO;
import com.paiondata.aristotle.model.vo.GraphAndNodeVO;
import com.paiondata.aristotle.model.dto.NodeDTO;
import com.paiondata.aristotle.model.dto.NodeDeleteDTO;
import com.paiondata.aristotle.model.dto.NodeRelationDTO;
import com.paiondata.aristotle.model.dto.GraphAndNodeCreateDTO;
import com.paiondata.aristotle.model.dto.NodeReturnDTO;
import com.paiondata.aristotle.model.dto.NodeUpdateDTO;
import com.paiondata.aristotle.model.dto.RelationUpdateDTO;
import com.paiondata.aristotle.model.dto.NodeCreateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.vo.NodeVO;
import com.paiondata.aristotle.repository.NodeRepository;
import com.paiondata.aristotle.service.CommonService;
import com.paiondata.aristotle.service.NodeService;
import lombok.AllArgsConstructor;

import org.neo4j.driver.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Service implementation for managing graph nodes.
 *
 * This class provides methods for CRUD operations on graph nodes and their relationships.
 */
@Service
@AllArgsConstructor
public class NodeServiceImpl implements NodeService {

    private static final Logger LOG = LoggerFactory.getLogger(NodeServiceImpl.class);

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private NodeMapper nodeMapper;

    /**
     * Retrieves a graph node by its UUID.
     *
     * @param uuid the UUID of the graph node
     * @return an {@code Optional} containing the graph node if found, or an empty {@code Optional} if not found
     */
    @Override
    public Optional<NodeVO> getNodeByUuid(final String uuid) {
        final NodeVO graphNode = nodeMapper.getNodeByUuid(uuid);
        return Optional.ofNullable(graphNode);
    }

    /**
     * Creates and binds nodes to an existing graph based on the provided DTO.
     * <p>
     * Checks if the provided Neo4j transaction ({@code tx}) is null.If it is, a {@link TransactionException} is thrown.
     * Retrieves the graph UUID from the {@code nodeCreateDTO}.
     * If the graph is not found, a {@link GraphNullException} is thrown.
     * Calls the {@link #checkInputRelationsAndBindGraphAndNode(List, List, String, Transaction)} method to
     * create and bind the nodes and their relations.
     * Returns the list of created nodes.
     * </p>
     *
     * @param nodeCreateDTO The DTO containing information for creating the nodes and their relations. <br>
     *                      It includes the graph UUID and the node and relation details.
     * @param tx The Neo4j transaction object used for the database operation.
     * @return The list of created nodes, each represented by a {@link NodeReturnDTO} object.
     * @throws TransactionException If the provided transaction is null.
     * @throws GraphNullException If the graph with the specified UUID is not found.
     */
    @Neo4jTransactional
    @Override
    public List<NodeReturnDTO> createAndBindGraphAndNode(final NodeCreateDTO nodeCreateDTO, final Transaction tx) {

        if (tx == null) {
            final String message = Message.TRANSACTION_NULL;
            LOG.error(message);
            throw new TransactionException(message);
        }

        final String graphUuid = nodeCreateDTO.getGraphUuid();

        final Optional<Graph> optionalGraph = commonService.getGraphByUuid(graphUuid);
        if (optionalGraph.isEmpty()) {
            final String message = Message.GRAPH_NULL + graphUuid;
            LOG.error(message);
            throw new GraphNullException(message);
        }

        return checkInputRelationsAndBindGraphAndNode(nodeCreateDTO.getGraphNodeDTO(),
                nodeCreateDTO.getGraphNodeRelationDTO(), graphUuid, tx);
    }

    /**
     * Creates a graph and binds it with a node based on the provided DTO.
     * <p>
     * Checks if the provided Neo4j transaction (`tx`) is null. If it is, a `TransactionException` is thrown.
     * Creates and binds a graph using the provided `GraphCreateDTO` and the transaction.
     * Constructs a `GraphAndNodeVO` object with the UUID, title, and description of the created graph.
     * If no node information is provided in the `graphNodeCreateDTO`, <br>
     * the method returns the constructed `GraphAndNodeVO`.
     * If node information is provided, it calls the `checkInputRelationsAndBindGraphAndNode`
     * method to create and bind the nodes and their relations.
     * Sets the created nodes in the `GraphAndNodeVO` and returns it.
     *
     * @param graphNodeCreateDTO The DTO containing information for creating the graph and node. <br>
     *                           It includes the graph creation details and optional node and relation details.
     * @param tx The Neo4j transaction object used for the database operation.
     * @return The created graph node, represented by a {@link GraphAndNodeVO} object.
     * @throws TransactionException If the provided transaction is null.
     */
    @Override
    @Neo4jTransactional
    public GraphAndNodeVO createGraphAndBindGraphAndNode(final GraphAndNodeCreateDTO graphNodeCreateDTO,
                                                         final Transaction tx) {

        if (tx == null) {
            final String message = Message.TRANSACTION_NULL;
            LOG.error(message);
            throw new TransactionException(message);
        }

        final Graph graph = commonService.createAndBindGraph(graphNodeCreateDTO.getGraphCreateDTO(), tx);

        final GraphAndNodeVO dto = GraphAndNodeVO.builder()
                .uuid(graph.getUuid())
                .title(graph.getTitle())
                .description(graph.getDescription())
                .build();

        if (graphNodeCreateDTO.getGraphNodeDTO() == null) {
            return dto;
        }

        final String graphUuid = graph.getUuid();

        final List<NodeReturnDTO> nodes = checkInputRelationsAndBindGraphAndNode(
                graphNodeCreateDTO.getGraphNodeDTO(),
                graphNodeCreateDTO.getGraphNodeRelationDTO(),
                graphUuid, tx);

        dto.setNodes(nodes);
        return dto;
    }

    /**
     * Checks input relations and binds a graph and its nodes.
     * <p>
     * Retrieves the current time.
     * Creates a map to store UUID mappings.
     * Creates nodes using the provided {@code nodeDTOs} and stores them in a list.
     * If no node relations are provided, it returns the list of created nodes.
     * Collects the IDs of nodes involved in the relations.
     * Retrieves the graph UUIDs associated with the collected node IDs.
     * Verifies that all nodes belong to the same graph by comparing their graph UUIDs.
     * Throws a {@link NodeRelationException} if any node is found to belong to a different graph.
     * Binds the node relations using the provided {@code nodeRelationDTOs}.
     * Returns the list of created nodes.
     *
     * @param nodeDTOs             the list of DTOs for creating nodes. <br>
     *                             Each DTO contains the properties and temporary ID of the node.
     * @param nodeRelationDTOs     the list of DTOs for creating node relations. <br>
     *                             Each DTO contains the {@code fromId} and {@code toId} of the relation.
     * @param graphUuid            the UUID of the graph
     * @param tx                   the Neo4j transaction object used for the database operation
     * @return the list of created nodes, each represented by a {@link NodeReturnDTO} object
     * @throws NodeRelationException if any node is found to belong to a different graph
     */
    private List<NodeReturnDTO> checkInputRelationsAndBindGraphAndNode(final List<NodeDTO> nodeDTOs,
                                                        final List<NodeRelationDTO> nodeRelationDTOs,
                                                        final String graphUuid, final Transaction tx) {
        final String currentTime = getCurrentTime();
        final Map<String, String> uuidMap = new HashMap<>();

        final List<NodeReturnDTO> nodes = createNodes(nodeDTOs, uuidMap, currentTime, graphUuid, tx);

        if (nodeRelationDTOs == null || nodeRelationDTOs.isEmpty()) {
            return nodes;
        }

        final Set<String> checkIds = new HashSet<>();
        for (final NodeRelationDTO dto : nodeRelationDTOs) {
            checkIds.add(dto.getFromId());
            checkIds.add(dto.getToId());
        }

        final List<String> graphUuidByGraphNodeUuid = nodeRepository.getGraphUuidByGraphNodeUuid(checkIds);
        for (final String s : graphUuidByGraphNodeUuid) {
            if (!s.equals(graphUuid)) {
                final String message = Message.BOUND_ANOTHER_GRAPH + s;
                LOG.error(message);
                throw new NodeRelationException(message);
            }
        }

        bindNodeRelations(nodeRelationDTOs, uuidMap, currentTime, tx);

        return nodes;
    }

    /**
     * Creates nodes based on the provided DTOs.
     * <p>
     * Initializes an empty list to store the created nodes.
     * Iterates over the list of {@code NodeDTO} objects provided in the {@code nodeDTOs} parameter.
     * For each DTO, it generates a unique UUID for the node and a unique UUID for the relation.
     * Validates the input parameters using the {@link #checkInputParameters(Map)} method.
     * Creates the node using <br>
     * the {@link NodeMapper#createNode(String, String, String, String, NodeDTO, Transaction)} method.
     * Retrieves the UUID of the created node.
     * Checks if the temporary ID of the DTO already exists in the {@code uuidMap}. <br>
     * If it does, it throws a {@link TemporaryKeyException}.
     * If the temporary ID is unique, it adds mapping from the temporary ID to the result UUID in the {@code uuidMap}.
     * Adds the created node to the list of nodes.
     * Returns the list of created nodes.
     *
     * @param nodeDTOs             the list of DTOs for creating nodes. <br>
     *                             Each DTO contains the properties and temporary ID of the node.
     * @param uuidMap              the map for storing UUID mappings. <br>
     *                             The keys are the temporary IDs, and the values are the result UUIDs.
     * @param currentTime          the current timestamp
     * @param graphUuid            the UUID of the graph
     * @param tx                   the Neo4j transaction object used for the database operation
     * @return the list of created nodes, each represented by a {@link NodeReturnDTO} object
     * @throws TemporaryKeyException if the temporary ID is duplicated
     */
    private List<NodeReturnDTO> createNodes(final List<NodeDTO> nodeDTOs, final Map<String, String> uuidMap,
                                      final String currentTime, final String graphUuid, final Transaction tx) {
        final List<NodeReturnDTO> nodes = new ArrayList<>();

        for (final NodeDTO dto : nodeDTOs) {
            final String nodeUuid = UUID.fastUUID().toString(true);
            final String relationUuid = UUID.fastUUID().toString(true);

            checkInputParameters(dto.getProperties());

            final NodeVO node = nodeMapper.createNode(graphUuid, nodeUuid, relationUuid, currentTime, dto, tx);

            final String resultUuid = node.getUuid();

            // check duplicate temporaryId
            if (uuidMap.containsKey(dto.getTemporaryId())) {
                final String message = Message.DUPLICATE_KEY + dto.getTemporaryId();
                LOG.error(message);
                throw new TemporaryKeyException(message);
            } else {
                uuidMap.put(dto.getTemporaryId(), resultUuid);
            }

            nodes.add(NodeReturnDTO.builder()
                    .uuid(resultUuid)
                    .createTime(node.getCreateTime())
                    .updateTime(node.getUpdateTime())
                    .properties(node.getProperties())
                    .build());
        }

        return nodes;
    }

    /**
     * Binds node relations based on the provided DTOs.
     * <p>
     * Checks if the provided list of {@code graphNodeRelationDTO} is null or empty. If so, it returns immediately.
     * Iterates over the list of {@code NodeRelationDTO} objects.
     * For each DTO, it extracts the relation name and generates a unique UUID for the new relation.
     * Retrieves the start node ID and end node ID from the provided {@code uuidMap} using <br>
     * the {@link #getNodeId(String, Map)} method.
     * Binds the start node to the end node using the <br>
     * {@link NodeMapper#bindGraphNodeToGraphNode(String, String, String, String, String, Transaction)} method.
     *
     * @param graphNodeRelationDTO the list of DTOs for creating node relations. <br>
     *                             Each DTO contains the start node ID, end node ID, and relation name.
     * @param uuidMap              the map for storing UUID mappings. The keys are the original IDs, <br>
     *                            and the values are the mapped node IDs.
     * @param now                  the current timestamp
     * @param tx                   the Neo4j transaction object used for the database operation
     */
    private void bindNodeRelations(final List<NodeRelationDTO> graphNodeRelationDTO, final Map<String, String> uuidMap,
                                   final String now, final Transaction tx) {
        if (graphNodeRelationDTO == null || graphNodeRelationDTO.isEmpty()) {
            return;
        }

        for (final NodeRelationDTO dto : graphNodeRelationDTO) {
            final String relation = dto.getRelationName();
            final String relationUuid = UUID.fastUUID().toString(true);

            final String fromId = getNodeId(dto.getFromId(), uuidMap);
            final String toId = getNodeId(dto.getToId(), uuidMap);

            nodeMapper.bindGraphNodeToGraphNode(fromId, toId, relation, relationUuid, now, tx);
        }
    }

    /**
     * Retrieves the node ID from the UUID map.
     * <p>
     * Checks if the provided {@code id} exists as a key in the {@code uuidMap}.
     * If the ID is found in the map, it returns the corresponding value.
     * If the ID is not found in the map, it returns the original ID.
     *
     * @param id       the ID of the node to retrieve
     * @param uuidMap  the map for storing UUID mappings. The keys are the original IDs, <br>
     * and the values are the mapped node IDs.
     * @return the node ID if found in the map, otherwise the original ID
     */
    private String getNodeId(final String id, final Map<String, String> uuidMap) {
        return uuidMap.getOrDefault(id, id);
    }

    /**
     * Binds nodes based on the provided DTOs.
     * <p>
     * Iterates over the list of {@code BindNodeDTO} objects provided in the {@code dtos} parameter.
     * For each DTO, it extracts the start node UUID, end node UUID, and relation name.
     * Retrieves the start and end nodes by their UUIDs using the {@link #getNodeByUuid(String)} method.
     * Generates a unique UUID for the new relation and gets the current time.
     * If either the start node or the end node is not found, <br>
     * it throws a {@link NodeNullException} with an error message including the missing node's UUID.
     * If both nodes are found, it binds the start node to the end node using the <br>
     * {@link NodeMapper#bindGraphNodeToGraphNode(String, String, String, String, String, Transaction)} method.
     *
     * @param dtos the list of DTOs for binding nodes. <br>
     * Each DTO contains the start node UUID, end node UUID, and relation name.
     * @param tx   the Neo4j transaction object used for the database operation
     * @throws NodeNullException if either the start node or the end node is not found in the graph
     */
    @Neo4jTransactional
    @Override
    public void bindNodes(final List<BindNodeDTO> dtos, final Transaction tx) {
        for (final BindNodeDTO dto : dtos) {
            final String startNode = dto.getFromId();
            final String endNode = dto.getToId();
            final Optional<NodeVO> graphNodeOptional1 = getNodeByUuid(startNode);
            final Optional<NodeVO> graphNodeOptional2 = getNodeByUuid(endNode);
            final String relationUuid = UUID.fastUUID().toString(true);
            final String now = getCurrentTime();

            if (graphNodeOptional1.isEmpty() || graphNodeOptional2.isEmpty()) {
                if (graphNodeOptional1.isEmpty()) {
                    final String message = Message.GRAPH_NODE_NULL + startNode;
                    LOG.error(message);
                    throw new NodeNullException(message);
                } else {
                    final String message = Message.GRAPH_NODE_NULL + endNode;
                    LOG.error(message);
                    throw new NodeNullException(message);
                }
            }

            nodeMapper.bindGraphNodeToGraphNode(startNode, endNode, dto.getRelationName(), relationUuid, now, tx);
        }
    }

    /**
     * Deletes graph nodes by their UUIDs.
     * <p>
     * Extracts the graph UUID and the list of node UUIDs from the provided {@code nodeDeleteDTO}.
     * Iterates over the list of node UUIDs.
     * For each UUID, it checks if the node exists in the graph using the {@link #getNodeByUuid(String)} method.
     * If the node is not found, it throws a {@link NodeNullException} with an error message including the UUID.
     * It then checks if the node is bound to another user using <br>
     * the {@link NodeRepository#getNodeByGraphUuidAndNodeUuid(String, String)} method.
     * If the node is bound to another user, it throws a {@link DeleteException} <br>
     * with an error message including the UUID.
     * Finally, it deletes the nodes with the specified UUIDs using the <br>
     * {@link NodeRepository#deleteByUuids(List)} method.
     *
     * @param nodeDeleteDTO the DTO containing the list of UUIDs of the graph nodes to be deleted. <br>
     * It includes the graph UUID and the list of node UUIDs.
     * @throws NodeNullException if any node with the specified UUID is not found in the graph
     * @throws DeleteException if any node is bound to another user
     */
    @Transactional
    @Override
    public void deleteByUuids(final NodeDeleteDTO nodeDeleteDTO) {
        final String graphUuid = nodeDeleteDTO.getUuid();
        final List<String> uuids = nodeDeleteDTO.getUuids();

        for (final String uuid : uuids) {
            if (getNodeByUuid(uuid).isEmpty()) {
                final String message = Message.GRAPH_NODE_NULL + uuid;
                LOG.error(message);
                throw new NodeNullException(message);
            }
            if (nodeRepository.getNodeByGraphUuidAndNodeUuid(graphUuid, uuid) == null) {
                final String message = Message.NODE_BIND_ANOTHER_USER + uuid;
                LOG.error(message);
                throw new DeleteException(message);
            }
        }

        nodeRepository.deleteByUuids(uuids);
    }

    /**
     * Updates a graph node based on the provided DTO.
     * <p>
     * Extracts the UUID from the provided {@code nodeUpdateDTO}.
     * Retrieves the graph node by the extracted UUID using the {@link #getNodeByUuid(String)} method.
     * If the node is found, it updates the node using <br>
     * the {@link NodeMapper#updateNodeByUuid(NodeUpdateDTO, String, Transaction)} method.
     * If the node is not found, it throws a {@link NodeNullException} with an error message including the UUID.
     *
     * @param nodeUpdateDTO the DTO containing information for updating the node. <br>
     * It includes the UUID and other update parameters.
     * @param tx            the transaction object used for the database operation
     * @throws NodeNullException if the node with the specified UUID is not found in the graph
     */
    @Neo4jTransactional
    @Override
    public void updateNode(final NodeUpdateDTO nodeUpdateDTO, final Transaction tx) {
        final String uuid = nodeUpdateDTO.getUuid();
        final Optional<NodeVO> graphNodeByUuid = getNodeByUuid(uuid);
        final String current = getCurrentTime();

        if (graphNodeByUuid.isPresent()) {
            nodeMapper.updateNodeByUuid(nodeUpdateDTO, current, tx);
        } else {
            final String message = Message.GRAPH_NODE_NULL + uuid;
            LOG.error(message);
            throw new NodeNullException(message);
        }
    }

    /**
     * Updates graph node relations based on the provided DTO.
     * <p>
     * Extracts the graph UUID, update map, and delete list from the provided {@code relationUpdateDTO}.
     * If the update map is not null and not empty, it calls the {@link #validateAndUpdateRelations(Map, String)} <br>
     * method to validate and update the specified graph node relations.
     * If the delete list is not null and not empty, it calls the {@link #validateAndDeleteRelations(List, String)} <br>
     * method to validate and delete the specified graph node relations.
     *
     * @param relationUpdateDTO the DTO containing information for updating the graph node relations. <br>
     * It includes the graph UUID, a map of relations to update, and a list of relations to delete.
     * @throws NodeRelationException if any relation UUID is not found in the repository during validation
     */
    @Transactional
    @Override
    public void updateRelation(final RelationUpdateDTO relationUpdateDTO) {
        final String graphUuid = relationUpdateDTO.getGraphUuid();
        final Map<String, String> updateMap = relationUpdateDTO.getUpdateMap();
        final List<String> deleteList = relationUpdateDTO.getDeleteList();

        if (updateMap != null && !updateMap.isEmpty()) {
            validateAndUpdateRelations(updateMap, graphUuid);
        }

        if (deleteList != null && !deleteList.isEmpty()) {
            validateAndDeleteRelations(deleteList, graphUuid);
        }
    }

    /**
     * Validates and updates graph node relations based on the provided map.
     * <p>
     * Iterates over the entries in the provided {@code updateMap}.
     * For each entry, it checks if the corresponding graph node relation exists in the repository using the UUID.
     * If a relation with the given UUID is not found, an {@link NodeRelationException} is thrown with an error message.
     * If the relation exists, it updates the relation with the new name provided in the map.
     *
     * @param updateMap  the map containing information for updating the graph node relations. <br>
     * The key is the UUID of the relation, and the value is the new name.
     * @param graphUuid  the UUID of the graph
     * @throws NodeRelationException if the relation UUID is not found in the repository
     */
    private void validateAndUpdateRelations(final Map<String, String> updateMap, final String graphUuid) {
        updateMap.forEach((uuid, newName) -> {
            if (nodeRepository.getRelationByUuid(uuid) == null) {
                final String message = Message.GRAPH_NODE_RELATION_NULL + uuid;
                LOG.error(message);
                throw new NodeRelationException(message);
            }
            nodeRepository.updateRelationByUuid(uuid, newName, graphUuid);
        });
    }

    /**
     * Validates and deletes graph node relations based on the provided list.
     * <p>
     * Iterates over the list of UUIDs provided in {@code deleteList}.
     * For each UUID, it checks if the corresponding graph node relation exists in the repository.
     * If a relation with the given UUID is not found, an {@link NodeRelationException} is thrown with an error message.
     * If the relation exists, it is deleted from the repository.
     *
     * @param deleteList the list of UUIDs of the graph node relations to be deleted
     * @param graphUuid  the UUID of the graph
     * @throws NodeRelationException if the relation UUID is not found in the repository
     */
    private void validateAndDeleteRelations(final List<String> deleteList, final String graphUuid) {
        deleteList.forEach(uuid -> {
            if (nodeRepository.getRelationByUuid(uuid) == null) {
                final String message = Message.GRAPH_NODE_RELATION_NULL + uuid;
                LOG.error(message);
                throw new NodeRelationException(message);
            }
            nodeRepository.deleteRelationByUuid(uuid, graphUuid);
        });
    }

    /**
     * Retrieves the current timestamp.
     *
     * @return the current timestamp as a string
     */
    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
    }

    /**
     * Checks the input parameters for invalid keys.
     * <p>
     * This method iterates over the keys in the input parameters and checks each key to determine if it is an <br>
     * invalid key.
     * Invalid keys include, but are not limited to:
     * - {@link Constants#UUID}
     * - {@link Constants#CREATE_TIME}
     * - {@link Constants#UPDATE_TIME}
     *
     * If any invalid keys are found, an {@link IllegalArgumentException} is thrown with an error message that <br>
     * includes all the invalid keys.
     *
     * @param properties the input parameters as a key-value map
     * @throws IllegalArgumentException if the input parameters contain any invalid keys
     */
    private void checkInputParameters(final Map<String, String> properties) {
        final List<String> invalidKeys = new ArrayList<>();
        for (final String key : properties.keySet()) {
            if (key.equals(Constants.UUID)
                    || key.equals(Constants.CREATE_TIME)
                    || key.equals(Constants.UPDATE_TIME)) {
                invalidKeys.add(key);
            }
        }
        if (!invalidKeys.isEmpty()) {
            final String message = Message.INPUT_PROPERTIES_ERROR + invalidKeys;
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }
    }
}
