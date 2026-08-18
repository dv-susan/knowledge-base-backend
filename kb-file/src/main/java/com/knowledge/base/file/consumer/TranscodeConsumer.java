package com.knowledge.base.file.consumer;

import com.knowledge.base.file.message.TranscodeMessage;
import com.knowledge.base.file.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 转码消息消费者
 * 异步消费转码队列，执行FFmpeg转码和缩略图生成
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TranscodeConsumer {

    private final MediaService mediaService;

    /**
     * 消费转码消息
     */
    @RabbitListener(queues = "#{@transcodeQueue.name}")
    public void handleTranscodeMessage(TranscodeMessage message) {
        log.info("收到转码消息：fileId={}, targetFormat={}", message.getFileId(), message.getTargetFormat());

        try {
            // 更新状态为处理中
            mediaService.updateTranscodeStatus(message.getFileId(), "PROCESSING");

            // 执行HLS转码
            mediaService.transcodeToHls(message.getFileId());

            // 生成缩略图
            mediaService.generateThumbnail(message.getFileId());

            // 更新状态为完成
            mediaService.updateTranscodeStatus(message.getFileId(), "DONE");

            log.info("转码完成：fileId={}", message.getFileId());
        } catch (Exception e) {
            log.error("转码失败：fileId={}, error={}", message.getFileId(), e.getMessage(), e);
            mediaService.updateTranscodeStatus(message.getFileId(), "FAILED");
        }
    }
}
