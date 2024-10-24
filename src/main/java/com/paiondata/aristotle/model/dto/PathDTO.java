package com.paiondata.aristotle.model.dto;

import com.paiondata.aristotle.model.vo.NodeVO;
import com.paiondata.aristotle.model.vo.RelationVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PathDTO {

    private List<RelationVO> relations;

    private Set<NodeVO> nodes;
}
