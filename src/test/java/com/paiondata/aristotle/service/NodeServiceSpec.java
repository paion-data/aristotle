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
package com.paiondata.aristotle.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.paiondata.aristotle.base.TestConstants;
import com.paiondata.aristotle.common.base.Constants;
import com.paiondata.aristotle.common.exception.DeleteException;
import com.paiondata.aristotle.common.exception.NodeNullException;
import com.paiondata.aristotle.common.exception.NodeRelationException;
import com.paiondata.aristotle.common.exception.GraphNullException;
import com.paiondata.aristotle.common.exception.TransactionException;
import com.paiondata.aristotle.mapper.NodeMapper;
import com.paiondata.aristotle.model.dto.GraphAndNodeCreateDTO;
import com.paiondata.aristotle.model.vo.GraphAndNodeVO;
import com.paiondata.aristotle.model.dto.NodeCreateDTO;
import com.paiondata.aristotle.model.dto.NodeDTO;
import com.paiondata.aristotle.model.dto.NodeDeleteDTO;
import com.paiondata.aristotle.model.dto.NodeRelationDTO;
import com.paiondata.aristotle.model.dto.BindNodeDTO;
import com.paiondata.aristotle.model.dto.GraphCreateDTO;
import com.paiondata.aristotle.model.dto.NodeReturnDTO;
import com.paiondata.aristotle.model.dto.NodeUpdateDTO;
import com.paiondata.aristotle.model.dto.RelationUpdateDTO;
import com.paiondata.aristotle.model.entity.Graph;
import com.paiondata.aristotle.model.vo.NodeVO;
import com.paiondata.aristotle.repository.NodeRepository;
import com.paiondata.aristotle.service.impl.NodeServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import cn.hutool.core.lang.UUID;
import org.mockito.junit.jupiter.MockitoExtension;
import org.neo4j.driver.Transaction;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Test class for the Graph Node Service.
 * Uses Mockito to mock dependencies and validate graph node service operations.
 */
@ExtendWith(MockitoExtension.class)
public class NodeServiceSpec {

    @InjectMocks
    private NodeServiceImpl nodeService;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private NodeMapper nodeMapper;

    @Mock
    private CommonService commonService;

    /**
     * Setup method to initialize mocks and test data.
     */
    @BeforeEach
    public void setup() {
    }

    /**
     * Tests that getting a GraphNode by UUID returns the node when it exists.
     */
    @Test
    void getNodeByUuidNodeExistsShouldReturnNode() {
        // Given
        final String uuid = TestConstants.TEST_ID1;
        final NodeVO node = new NodeVO();
        when(nodeMapper.getNodeByUuid(uuid)).thenReturn(node);

        // When
        final Optional<NodeVO> result = nodeService.getNodeByUuid(uuid);

        // Then
        Assertions.assertTrue(result.isPresent());
        assertEquals(node, result.get());
        verify(nodeMapper).getNodeByUuid(uuid);
    }

    /**
     * Tests that getting a GraphNode by UUID returns an empty Optional when the node does not exist.
     */
    @Test
    void getNodeByUuidNodeDoesNotExistShouldReturnEmpty() {
        // Given
        final String uuid = TestConstants.TEST_ID1;
        when(nodeMapper.getNodeByUuid(uuid)).thenReturn(null);

        // When
        final Optional<NodeVO> result = nodeService.getNodeByUuid(uuid);

        // Then
        Assertions.assertFalse(result.isPresent());
        verify(nodeMapper).getNodeByUuid(uuid);
    }

    /**
     * Tests that creating and binding a Graph and Node throws a TransactionException when the transaction is null.
     */
    @Test
    public void createAndBindGraphAndNodeTransactionNullThrowsTransactionException() {
        // Given
        final NodeCreateDTO nodeCreateDTO = new NodeCreateDTO();

        // Then
        assertThrows(TransactionException.class, () -> {
            nodeService.createAndBindGraphAndNode(nodeCreateDTO, null);
        });
    }

