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
package com.paiondata.aristotle.common.base;

/**
 * Defines common constants used throughout the application.
 */
public interface Constants {

    /**
     * Represents the UTF-8 character encoding.
     */
    String UTF8 = "UTF-8";

    /**
     * Represents the GBK character encoding.
     */
    String GBK = "GBK";

    /**
     * Prefix for web addresses.
     */
    String WWW = "www.";

    /**
     * Prefix for HTTP protocol.
     */
    String HTTP = "http://";

    /**
     * Prefix for HTTPS protocol.
     */
    String HTTPS = "https://";

    /**
     * Indicates a successful operation.
     */
    Integer SUCCESS = HttpStatus.SUCCESS;

    /**
     * Indicates a failed operation.
     */
    Integer FAIL = HttpStatus.ERROR;

    /**
     * Represents the ID field in the database.
     */
    String ID = "id";

    /**
     * Represents the UIDCID field in the database.
     */
    String UIDCID = "uidcid";

    /**
     * Represents the UUID field in the database.
     */
    String UUID = "uuid";

    /**
     * Represents the graph UUID field in the database.
     */
    String GRAPH_UUID = "graphUuid";

    /**
     * Represents the node UUID field in the database.
     */
    String NODE_UUID = "nodeUuid";

    /**
     * Represents hte relation field in the database.
     */
    String RELATION = "relation";

    /**
     * Represents the relation UUID field in the database.
     */
    String RELATION_UUID = "relationUuid";

    /**
     * Represents the start node field in the database.
     */
    String START_NODE = "startNode";

    /**
     * Represents the end node field in the database.
     */
    String END_NODE = "endNode";

    /**
     * Represents the title field in the database.
     */
    String TITLE = "title";

    /**
     * Represents the description field in the database.
     */
    String DESCRIPTION = "description";

    /**
     * Represents the update time field in the database.
     */
    String UPDATE_TIME = "updateTime";

    /**
     * Represents the update time field in the database without hump.
     */
    String UPDATE_TIME_WITHOUT_HUMP = "update_time";

    /**
     * Represents the create time field in the database.
     */
    String CREATE_TIME = "createTime";

    /**
     * Represents the create time field in the database without hump.
     */
    String CREATE_TIME_WITHOUT_HUMP = "create_time";

    /**
     * Represents the current time field in the database.
     */
    String CURRENT_TIME = "currentTime";

    /**
     * Represents the properties field in the database.
     */
    String PROPERTIES = "properties";

    /**
     * Represents the name field in the database.
     */
    String NAME = "name";

    /**
     * Represents the graph node in cypher.
     */
    String GRAPH_IN_CYPHER = "g";

    /**
     * Represents the node alias in cypher.
     */
    String NODE_ALIAS_N1 = "n1";

    /**
     * Represents the node alias in cypher.
     */
    String NODE_ALIAS_N2 = "n2";

    /**
     * Represents the quote in cypher.
     */
    String QUOTE = "'";
}
