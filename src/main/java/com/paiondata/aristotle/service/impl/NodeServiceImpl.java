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
import com.paiondata.aristotle.model.dto.GraphNodeDTO;
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
     * @return an {@code Optional} containing the graph node if found
     */
    @Override
    public Optional<NodeVO> getNodeByUuid(final String uuid) {
        final NodeVO graphNode = nodeMapper.getNodeByUuid(uuid);
        return Optional.ofNullable(graphNode);
    }

    /**
     * Creates and binds a graph and a node based on the provided DTO.
     *
     * @param nodeCreateDTO the DTO containing information for creating the graph and node
     * @param tx the Neo4j transaction
     * @return the list of created nodes
     */
    @Neo4jTransactional
    @Override
    public List<NodeReturnDTO> createAndBindGraphAndNode(final NodeCreateDTO nodeCreateDTO, final Transaction tx) {

        if (tx == null) {
            throw new TransactionException(Message.TRANSACTION_NULL);
        }

        final String graphUuid = nodeCreateDTO.getGraphUuid();

        final Optional<Graph> optionalGraph = commonService.getGraphByUuid(graphUuid);
        if (optionalGraph.isEmpty()) {
            throw new GraphNullException(Message.GRAPH_NULL + graphUuid);
        }

        return checkInputRelationsAndBindGraphAndNode(nodeCreateDTO.getGraphNodeDTO(),
                nodeCreateDTO.getGraphNodeRelationDTO(), graphUuid, tx);
    }

    /**
     * Creates a graph and binds it with a node based on the provided DTO.
     *
     * @param graphNodeCreateDTO the DTO containing information for creating the graph and node
     * @return the created graph node
     */
    @Override
    @Neo4jTransactional
    public GraphNodeDTO createGraphAndBindGraphAndNode(final GraphAndNodeCreateDTO graphNodeCreateDTO,
                                                       final Transaction tx) {

        if (tx == null) {
            throw new TransactionException(Message.TRANSACTION_NULL);
        }

        final Graph graph = commonService.createAndBindGraph(graphNodeCreateDTO.getGraphCreateDTO(), tx);

        final GraphNodeDTO dto = GraphNodeDTO.builder()
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
     * Checks input relations and binds a graph and a node.
     *
     * @param nodeDTOs             the list of DTOs for creating nodes
     * @param nodeRelationDTOs     the list of DTOs for creating node relations
     * @param graphUuid            the UUID of the graph
     * @param tx                   the Neo4j transaction
     *
     * @return the list of created nodes
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
                throw new NodeRelationException(Message.BOUND_ANOTHER_GRAPH + s);
            }
        }

        bindNodeRelations(nodeRelationDTOs, uuidMap, currentTime, tx);

        return nodes;
    }

    /**
     * Creates nodes based on the provided DTOs.
     *
     * @param nodeDTOs             the list of DTOs for creating nodes
     * @param uuidMap              the map for storing UUID mappings
     * @param currentTime                  the current timestamp
     * @param graphUuid            the UUID of the graph
     * @param tx                   the Neo4j transaction
     *
     * @return the created nodes
     *
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
                throw new TemporaryKeyException(Message.DUPLICATE_KEY + dto.getTemporaryId());
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
     *
     * @param graphNodeRelationDTO the list of DTOs for creating node relations
     * @param uuidMap              the map for storing UUID mappings
     * @param now                  the current timestamp
     * @param tx                   the Neo4j transaction
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
     *
     * @param id           the ID of the node
     * @param uuidMap      the map for storing UUID mappings
     * @return the node ID or the original ID if not found in the map
     */
    private String getNodeId(final String id, final Map<String, String> uuidMap) {
        return uuidMap.getOrDefault(id, id);
    }

    /**
     * Binds nodes based on the provided DTOs.
     *
     * @param dtos the list of DTOs for binding nodes
     * @param tx   the Neo4j transaction
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
                    throw new NodeNullException(Message.GRAPH_NODE_NULL + startNode);
                } else {
                    throw new NodeNullException(Message.GRAPH_NODE_NULL + endNode);
                }
            }

            nodeMapper.bindGraphNodeToGraphNode(startNode, endNode, dto.getRelationName(), relationUuid, now, tx);
        }
    }

    /**
     * Deletes graph nodes by their UUIDs.
     *
     * @param nodeDeleteDTO the list of UUIDs of the graph nodes to be deleted
     */
    @Transactional
    @Override
    public void deleteByUuids(final NodeDeleteDTO nodeDeleteDTO) {
        final String graphUuid = nodeDeleteDTO.getUuid();
        final List<String> uuids = nodeDeleteDTO.getUuids();

        for (final String uuid : uuids) {
            if (getNodeByUuid(uuid).isEmpty()) {
                throw new NodeNullException(Message.GRAPH_NODE_NULL + uuid);
            }
            if (nodeRepository.getNodeByGraphUuidAndNodeUuid(graphUuid, uuid) == null) {
                throw new DeleteException(Message.NODE_BIND_ANOTHER_USER + uuid);
            }
        }

        nodeRepository.deleteByUuids(uuids);
    }

    /**
     * Updates a graph node based on the provided DTO.
     *
     * @param nodeUpdateDTO the DTO containing information for updating the node
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
            throw new NodeNullException(Message.GRAPH_NODE_NULL + uuid);
        }
    }

    /**
     * Updates graph node relations based on the provided DTO.
     *
     * @param relationUpdateDTO the DTO containing information for updating the graph node relations
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
     *
     * @param updateMap  the map containing information for updating the graph node relations
     * @param graphUuid  the UUID of the graph
     *
     * @throws NodeRelationException if the relation UUID is not found
     */
    private void validateAndUpdateRelations(final Map<String, String> updateMap, final String graphUuid) {
        updateMap.forEach((uuid, newName) -> {
            if (nodeRepository.getRelationByUuid(uuid) == null) {
                throw new NodeRelationException(Message.GRAPH_NODE_RELATION_NULL + uuid);
            }
            nodeRepository.updateRelationByUuid(uuid, newName, graphUuid);
        });
    }

    /**
     * Validates and deletes graph node relations based on the provided list.
     *
     * @param deleteList the list of UUIDs of the graph node relations to be deleted
     * @param graphUuid  the UUID of the graph
     *
     * @throws NodeRelationException if the relation UUID is not found
     */
    private void validateAndDeleteRelations(final List<String> deleteList, final String graphUuid) {
        deleteList.forEach(uuid -> {
            if (nodeRepository.getRelationByUuid(uuid) == null) {
                throw new NodeRelationException(Message.GRAPH_NODE_RELATION_NULL + uuid);
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
     * @param properties the input parameters
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
            throw new IllegalArgumentException(Message.INPUT_PROPERTIES_ERROR + invalidKeys);
        }
    }
}
