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

import com.paiondata.aristotle.common.annotion.Neo4jTransactional;
import com.paiondata.aristotle.common.base.Constants;
import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.DeleteException;
import com.paiondata.aristotle.common.exception.GraphNullException;
import com.paiondata.aristotle.common.exception.TransactionException;
import com.paiondata.aristotle.mapper.GraphMapper;
import com.paiondata.aristotle.mapper.NodeMapper;
import com.paiondata.aristotle.model.dto.FilterQueryGraphDTO;
import com.paiondata.aristotle.model.dto.GetRelationDTO;
import com.paiondata.aristotle.model.dto.GraphDeleteDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.vo.GraphVO;
import com.paiondata.aristotle.repository.NodeRepository;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.service.CommonService;
import com.paiondata.aristotle.service.GraphService;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service implementation for managing graphs.
 * This class provides methods for CRUD operations on graphs and their relationships.
 */
@Service
@AllArgsConstructor
public class GraphServiceImpl implements GraphService {

    @Autowired
    private GraphRepository graphRepository;

    @Autowired
    private GraphMapper graphMapper;

    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private CommonService commonService;

    /**
     * Retrieves a graph by its UUID and filter parameters.
     * @param filterQueryGraphDTO the filter query DTO
     * @return the graph VO contains the graph details and nodes and relations
     */
    @Override
    public GraphVO getGraphVOByUuid(final FilterQueryGraphDTO filterQueryGraphDTO) {
        final String uuid = filterQueryGraphDTO.getUuid();

        final Graph graphByUuid = graphRepository.getGraphByUuid(uuid);

        if (graphByUuid == null) {
            throw new GraphNullException(Message.GRAPH_NULL + uuid);
        }

        final Map<String, String> properties = filterQueryGraphDTO.getProperties();
        if (properties != null) {
            checkInputParameters(properties);
        }

        final GetRelationDTO dto = nodeMapper.getRelationByGraphUuid(uuid, properties);
        return new GraphVO(graphByUuid.getUuid(), graphByUuid.getTitle(), graphByUuid.getDescription(),
                graphByUuid.getCreateTime(), graphByUuid.getUpdateTime(), dto.getNodes(), dto.getRelations());
    }

    /**
     * Deletes graphs by their UUIDs.
     *
     * @param graphDeleteDTO the DTO containing the UUIDs of the graphs to delete
     */
    @Transactional
    @Override
    public void deleteByUuids(final GraphDeleteDTO graphDeleteDTO) {
        final String uidcid = graphDeleteDTO.getUidcid();
        final List<String> uuids = graphDeleteDTO.getUuids();

        for (final String uuid : uuids) {
            if (commonService.getGraphByUuid(uuid).isEmpty()) {
                throw new GraphNullException(Message.GRAPH_NULL + uuid);
            }
            if (graphRepository.getGraphByGraphUuidAndUidcid(uuid, uidcid) == null) {
                throw new DeleteException(Message.GRAPH_BIND_ANOTHER_USER + uuid);
            }
        }

        final List<String> relatedGraphNodeUuids = getRelatedGraphNodeUuids(uuids);

        nodeRepository.deleteByUuids(relatedGraphNodeUuids);
        graphRepository.deleteByUuids(uuids);
    }

    /**
     * Updates a graph using the provided DTO.
     *
     * @param graphUpdateDTO the DTO containing details to update an existing graph
     * @param tx the Neo4j transaction
     */
    @Neo4jTransactional
    @Override
    public void updateGraph(final GraphUpdateDTO graphUpdateDTO, final Transaction tx) {

        if (tx == null) {
            throw new TransactionException(Message.TRANSACTION_NULL);
        }

        final String uuid = graphUpdateDTO.getUuid();
        final Optional<Graph> graphByUuid = commonService.getGraphByUuid(uuid);
        final String now = getCurrentTime();

        if (graphByUuid.isPresent()) {
            graphMapper.updateGraphByUuid(uuid, graphUpdateDTO.getTitle(), graphUpdateDTO.getDescription(), now, tx);
        } else {
            throw new GraphNullException(Message.GRAPH_NULL + uuid);
        }
    }

    /**
     * Retrieves the UUIDs of graph nodes related to a list of graph UUIDs.
     *
     * @param uuids the list of UUIDs of graphs
     *
     * @return the list of UUIDs of graph nodes
     */
    private List<String> getRelatedGraphNodeUuids(final List<String> uuids) {
        return graphRepository.getGraphNodeUuidsByGraphUuids(uuids);
    }

    /**
     * Gets the current time in the specified format.
     *
     * @return the current time
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
