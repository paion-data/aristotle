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

/**
 * Represents relationships.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Include all relationships. "
        + "The independent node in the relationship's data will only contain the sourceNode data")
public class RelationVO extends BaseEntity {

    /**
     * The UUID of the relationship.
     */
    @ApiModelProperty(value = "The UUID of the relationship")
    private String uuid;

    /**
     * The name of the relationship.
     */
    @ApiModelProperty(value = "The name of the relationship")
    private String name;

    /**
     * The creation time of the relationship.
     */
    @ApiModelProperty(value = "The creation time of the relationship")
    private String createTime;

    /**
     * The last update time of the relationship.
     */
    @ApiModelProperty(value = "The last update time of the relationship")
    private String updateTime;

    /**
     * The source node of the relationship.
     */
    @ApiModelProperty(value = "The source node of the relationship")
    private String sourceNode;

    /**
     * The target node of the relationship.
     */
    @ApiModelProperty(value = "The target node of the relationship")
    private String targetNode;
}
