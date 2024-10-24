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
package com.paiondata.aristotle.common.exception;

/**
 * This class represents an exception thrown when a delete operation fails.
 */
public class DeleteException extends BaseException {

    /**
     * Constructs a new DeleteException with no detail message.
     */
    public DeleteException() {
    }

    /**
     * Constructs a new DeleteException with the specified detail message.
     * @param msg the detail message.
     */
    public DeleteException(final String msg) {
        super(msg);
    }
}
