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
 * Data Transfer Object (DTO) for creating graphs.
 *
 * This DTO is used to encapsulate the data required for creating a new graph.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Data Transfer Object (DTO) for creating graphs.")
public class GraphCreateDTO extends BaseEntity {

    /**
     * The title of the graph.
     *
     * <p>
     * This field is required and must not be blank. It represents the title of the graph,
     * which is a human-readable name for the graph.
     *
     * @see Message#TITLE_MUST_NOT_BE_BLANK
     */
    @ApiModelProperty(value = "The title of the graph. This field is required and must not be blank. "
            + "It represents the title of the graph, which is a human-readable name for the graph.", required = true)
    @NotBlank(message = Message.TITLE_MUST_NOT_BE_BLANK)
    private String title;

    /**
     * The description of the graph.
     *
     * <p>
     * This field is required and must not be blank. It provides a detailed description of the graph,
     * which can include its purpose, content, or any other relevant information.
     *
     * @see Message#DESCRIPTION_MUST_NOT_BE_BLANK
     */
    @ApiModelProperty(value = "The description of the graph. This field is required and must not be blank. "
            + "It provides a detailed description of the graph, which can include its purpose, content, "
            + "or any other relevant information.", required = true)
    @NotBlank(message = Message.DESCRIPTION_MUST_NOT_BE_BLANK)
    private String description;

    /**
     * The UID/CID of the user who owns the graph.
     *
     * <p>
     * This field is required and must not be blank. It uniquely identifies the user who owns the graph.
     * The UID/CID can be a unique identifier such as a username or a user ID.
     *
     * @see Message#UIDCID_MUST_NOT_BE_BLANK
     */
    @ApiModelProperty(value = "The UID/CID of the user who owns the graph. This field is required and must "
            + "not be blank. It uniquely identifies the user who owns the graph. The UID/CID can be a unique "
            + "identifier such as a username or a user ID.", required = true)
    @NotBlank(message = Message.UIDCID_MUST_NOT_BE_BLANK)
    private String userUidcid;
}
