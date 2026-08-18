package com.knowledge.base.file.storage;

/**
 * 存储类型枚举
 * 
 * @author knowledge-base-team
 * @since 1.0.0
 */
public enum StorageType {
    
    /**
     * RustFS 分布式文件存储
     */
    RUSTFS("rustfs", "rustFileStorage");

    private final String code;
    private final String beanName;

    StorageType(String code, String beanName) {
        this.code = code;
        this.beanName = beanName;
    }

    public String getCode() {
        return code;
    }

    public String getBeanName() {
        return beanName;
    }

    public static StorageType fromCode(String code) {
        for (StorageType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return RUSTFS;
    }
}