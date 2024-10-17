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
     * The filter properties of the graph. Query all data when the filter parameters is empty.
     */
    @ApiModelProperty(value = "The filter properties of the graph. Query all data when the filter parameters is empty.")
    private Map<String, String> properties;
}
