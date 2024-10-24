/*
 * Copyright Jiaqi Liu
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
package com.paiondata.aristotle.controller

import com.paiondata.aristotle.AbstractITSpec
import com.paiondata.aristotle.base.TestConstants

import org.junit.jupiter.api.Assertions

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response

class GraphControllerITSpec extends AbstractITSpec {

    private static final String GRAPH_ENDPOINT = "/graph"

    def "JSON API handles invalid graph retrieving requests"() {
        expect:
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(GET_GRAPH_JSON), ""))
                .when()
                .post(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response()

        Assertions.assertEquals("Request parameter verification error: ", response.jsonPath().get("msg"))
        Assertions.assertEquals("uuid must not be blank!", response.jsonPath().get("data[0]"))
    }

    def "JSON API handles invalid graph updating requests"() {
        expect:
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(UPDATE_GRAPH_JSON), "", TestConstants.TEST_TILE1))
                .when()
                .put(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response()

        Assertions.assertEquals("Request parameter verification error: ", response.jsonPath().get("msg"))
        Assertions.assertEquals("uuid must not be blank!", response.jsonPath().get("data[0]"))
    }

    def "JSON API handles invalid graph deleting requests"() {
        expect:
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("delete-graph-to-valid.json"), uidcid))
                .when()
                .delete(GRAPH_ENDPOINT)
                .then()
                .extract()
                .response()

        def actualData = response.jsonPath().get(TestConstants.DATA) as List<String>
        def sortedActualData = actualData.sort()
        def sortedExpectedData = expectedData.sort()
        assert sortedActualData == sortedExpectedData

        where:
        uidcid   | expectedMsg                              | expectedData
        ""       | "Request parameter verification error: " | ["uidcid must not be blank!", "uuids must not be empty!"]
        "id"     | "Request parameter verification error: " | ["uuids must not be empty!"]
    }
}
