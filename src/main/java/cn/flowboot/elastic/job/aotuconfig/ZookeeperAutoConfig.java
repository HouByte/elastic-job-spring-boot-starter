package cn.flowboot.elastic.job.aotuconfig;

import cn.flowboot.elastic.job.properties.ZookeeperProperties;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h1></h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2022/01/20
 */
@Configuration
@ConditionalOnProperty("elastic-job.zookeeper.server")
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperAutoConfig {

    @Autowired
    private ZookeeperProperties zookeeperProperties;

    @Bean(initMethod = "init")
    public CoordinatorRegistryCenter zkCenter(){
        if (StringUtils.isBlank(zookeeperProperties.getNamespace())) {
            throw new RuntimeException("zkCenter: namespace parameter is not configured");
        }
        ZookeeperConfiguration zc = new ZookeeperConfiguration(zookeeperProperties.getServer(),zookeeperProperties.getNamespace());
        zc.setMaxSleepTimeMilliseconds(zookeeperProperties.getMaxSleepTimeMilliseconds());
        zc.setBaseSleepTimeMilliseconds(zookeeperProperties.getBaseSleepTimeMilliseconds());
        zc.setConnectionTimeoutMilliseconds(zookeeperProperties.getConnectionTimeoutMilliseconds());
        zc.setSessionTimeoutMilliseconds(zookeeperProperties.getSessionTimeoutMilliseconds());
        zc.setMaxRetries(zookeeperProperties.getMaxRetries());
        //初始化 交给Spring执行
        //crc.init();
        return new ZookeeperRegistryCenter(zc);
    }
}
