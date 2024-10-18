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
package com.paiondata.aristotle.aop;

import com.paiondata.aristotle.common.base.Message;
import com.paiondata.aristotle.common.exception.TransactionException;
import com.paiondata.aristotle.common.util.TransactionManager;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.annotation.Resource;

/**
 * Aspect for handling Neo4j transactions.
 */
@Aspect
@Component
@Order(1)
public class Neo4jTransactionAspect {

    private static final Logger LOG = LoggerFactory.getLogger(Neo4jTransactionAspect.class);

    @Resource
    private TransactionManager neo4jTransactionManager;

    @Autowired
    private Session neo4jSession;

    /**
     * Handles Neo4j transactions.
     * @param joinPoint Join point
     * @return The result of the method call
     * @throws Throwable if an error occurs
     */
    @Around("@annotation(com.paiondata.aristotle.common.annotion.Neo4jTransactional)")
    public Object manageTransaction(final ProceedingJoinPoint joinPoint) throws Throwable {
        Transaction tx = null;
        try {
            tx = neo4jSession.beginTransaction();

            final Object[] args = joinPoint.getArgs();
            final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            final Method method = signature.getMethod();
            final Parameter[] parameters = method.getParameters();

            // Inject the transaction
            injectTransaction(tx, args, parameters);

            final Object result = joinPoint.proceed(args);

            neo4jTransactionManager.commitTransaction(tx);
            return result;
        } catch (final Exception e) {
            if (tx != null) {
                neo4jTransactionManager.rollbackTransaction(tx);
            }

            final String message = e.getMessage();
            LOG.error(message);
            throw e;
        } finally {
            closeTransaction(tx);
        }
    }

    /**
     * Inject the transaction into the method arguments.
     * @param tx Transaction
     * @param args the method arguments
     * @param parameters the method parameters
     */
    private void injectTransaction(final Transaction tx, final Object[] args, final Parameter[] parameters) {
        boolean transactionInjected = false;
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getType().equals(Transaction.class)) {
                args[i] = tx;
                transactionInjected = true;
                break;
            }
        }

        if (!transactionInjected) {
            final String message = Message.METHOD_WITHOUT_TRANSACTION;
            LOG.error(message);
            throw new TransactionException(message);
        }
    }

    /**
     * Close the transaction.
     * @param tx Transaction
     */
    private void closeTransaction(final Transaction tx) {
        if (tx != null && tx.isOpen()) {
            tx.close();
        }
    }
}
