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

import com.paiondata.aristotle.model.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for creating graphs and nodes.
 *
 * This DTO is used to encapsulate the data required for creating a graph along with its nodes and relations.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Data Transfer Object (DTO) for creating graphs and nodes.")
public class GraphAndNodeCreateDTO extends BaseEntity {

    /**
     * The details of the graph to be created.
     *
     * <p>
     * This field is required and must not be null. It contains the details of the graph that will be created.
     *
     * @see GraphCreateDTO
     */
    @ApiModelProperty(value = "The details of the graph to be created. "
            + "This field is required and must not be null.", required = true)
    @Valid
    @NotNull
    private GraphCreateDTO graphCreateDTO;

    /**
     * The list of nodes to be created within the graph.
     *
     * <p>
     * This field is optional. It contains a list of {@link NodeDTO} objects representing the nodes that will
     * be created within the graph.
     *
     * @see NodeDTO
     */
    @ApiModelProperty(value = "The list of nodes to be created within the graph. This field is optional.")
    @Valid
    private List<NodeDTO> graphNodeDTO;

    /**
     * The list of relations between nodes within the graph.
     *
     * <p>
     * This field is optional. It contains a list of {@link NodeRelationDTO} objects representing the relations
     * between the nodes that will be created within the graph.
     *
     * @see NodeRelationDTO
     */
    @ApiModelProperty(value = "The list of relations between nodes within the graph. This field is optional.")
    @Valid
    private List<NodeRelationDTO> graphNodeRelationDTO;
}
