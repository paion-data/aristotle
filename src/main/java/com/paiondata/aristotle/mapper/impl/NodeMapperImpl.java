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
package com.paiondata.aristotle.mapper.impl;

import com.paiondata.aristotle.common.base.Constants;
import com.paiondata.aristotle.common.util.NodeExtractor;
import com.paiondata.aristotle.mapper.NodeMapper;
import com.paiondata.aristotle.model.dto.GetRelationDTO;
import com.paiondata.aristotle.model.dto.NodeDTO;
import com.paiondata.aristotle.model.dto.NodeUpdateDTO;
import com.paiondata.aristotle.model.vo.NodeVO;
import com.paiondata.aristotle.model.vo.RelationVO;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Repository class for executing Cypher queries related to nodes in Neo4j.
 */
@Repository
public class NodeMapperImpl implements NodeMapper {

    private final Driver driver;

    private final NodeExtractor nodeExtractor;

    /**
     * Constructs a new NodeMapperImpl object with the specified Driver and NodeExtractor.
     * @param driver the Driver instance
     * @param nodeExtractor the NodeExtractor instance
     */
    @Autowired
    public NodeMapperImpl(final Driver driver, final NodeExtractor nodeExtractor) {
        this.driver = driver;
        this.nodeExtractor = nodeExtractor;
    }

    /**
     * Retrieves a node by its UUID.
     *
     * @param uuid the UUID of the node to retrieve
     *
     * @return the NodeVO representing the retrieved node
     *
     */
    @Override
    public NodeVO getNodeByUuid(final String uuid) {
        final String cypherQuery = "MATCH (n:GraphNode { uuid: $uuid }) RETURN n";

        try (Session session = driver.session(SessionConfig.builder().build())) {
            return session.readTransaction(tx -> {
                final var result = tx.run(cypherQuery, Values.parameters(Constants.UUID, uuid));

                NodeVO n = null;
                while (result.hasNext()) {
                    final Record record = result.next();
                    n = nodeExtractor.extractNode(record.get("n"));
                }

                return n;
            });
        }
    }

    /**
     * Creates a new node within a graph.
     *
     * @param graphUuid the UUID of the graph where the node will be created
     * @param nodeUuid the UUID of the new node
     * @param relationUuid the UUID of the link between the created node and the graph this node belongs to
     * @param currentTime the current time for creating the node
     * @param nodeDTO the DTO containing node details
     * @param tx the transaction in which the operation will be performed
     *
     * @return the created Node
     *
     */
    @Override
    public NodeVO createNode(final String graphUuid, final String nodeUuid, final String relationUuid,
                                final String currentTime, final NodeDTO nodeDTO, final Transaction tx) {
        final StringBuilder setProperties = getSetProperties(nodeDTO.getProperties().entrySet());

        final String cypherQuery = "MATCH (g:Graph) WHERE g.uuid = $graphUuid SET g.update_time = $currentTime "
                + "CREATE (gn:GraphNode{uuid:$nodeUuid "
                + setProperties
                + ",create_time:$currentTime,update_time:$currentTime}) "
                + "WITH g, gn "
                + "CREATE (g)-[r:RELATION {name: 'HAVE', uuid: $relationUuid, "
                + "create_time: $currentTime, update_time: $currentTime}]->(gn) "
                + "RETURN gn";

        final var result = tx.run(cypherQuery, Values.parameters(
                        Constants.GRAPH_UUID, graphUuid,
                        Constants.NODE_UUID, nodeUuid,
                        Constants.CURRENT_TIME, currentTime,
                        Constants.RELATION_UUID, relationUuid
                )
        );

        final Record record = result.next();

        return nodeExtractor.extractNode(record.get("gn"));
    }

