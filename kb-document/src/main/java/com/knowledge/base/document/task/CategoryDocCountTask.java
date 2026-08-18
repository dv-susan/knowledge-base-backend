package com.knowledge.base.document.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.knowledge.base.document.dto.CategoryDocCountDTO;
import com.knowledge.base.document.entity.Category;
import com.knowledge.base.document.mapper.CategoryMapper;
import com.knowledge.base.document.mapper.DocumentMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 分类文档数量同步定时任务
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class CategoryDocCountTask {

    @Resource
    private DocumentMapper documentMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private ThreadPoolTaskExecutor asyncTaskExecutor;

    /**
     * 项目启动时异步执行一次数据初始化
     */
    @PostConstruct
    public void init() {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000); // 等待应用完全启动
                recalculateCategoryDocumentCounts();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("启动初始化分类文档数量被中断");
            }
        }, asyncTaskExecutor);
    }

    /**
     * 每5分钟重新计算各分类下的文档数量（含子分类），更新到数据库并清除缓存
     */
    @Scheduled(cron = "0 */5 * * * ?")
    @CacheEvict(value = "sidebar:categories", allEntries = true)
    public void recalculateCategoryDocumentCounts() {
        log.info("定时任务：开始重新计算分类文档数量");
        try {
            // 1. 查询所有分类
            List<Category> allCategories = categoryMapper.selectList(
                    new LambdaQueryWrapper<Category>().eq(Category::getDeleted, 0));

            // 2. 查询各分类的直接文档数量，转为列表便于查找
            List<CategoryDocCountDTO> directCounts = documentMapper.countByCategory();

            // 3. 计算每个分类及其子分类的文档总数
            for (Category cat : allCategories) {
                int totalCount = calcTotalCount(cat.getId(), allCategories, directCounts);
                categoryMapper.updateDocumentCount(cat.getId(), totalCount);
            }

            log.info("定时任务：分类文档数量计算完成，共更新 {} 个分类", allCategories.size());
        } catch (Exception e) {
            log.error("定时任务：重新计算分类文档数量失败", e);
        }
    }

    /**
     * 递归计算分类及其所有子分类的文档总数
     */
    private int calcTotalCount(Long categoryId, List<Category> allCategories,
                               List<CategoryDocCountDTO> directCounts) {
        // 当前分类的直接文档数（跳过 category_id 为 null 的未分类文档）
        int total = 0;
        for (CategoryDocCountDTO dto : directCounts) {
            if (dto.getCategoryId() != null && dto.getCategoryId().equals(categoryId)) {
                total = dto.getCount();
                break;
            }
        }

        // 递归加上所有子分类的文档数
        for (Category cat : allCategories) {
            Long parentId = cat.getParentId() != null ? cat.getParentId() : 0L;
            if (parentId.equals(categoryId)) {
                total += calcTotalCount(cat.getId(), allCategories, directCounts);
            }
        }

        return total;
    }
}
