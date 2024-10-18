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
     * Constructs a Cypher query to match a node by its UUID and return it.
     * Executes the Cypher query within a read transaction using the Neo4j session.
     * Extracts the node details from the query result and returns a {@link NodeVO} object.
     * If no node is found, returns {@code null}.
     *
     * @param uuid the UUID of the node to retrieve
     * @return a {@link NodeVO} object representing the node, or {@code null} if no node is found
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
     * Creates a new node and associates it with a graph.
     *
     * Constructs a Cypher query to match a graph by its UUID, update its update time, <br>
     * create a new node with the provided details,
     * and establish a relationship between the graph and the new node.
     * Executes the Cypher query using the provided transaction.
     * Extracts the node details from the query result and returns a {@link NodeVO} object.
     *
     * @param graphUuid the UUID of the graph to which the node will be added
     * @param nodeUuid the UUID of the new node
     * @param relationUuid the UUID of the relationship between the graph and the new node
     * @param currentTime the current timestamp for creation and update times
     * @param nodeDTO the DTO containing the properties of the new node
     * @param tx the Neo4j transaction to execute the Cypher query
     * @return a {@link NodeVO} object representing the newly created node
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
     * Retrieves the relationships and nodes associated with a graph by its UUID.
     *
     * Constructs a Cypher query to match a graph by its UUID and find its related nodes and relationships.
     * Optionally filters the nodes based on the provided properties.
     * Executes the Cypher query within a read transaction using the Neo4j session.
     * Extracts the node and relationship details from the query results and <br>
     * returns them in a {@link GetRelationDTO} object.
     *
     * @param uuid the UUID of the graph
     * @param properties a map of properties to filter the nodes (optional)
     * @return a {@link GetRelationDTO} object containing the list of relationships and nodes
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
     * Binds two graph nodes together with a specified relationship.
     *
     * Constructs a Cypher query to match two graph nodes by their UUIDs, update their update times,
     * and create a relationship between them.
     * Executes the Cypher query using the provided transaction.
     *
     * @param uuid1 the UUID of the first graph node
     * @param uuid2 the UUID of the second graph node
     * @param relation the name of the relationship to create
     * @param relationUuid the UUID of the relationship
     * @param currentTime the current timestamp for creation and update times
     * @param tx the Neo4j transaction to execute the Cypher query
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
     * Updates a graph node by its UUID.
     *
     * Constructs a Cypher query to match a graph node by its UUID and update its properties.
     * The query dynamically includes only the fields that need to be updated based on the provided properties.
     * Executes the Cypher query using the provided transaction.
     *
     * @param nodeUpdateDTO the DTO containing the updated properties of the node
     * @param currentTime the current timestamp for the update time
     * @param tx the Neo4j transaction to execute the Cypher query
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
     * Generates a string builder containing the SET properties clause for a Cypher query.
     *
     * Iterates through the provided map entries and appends each key-value pair to the string builder
     * in the format suitable for a Cypher query's SET clause.
     * The resulting string builder can be used to dynamically construct the SET part of a Cypher query.
     *
     * @param entries the set of map entries containing the properties to set
     * @return a {@link StringBuilder} object containing the SET properties clause
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
     * Generates a string builder containing the filter properties clause for a Cypher query.
     *
     * Iterates through the provided map entries and appends each key-value pair to the string builder
     * in the format suitable for a Cypher query's WHERE clause.
     * The resulting string builder can be used to dynamically construct the WHERE part of a Cypher query.
     *
     * @param node the alias of the node to apply the filters to
     * @param entries the set of map entries containing the properties to filter
     * @return a {@link StringBuilder} object containing the filter properties clause
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
