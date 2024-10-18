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

import com.paiondata.aristotle.model.dto.FilterQueryGraphDTO;
import com.paiondata.aristotle.model.dto.GraphDeleteDTO;
import com.paiondata.aristotle.model.dto.GraphUpdateDTO;
import com.paiondata.aristotle.model.vo.GraphVO;
import org.neo4j.driver.Transaction;

/**
 * Service implementation for managing graphs.
 * This class provides methods for CRUD operations on graphs and their relationships.
 */
public interface GraphService {

    /**
     * Retrieves a graph by its UUID and filter parameters.
     * @param filterQueryGraphDTO the filter query DTO
     * @return the graph VO contains the graph details and nodes and relations
     */
    GraphVO getGraphVOByUuid(FilterQueryGraphDTO filterQueryGraphDTO);

    /**
     * Deletes graphs by their UUIDs.
     *
     * @param graphDeleteDTO the DTO containing the UUIDs of the graphs to delete
     */
    void deleteByUuids(GraphDeleteDTO graphDeleteDTO);

    /**
     * Updates a graph using the provided DTO.
     *
     * @param graphUpdateDTO the DTO containing details to update an existing graph
     * @param tx the Neo4j transaction
     */
    void updateGraph(GraphUpdateDTO graphUpdateDTO, Transaction tx);
}
