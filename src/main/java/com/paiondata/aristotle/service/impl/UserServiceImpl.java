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
import com.paiondata.aristotle.common.exception.UserExistsException;
import com.paiondata.aristotle.model.dto.UserDTO;
import com.paiondata.aristotle.model.entity.User;
import com.paiondata.aristotle.model.vo.UserVO;
import com.paiondata.aristotle.repository.NodeRepository;
import com.paiondata.aristotle.repository.GraphRepository;
import com.paiondata.aristotle.repository.UserRepository;
import com.paiondata.aristotle.service.CommonService;
import com.paiondata.aristotle.service.UserService;

import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for user-related operations.
 * This class provides methods for managing users, including creating, updating, and deleting users.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GraphRepository graphRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private CommonService commonService;

    /**
     * Retrieves a user view object (VO) by their unique identifier (uidcid).
     *
     * Attempts to find the user by their uidcid using the {@link UserRepository#getUserByUidcid(String)} method.
     * Throws a {@link UserNullException} if the user is not found.
     * Constructs and returns a {@link UserVO} object containing the user's details and their associated graphs.
     * The associated graphs are retrieved using the {@link CommonService#getGraphsByUidcid(String)} method.
     *
     * @param uidcid the unique identifier of the user
     * @return a {@link UserVO} object representing the user and their associated graphs
     * @throws UserNullException if the user with the specified uidcid is not found
     */
    @Transactional(readOnly = true)
    @Override
    public UserVO getUserVOByUidcid(final String uidcid) {
        final User user = userRepository.getUserByUidcid(uidcid);

        if (user == null) {
            final String message = Message.USER_NULL + uidcid;
            LOG.error(message);
            throw new UserNullException(message);
        }

        return UserVO.builder()
                .uidcid(user.getUidcid())
                .nickName(user.getNickName())
                .graphs(commonService.getGraphsByUidcid(user.getUidcid()))
                .build();
    }

    /**
     * Retrieves a list of all users as user view objects (VOs).
     *
     * Retrieves all users from the repository using the {@link UserRepository#findAll()} method.
     * Maps each user to a {@link UserVO} object containing the user's details and their associated graphs.
     * The associated graphs are retrieved using the {@link CommonService#getGraphsByUidcid(String)} method.
     * Returns a list of {@link UserVO} objects.
     *
     * @return a list of {@link UserVO} objects representing all users and their associated graphs
     */
    @Transactional(readOnly = true)
    @Override
    public List<UserVO> getAllUsers() {
        final List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> UserVO.builder()
                        .uidcid(user.getUidcid())
                        .nickName(user.getNickName())
                        .graphs(commonService.getGraphsByUidcid(user.getUidcid()))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Creates a new user.
     * <p>
     * Attempts to create a new user in the repository using
     * the {@link UserRepository#createUser(String, String)} method.
     * If the user creation fails due to a data integrity violation (e.g., duplicate uidcid),
     * a {@link UserExistsException} is thrown.
     * Returns a {@link UserDTO} object containing the details of the newly created user.
     *
     * @param user the {@link UserDTO} object containing the user's details
     * @return a {@link UserDTO} object representing the newly created user
     * @throws UserExistsException if a user with the same uidcid already exists
     */
    @Transactional
    @Override
    public UserDTO createUser(final UserDTO user) {
        final String uidcid = user.getUidcid();

        try {
            final User returnUser = userRepository.createUser(uidcid, user.getNickName());
            return new UserDTO(returnUser.getUidcid(), returnUser.getNickName());
        } catch (final DataIntegrityViolationException e) {
            final String message = Message.UIDCID_EXISTS + uidcid;
            LOG.error(message);
            throw new UserExistsException(message);
        }
    }

    /**
     * Updates an existing user.
     *
     * Checks if a user with the given uidcid exists using the {@link UserRepository#checkUidcidExists(String)} method.
     * If the user exists, updates the user's nickname using
     * the {@link UserRepository#updateUser(String, String)} method.
     * If the user does not exist, throws a {@link UserNullException}.
     *
     * @param userDTO the {@link UserDTO} object containing the updated user details
     * @throws UserNullException if the user with the specified uidcid does not exist
     */
    @Transactional
    @Override
    public void updateUser(final UserDTO userDTO) {
        final String uidcid = userDTO.getUidcid();

        if (userRepository.checkUidcidExists(uidcid) != 0) {
            userRepository.updateUser(uidcid, userDTO.getNickName());
        } else {
            final String message = Message.USER_NULL + uidcid;
            LOG.error(message);
            throw new UserNullException(message);
        }
    }

    /**
     * Deletes multiple users and their related graphs and nodes.
     * <p>
     * Iterates through the provided list of user identifiers (uidcids) and checks if each user exists using
     * the {@link CommonService#getUserByUidcid(String)} method.
     * Throws a {@link UserNullException} if any user does not exist.
     * Retrieves the UUIDs of graphs related to the users using the {@link #getRelatedGraphUuids(List)} method.
     * Retrieves the UUIDs of nodes related to the graphs using the {@link #getRelatedGraphNodeUuids(List)} method.
     * Deletes the users from the user repository using the {@link UserRepository#deleteByUidcids(List)} method.
     * Deletes the related graph from the graph repository using the {@link GraphRepository#deleteByUuids(List)} method.
     * Deletes the related nodes from the node repository using the {@link NodeRepository#deleteByUuids(List)} method.
     *
     * @param uidcids the list of user identifiers to be deleted
     * @throws UserNullException if any user with the specified uidcid does not exist
     */
    @Transactional
    @Override
    public void deleteUser(final List<String> uidcids) {
        for (final String uidcid : uidcids) {
            if (commonService.getUserByUidcid(uidcid).isEmpty()) {
                final String message = Message.USER_NULL + uidcid;
                LOG.error(message);
                throw new UserNullException(message);
            }
        }

        final List<String> graphUuids = getRelatedGraphUuids(uidcids);
        final List<String> graphNodeUuids = getRelatedGraphNodeUuids(graphUuids);

        userRepository.deleteByUidcids((uidcids));
        graphRepository.deleteByUuids(graphUuids);
        nodeRepository.deleteByUuids(graphNodeUuids);
    }

    /**
     * Retrieves the UUIDs of related graphs for a list of user UIDCIDs.
     *
     * @param userUidcids a list of user UIDCIDs
     * @return a list of related graph UUIDs
     */
    private List<String> getRelatedGraphUuids(final List<String> userUidcids) {
        return userRepository.getGraphUuidsByUserUidcid(userUidcids);
    }

    /**
     * Retrieves the UUIDs of related graph nodes for a list of graph UUIDs.
     *
     * @param graphUuids a list of graph UUIDs
     * @return a list of related graph node UUIDs
     */
    private List<String> getRelatedGraphNodeUuids(final List<String> graphUuids) {
        return graphRepository.getGraphNodeUuidsByGraphUuids(graphUuids);
    }
}
