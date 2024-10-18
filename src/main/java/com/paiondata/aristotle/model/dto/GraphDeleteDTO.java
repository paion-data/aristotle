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

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * Data Transfer Object (DTO) for deleting graphs.
 *
 * <p>
 * This DTO is used to encapsulate the data required for deleting graphs. It includes the user's
 * unique identifier (uidcid) and a list of unique identifiers (uuids) for the graphs to be deleted.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Data Transfer Object (DTO) for deleting graphs."
        + "This DTO is used to encapsulate the data required for deleting graphs. It includes the user's unique "
        + "identifier (uidcid) and a list of unique identifiers (uuids) for the graphs to be deleted.")
public class GraphDeleteDTO {

    /**
     * The unique identifier (uidcid) of the user.
     *
     * <p>
     * This field is required and must not be blank.
     * It uniquely identifies the user who is performing the deletion operation.
     *
     * @see Message#UIDCID_MUST_NOT_BE_BLANK
     */
    @ApiModelProperty(value = "The unique identifier (uidcid) of the user."
            + "This field is required and must not be blank.", required = true)
    @NotBlank(message = Message.UIDCID_MUST_NOT_BE_BLANK)
    private String uidcid;

    /**
     * The unique identifiers (uuids) of the graphs to be deleted.
     *
     * <p>
     * This field is required and must not be empty.
     * It contains a list of unique identifiers (uuids) for the graphs that need to be deleted.
     *
     * @see Message#UUID_MUST_NOT_BE_BLANK
     */
    @ApiModelProperty(value = "The unique identifiers (uuids) of the graphs to be deleted. "
            + "This field is required and must not be empty.", required = true)
    @NotEmpty(message = Message.UUID_MUST_NOT_BE_BLANK)
    private List<String> uuids;
}
