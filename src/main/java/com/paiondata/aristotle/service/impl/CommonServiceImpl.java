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
package com.paiondata.aristotle.service.impl;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.UserNullException;
import com.paiondata.aristotle.mapper.GraphMapper;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.repository.UserRepository;
import com.paiondata.aristotle.service.CommonService;

import org.neo4j.driver.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.UUID;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the CommonService interface.
 */
@Service
public class CommonServiceImpl implements CommonService {

    private static final Logger LOG = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GraphRepository graphRepository;

    @Autowired
    private GraphMapper graphMapper;

    /**
     * Retrieves a user by their unique identifier (uidcid).
     *
     * Attempts to find the user by their uidcid using the {@link UserRepository#getUserByUidcid(String)} method.
     * Returns an {@code Optional} containing the user if found, or an empty {@code Optional} if not found.
     *
     * @param uidcid the unique identifier of the user
     * @return an {@code Optional} containing the user if found, or an empty {@code Optional} if not found
     */
    @Override
    public Optional<User> getUserByUidcid(final String uidcid) {
        final User user = userRepository.getUserByUidcid(uidcid);
        return Optional.ofNullable(user);
    }

    /**
     * Retrieves a graph by its UUID.
     *
     * Attempts to find the graph by its UUID using the {@link GraphRepository#getGraphByUuid(String)} method.
     * Returns an {@code Optional} containing the graph if found, or an empty {@code Optional} if not found.
     *
     * @param uuid the UUID of the graph
     * @return an {@code Optional} containing the graph if found, or an empty {@code Optional} if not found
     */
    @Override
    public Optional<Graph> getGraphByUuid(final String uuid) {
        final Graph graph = graphRepository.getGraphByUuid(uuid);
        return Optional.ofNullable(graph);
    }

    /**
     * Retrieves a list of graphs associated with a user by their user identifier (uidcid).
     *
     * Checks if the user with the provided uidcid exists using <br>
     * the {@link UserRepository#getUserByUidcid(String)} method.
     * Throws a {@link UserNullException} if the user is not found.
     * Retrieves the list of graphs associated with the user using <br>
     * the {@link GraphMapper#getGraphsByUidcid(String)} method.
     *
     * @param uidcid The user identifier.
     * @return A list of maps, where each map represents a graph and contains its details.
     * @throws UserNullException If the user with the specified uidcid is not found.
     */
    @Override
    public List<Map<String, Object>> getGraphsByUidcid(final String uidcid) {
        if (userRepository.getUserByUidcid(uidcid) == null) {
            final String message = Message.USER_NULL + uidcid;
            LOG.error(message);
            throw new UserNullException(message);
        }

        return graphMapper.getGraphsByUidcid(uidcid);
    }

    /**
     * Creates and binds a new graph to a user.
     *
     * Retrieves the title, description, and user identifier from the provided DTO.
     * Generates unique UUIDs for the graph and its relation.
     * Retrieves the current time.
     * Attempts to find the user by the provided user identifier using the {@link #getUserByUidcid(String)} method.
     * Throws a {@link UserNullException} if the user is not found.
     * Creates and binds the graph to the user using <br>
     * the {@link GraphMapper#createGraph(String, String, String, String, String, String, Transaction)} method.
     *
     * @param graphCreateDTO The DTO containing the information for creating the graph. <br>
     *                       It includes the graph title, description, and user identifier.
     * @param tx The Neo4j transaction object used for the database operation.
     * @return The created {@link Graph} object.
     * @throws UserNullException If the user with the specified identifier is not found.
     */
    @Override
    public Graph createAndBindGraph(final GraphCreateDTO graphCreateDTO, final Transaction tx) {
        final String title = graphCreateDTO.getTitle();
        final String description = graphCreateDTO.getDescription();
        final String uidcid = graphCreateDTO.getUserUidcid();
        final String graphUuid = UUID.fastUUID().toString(true);
        final String relationUuid = UUID.fastUUID().toString(true);
        final String currentTime = getCurrentTime();

        final Optional<User> optionalUser = getUserByUidcid(uidcid);
        if (optionalUser.isEmpty()) {
            final String message = Message.USER_NULL + uidcid;
            LOG.error(message);
            throw new UserNullException(message);
        }

        return graphMapper.createGraph(title, description, uidcid, graphUuid, relationUuid, currentTime, tx);
    }

    /**
     * Gets the current time in the specified format.
     *
     * @return the current time
     */
    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
    }
}
