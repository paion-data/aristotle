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
package org.qubitpi.wilhelm.web.endpoints;

import org.qubitpi.wilhelm.config.ApplicationConfig;

import org.aeonbits.owner.ConfigFactory;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.EagerResult;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.QueryConfig;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.types.InternalTypeSystem;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Endpoint that contains a basic sanity-check.
 */
@Singleton
@Immutable
@ThreadSafe
@Path("/data")
@Produces(MediaType.APPLICATION_JSON)
public class DataServlet {

    private static final ApplicationConfig APPLICATION_CONFIG = ConfigFactory.create(ApplicationConfig.class);
    private static final String NEO4J_URL = APPLICATION_CONFIG.neo4jUrl();
    private static final String NEO4J_USERNAME = APPLICATION_CONFIG.neo4jUsername();
    private static final String NEO4J_PASSWORD = APPLICATION_CONFIG.neo4jPassword();
    private static final String NEO4J_DATABASE = APPLICATION_CONFIG.neo4jDatabase();

    private static final Map<String, String> LANGUAGES = Stream.of(
            new AbstractMap.SimpleImmutableEntry<>("german", "German"),
            new AbstractMap.SimpleImmutableEntry<>("ancientGreek", "Ancient Greek"),
            new AbstractMap.SimpleImmutableEntry<>("latin", "Latin")
    )
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));


    /**
     * Constructor for dependency injection.
     */
    @Inject
    public DataServlet() {
        // intentionally left blank
    }

    /**
     * A webservice sanity-check endpoint.
     *
     * @return 200 OK response
     */
    @GET
    @Path("/healthcheck")
    public Response healthcheck() {
        return Response
                .status(Response.Status.OK)
                .build();
    }

    /**
     * Get all vocabularies of a language.
     *
     * @param language  The language. Must be one of "german", "ancientGreek", or "latin". Otherwise a 404 response is
     * returned
     *
     * @return the Neo4J query results in JSON format
     */
    @GET
    @Path("/languages/{language}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVocabularyByLanguage(@NotNull @PathParam("language") final String language) {
        if (!LANGUAGES.containsKey(Objects.requireNonNull(language, "language"))) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(
                            String.format(
                                    "'language' path parameter has to be one of %s",
                                    String.join(", ", LANGUAGES.keySet())
                            )
                    )
                    .build();
        }

        final String query = String.format(
                "MATCH (t:Term WHERE t.language = '%s')-[r]->(d:Definition) " +
                        "RETURN t.name AS term, d.name AS definition",
                LANGUAGES.get(language)
        );

        try (Driver driver = GraphDatabase.driver(NEO4J_URL, AuthTokens.basic(NEO4J_USERNAME, NEO4J_PASSWORD))) {
            driver.verifyConnectivity();

            final EagerResult result = driver.executableQuery(query)
                    .withConfig(QueryConfig.builder().withDatabase(NEO4J_DATABASE).build())
                    .execute();

            return Response
                    .status(Response.Status.OK)
                    .entity(
                            result
                                    .records()
                                    .stream()
                                    .map(
                                            record -> record.keys()
                                                    .stream()
                                                    .map(key -> new AbstractMap.SimpleImmutableEntry<>(
                                                            key,
                                                            expand(record.get(key))
                                                    ))
                                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))

                                    )
                                    .collect(Collectors.toList())
                    )
                    .build();
        }
    }

    /**
     * Transforms a Neo4J {@link Value} object into a Jackson-serializable Java object.
     *
     * See https://neo4j.com/docs/java-manual/current/data-types/ for more details
     *
     * @param value  An object graph. Cannot be {@code null}
     *
     * @return a {@link Map} representation of the object graph and can be Jackson-serialized
     */
    private static Object expand(@NotNull final Value value) {
        if (isTerminalValue(value)) {
            if (value.type().equals(InternalTypeSystem.TYPE_SYSTEM.INTEGER())) {
                return value.asInt();
            } else if (value.type().equals(InternalTypeSystem.TYPE_SYSTEM.BOOLEAN())) {
                return value.asBoolean();
            } else {
                return value.asString();
            }
        }

        return StreamSupport.stream(value.keys().spliterator(), false)
                .map(key -> new AbstractMap.SimpleImmutableEntry<>(key, expand(value.get(key))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Returns whether or not a {@link Value} object is the "leaf" node in Jackson serialization.
     * <p>
     * A "leaf" node is defined to be one of
     * <ul>
     *     <li> integer
     *     <li> string
     *     <li> boolean
     * </ul>
     *
     * @param value  An object graph. Cannot be {@code null}
     *
     * @return {@code true} if the object is simply a Jackson-serializable leaf node or {@code false} otherwise
     */
    private static boolean isTerminalValue(@NotNull final Value value) {
        return value.type().equals(InternalTypeSystem.TYPE_SYSTEM.INTEGER())
                || value.type().equals(InternalTypeSystem.TYPE_SYSTEM.STRING())
                || value.type().equals(InternalTypeSystem.TYPE_SYSTEM.BOOLEAN());
    }
}