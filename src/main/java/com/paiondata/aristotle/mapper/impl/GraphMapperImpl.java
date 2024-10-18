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
import com.paiondata.aristotle.mapper.GraphMapper;
import com.paiondata.aristotle.model.entity.Graph;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GraphMapperImpl class provides methods for interacting with the Neo4j database using Cypher queries.
 */
@Repository
public class GraphMapperImpl implements GraphMapper {

    private final Driver driver;

    private final NodeExtractor nodeExtractor;

    /**
     * Constructs a new GraphMapperImpl object with the specified Driver and NodeExtractor.
     * @param driver the Driver instance
     * @param nodeExtractor the NodeExtractor instance
     */
    @Autowired
    public GraphMapperImpl(final Driver driver, final NodeExtractor nodeExtractor) {
        this.driver = driver;
        this.nodeExtractor = nodeExtractor;
    }

    /**
     * Creates a new graph and associates it with a user.
     *
     * Constructs a Cypher query to match a user by their uidcid, create a new graph with the provided details,
     * and establish a relationship between the user and the graph.
     * Executes the Cypher query using the provided transaction.
     * Extracts the graph details from the query result and returns a {@link Graph} object.
     *
     * @param title the title of the graph
     * @param description the description of the graph
     * @param userUidcid the uidcid of the user who owns the graph
     * @param graphUuid the UUID of the graph
     * @param relationUuid the UUID of the relationship between the user and the graph
     * @param currentTime the current timestamp for creation and update times
     * @param tx the Neo4j transaction to execute the Cypher query
     * @return a {@link Graph} object representing the newly created graph
     */
    public Graph createGraph(final String title, final String description, final String userUidcid,
                             final String graphUuid, final String relationUuid, final String currentTime,
                             final Transaction tx) {
        final String cypherQuery = "MATCH (u:User) WHERE u.uidcid = $uidcid "
                + "CREATE (g:Graph {uuid: $graphUuid, title: $title, description: $description, "
                + "create_time: $currentTime, update_time: $currentTime}) "
                + "WITH u, g "
                + "CREATE (u)-[r:RELATION {name: 'HAVE', uuid: $relationUuid, create_time: $currentTime, "
                + "update_time: $currentTime}]->(g) RETURN g";

        final var result = tx.run(cypherQuery, Values.parameters(
                        Constants.TITLE, title,
                        Constants.DESCRIPTION, description,
                        Constants.UIDCID, userUidcid,
                        Constants.GRAPH_UUID, graphUuid,
                        Constants.CURRENT_TIME, currentTime,
                        Constants.RELATION_UUID, relationUuid
                )
        );

        final Record record = result.next();
        final Map<String, Object> objectMap = nodeExtractor.extractGraph(record.get(Constants.GRAPH_IN_CYPHER));

        return Graph.builder()
                .id((Long) objectMap.get(Constants.ID))
                .uuid((String) objectMap.get(Constants.UUID))
                .title((String) objectMap.get(Constants.TITLE))
                .description((String) objectMap.get(Constants.DESCRIPTION))
                .createTime((String) objectMap.get(Constants.CREATE_TIME))
                .updateTime((String) objectMap.get(Constants.UPDATE_TIME))
                .build();
    }

    /**
     * Retrieves a list of graphs associated with a user by their unique identifier (uidcid).
     *
     * Constructs a Cypher query to match a user by their uidcid and retrieve all graphs they are related to.
     * Executes the Cypher query within a read transaction using the Neo4j session.
     * Extracts the graph details from the query results and returns a list of maps, where each map represents a graph.
     *
     * @param uidcid the unique identifier of the user
     * @return a list of maps, where each map contains the details of a graph associated with the user
     */
    @Override
    public List<Map<String, Object>> getGraphsByUidcid(final String uidcid) {
        final String cypherQuery = "MATCH (u:User)-[r:RELATION]->(g:Graph) WHERE u.uidcid = $uidcid RETURN DISTINCT g";

        try (Session session = driver.session(SessionConfig.builder().build())) {
            return session.readTransaction(tx -> {
                final var result = tx.run(cypherQuery, Values.parameters("uidcid", uidcid));
                final List<Map<String, Object>> resultList = new ArrayList<>();
                while (result.hasNext()) {
                    final Record record = result.next();
                    final Map<String, Object> graph = nodeExtractor.extractGraph(record.get(Constants.GRAPH_IN_CYPHER));

                    resultList.add(graph);
                }
                return resultList;
            });
        }
    }

    /**
     * Updates the details of a graph by its UUID.
     *
     * Constructs a Cypher query to match a graph by its UUID and update its title, description, and update time.
     * The query dynamically includes only the fields that need to be updated based on the provided parameters.
     * Executes the Cypher query using the provided transaction.
     *
     * @param uuid the UUID of the graph to be updated
     * @param title the new title of the graph (optional)
     * @param description the new description of the graph (optional)
     * @param currentTime the current timestamp for the update time
     * @param tx the Neo4j transaction to execute the Cypher query
     */
    @Override
    public void updateGraphByUuid(final String uuid, final String title,
                                  final String description, final String currentTime, final Transaction tx) {
        final StringBuilder cypherQuery = new StringBuilder("MATCH (g:Graph { uuid: $uuid }) ");
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(Constants.UUID, uuid);

        if (title != null) {
            cypherQuery.append("SET g.title = $title ");
            parameters.put(Constants.TITLE, title);
        }

        if (description != null) {
            cypherQuery.append("SET g.description = $description ");
            parameters.put(Constants.DESCRIPTION, description);
        }

        cypherQuery.append("SET g.update_time = $updateTime ");
        parameters.put(Constants.UPDATE_TIME, currentTime);

        cypherQuery.append("RETURN g");

        tx.run(cypherQuery.toString(), parameters);
    }
}
