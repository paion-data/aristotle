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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) for filtering graphs.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Data Transfer Object (DTO) for binding nodes.")
public class FilterQueryGraphDTO {

    /**
     * The uuid of the graph.
     */
    @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
    @ApiModelProperty(value = "The uuid of the graph.", required = true)
    private String uuid;

    /**
     * The filtering criteria on the requested graph. The criteria is a JSON object with non-nested key-value pairs.
     * Each key represents an attribute name, and each value represents the attribute value to filter by.
     * For example, to filter nodes by name and graduation status, you can use:
     * {
     *   "name": "Peter",
     *   "graduated": "false"
     * }
     * If the filter parameters are empty, all data will be queried.
     *
     * @see <a href="https://aristotle-ws.com/docs/intro">Aristotle WS Documentation</a> for more details.
     */
    @ApiModelProperty(
            value = "The filtering criteria on the requested graph. The criteria is a JSON object with non-nested "
                    + "key-value pairs. Each key represents an attribute name, and each value represents the attribute "
                    + "value to filter by. For example, to filter nodes by name and graduation status, you can use: "
                    + "{ \"name\": \"Peter\", \"graduated\": \"false\" }. If the filter parameters are empty, all data "
                    + "will be queried.",
            example = "{\"name\": \"Peter\", \"graduated\": \"false\"}"
    )
    private Map<String, String> properties;

    /**
     * Returns an Optional containing the filter properties of the graph.
     * If the properties map is null, returns an empty Optional.
     *
     * @return an Optional containing the filter properties of the graph
     */
    public Optional<Map<String, String>> getProperties() {
        return Optional.ofNullable(properties);
    }
}
