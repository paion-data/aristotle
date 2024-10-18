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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * Data Transfer Object (DTO) for deleting nodes.
 *
 * This DTO is used to encapsulate the data required for deleting nodes.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Data Transfer Object (DTO) for deleting nodes.")
public class NodeDeleteDTO {

    /**
     * The unique identifier (UUID) of the graph.
     *
     * <p>
     * This field is the unique identifier for the graph. It is typically a UUID and is used to reference the graph
     * in other parts of the system.
     *
     * <p>
     * <strong>Note:</strong> This field is required and must not be blank.
     *
     * @example "123e4567e89b12d3a456426614174001"
     */
    @ApiModelProperty(value = "The unique identifier (UUID) of the graph. This field is the unique identifier "
            + "for the graph. It is typically a UUID and is used to reference the graph in other parts of the system. "
            + "This field is required and must not be blank.", required = true,
            example = "123e4567e89b12d3a456426614174001")
    @NotBlank(message = "UUID must not be blank")
    private String uuid;

    /**
     * The unique identifiers (UUIDs) of the nodes to be deleted.
     *
     * <p>
     * This field is a list of unique identifiers (UUIDs) of the nodes that need to be deleted from the graph.
     * Each UUID in the list corresponds to a specific node.
     *
     * <p>
     * <strong>Note:</strong> This field is required and must not be empty.
     *
     * @example ["123e4567e89b12d3a456426614174001", "234f5678f9ab23c4d5ef678901234567"]
     */
    @ApiModelProperty(value = "The unique identifiers (UUIDs) of the nodes to be deleted."
            + "This field is a list of unique identifiers (UUIDs) of the nodes that need to be deleted from the graph."
            + "Each UUID in the list corresponds to a specific node. This field is required and must not be empty.",
            required = true, example = "[\"123e4567e89b12d3a456426614174001\", \"234f5678f9ab23c4d5ef678901234567\"]")
    @NotEmpty(message = "UUIDs must not be empty")
    private List<String> uuids;
}
