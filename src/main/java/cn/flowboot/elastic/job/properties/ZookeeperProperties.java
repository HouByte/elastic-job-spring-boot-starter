package cn.flowboot.elastic.job.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <h1></h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2022/01/19
 */
@ConfigurationProperties(prefix = "elastic-job.zookeeper")
public class ZookeeperProperties {

    /**
     * zookeeper服务器地址
     */
    private String server = "localhost:2181";
    /**
     * zookeeper命名空间
     */
    private String namespace = "elastic-job-zookeeper";
    /**
     * 等待重试的间隔时间的初始值 默认1000，单位：毫秒
     */
    private int baseSleepTimeMilliseconds = 1000;
    /**
     * 等待重试的间隔时间的最大值 默认3000，单位：毫秒
     */
    private int maxSleepTimeMilliseconds = 3000;
    /**
     * 最大重试次数 默认3
     */
    private int maxRetries = 3;
    /**
     * 会话超时时间 默认60000，单位：毫秒
     */
    private int sessionTimeoutMilliseconds = 60000;
    /**
     * 连接超时时间 默认15000，单位：毫秒
     */
    private int  connectionTimeoutMilliseconds = 15000;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public int getBaseSleepTimeMilliseconds() {
        return baseSleepTimeMilliseconds;
    }

    public void setBaseSleepTimeMilliseconds(int baseSleepTimeMilliseconds) {
        this.baseSleepTimeMilliseconds = baseSleepTimeMilliseconds;
    }

    public int getMaxSleepTimeMilliseconds() {
        return maxSleepTimeMilliseconds;
    }

    public void setMaxSleepTimeMilliseconds(int maxSleepTimeMilliseconds) {
        this.maxSleepTimeMilliseconds = maxSleepTimeMilliseconds;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getSessionTimeoutMilliseconds() {
        return sessionTimeoutMilliseconds;
    }

    public void setSessionTimeoutMilliseconds(int sessionTimeoutMilliseconds) {
        this.sessionTimeoutMilliseconds = sessionTimeoutMilliseconds;
    }

    public int getConnectionTimeoutMilliseconds() {
        return connectionTimeoutMilliseconds;
    }

    public void setConnectionTimeoutMilliseconds(int connectionTimeoutMilliseconds) {
        this.connectionTimeoutMilliseconds = connectionTimeoutMilliseconds;
    }

    @Override
    public String toString() {
        return "ZookeeperProperties{" +
                "server='" + server + '\'' +
                ", namespace='" + namespace + '\'' +
                ", baseSleepTimeMilliseconds=" + baseSleepTimeMilliseconds +
                ", maxSleepTimeMilliseconds=" + maxSleepTimeMilliseconds +
                ", maxRetries=" + maxRetries +
                ", sessionTimeoutMilliseconds=" + sessionTimeoutMilliseconds +
                ", connectionTimeoutMilliseconds=" + connectionTimeoutMilliseconds +
                '}';
    }
}