    /**
     * Tests that creating and binding a Graph and Node throws a GraphNullException when the graph UUID is not found.
     */
    @Test
    public void createAndBindGraphAndNodeGraphUuidNotFoundThrowsGraphNullException() {
        // Given
        final Transaction tx = mock(Transaction.class);
        final NodeCreateDTO nodeCreateDTO = new NodeCreateDTO();
        nodeCreateDTO.setGraphUuid(TestConstants.TEST_ID1);

        // When
        when(commonService.getGraphByUuid(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(GraphNullException.class, () -> {
            nodeService.createAndBindGraphAndNode(nodeCreateDTO, tx);
        });
    }

    /**
     * Tests that creating and binding a graph and its nodes succeeds when the graph exists.
     */
    @Test
    void createAndBindGraphAndNodeGraphExistsShouldCreateAndBindNodes() {
        // Given
        final Transaction tx = mock(Transaction.class);
        final NodeCreateDTO nodeCreateDTO = new NodeCreateDTO();
        nodeCreateDTO.setGraphUuid(TestConstants.TEST_ID1);
        nodeCreateDTO.setGraphNodeDTO(List.of(
                new NodeDTO(TestConstants.TEST_ID1, Map.of(Constants.TITLE, TestConstants.TEST_TILE1,
                        Constants.DESCRIPTION, TestConstants.TEST_DESCRIPTION1)),
                new NodeDTO(TestConstants.TEST_ID2, Map.of(Constants.TITLE, TestConstants.TEST_TILE2,
                        Constants.DESCRIPTION, TestConstants.TEST_DESCRIPTION2))));
        nodeCreateDTO.setGraphNodeRelationDTO(List.of(
                new NodeRelationDTO(TestConstants.TEST_ID1, TestConstants.TEST_ID2, TestConstants.TEST_RELATION1),
                new NodeRelationDTO(TestConstants.TEST_ID2, TestConstants.TEST_ID1, TestConstants.TEST_RELATION2)
        ));

        when(commonService.getGraphByUuid(TestConstants.TEST_ID1)).thenReturn(Optional.of(new Graph()));

        // Mock createGraphAndBindGraphAndNode to return a non-null GraphNode
        final String graphNodeUuid = UUID.fastUUID().toString(true);
        final String currentTime = getCurrentTime();

        when(nodeMapper.createNode(anyString(), anyString(), anyString(), anyString(), any(NodeDTO.class),
                any(Transaction.class)))
                .thenReturn((new NodeVO(graphNodeUuid, Map.of(Constants.TITLE, TestConstants.TEST_TILE1,
                        Constants.DESCRIPTION, TestConstants.TEST_DESCRIPTION1), currentTime, currentTime)));
        doNothing().when(nodeMapper).bindGraphNodeToGraphNode(anyString(), anyString(), anyString(), anyString(),
                anyString(), any(Transaction.class));

        // When
        final List<NodeReturnDTO> dtos = nodeService.createAndBindGraphAndNode(nodeCreateDTO, tx);

        // Then
        verify(commonService, times(1)).getGraphByUuid(TestConstants.TEST_ID1);
        verify(nodeRepository, times(1)).getGraphUuidByGraphNodeUuid(Set.of(TestConstants.TEST_ID1,
                TestConstants.TEST_ID2));
        Assertions.assertNotNull(dtos);
        Assertions.assertFalse(dtos.isEmpty());
    }

    /**
     * Tests that creating and binding a graph and its nodes throws a GraphNullException when the graph does not exist.
     */
    @Test
    void createAndBindGraphAndNodeGraphDoesNotExistShouldThrowException() {
        // Given
        final Transaction tx = mock(Transaction.class);
        final NodeCreateDTO graphNodeCreateDTO = new NodeCreateDTO();
        graphNodeCreateDTO.setGraphUuid(TestConstants.TEST_ID1);

        when(commonService.getGraphByUuid(TestConstants.TEST_ID1)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(GraphNullException.class, () -> nodeService.createAndBindGraphAndNode(graphNodeCreateDTO, tx));

        // Then
        verify(commonService, times(1)).getGraphByUuid(TestConstants.TEST_ID1);
    }

    /**
     * Tests that creating a graph and binding nodes succeeds when the graph is created.
     */
    @Test
    void createGraphAndBindGraphAndNodeGraphCreatedShouldCreateGraphAndBindGraphAndNode() {
        // Given
        final Transaction tx = mock(Transaction.class);
        final GraphAndNodeCreateDTO graphNodeCreateDTO = new GraphAndNodeCreateDTO();
        graphNodeCreateDTO.setGraphCreateDTO(new GraphCreateDTO(TestConstants.TEST_TILE1,
                TestConstants.TEST_DESCRIPTION1, TestConstants.TEST_ID1));
        graphNodeCreateDTO.setGraphNodeDTO(List.of(
                new NodeDTO(TestConstants.TEST_ID1, Map.of(Constants.TITLE, TestConstants.TEST_TILE1,
                        Constants.DESCRIPTION, TestConstants.TEST_DESCRIPTION1)),
                new NodeDTO(TestConstants.TEST_ID2, Map.of(Constants.TITLE, TestConstants.TEST_TILE2,
                        Constants.DESCRIPTION, TestConstants.TEST_DESCRIPTION2))));
        graphNodeCreateDTO.setGraphNodeRelationDTO(List.of(
                new NodeRelationDTO(TestConstants.TEST_ID1, TestConstants.TEST_ID2, TestConstants.TEST_RELATION1),
                new NodeRelationDTO(TestConstants.TEST_ID2, TestConstants.TEST_ID1, TestConstants.TEST_RELATION2)
        ));

        when(commonService.createAndBindGraph(graphNodeCreateDTO.getGraphCreateDTO(), tx))
                .thenReturn(Graph.builder()
                        .uuid(TestConstants.TEST_ID1)
                        .title(TestConstants.TEST_TILE1)
                        .description(TestConstants.TEST_DESCRIPTION1)
                        .build());

        // Mock createGraphAndBindGraphAndNode to return a non-null GraphNode
        final String graphNodeUuid = UUID.fastUUID().toString(true);
        final String currentTime = getCurrentTime();

        when(nodeMapper.createNode(anyString(), anyString(), anyString(), anyString(), any(NodeDTO.class),
                any(Transaction.class)))
                .thenReturn((new NodeVO(graphNodeUuid, Map.of(Constants.TITLE, TestConstants.TEST_TILE1,
                        Constants.DESCRIPTION, TestConstants.TEST_DESCRIPTION1), currentTime, currentTime)));
        doNothing().when(nodeMapper).bindGraphNodeToGraphNode(anyString(), anyString(), anyString(), anyString(),
                anyString(), any(Transaction.class));

        // When
        final GraphAndNodeVO dto = nodeService.createGraphAndBindGraphAndNode(graphNodeCreateDTO, tx);

        // Then
        verify(commonService, times(1)).createAndBindGraph(graphNodeCreateDTO.getGraphCreateDTO(), tx);
        verify(nodeRepository, times(1)).getGraphUuidByGraphNodeUuid(Set.of(TestConstants.TEST_ID1,
                TestConstants.TEST_ID2));
        Assertions.assertNotNull(dto);
        Assertions.assertNotNull(dto.getUuid());
        Assertions.assertEquals(dto.getTitle(), TestConstants.TEST_TILE1);
        Assertions.assertEquals(dto.getDescription(), TestConstants.TEST_DESCRIPTION1);
        Assertions.assertNotNull(dto.getNodes());
    }

    /**
     * Tests that binding nodes succeeds when both nodes exist.
     */
    @Test
    void bindNodesNodesExistShouldBindNodes() {
        // Given
        final Transaction tx = mock(Transaction.class);
        final List<BindNodeDTO> dtos = Collections.singletonList(new BindNodeDTO(TestConstants.TEST_ID1,
                TestConstants.TEST_ID2, TestConstants.TEST_RELATION1));

        when(nodeMapper.getNodeByUuid(TestConstants.TEST_ID1)).thenReturn(new NodeVO());
        when(nodeMapper.getNodeByUuid(TestConstants.TEST_ID2)).thenReturn(new NodeVO());

        // When & Then
        assertDoesNotThrow(() -> nodeService.bindNodes(dtos, tx));

        // Then
        verify(nodeMapper, times(1)).getNodeByUuid(TestConstants.TEST_ID1);
        verify(nodeMapper, times(1)).getNodeByUuid(TestConstants.TEST_ID2);
        verify(nodeMapper, times(1))
                .bindGraphNodeToGraphNode(anyString(), anyString(), anyString(), anyString(), anyString(), eq(tx));
    }

    /**
     * Tests that binding nodes throws a NodeNullException when one of the nodes does not exist.
     */
    @Test
    void bindNodesNodesDoesNotExistShouldThrowException() {
        // Given
        final Transaction tx = mock(Transaction.class);
        final List<BindNodeDTO> dtos = Collections.singletonList(new BindNodeDTO(TestConstants.TEST_ID1,
                TestConstants.TEST_ID2, TestConstants.TEST_RELATION1));

        when(nodeMapper.getNodeByUuid(TestConstants.TEST_ID1)).thenReturn(null);

        // When & Then
        assertThrows(NodeNullException.class, () -> nodeService.bindNodes(dtos, tx));

        // Then
        verify(nodeMapper, times(2)).getNodeByUuid(anyString());
    }

    /**
     * Tests that deleting nodes succeeds when the node exists and belongs to the graph.
     */
    @Test
    public void deleteByUuidsNodeExistsAndBelongsToGraphSuccess() {
        // Arrange
        final String graphUuid = TestConstants.TEST_ID1;
        final String nodeUuid = TestConstants.TEST_ID2;
        final NodeDeleteDTO dto = NodeDeleteDTO.builder()
                .uuid(graphUuid)
                .uuids(Collections.singletonList(nodeUuid))
                .build();

        final NodeVO node = NodeVO.builder()
                .uuid(nodeUuid)
                .build();

        when(nodeMapper.getNodeByUuid(nodeUuid)).thenReturn(node);
        when(nodeRepository.getNodeByGraphUuidAndNodeUuid(graphUuid, nodeUuid)).thenReturn(nodeUuid);

        // Act
        nodeService.deleteByUuids(dto);

        // Assert
        verify(nodeRepository, times(1)).deleteByUuids(Collections.singletonList(nodeUuid));
    }

    /**
     * Tests that deleting nodes throws a NodeNullException when the node does not exist.
     */
    @Test
    public void deleteByUuidsNodeDoesNotExistThrowsException() {
        // Arrange
        final String graphUuid = TestConstants.TEST_ID1;
        final String nodeUuid = TestConstants.TEST_ID2;
        final NodeDeleteDTO dto = NodeDeleteDTO.builder()
                .uuid(graphUuid)
                .uuids(Collections.singletonList(nodeUuid))
                .build();

        when(nodeMapper.getNodeByUuid(nodeUuid)).thenReturn(null);

        // Assert handled by expected exception
        assertThrows(NodeNullException.class, () -> nodeService.deleteByUuids(dto));
    }

    /**
     * Tests that deleting nodes throws a DeleteException when the node belongs to another graph.
     */
    @Test
    public void deleteByUuidsNodeBelongsToAnotherGraphThrowsException() {
        // Arrange
        final String graphUuid = TestConstants.TEST_ID1;
        final String nodeUuid = TestConstants.TEST_ID2;
        final NodeDeleteDTO dto = NodeDeleteDTO.builder()
                .uuid(graphUuid)
                .uuids(Collections.singletonList(nodeUuid))
                .build();

        final NodeVO node = NodeVO.builder()
                .uuid(nodeUuid)
                .build();

        when(nodeMapper.getNodeByUuid(nodeUuid)).thenReturn(node);
        when(nodeRepository.getNodeByGraphUuidAndNodeUuid(graphUuid, nodeUuid)).thenReturn(null);

        // Assert handled by expected exception
        assertThrows(DeleteException.class, () -> nodeService.deleteByUuids(dto));
    }

    /**
     * Tests that updating a graph node succeeds when the node exists.
     */
    @Test
    void updateGraphNodeGraphNodeExistsShouldUpdateNode() {
        // Given
        final Transaction tx = mock(Transaction.class);
        final String uuid = TestConstants.TEST_ID1;
        final NodeUpdateDTO nodeUpdateDTO = NodeUpdateDTO.builder()
                .uuid(uuid)
                .properties(Map.of(Constants.TITLE, TestConstants.TEST_TILE2,
                        Constants.DESCRIPTION, TestConstants.TEST_DESCRIPTION2))
                .build();

        when(nodeMapper.getNodeByUuid(uuid)).thenReturn(new NodeVO());

        // When
        assertDoesNotThrow(() -> nodeService.updateNode(nodeUpdateDTO, tx));

        // Then
        verify(nodeMapper, times(1)).updateNodeByUuid(eq(nodeUpdateDTO), anyString(), eq(tx));
    }

    /**
     * Tests that updating a non-existent graph node throws an exception.
     */
    @Test
    void updateGraphNodeNodeDoesNotExistShouldThrowException() {
        // Given
        final Transaction tx = mock(Transaction.class);
        final String uuid = TestConstants.TEST_ID1;
        final NodeUpdateDTO nodeUpdateDTO = NodeUpdateDTO.builder()
                .uuid(uuid)
                .properties(Map.of(Constants.TITLE, TestConstants.TEST_TILE2,
                        Constants.DESCRIPTION, TestConstants.TEST_DESCRIPTION2))
                .build();

        when(nodeMapper.getNodeByUuid(TestConstants.TEST_ID1)).thenReturn(null);

        // When & Then
        assertThrows(NodeNullException.class, () -> nodeService.updateNode(nodeUpdateDTO, tx));

        // Then
        verify(nodeMapper).getNodeByUuid(TestConstants.TEST_ID1);
    }

    /**
     * Tests updating a graph relation with an update map.
     */
    @Test
    void updateRelationWithUpdateMap() {
        // Given
        final String graphUuid = TestConstants.TEST_ID1;
        final Map<String, String> updateMap = new HashMap<>();
        updateMap.put(TestConstants.TEST_ID1, TestConstants.TEST_NAME1);
        updateMap.put(TestConstants.TEST_ID2, TestConstants.TEST_NAME1);

        when(nodeRepository.getRelationByUuid(TestConstants.TEST_ID1)).thenReturn(TestConstants.TEST_ID1);
        when(nodeRepository.getRelationByUuid(TestConstants.TEST_ID2)).thenReturn(TestConstants.TEST_ID2);

        final RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid, updateMap,
                Collections.emptyList());

        // When
        nodeService.updateRelation(relationUpdateDTO);

        // Then
        verify(nodeRepository, times(1)).updateRelationByUuid(TestConstants.TEST_ID1,
                TestConstants.TEST_NAME1, graphUuid);
        verify(nodeRepository, times(1)).updateRelationByUuid(TestConstants.TEST_ID2,
                TestConstants.TEST_NAME1, graphUuid);
    }

    /**
     * Tests updating a graph relation with a delete list.
     */
    @Test
    void testUpdateRelationWithDeleteList() {
        // Given
        final String graphUuid = TestConstants.TEST_ID1;
        final List<String> deleteList = List.of(TestConstants.TEST_ID1, TestConstants.TEST_ID2);

        when(nodeRepository.getRelationByUuid(TestConstants.TEST_ID1)).thenReturn(TestConstants.TEST_ID1);
        when(nodeRepository.getRelationByUuid(TestConstants.TEST_ID2)).thenReturn(TestConstants.TEST_ID2);

        final RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid,
                Collections.emptyMap(), deleteList);

        // When
        nodeService.updateRelation(relationUpdateDTO);

        // Then
        verify(nodeRepository, times(1)).deleteRelationByUuid(TestConstants.TEST_ID1, graphUuid);
        verify(nodeRepository, times(1)).deleteRelationByUuid(TestConstants.TEST_ID2, graphUuid);
    }

    /**
     * Tests updating a graph relation with both an update map and a delete list.
     */
    @Test
    void testUpdateRelationWithBothUpdateMapAndDeleteList() {
        // Given
        final String graphUuid = TestConstants.TEST_ID1;
        final Map<String, String> updateMap = new HashMap<>();
        updateMap.put(TestConstants.TEST_ID1, TestConstants.TEST_NAME1);
        updateMap.put(TestConstants.TEST_ID2, TestConstants.TEST_NAME1);

        final List<String> deleteList = List.of(TestConstants.TEST_ID3, TestConstants.TEST_ID4);

        when(nodeRepository.getRelationByUuid(TestConstants.TEST_ID1)).thenReturn(TestConstants.TEST_ID1);
        when(nodeRepository.getRelationByUuid(TestConstants.TEST_ID2)).thenReturn(TestConstants.TEST_ID2);
        when(nodeRepository.getRelationByUuid(TestConstants.TEST_ID3)).thenReturn(TestConstants.TEST_ID3);
        when(nodeRepository.getRelationByUuid(TestConstants.TEST_ID4)).thenReturn(TestConstants.TEST_ID4);

        final RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid, updateMap, deleteList);

        // When
        nodeService.updateRelation(relationUpdateDTO);

        // Then
        verify(nodeRepository).updateRelationByUuid(TestConstants.TEST_ID1, TestConstants.TEST_NAME1, graphUuid);
        verify(nodeRepository).updateRelationByUuid(TestConstants.TEST_ID2, TestConstants.TEST_NAME1, graphUuid);
        verify(nodeRepository).deleteRelationByUuid(TestConstants.TEST_ID3, graphUuid);
        verify(nodeRepository).deleteRelationByUuid(TestConstants.TEST_ID4, graphUuid);
    }

