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
package com.paiondata.aristotle

import com.paiondata.aristotle.base.TestConstants

import io.restassured.http.ContentType
import org.junit.jupiter.api.Assertions

import javax.validation.constraints.NotNull
import org.junit.Assert
import io.restassured.RestAssured
import io.restassured.response.Response
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

abstract class AbstractITSpec extends Specification {

    protected static final int WS_PORT = 8080
    private static final int OK_CODE = 200
    private static final String USER_ENDPOINT = "/user"

    private static final String NODE_ENDPOINT = "/node"
    private static final String CREATE_UPDATE_USER_JSON = "create-update-user.json"
    private static final String UPDATE_GRAPH_JSON = "update-graph.json"
    private static final String GET_GRAPH_JSON = "get-graph.json"
    private static final String DELETE_GRAPH_JSON = "delete-graph.json"
    private static final String UPDATE_NODE_JSON = "update-node.json"
    private static final String TEST_UIDCID = "6b47"
    private static final String TEST_NICK_NAME = "Jame"
    private static final String UPDATE_NICK_NAME = "Fame"
    private static final String TEST_GRAPH_TITLE = "Rus"
    private static final String UPDATE_GRAPH_TITLE = "Kas"
    private static final String UPDATE_NODE_TITLE = "Los"

    @NotNull
    protected String payload(final @NotNull String resourceName) {
        return resource("payload", resourceName)
    }

    @NotNull
    protected String resource(final @NotNull String resourceDirPath, final @NotNull String resourceFilename) {
        Objects.requireNonNull(resourceDirPath)
        Objects.requireNonNull(resourceFilename)

        final String resource = String.format(
                "%s/%s",
                resourceDirPath.endsWith("/")
                        ? resourceDirPath.substring(0, resourceDirPath.length() - 1)
                        : resourceDirPath,
                resourceFilename
        )

        try {
            return new String(
                    Files.readAllBytes(
                            Paths.get(
                                    Objects.requireNonNull(
                                            this.getClass()
                                                    .getClassLoader()
                                                    .getResource(resource)
                                    )
                                            .toURI()
                            )
                    )
            )
        } catch (final IOException exception) {
            final String message = String.format("Error reading file stream from '%s'", resource)
            throw new IllegalStateException(message, exception)
        } catch (final URISyntaxException exception) {
            final String message = String.format("'%s' is not a valid URI fragment", resource)
            throw new IllegalArgumentException(message, exception)
        }
    }

    def childSetupSpec() {
        // intentionally left blank
    }

    def childCleanupSpec() {
        // intentionally left blank
    }

    def setupSpec() {
        childSetupSpec()

        RestAssured.baseURI = "http://localhost"
        RestAssured.port = WS_PORT
    }

    def cleanupSpec() {
        RestAssured.reset()

        childCleanupSpec()
    }

