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

import javax.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) for updating graphs.
 *
 * This DTO is used to encapsulate the data required for updating an existing graph.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Data Transfer Object (DTO) for updating graphs or nodes.")
public class GraphUpdateDTO extends BaseEntity {

    /**
     * The unique identifier (UUID) of the graph or node.
     *
     * <p>
     * This field is required and must not be blank. It uniquely identifies the graph or node that needs to be updated.
     *
     * @see Message#UUID_MUST_NOT_BE_BLANK
     */
    @ApiModelProperty(value = "The unique identifier (UUID) of the graph or node. "
            + "This field is required and must not be blank.", required = true)
    @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
    private String uuid;

    /**
     * The updated title of the graph or node.
     *
     * <p>
     * This field is optional. If provided, it will update the title of the graph or node.
     */
    @ApiModelProperty(value = "The updated title of the graph or node. This field is optional.")
    private String title;

    /**
     * The updated description of the graph or node.
     *
     * <p>
     * This field is optional. If provided, it will update the description of the graph or node.
     */
    @ApiModelProperty(value = "The updated description of the graph or node. This field is optional.")
    private String description;
}
