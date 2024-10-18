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
 * Data Transfer Object (DTO) for binding nodes.
 *
 * This DTO is used to represent the relationship between two nodes in the system.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Data Transfer Object (DTO) for binding nodes.")
public class BindNodeDTO extends BaseEntity {

    /**
     * The ID of the source node.
     *
     * <p>
     * This field is required and must not be blank. It uniquely identifies the source node in the system.
     * The ID is typically a UUID.
     *
     * @see Message#UUID_MUST_NOT_BE_BLANK
     */
    @ApiModelProperty(value = "The ID of the source node. This field is required and must not be blank. "
            + "It uniquely identifies the source node in the system. The ID is typically a UUID.", required = true)
    @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
    private String fromId;

    /**
     * The ID of the target node.
     *
     * <p>
     * This field is required and must not be blank. It uniquely identifies the target node in the system.
     * The ID is typically a UUID.
     *
     * @see Message#UUID_MUST_NOT_BE_BLANK
     */
    @ApiModelProperty(value = "The ID of the target node. This field is required and must not be blank. "
            + "It uniquely identifies the target node in the system. The ID is typically a UUID.", required = true)
    @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
    private String toId;

    /**
     * The name of the relationship between the source and target nodes.
     *
     * <p>
     * This field is required and must not be blank. It specifies the type of relationship between the source and
     * target nodes. For example, "FRIEND_OF", "WORKS_AT", etc.
     *
     * @see Message#RELATION_MUST_NOT_BE_BLANK
     */
    @ApiModelProperty(value = "The name of the relationship between the source and target nodes. "
            + "This field is required and must not be blank. It specifies the type of relationship between the source "
            + "and target nodes. For example, 'FRIEND_OF', 'WORKS_AT', etc.", required = true)
    @NotBlank(message = Message.RELATION_MUST_NOT_BE_BLANK)
    private String relationName;
}