    def "JSON API handles invalid user creation requests"() {
        expect:
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(CREATE_UPDATE_USER_JSON), uidcid, nickName))
                .when()
                .post(USER_ENDPOINT)
                .then()
                .extract()
                .response()

        def actualData = response.jsonPath().get(TestConstants.DATA) as List<String>
        def sortedActualData = actualData.sort()
        def sortedExpectedData = expectedData.sort()
        assert sortedActualData == sortedExpectedData

        where:
        uidcid   | nickName       | expectedMsg                              | expectedData
        ""       | ""             | "Request parameter verification error: " | ["nickName must not be blank!", "uidcid must not be blank!"]
        ""       | "name"         | "Request parameter verification error: " | ["uidcid must not be blank!"]
        "id"     | ""             | "Request parameter verification error: " | ["nickName must not be blank!"]
    }

    def "JSON API handles invalid user updating requests"() {
        expect:
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(CREATE_UPDATE_USER_JSON), uidcid, nickName))
                .when()
                .put(USER_ENDPOINT)
                .then()
                .extract()
                .response()

        def actualData = response.jsonPath().get(TestConstants.DATA) as List<String>
        def sortedActualData = actualData.sort()
        def sortedExpectedData = expectedData.sort()
        assert sortedActualData == sortedExpectedData

        where:
        uidcid   | nickName       | expectedMsg                              | expectedData
        ""       | ""             | "Request parameter verification error: " | ["nickName must not be blank!", "uidcid must not be blank!"]
        ""       | "name"         | "Request parameter verification error: " | ["uidcid must not be blank!"]
        "id"     | ""             | "Request parameter verification error: " | ["nickName must not be blank!"]
    }

    def "JSON API handles invalid user deleting requests"() {
        expect:
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("[]")
                .when()
                .delete(USER_ENDPOINT)
                .then()
                .extract()
                .response()

        Assertions.assertEquals("deleteUser.uidcids: uidcids must not be empty!", response.jsonPath().get("msg"))
    }

    def "JSON API handles invalid graph and node creation requests"() {
        expect:
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-graph-nodes-to-valid.json"), uidcid, title, description,
                        temporaryId1, temporaryId2, fromId, relationName, toId))
                .when()
                .post(NODE_ENDPOINT + "/graph")
                .then()
                .extract()
                .response()

        def actualData = response.jsonPath().get(TestConstants.DATA) as List<String>
        def sortedActualData = actualData.sort()
        def sortedExpectedData = expectedData.sort()
        assert sortedActualData == sortedExpectedData

        where:
        uidcid   | title      | description   | temporaryId1 | temporaryId2 | fromId | relationName | toId  | expectedMsg                              | expectedData
        ""       | "title"    | "description" | "id1"        | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["uidcid must not be blank!"]
        "uidcid" | ""         | "description" | "id1"        | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["title must not be blank!"]
        "uidcid" | "title"    | ""            | "id1"        | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["description must not be blank!"]
        "uidcid" | "title"    | "description" | ""           | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["temporaryId must not null!"]
        "uidcid" | "title"    | "description" | "id1"        | ""           | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["temporaryId must not null!"]
        "uidcid" | "title"    | "description" | "id1"        | "id2"        | ""     | "relation"   | "id2" | "Request parameter verification error: " | ["fromId must not be blank!"]
        "uidcid" | "title"    | "description" | "id1"        | "id2"        | "id1"  | ""           | "id2" | "Request parameter verification error: " | ["relation must not be blank!"]
        "uidcid" | "title"    | "description" | "id1"        | "id2"        | "id1"  | "relation"   | ""    | "Request parameter verification error: " | ["toId must not be blank!"]
        ""       | ""         | "description" | "id1"        | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["uidcid must not be blank!", "title must not be blank!"]
        ""       | "title"    | ""            | "id1"        | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["uidcid must not be blank!", "description must not be blank!"]
        ""       | "title"    | "description" | ""           | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["uidcid must not be blank!", "temporaryId must not null!"]
        ""       | "title"    | "description" | "id1"        | ""           | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["uidcid must not be blank!", "temporaryId must not null!"]
        ""       | "title"    | "description" | "id1"        | "id2"        | ""     | "relation"   | "id2" | "Request parameter verification error: " | ["uidcid must not be blank!", "fromId must not be blank!"]
        ""       | "title"    | "description" | "id1"        | "id2"        | "id1"  | ""           | "id2" | "Request parameter verification error: " | ["uidcid must not be blank!", "relation must not be blank!"]
        ""       | "title"    | "description" | "id1"        | "id2"        | "id1"  | "relation"   | ""    | "Request parameter verification error: " | ["uidcid must not be blank!", "toId must not be blank!"]
        "uidcid" | ""         | ""            | "id1"        | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["title must not be blank!", "description must not be blank!"]
        "uidcid" | ""         | "description" | ""           | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["title must not be blank!", "temporaryId must not null!"]
        "uidcid" | ""         | "description" | "id1"        | ""           | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["title must not be blank!", "temporaryId must not null!"]
        "uidcid" | ""         | "description" | "id1"        | "id2"        | ""     | "relation"   | "id2" | "Request parameter verification error: " | ["title must not be blank!", "fromId must not be blank!"]
        "uidcid" | ""         | "description" | "id1"        | "id2"        | "id1"  | ""           | "id2" | "Request parameter verification error: " | ["title must not be blank!", "relation must not be blank!"]
        "uidcid" | ""         | "description" | "id1"        | "id2"        | "id1"  | "relation"   | ""    | "Request parameter verification error: " | ["title must not be blank!", "toId must not be blank!"]
        "uidcid" | "title"    | ""            | ""           | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["description must not be blank!", "temporaryId must not null!"]
        "uidcid" | "title"    | ""            | "id1"        | ""           | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["description must not be blank!", "temporaryId must not null!"]
        "uidcid" | "title"    | ""            | "id1"        | "id2"        | ""     | "relation"   | "id2" | "Request parameter verification error: " | ["description must not be blank!", "fromId must not be blank!"]
        "uidcid" | "title"    | ""            | "id1"        | "id2"        | "id1"  | ""           | "id2" | "Request parameter verification error: " | ["description must not be blank!", "relation must not be blank!"]
        "uidcid" | "title"    | ""            | "id1"        | "id2"        | "id1"  | "relation"   | ""    | "Request parameter verification error: " | ["description must not be blank!", "toId must not be blank!"]
        "uidcid" | "title"    | "description" | ""           | ""           | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["temporaryId must not null!", "temporaryId must not null!"]
        "uidcid" | "title"    | "description" | ""           | "id2"        | ""     | "relation"   | "id2" | "Request parameter verification error: " | ["temporaryId must not null!", "fromId must not be blank!"]
    }

    def "JSON API handles invalid node creation requests"() {
        expect:
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-nodes-to-valid.json"), graphUuid,
                        temporaryId1, temporaryId2, fromId, relationName, toId))
                .when()
                .post(NODE_ENDPOINT)
                .then()
                .extract()
                .response()

        def actualData = response.jsonPath().get(TestConstants.DATA) as List<String>
        def sortedActualData = actualData.sort()
        def sortedExpectedData = expectedData.sort()
        assert sortedActualData == sortedExpectedData

        where:
        graphUuid | temporaryId1 | temporaryId2 | fromId | relationName | toId  | expectedMsg                              | expectedData
        ""        | "id1"        | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["uuid must not be blank!"]
        "id"      | ""           | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["temporaryId must not null!"]
        ""        | ""           | "id2"        | "id1"  | "relation"   | "id2" | "Request parameter verification error: " | ["uuid must not be blank!", "temporaryId must not null!"]
        "id"      | "id1"        | "id2"        | ""     | "relation"   | "id2" | "Request parameter verification error: " | ["fromId must not be blank!"]
        "id"      | "id1"        | "id2"        | "id1"  | "relation"   | ""    | "Request parameter verification error: " | ["toId must not be blank!"]
        "id"      | "id1"        | "id2"        | ""     | "relation"   | ""    | "Request parameter verification error: " | ["fromId must not be blank!", "toId must not be blank!"]
    }

    def "JSON API handles invalid node binding requests"() {
        expect:
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("relate-node-to-valid.json"), fromId, relationName, toId))
                .when()
                .post(NODE_ENDPOINT + "/bind")
                .then()
                .extract()
                .response()

        def actualMsg = response.jsonPath().get("msg")
        assert actualMsg == expectedMsg

        where:
        fromId | relationName | toId  | expectedMsg
        ""     | "relation"   | "id2" | "bindNodes.dtos[0].fromId: uuid must not be blank!"
        "id1"  | ""           | "id2" | "bindNodes.dtos[0].relationName: relation must not be blank!"
        "id1"  | "relation"   | ""    | "bindNodes.dtos[0].toId: uuid must not be blank!"
    }

    def "JSON API handles invalid node updating requests"() {
        expect:
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(UPDATE_NODE_JSON), "", ""))
                .when()
                .post(NODE_ENDPOINT + "/update")
                .then()
                .extract()
                .response()

        Assertions.assertEquals("Request parameter verification error: ", response.jsonPath().get("msg"))
        Assertions.assertEquals("uuid must not be blank!", response.jsonPath().get("data[0]"))
    }

    def "JSON API allows for POSTing, GETing, PUTTing, and DELETing the user, graph and node"() {
        expect: "database is initially empty"
        Response getUserResponse = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT)
        getUserResponse.then()
                .statusCode(OK_CODE)

        Assert.assertEquals([], getUserResponse.jsonPath().getList(TestConstants.DATA))

        when: "an User entity is POSTed via JSON API"
        Response postUserResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                    .body(String.format(payload(CREATE_UPDATE_USER_JSON), TEST_UIDCID, TEST_NICK_NAME))
                .when()
                .post(USER_ENDPOINT)
                .then()
                .extract()
                .response()

        postUserResponse.then()
                .statusCode(OK_CODE)

        Assertions.assertEquals(postUserResponse.jsonPath().get("data.uidcid"), TEST_UIDCID)
        Assertions.assertEquals(postUserResponse.jsonPath().get("data.nickName"), TEST_NICK_NAME)

        then: "we can GET that User entity next"
        Response getUserEntityResponse = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT + "/" + TEST_UIDCID)
                .then()
                .extract()
                .response()

        getUserEntityResponse.then()
                .statusCode(200)

        Assert.assertEquals(TEST_UIDCID, getUserEntityResponse.jsonPath().get("data.uidcid"))
        Assert.assertEquals(TEST_NICK_NAME, getUserEntityResponse.jsonPath().get("data.nickName"))

        when: "we update that User entity"
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(CREATE_UPDATE_USER_JSON), TEST_UIDCID, UPDATE_NICK_NAME))
                .when()
                .put(USER_ENDPOINT)
                .then()
                .statusCode(OK_CODE)

        then: "we can GET that User entity with updated attribute"
        Response getUpdatedUserEntityResponse = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT + "/" + TEST_UIDCID)
                .then()
                .extract()
                .response()

        getUpdatedUserEntityResponse.then()
                .statusCode(200)

        Assert.assertEquals(TEST_UIDCID, getUpdatedUserEntityResponse.jsonPath().get("data.uidcid"))
        Assert.assertEquals(UPDATE_NICK_NAME, getUpdatedUserEntityResponse.jsonPath().get("data.nickName"))

        when: "a Graph entity is POSTed via JSON API"
        Response postGraphResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-graph.json"), TEST_UIDCID, TEST_GRAPH_TITLE))
                .when()
                .post("/node/graph")
                .then()
                .extract()
                .response()

        postGraphResponse.then()
                .statusCode(OK_CODE)

        Assertions.assertNotNull(postGraphResponse.jsonPath().get("data.uuid"))
        Assertions.assertEquals(postGraphResponse.jsonPath().get("data.title"), TEST_GRAPH_TITLE)

        then: "we can GET that Graph entity next"
        Response getGraphEntityResponse = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT + "/" + TEST_UIDCID)
                .then()
                .extract()
                .response()

        getGraphEntityResponse.then()
                .statusCode(200)

        Assert.assertEquals(TEST_UIDCID, getGraphEntityResponse.jsonPath().get("data.uidcid"))
        Assert.assertEquals(TEST_GRAPH_TITLE, getGraphEntityResponse.jsonPath().get("data.graphs[0].title"))

        when: "we update that Graph entity"
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(UPDATE_GRAPH_JSON),
                        getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid"), UPDATE_GRAPH_TITLE))
                .when()
                .put(GRAPH_ENDPOINT)
                .then()
                .statusCode(OK_CODE)

        then: "we can GET that Graph entity with updated attribute"
        Response getUpdatedGraphEntityResponse = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT + "/" + TEST_UIDCID)
                .then()
                .extract()
                .response()

        getUpdatedGraphEntityResponse.then()
                .statusCode(200)

        Assert.assertEquals(getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid"),
                getUpdatedGraphEntityResponse.jsonPath().get("data.graphs[0].uuid"))
        Assert.assertEquals(UPDATE_GRAPH_TITLE, getUpdatedGraphEntityResponse.jsonPath().get("data.graphs[0].title"))

        when: "the Nodes entity is POSTed via JSON API"
        Response postNodeResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-node.json"),
                        getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid"),
                        "Mos", "Nos", "Aos"))
                .when()
                .post(NODE_ENDPOINT)
                .then()
                .extract()
                .response()

        postNodeResponse.then()
                .statusCode(OK_CODE)

        then: "we can GET that Node entity next"
        Response getNodeEntityResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(GET_GRAPH_JSON),
                        getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid")))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response()

        getNodeEntityResponse.then()
                .statusCode(200)

        Assert.assertNotNull(getNodeEntityResponse.jsonPath().get("data.nodes[0].properties.title"))
        Assert.assertNotNull(getNodeEntityResponse.jsonPath().get("data.nodes[1].properties.title"))
        Assert.assertNotNull(getNodeEntityResponse.jsonPath().get("data.nodes[2].properties.title"))

        when: "we update that Node entity"
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(UPDATE_NODE_JSON),
                        getNodeEntityResponse.jsonPath().get("data.nodes[0].uuid"), UPDATE_NODE_TITLE))
                .when()
                .post(NODE_ENDPOINT + "/update")
                .then()
                .statusCode(OK_CODE)

        then: "we can GET that Node entity with updated attribute"
        Response getUpdatedNodeEntityResponse = RestAssured
                .given()
                .when()
                .get(NODE_ENDPOINT + "/" + getNodeEntityResponse.jsonPath().get("data.nodes[0].uuid"))
                .then()
                .extract()
                .response()

        getUpdatedNodeEntityResponse.then()
                .statusCode(200)

        Assert.assertEquals(getNodeEntityResponse.jsonPath().get("data.nodes[0].uuid"),
                getUpdatedNodeEntityResponse.jsonPath().get("data.uuid"))
        Assert.assertEquals(UPDATE_NODE_TITLE, getUpdatedNodeEntityResponse.jsonPath().get("data.properties.title"))

        when: "the nodes are related"
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("relate-node.json"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[0].uuid"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[1].uuid"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[0].uuid"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[2].uuid"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[1].uuid"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[2].uuid")))
                .when()
                .post("/node/bind")
                .then()
                .extract()
                .response()

        then: "we can get the relation of nodes via graph uuid"
        final Response getRelationResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(GET_GRAPH_JSON),
                        getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid")))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response()

        Assert.assertEquals("-", getRelationResponse.jsonPath().get("data.relations[0].name"))

        when: "the Node entity is deleted"
        final Response deleteNodeResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("delete-node.json"),
                        getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid"),
                        getNodeEntityResponse.jsonPath().get("data.nodes[1].startNode.uuid")))
                .when()
                .delete(NODE_ENDPOINT)
        deleteNodeResponse.then()
                .statusCode(OK_CODE)

        then: "that Node entity is not found in database anymore"
        Response getResponse4 = RestAssured
                .given()
                .when()
                .get(NODE_ENDPOINT + "/" + getNodeEntityResponse.jsonPath().get("data.nodes[1].startNode.uuid"))
        getResponse4.then()
                .statusCode(OK_CODE)

        Assert.assertEquals(null, getResponse4.jsonPath().get(TestConstants.DATA))

        when: "create graph and nodes and bind relationships in one step"
        Response GraphResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-graph-nodes.json"), TEST_UIDCID, TEST_GRAPH_TITLE,
                        TestConstants.BLUE, TestConstants.BLUE, TestConstants.GREEN,
                        TestConstants.BLUE, TestConstants.BLUE, TestConstants.BLUE, TestConstants.BLUE))
                .when()
                .post("/node/graph")
                .then()
                .extract()
                .response()

        postGraphResponse.then()
                .statusCode(OK_CODE)

        Assertions.assertNotNull(postGraphResponse.jsonPath().get("data.uuid"))
        Assertions.assertEquals(postGraphResponse.jsonPath().get("data.title"), TEST_GRAPH_TITLE)

        then: "we can GET that Graph entity next by filter and page params"
        Response getGraphEntityFilterResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("get-graph-filter.json"),
                        GraphResponse.jsonPath().get("data.uuid"), TestConstants.BLUE,
                        TestConstants.DEFALUT_PAGE_SIZE, TestConstants.DEFALUT_PAGE_NUMBER))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response()

        getGraphEntityFilterResponse.then()
                .statusCode(200)

        Assert.assertEquals(TestConstants.DEFALUT_PAGE_NUMBER, getGraphEntityFilterResponse
                .jsonPath().get("data.pageNumber"))
        Assert.assertEquals(TestConstants.DEFALUT_PAGE_SIZE, getGraphEntityFilterResponse
                .jsonPath().get("data.pageSize"))
        Assert.assertEquals(TestConstants.EXPECT_TOTAL_COUNT_02, getGraphEntityFilterResponse
                .jsonPath().get("data.totalCount"))
        Assert.assertEquals(TEST_GRAPH_TITLE,
                getGraphEntityFilterResponse.jsonPath().get("data.title"))
        Assert.assertEquals(TestConstants.BLUE,
                getGraphEntityFilterResponse.jsonPath().get("data.nodes[0].properties.color"))
        Assert.assertEquals(TestConstants.BLUE,
                getGraphEntityFilterResponse.jsonPath().get("data.nodes[1].properties.color"))
        Assert.assertNotNull(getGraphEntityFilterResponse.jsonPath().get("data.relations[0]"))
        Assert.assertNull(getGraphEntityFilterResponse.jsonPath().get("data.relations[1]"))

        Response getGraphEntityFilterResponsePage1 = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("get-graph-filter.json"),
                        GraphResponse.jsonPath().get("data.uuid"), TestConstants.BLUE,
                        TestConstants.TEST_PAGE_SIZE_01, TestConstants.DEFALUT_PAGE_NUMBER))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response()

        getGraphEntityFilterResponsePage1.then()
                .statusCode(200)

        Assert.assertEquals(TestConstants.DEFALUT_PAGE_NUMBER, getGraphEntityFilterResponsePage1
                .jsonPath().get("data.pageNumber"))
        Assert.assertEquals(TestConstants.TEST_PAGE_SIZE_01, getGraphEntityFilterResponsePage1
                .jsonPath().get("data.pageSize"))
        Assert.assertEquals(TestConstants.EXPECT_TOTAL_COUNT_03, getGraphEntityFilterResponsePage1
                .jsonPath().get("data.totalCount"))

        Response getGraphEntityFilterResponsePage2 = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("get-graph-filter.json"),
                        GraphResponse.jsonPath().get("data.uuid"), TestConstants.BLUE,
                        TestConstants.TEST_PAGE_SIZE_01, TestConstants.TEST_PAGE_NUMBER_01))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response()

        getGraphEntityFilterResponsePage2.then()
                .statusCode(200)

        Assert.assertEquals(TestConstants.TEST_PAGE_NUMBER_01, getGraphEntityFilterResponsePage2
                .jsonPath().get("data.pageNumber"))
        Assert.assertEquals(TestConstants.TEST_PAGE_SIZE_01, getGraphEntityFilterResponsePage2
                .jsonPath().get("data.pageSize"))
        Assert.assertEquals(TestConstants.EXPECT_TOTAL_COUNT_03, getGraphEntityFilterResponsePage2
                .jsonPath().get("data.totalCount"))

        when: "the Graph entity is deleted"
        final Response deleteGraphResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload((DELETE_GRAPH_JSON)), TEST_UIDCID,
                        getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid")))
                .when()
                .delete(GRAPH_ENDPOINT)
        deleteGraphResponse.then()
                .statusCode(OK_CODE)

        then: "that Graph entity is not found in database anymore"
        Response getResponse3 = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(GET_GRAPH_JSON),
                        getGraphEntityResponse.jsonPath().get("data.graphs[0].uuid")))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response()

        Assert.assertEquals(null, getResponse3.jsonPath().getList(TestConstants.DATA))

        when: "the User entity is deleted"
        final Response deleteUserResponse = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Arrays.asList(TEST_UIDCID))
                .when()
                .delete(USER_ENDPOINT)
        deleteUserResponse.then()
                .statusCode(OK_CODE)

        then: "that User entity is not found in database anymore"
        Response getResponse2 = RestAssured
                .given()
                .when()
                .get(USER_ENDPOINT + "/" + TEST_UIDCID)
        getResponse2.then()
                .statusCode(OK_CODE)

        Assert.assertEquals(null, getResponse2.jsonPath().getList(TestConstants.DATA))
    }
}