    /**
     * Tests that updating a non-existent relation in the update map throws an exception.
     */
    @Test
    void testUpdateRelationWithNonExistentRelationInUpdateMap() {
        // Given
        final String graphUuid = TestConstants.TEST_ID1;
        final Map<String, String> updateMap = new HashMap<>();
        updateMap.put(TestConstants.TEST_ID1, TestConstants.TEST_NAME1);

        when(nodeRepository.getRelationByUuid(TestConstants.TEST_ID1)).thenReturn(null);

        final RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid,
                updateMap, Collections.emptyList());

        // When & Then
        assertThrows(NodeRelationException.class,
                () -> nodeService.updateRelation(relationUpdateDTO));
    }

    /**
     * Tests that deleting a non-existent relation in the delete list throws an exception.
     */
    @Test
    void testUpdateRelationWithNonExistentRelationInDeleteList() {
        // Given
        final String graphUuid = TestConstants.TEST_ID1;
        final List<String> deleteList = List.of(TestConstants.TEST_ID1);

        when(nodeRepository.getRelationByUuid(TestConstants.TEST_ID1)).thenReturn(null);

        final RelationUpdateDTO relationUpdateDTO = new RelationUpdateDTO(graphUuid,
                Collections.emptyMap(), deleteList);

        // When & Then
        assertThrows(NodeRelationException.class,
                () -> nodeService.updateRelation(relationUpdateDTO));
    }

    /**
     * Get current time.
     * @return current time
     */
    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
    }
}
