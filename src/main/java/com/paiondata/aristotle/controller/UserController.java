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
package com.paiondata.aristotle.controller;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.base.Result;
import com.paiondata.aristotle.model.dto.UserDTO;
import com.paiondata.aristotle.model.vo.UserVO;
import com.paiondata.aristotle.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Controller for handling user-related operations.
 */
@Api(tags = "User controller for handling user-related operations")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Retrieves a user and its graphs by UID/CID.
     *
     * @param uidcid the UID/CID of the user
     * @return the result containing the user and its graphs or an error message if not found
     */
    @ApiOperation(value = "Retrieves a user and its graphs by UID/CID")
    @GetMapping("/{uidcid}")
    public Result<UserVO> getUser(@PathVariable @NotBlank(message = Message.UIDCID_MUST_NOT_BE_BLANK)
                                      final String uidcid) {
        return Result.ok(userService.getUserVOByUidcid(uidcid));
    }

    /**
     * Retrieves all users and their graphs.
     *
     * @return the result containing a list of all users and their graphs
     */
    @ApiOperation(value = "Retrieves all users and their graphs")
    @GetMapping
    public Result<List<UserVO>> getAll() {
        final List<UserVO> allUsers = userService.getAllUsers();
        return Result.ok(allUsers);
    }

    /**
     * Creates a new user.
     *
     * @param userDTO the DTO containing the user creation information
     * @return the result indicating the success of the creation
     */
    @ApiOperation(value = "Creates a new user")
    @PostMapping
    public Result<UserDTO> createUser(@RequestBody @Valid final UserDTO userDTO) {
        return Result.ok(Message.CREATE_SUCCESS, userService.createUser(userDTO));
    }

    /**
     * Updates an existing user.
     *
     * @param userDTO the DTO containing the updated user information
     * @return the result indicating the success of the update
     */
    @ApiOperation(value = "Updates an existing user")
    @PutMapping
    public Result<String> updateUser(@RequestBody final @Valid UserDTO userDTO) {
        userService.updateUser(userDTO);
        return Result.ok(Message.UPDATE_SUCCESS);
    }

    /**
     * Deletes users by their UID/CIDs.
     *
     * @param uidcids the list of UID/CIDs of the users to be deleted
     * @return the result indicating the success of the deletion
     */
    @ApiOperation(value = "Deletes users by their UID/CIDs")
    @DeleteMapping
    public Result<String> deleteUser(@RequestBody @NotEmpty(message = Message.UIDCID_MUST_NOT_BE_BLANK)
                                         final List<String> uidcids) {
        userService.deleteUser(uidcids);
        return Result.ok(Message.DELETE_SUCCESS);
    }
}