    /**
     * Retrieves all relationships by graph uuid.
     * @param uuid the UUID of the graph
     * @param properties the filter properties of the node
     * @return Data Transfer Object (DTO) contains relations and nodes
     */
    @Override
    public GetRelationDTO getRelationByGraphUuid(final String uuid, final Map<String, String> properties) {
        final String cypherQuery = "MATCH (g1:Graph { uuid: $uuid }) "
                + "OPTIONAL MATCH (g1)-[:RELATION]->(n1:GraphNode) "
                + (properties != null && !properties.isEmpty() ?
                getFilterProperties(Constants.NODE_ALIAS_N1, properties.entrySet()) : "")
                + " OPTIONAL MATCH (n1)-[r:RELATION]->(n2:GraphNode) "
                + (properties != null && !properties.isEmpty() ?
                getFilterProperties(Constants.NODE_ALIAS_N2, properties.entrySet()) : "")
                + " RETURN DISTINCT n1, r, n2";

        System.out.println(properties);
        System.out.println(cypherQuery);

        try (Session session = driver.session(SessionConfig.builder().build())) {
            return session.readTransaction(tx -> {
                final var result = tx.run(cypherQuery, Values.parameters(Constants.UUID, uuid));
                final List<RelationVO> relations = new ArrayList<>();
                final Set<NodeVO> nodes = new HashSet<>();

                while (result.hasNext()) {
                    final Record record = result.next();
                    final NodeVO n1 = nodeExtractor.extractNode(record.get(Constants.NODE_ALIAS_N1));
                    nodes.add(n1);

                    if (record.get(Constants.NODE_ALIAS_N2) == null ||
                            "NULL".equals(record.get(Constants.NODE_ALIAS_N2).toString())) {
                        continue;
                    }

                    final NodeVO n2 = nodeExtractor.extractNode(record.get(Constants.NODE_ALIAS_N2));
                    nodes.add(n2);
                    final Map<String, Object> relation = nodeExtractor.extractRelationship(record.get("r"));

                    final RelationVO relationVO = new RelationVO();
                    relationVO.setSourceNode(n1.getUuid());
                    relationVO.setTargetNode(n2.getUuid());
                    relationVO.setUuid((String) relation.get(Constants.UUID));
                    relationVO.setName((String) relation.get(Constants.NAME));
                    relationVO.setCreateTime((String) relation.get(Constants.CREATE_TIME));
                    relationVO.setUpdateTime((String) relation.get(Constants.UPDATE_TIME));

                    relations.add(relationVO);
                }

                return new GetRelationDTO(relations, new ArrayList<>(nodes));
            });
        }
    }

    /**
     * Binds two graph nodes together with a specified relation.
     *
     * @param uuid1 the UUID of the first graph node
     * @param uuid2 the UUID of the second graph node
     * @param relation the name of the relation
     * @param relationUuid the UUID of the link between the created node and the graph this node belongs to
     * @param currentTime the current time for updating the nodes and relation
     * @param tx the transaction in which the operation will be performed
     */
    @Override
    public void bindGraphNodeToGraphNode(final String uuid1, final String uuid2, final String relation,
                                         final String relationUuid, final String currentTime, final Transaction tx) {
        final String cypherQuery = "MATCH (gn1:GraphNode) WHERE gn1.uuid = $uuid1 SET gn1.update_time = $currentTime "
                + "WITH gn1 "
                + "MATCH (gn2:GraphNode) WHERE gn2.uuid = $uuid2 SET gn2.update_time = $currentTime "
                + "WITH gn1,gn2 "
                + "CREATE (gn1)-[r:RELATION{name: $relation, uuid: $relationUuid, "
                + "create_time: $currentTime, update_time: $currentTime}]->(gn2)";

        tx.run(cypherQuery, Values.parameters(
                "uuid1", uuid1,
                "uuid2", uuid2,
                Constants.RELATION, relation,
                Constants.CURRENT_TIME, currentTime,
                Constants.RELATION_UUID, relationUuid
        ));
    }

    /**
     * Updates a node by its UUID.
     *
     * @param nodeUpdateDTO the DTO containing updated node properties
     * @param currentTime the current time for updating the node
     * @param tx the transaction in which the operation will be performed
     *
     */
    @Override
    public void updateNodeByUuid(final NodeUpdateDTO nodeUpdateDTO, final String currentTime, final Transaction tx) {
        final StringBuilder setProperties = getSetProperties(nodeUpdateDTO.getProperties().entrySet());

        final String cypherQuery = "MATCH (gn:GraphNode {uuid: $nodeUuid}) "
                + "SET gn = { uuid: gn.uuid, "
                + "create_time: gn.create_time, "
                + "update_time: $updateTime"
                + setProperties
                + " }";

        tx.run(cypherQuery, Values.parameters(
                Constants.NODE_UUID, nodeUpdateDTO.getUuid(),
                Constants.UPDATE_TIME, currentTime));
    }

    /**
     * Generates a StringBuilder with property assignments for Cypher queries.
     *
     * @param entries the set of property entries
     *
     * @return a StringBuilder with formatted property assignments
     */
    private StringBuilder getSetProperties(final Set<Map.Entry<String, String>> entries) {
        final StringBuilder setProperties = new StringBuilder();
        for (final Map.Entry<String, String> entry : entries) {
            setProperties.append(", ").append(entry.getKey()).append(": '").append(entry.getValue())
                    .append(Constants.QUOTE);
        }

        return setProperties;
    }

    /**
     * Generates a StringBuilder with filter conditions for Cypher queries.
     * @param node the node name
     * @param entries the set of property entries
     * @return a StringBuilder with formatted filter conditions
     */
    private StringBuilder getFilterProperties(final String node, final Set<Map.Entry<String, String>> entries) {
        final StringBuilder filterProperties = new StringBuilder();
        filterProperties.append("WHERE ");

        boolean isFirstEntry = true;
        for (final Map.Entry<String, String> entry : entries) {
            if (!isFirstEntry) {
                filterProperties.append(" AND ");
            }
            filterProperties.append(node).append(".").append(entry.getKey()).append(" = '")
                    .append(entry.getValue()).append(Constants.QUOTE);
            isFirstEntry = false;
        }

        return filterProperties;
    }
}
