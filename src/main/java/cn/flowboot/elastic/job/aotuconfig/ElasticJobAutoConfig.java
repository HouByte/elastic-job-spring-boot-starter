package cn.flowboot.elastic.job.aotuconfig;

import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <h1></h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2022/01/20
 */
@Configuration
@ConditionalOnBean(CoordinatorRegistryCenter.class)
@AutoConfigureAfter(ZookeeperAutoConfig.class)
public class ElasticJobAutoConfig {
    /**
     * 获取Spring上下文
     */
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CoordinatorRegistryCenter zkCenter;

    @Autowired(required = false)
    private DataSource dataSource;

    /**
     * @PostConstruct 在对象加载完依赖注入后执行
     * 初始化 Simple Job
     */
    @PostConstruct
    public void initInstance(){

        /**
         * 获取@ElasticJob注解的实例bean
         */
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(cn.flowboot.elastic.job.annotation.ElasticJob.class);

        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();
            Class<?>[] interfaces = instance.getClass().getInterfaces();
            for (Class<?> superInterface : interfaces) {
                //判断是否是实例
                if (superInterface == SimpleJob.class){
                    initElasticJob(instance,SimpleJob.class);

                } else if (superInterface == DataflowJob.class){
                    initElasticJob(instance,DataflowJob.class);

                }
            }
        }

    }


    public void initElasticJob(Object instance,Class<?> type) {
        /**
         * 获取注解信息
         */
        cn.flowboot.elastic.job.annotation.ElasticJob elasticJob = instance.getClass().getAnnotation(cn.flowboot.elastic.job.annotation.ElasticJob.class);
        String jobName = elasticJob.name();
        String cron = elasticJob.cron();
        int shardingTotalCount = elasticJob.shardingTotalCount();
        boolean override = elasticJob.override();
        boolean streamingProcess = elasticJob.streamingProcess();
        Class<?> jobStrategy = elasticJob.jobStrategy();
        boolean isJobEvent = elasticJob.jobEvent();
        Class<? extends ElasticJobListener>[] listeners = elasticJob.jobListener();
        /**
         * 获取监听器
         */
        ElasticJobListener[] listenerInstances = getElasticJobListeners(listeners);
        /**
         * 校验参数是否存在
         */
        verificationAttribute(jobName, cron, shardingTotalCount);
        /**
         * job 核心配置
         */
        JobCoreConfiguration jcc = JobCoreConfiguration
                .newBuilder(jobName,cron,shardingTotalCount)
                .build();
        /**
         * job 类配置
         */
        JobTypeConfiguration jtc = getJobTypeConfiguration(instance, type, streamingProcess, jcc);

        /**
         * job 根的配置 （LiteJobConfiguration）
         */
        LiteJobConfiguration configuration = LiteJobConfiguration
                .newBuilder(jtc)
                .jobShardingStrategyClass(jobStrategy.getCanonicalName())
                //覆盖配置
                .overwrite(override)
                .build();

        if (isJobEvent && dataSource != null){
            //配置数据源
            JobEventConfiguration jec = new JobEventRdbConfiguration(dataSource);
            new SpringJobScheduler((ElasticJob) instance, zkCenter, configuration,jec,listenerInstances).init();
        } else {
            new SpringJobScheduler((ElasticJob) instance, zkCenter, configuration,listenerInstances).init();
        }
    }

    /**
     * 获取工作类型配置
     * @param instance
     * @param type
     * @param streamingProcess
     * @param jcc
     * @return
     */
    private JobTypeConfiguration getJobTypeConfiguration(Object instance, Class<?> type, boolean streamingProcess, JobCoreConfiguration jcc) {
        JobTypeConfiguration jtc = null;
        //全路径
        String canonicalName = instance.getClass().getCanonicalName();
        if (SimpleJob.class.equals(type)) {
            jtc = new SimpleJobConfiguration(jcc, canonicalName);
        } else if (DataflowJob.class.equals(type)) {
            jtc = new DataflowJobConfiguration(jcc, canonicalName, streamingProcess);
        }
        return jtc;
    }

    private ElasticJobListener[] getElasticJobListeners(Class<? extends ElasticJobListener>[] listeners) {
        ElasticJobListener[] listenerInstances = null;
        if (listeners != null && listeners.length > 0){
            listenerInstances = new ElasticJobListener[listeners.length];
            int i = 0;
            for (Class<? extends ElasticJobListener> listener : listeners) {
                try {
                    listenerInstances[i++] = listener.getDeclaredConstructor().newInstance();

                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        } else {
            listenerInstances = new ElasticJobListener[0];
        }
        return listenerInstances;
    }

    public void verificationAttribute(String jobName, String cron, int shardingTotalCount) {
        if (StringUtils.isBlank(jobName)){
            throw new RuntimeException("ElasticSimpleJob:The attribute of name cannot be empty ");
        }
        if (StringUtils.isBlank(cron)){
            throw new RuntimeException("ElasticSimpleJob:The attribute of cron cannot be empty ");
        }
        if (shardingTotalCount <= 0){
            throw new RuntimeException("The attribute of shardingTotalCount cannot be less than or equal to 0 ");
        }
    }
}
