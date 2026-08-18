package com.knowledge.base.file.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 转码消息体
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranscodeMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 文件ID */
    private Long fileId;

    /** 目标格式，固定为 "hls" */
    private String targetFormat;
}
