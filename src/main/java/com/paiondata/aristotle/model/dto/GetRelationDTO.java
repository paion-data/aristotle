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
package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.model.vo.NodeVO;
import com.paiondata.aristotle.model.vo.RelationVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object (DTO) for getting relations.
 *
 * <p>
 * This DTO is used to encapsulate the data required for retrieving relationships and nodes in the system.
 * It includes a list of relationships and a list of nodes.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetRelationDTO {

    /**
     * The relations between nodes.
     *
     * <p>
     * This field contains a list of {@link RelationVO} objects, each representing a relationship between two nodes.
     * Each {@link RelationVO} object includes details such as the source node ID, target node ID,
     * and the name of the relationship.
     */
    private List<RelationVO> relations;

    /**
     * The nodes.
     *
     * <p>
     * This field contains a list of {@link NodeVO} objects, each representing a node in the system.
     * Each {@link NodeVO} object includes details such as the node ID, labels, and attributes.
     */
    private List<NodeVO> nodes;
}
