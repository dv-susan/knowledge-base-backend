package com.knowledge.base.file.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 媒体元数据
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaMetadata implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 时长（秒） */
    private Integer duration;

    /** 分辨率，如 "1920x1080" */
    private String resolution;

    /** 码率（kbps） */
    private Integer bitrate;

    /** 视频编码，如 "h264" */
    private String videoCodec;

    /** 音频编码，如 "aac" */
    private String audioCodec;
}
