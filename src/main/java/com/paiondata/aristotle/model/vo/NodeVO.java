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

import com.paiondata.aristotle.model.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Represents a node entity within a graph.
 *
 * This class encapsulates the properties and metadata of a node.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Represents a node")
public class NodeVO extends BaseEntity {

    /**
     * The UUID of the node.
     */
    @ApiModelProperty(value = "The UUID of the node")
    private String uuid;

    /**
     * The properties of the node.
     */
    @ApiModelProperty(value = "The properties of the node")
    private Map<String, String> properties;

    /**
     * The creation time of the node.
     */
    @ApiModelProperty(value = "The creation time of the node")
    private String createTime;

    /**
     * The last update time of the node.
     */
    @ApiModelProperty(value = "The last update time of the node")
    private String updateTime;
}
