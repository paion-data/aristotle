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
package com.paiondata.aristotle.common.util;

import com.paiondata.aristotle.common.base.Constants;
import com.paiondata.aristotle.model.vo.NodeVO;

import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Extracts information from graph nodes, relationships, and nodes.
 */
@Component
public class NodeExtractor {

    /**
     * Extracts information from a graph node.
     *
     * @param node the graph node object
     *
     * @return a map containing the extracted information
     *
     * @throws IllegalArgumentException if the input node is not a valid NodeValue
     */
    public Map<String, Object> extractGraph(final Object node) {
        final Map<String, Object> nodeInfo = new HashMap<>();
        if (node instanceof NodeValue) {
            final NodeValue nodeValue = (NodeValue) node;
            final Map<String, Object> nodeMap = nodeValue.asNode().asMap();

            nodeInfo.put(Constants.DESCRIPTION, nodeMap.get(Constants.DESCRIPTION));
            nodeInfo.put(Constants.UPDATE_TIME, nodeMap.get(Constants.UPDATE_TIME_WITHOUT_HUMP));
            nodeInfo.put(Constants.CREATE_TIME, nodeMap.get(Constants.CREATE_TIME_WITHOUT_HUMP));
            nodeInfo.put(Constants.TITLE, nodeMap.get(Constants.TITLE));
            nodeInfo.put(Constants.UUID, nodeMap.get(Constants.UUID));
        }
        return nodeInfo;
    }

    /**
     * Extracts information from a relationship.
     *
     * @param relationship the relationship object
     *
     * @return a map containing the extracted information
     *
     * @throws IllegalArgumentException if the input relationship is not a valid RelationshipValue
     */
    public Map<String, Object> extractRelationship(final Object relationship) {
        final Map<String, Object> relationshipInfo = new HashMap<>();
        if (relationship instanceof RelationshipValue) {
            final RelationshipValue relationshipValue = (RelationshipValue) relationship;
            final Map<String, Object> relMap = relationshipValue.asRelationship().asMap();

            relationshipInfo.put(Constants.NAME, relMap.get(Constants.NAME));
            relationshipInfo.put(Constants.CREATE_TIME, relMap.get(Constants.CREATE_TIME_WITHOUT_HUMP));
            relationshipInfo.put(Constants.UPDATE_TIME, relMap.get(Constants.UPDATE_TIME_WITHOUT_HUMP));
            relationshipInfo.put(Constants.UUID, relMap.get(Constants.UUID));
        }
        return relationshipInfo;
    }

    /**
     * Extracts information from a graph node.
     *
     * @param node the node object
     *
     * @return the extracted node information
     *
     * @throws IllegalArgumentException if the input node is not a valid NodeValue
     */
    public NodeVO extractNode(final Object node) {
        final NodeVO nodeInfo = new NodeVO();
        if (node instanceof NodeValue) {
            final NodeValue nodeValue = (NodeValue) node;
            final Map<String, Object> nodeMap = nodeValue.asNode().asMap();

            nodeInfo.setUuid((String) nodeMap.get(Constants.UUID));
            nodeInfo.setCreateTime((String) nodeMap.get(Constants.CREATE_TIME_WITHOUT_HUMP));
            nodeInfo.setUpdateTime((String) nodeMap.get(Constants.UPDATE_TIME_WITHOUT_HUMP));

            final Map<String, String> properties = new HashMap<>();
            for (final Map.Entry<String, Object> entry : nodeMap.entrySet()) {
                if (!Constants.UUID.equals(entry.getKey())
                        && !Constants.UPDATE_TIME_WITHOUT_HUMP.equals(entry.getKey())
                        && !Constants.CREATE_TIME_WITHOUT_HUMP.equals(entry.getKey())) {
                    properties.put(entry.getKey(), (String) entry.getValue());
                }
            }

            nodeInfo.setProperties(properties);
        }
        return nodeInfo;
    }
}
