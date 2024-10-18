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
package com.paiondata.aristotle.model.vo;

import com.paiondata.aristotle.model.dto.NodeReturnDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Return created graph and node.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Data Transfer Object (DTO) for creating graph and node.")
public class GraphAndNodeVO {

    /**
     * The unique identifier (UUID) of the graph.
     *
     * <p>
     * This field is the unique identifier for the graph.
     * It is typically a UUID and is used to reference the graph in other parts of the system.
     *
     * @example "123e4567e89b12d3a456426614174000"
     */
    @ApiModelProperty(value = "The unique identifier (UUID) of the graph. This field is the unique identifier for "
            + "the graph. It is typically a UUID and is used to reference the graph in other parts of the system.",
            example = "123e4567e89b12d3a456426614174000")
    private String uuid;

    /**
     * The title of the graph.
     *
     * <p>
     * This field is the title of the graph.
     * It provides a human-readable name for the graph and is used to identify the graph in user interfaces.
     *
     * @example "My First Graph"
     */
    @ApiModelProperty(value = "The title of the graph. This field is the title of the graph. "
            + "It provides a human-readable name for the graph and is used to identify the graph in user interfaces.",
            example = "My First Graph")
    private String title;

    /**
     * The description of the graph.
     *
     * <p>
     * This field is the description of the graph. It provides additional information about the graph and can be
     * used to explain its purpose or content.
     *
     * @example "This graph represents the relationships between employees in a company."
     */
    @ApiModelProperty(value = "The description of the graph. This field is the description of the graph. "
            + "It provides additional information about the graph and can be used to explain its purpose or content.",
            example = "This graph represents the relationships between employees in a company.")
    private String description;

    /**
     * The list of nodes in the graph.
     *
     * <p>
     * This field is a list of {@link NodeReturnDTO} objects, each representing a node in the graph.
     * Each node contains its unique identifier (UUID), creation and update times, and a map of node attributes.
     *
     * @example [
     *   {
     *     "uuid": "123e4567e89b12d3a456426614174001",
     *     "createTime": "2024-10-18 13:48:33",
     *     "updateTime": "2024-10-18 13:48:33",
     *     "properties": {
     *       "name": "Peter",
     *       "age": "30",
     *       "position": "Software Engineer"
     *     }
     *   },
     *   {
     *     "uuid": "234f5678f9ab23c4d5ef678901234567",
     *     "createTime": "2024-10-18 13:48:33",
     *     "updateTime": "2024-10-18 13:48:33",
     *     "properties": {
     *       "name": "Alice",
     *       "age": "25",
     *       "position": "Data Scientist"
     *     }
     *   }
     * ]
     */
    @ApiModelProperty(value = "The list of nodes in the graph. This field is a list of NodeReturnDTO objects, "
            + "each representing a node in the graph. Each node contains its unique identifier (UUID), creation "
            + "and update times, and a map of node attributes.", example = "[\n" +
            "  {\n" +
            "    \"uuid\": \"123e4567e89b12d3a456426614174001\",\n" +
            "    \"createTime\": \"2024-10-18 13:48:33\",\n" +
            "    \"updateTime\": \"2024-10-18 13:48:33\",\n" +
            "    \"properties\": {\n" +
            "      \"name\": \"Peter\",\n" +
            "      \"age\": \"30\",\n" +
            "      \"position\": \"Software Engineer\"\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"uuid\": \"234f5678f9ab23c4d5ef678901234567\",\n" +
            "    \"createTime\": \"2024-10-18 13:48:33\",\n" +
            "    \"updateTime\": \"2024-10-18 13:48:33\",\n" +
            "    \"properties\": {\n" +
            "      \"name\": \"Alice\",\n" +
            "      \"age\": \"25\",\n" +
            "      \"position\": \"Data Scientist\"\n" +
            "    }\n" +
            "  }\n" +
            "]")
    private List<NodeReturnDTO> nodes;
}
