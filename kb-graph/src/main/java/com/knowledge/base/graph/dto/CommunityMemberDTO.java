package com.knowledge.base.graph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 社区成员（用于社区检测结果）
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityMemberDTO {

    /** 实体名称 */
    private String name;

    /** 实体类型 */
    private String type;

    /** 连接度数 */
    private Long degree;
}
