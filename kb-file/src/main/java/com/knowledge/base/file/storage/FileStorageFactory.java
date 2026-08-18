package com.knowledge.base.file.storage;

import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.file.config.FileStorageProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 文件存储工厂
 * 
 * <p>根据配置动态选择文件存储实现</p>
 * 
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileStorageFactory {

    private final ApplicationContext applicationContext;
    private final FileStorageProperties storageProperties;
    
    private FileStorage currentStorage;

    @PostConstruct
    public void init() {
        StorageType storageType = StorageType.fromCode(storageProperties.getType());
        log.info("初始化文件存储: type={}", storageType);
        
        try {
            currentStorage = applicationContext.getBean(storageType.getBeanName(), FileStorage.class);
            log.info("文件存储初始化成功: type={}, implementation={}", 
                    storageType, currentStorage.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("文件存储初始化失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件存储初始化失败", e);
        }
    }

    /**
     * 获取当前存储实现
     *
     * @return 文件存储实现
     */
    public FileStorage getStorage() {
        if (currentStorage == null) {
            throw new BusinessException("文件存储服务未初始化");
        }
        return currentStorage;
    }

    /**
     * 获取当前存储类型
     *
     * @return 存储类型
     */
    public String getStorageType() {
        return storageProperties.getType();
    }
}