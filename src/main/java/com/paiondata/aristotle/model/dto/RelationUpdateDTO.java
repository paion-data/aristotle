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
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object (DTO) for updating relations in a graph.
 *
 * This DTO is used to encapsulate the data required for updating and deleting relations in a graph.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Data Transfer Object for updating relations in a graph.")
public class RelationUpdateDTO extends BaseEntity {

    /**
     * The unique identifier (UUID) of the graph where the relations will be updated.
     *
     * <p>
     * This field is the unique identifier for the graph.
     * It is typically a UUID and is used to reference the graph in other parts of the system.
     * </p>
     *
     * <p>
     * <strong>Note:</strong> This field is required and must not be blank.
     * </p>
     *
     * @example "123e4567-e89b-12d3-a456-426614174001"
     * @see Message#UUID_MUST_NOT_BE_BLANK
     */
    @ApiModelProperty(value = "The unique identifier (UUID) of the graph where the relations will be updated. "
            + "This field is the unique identifier for the graph. It is typically a UUID and is used to reference the "
            + "graph in other parts of the system. This field is required and must not be blank.", required = true,
            example = "123e4567-e89b-12d3-a456-426614174001")
    @NotBlank(message = Message.UUID_MUST_NOT_BE_BLANK)
    private String graphUuid;

    /**
     * A map containing the updates to be applied to the relations.
     *
     * <p>
     * This field is a map where each key represents the identifier of a relation, and the corresponding value
     * represents the updated value of the relation. The updates provided will overwrite the existing values
     * of the relations.
     * </p>
     *
     * <p>
     * <strong>Note:</strong> This is an overwrite operation. The values provided will overwrite the existing values
     * of the relations. If you want to update only specific attributes, you must provide all the attributes you
     * want to keep, including the ones you are not changing.
     * </p>
     *
     * <p>
     * <strong>Key:</strong> The identifier of the relation ("relation1", "relation2").
     * </p>
     *
     * <p>
     * <strong>Value:</strong> The updated value of the relation ("new_value1", "new_value2").
     * </p>
     *
     * @example {
     *   "relation1": "new_value1",
     *   "relation2": "new_value2"
     * }
     */
    @ApiModelProperty(value = "A map containing the updates to be applied to the relations."
            + "This field is a map where each key represents the identifier of a relation, and the corresponding"
            + "value represents the updated value of the relation. The updates provided will overwrite the existing"
            + "values of the relations. This is an overwrite operation. The values provided will overwrite the"
            + "existing values of the relations. If you want to update only specific attributes, you must provide all"
            + "the attributes you want to keep, including the ones you are not changing.", example = "{\n" +
            "  \"relation1\": \"new_value1\",\n" +
            "  \"relation2\": \"new_value2\"\n" +
            "}")
    private Map<String, String> updateMap;

    /**
     * A list of identifiers of relations to be deleted.
     *
     * <p>
     * This field is a list of identifiers of the relations that need to be deleted from the graph.
     * Each identifier in the list corresponds to a specific relation.
     * </p>
     *
     * <p>
     * <strong>Note:</strong> This field is optional. If not provided, no relations will be deleted.
     * </p>
     *
     * @example ["relation1", "relation2"]
     */
    @ApiModelProperty(value = "A list of identifiers of relations to be deleted. This field is a list of identifiers "
            + "of the relations that need to be deleted from the graph. Each identifier in the list corresponds to "
            + "a specific relation. This field is optional. If not provided, no relations will be deleted.",
            example = "[\"relation1\", \"relation2\"]")
    private List<String> deleteList;
}
