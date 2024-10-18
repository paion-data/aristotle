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

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.model.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

import javax.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) for updating a node.
 *
 * This DTO is used to encapsulate the data required for a node in a graph.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Updates a node in a graph.")
public class NodeUpdateDTO extends BaseEntity {

    /**
     * The unique identifier (UUID) of the node.
     *
     * <p>
     * This field is the unique identifier for the node.
     * It is typically a UUID and is used to reference the node in other parts of the system.
     * </p>
     *
     * <p>
     * <strong>Note:</strong> This field is required and must not be blank.
     * </p>
     *
     * @example "123e4567-e89b-12d3-a456-426614174001"
     * @see Message#UUID_MUST_NOT_BE_BLANK
     */
    @ApiModelProperty(value = "The unique identifier (UUID) of the node. This field is the unique identifier for the "
            + "node. It is typically a UUID and is used to reference the node in other parts of the system. This field"
            + "is required and must not be blank.", required = true, example = "123e4567-e89b-12d3-a456-426614174001")
    @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
    private String uuid;

    /**
     * The attributes of the node.
     *
     * <p>
     * This field is a map of node attributes.
     * Each key in the map represents an attribute name, and the corresponding value represents the attribute value.
     * These attributes can include any relevant information about the node.
     * </p>
     *
     * <p>
     * <strong>Note:</strong> This is an overwrite operation. The attributes provided will
     * overwrite the existing attributes of the node. If you want to update only specific attributes,
     * you must provide all the attributes you want to keep, including the ones you are not changing.
     * </p>
     *
     * <p>
     * <strong>Key:</strong> The name of the attribute (e.g., "name", "age").
     * </p>
     *
     * <p>
     * <strong>Value:</strong> The value of the attribute (e.g., "Peter", "30").
     * </p>
     *
     * @example {
     *   "name": "Peter",
     *   "age": "30",
     *   "position": "Software Engineer"
     * }
     */
    @ApiModelProperty(value = "The attributes of the node. This field is a map of node attributes. "
            + "Each key in the map represents an attribute name, and the corresponding value represents the "
            + "attribute value. These attributes can include any relevant information about the node. "
            + "This is an overwrite operation. The attributes provided will overwrite the existing attributes "
            + "of the node. If you want to update only specific attributes, you must provide all the attributes "
            + "you want to keep, including the ones you are not changing.", example = "{\n" +
            "  \"name\": \"Peter\",\n" +
            "  \"age\": \"30\",\n" +
            "  \"position\": \"Software Engineer\"\n" +
            "}")
    private Map<String, String> properties;
}
