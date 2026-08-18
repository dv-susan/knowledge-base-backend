package com.knowledge.base.ai.rag.kag.extraction;

/**
 * 实体抽取异常
 *
 * @author 苏三
 * @since 1.0.0
 */
public class ExtractionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final Long docId;
    private final String chunkId;

    public ExtractionException(String message, Long docId, String chunkId) {
        super(message);
        this.docId = docId;
        this.chunkId = chunkId;
    }

    public ExtractionException(String message, Long docId, String chunkId, Throwable cause) {
        super(message, cause);
        this.docId = docId;
        this.chunkId = chunkId;
    }

    public Long getDocId() {
        return docId;
    }

    public String getChunkId() {
        return chunkId;
    }
}
