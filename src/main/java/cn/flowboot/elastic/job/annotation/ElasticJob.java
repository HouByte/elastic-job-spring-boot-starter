package cn.flowboot.elastic.job.annotation;

import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.api.strategy.impl.AverageAllocationJobShardingStrategy;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h1>ElasticJob 任务注解</h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2022/01/20
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ElasticJob {

    /**
     * 是否启用
     */
    boolean enable() default true;
    /**
     * 任务名称 【必填】
     */
    String name() default  "";

    /**
     * cron表达式，用于控制作业触发时间,默认每间隔10秒钟执行一次 【必填】
     */
    String cron() default  "";

    /**
     * 作业分片总数 【不能为0或者小于0】
     */
    int shardingTotalCount() default 1;

    /**
     * 是否可覆盖
     */
    boolean override() default false;

    /**
     * 是否流式处理 注: 在DataflowJob 中才有效
     */
    boolean streamingProcess() default false;

    /**
     * 分片策略
     */
    Class<?> jobStrategy() default AverageAllocationJobShardingStrategy.class;

    /**
     * 是否支持事件记录
     */
    boolean jobEvent() default false;

    /**
     * 监听器
     * @return
     */
    Class<? extends ElasticJobListener>[] jobListener() default {};

}
