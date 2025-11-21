package com.agrismart.agrimallbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 异步任务配置类。
 *
 * 该类配置了系统中异步任务执行的线程池。
 * 主要用于邮件发送等耗时操作的异步执行，避免阻塞主业务流程。
 *
 * 功能特性：
 *
 * - 启用 Spring 异步任务支持（{@link EnableAsync}）
 * - 配置邮件发送专用线程池
 * - 自定义线程池参数，优化邮件发送性能
 *
 * 使用场景：
 *
 * - 在 {@link com.agrismart.agrimallbackend.common.util.MailUtil#sendMailAsync} 中使用
 * - 通过 {@code @Async("mailTaskExecutor")} 注解指定使用该线程池
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.common.util.MailUtil
 * @since 1.0
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 邮件任务执行器 Bean。
     *
     * 配置邮件发送专用的线程池，用于异步执行邮件发送任务。
     * 线程池配置：
     *
     * - 核心线程数：2，保持运行的线程数量
     * - 最大线程数：5，线程池最大可创建的线程数量
     * - 队列容量：50，等待执行的任务队列大小
     * - 线程名前缀：mail-exec-，便于日志追踪和问题排查
     *
     * 线程池工作流程：
     *
     * - 任务提交时，优先使用核心线程执行
     * - 核心线程满时，任务进入队列等待
     * - 队列满时，创建新线程（最多到最大线程数）
     * - 超过最大线程数时，根据拒绝策略处理
     *
     * @return 邮件任务执行器
     */
    @Bean(name = "mailTaskExecutor")
    public Executor mailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);        // 核心线程数
        executor.setMaxPoolSize(5);         // 最大线程数
        executor.setQueueCapacity(50);      // 队列容量
        executor.setThreadNamePrefix("mail-exec-");  // 线程名前缀
        executor.initialize();
        return executor;
    }
}

