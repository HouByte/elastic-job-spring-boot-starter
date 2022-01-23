# elastic-job-spring-boot-starter

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Gitee release](https://img.shields.io/badge/release-Gitee-4EB1BA.svg)](https://gitee.com/Vincent-Vic/elastic-job-spring-boot-starter/releases/1.0.0)
[![JDK](https://img.shields.io/badge/JDK-1.8+-4EB1BA.svg)](https://docs.oracle.com/javase/8/docs/index.html)
[![SpringBoot](https://img.shields.io/badge/SpringBoot-2.6.3-green.svg)](https://docs.spring.io/spring-boot/docs/2.6.3/reference/htmlsingle/)
[![Maven](https://img.shields.io/badge/Maven-1.0.0-4EB1BA.svg)](https://docs.oracle.com/javase/8/docs/index.html)
[![Author](https://img.shields.io/badge/Author-VincentVic-orange.svg?style=flat-square)](https://gitee.com/Vincent-Vic)

## 简介

### 官网介绍
[ElasticJob 官网](https://shardingsphere.apache.org/elasticjob/index_zh.html)
> ElasticJob 是一个分布式调度解决方案，由 2 个相互独立的子项目 ElasticJob-Lite 和 ElasticJob-Cloud 组成。
> 
> ElasticJob-Lite 定位为轻量级无中心化解决方案，使用jar的形式提供分布式任务的协调服务；
> 
> ElasticJob-Cloud 使用 Mesos 的解决方案，额外提供资源治理、应用分发以及进程隔离等服务。

> ElasticJob 是面向互联网生态和海量任务的分布式调度解决方案，由两个相互独立的子项目 ElasticJob-Lite 和 ElasticJob-Cloud 组成。 它通过弹性调度、资源管控、以及作业治理的功能，打造一个适用于互联网场景的分布式调度解决方案，并通过开放的架构设计，提供多元化的作业生态。 它的各个产品使用统一的作业 API，开发者仅需一次开发，即可随意部署。

### 本项目简介
Spring Boot 官方还未提供Spring Boot的Starter，由此学习编写了本项目
## 依赖
本项目实现elastic-job lite在Spring Boot 下的整合的使用了Spring Boot自动配置,以及elastic-job-lite的核心和spring依赖
```xml
 <!-- Spring Boot自身的自动配置 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-autoconfigure</artifactId>
    <version>2.6.3</version>
</dependency>
<!-- elastic-job-lite-->
<dependency>
    <groupId>com.dangdang</groupId>
    <artifactId>elastic-job-lite-core</artifactId>
    <version>2.1.5</version>
</dependency>
<!-- elastic-job-lite-->
<dependency>
    <groupId>com.dangdang</groupId>
    <artifactId>elastic-job-lite-spring</artifactId>
    <version>2.1.5</version>
</dependency>
```
## 如何使用
### 添加依赖
```xml
<dependency>
    <groupId>cn.flowboot</groupId>
    <artifactId>elastic-job-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 配置
elastic-job 需要依赖zookeeper，如果zookeeper的服务器未配置，本依赖并不会生效,namespace 命名空间未配置将会抛出异常

如果需要事件追踪需要配置数据源,如果项目中已经存在连接池或者mybatis配置无需单独配置

```yaml
elastic-job:
  zookeeper:
    # zk服务地址
    server: localhost:2181
    # zk命名空间
    namespace: simpleJob
    # 等待重试的间隔时间的初始值 默认1000，单位：毫秒
    baseSleepTimeMilliseconds: 1000
    # 等待重试的间隔时间的最大值 默认3000，单位：毫秒
    maxSleepTimeMilliseconds: 3000
    # 最大重试次数 默认3
    maxRetries: 3
    # 会话超时时间 默认60000，单位：毫秒
    sessionTimeoutMilliseconds: 60000
    # 连接超时时间 默认15000，单位：毫秒
    connectionTimeoutMilliseconds: 15000


spring:
  ## 数据源配置
  datasource: # 数据库的数据配置
    driver-class-name: com.mysql.cj.jdbc.Driver
    # url
    url: jdbc:mysql://ip:port/dbName?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false
    username: dbUsername
    password: dbPassword
```

### 注解使用
本项目提供了@ElasticJob注解更方便的使用Elastic-Job,并且无需单独添加spring bean 的扫描注解,参数如下表

  参数   | 默认 | 描述  
  :----:  |:----:| :----:
**name** | 无 | 任务名称 【必填】 
**cron** | 无 | cron表达式，用于控制作业触发时间,默认每间隔10秒钟执行一次 【必填】 
shardingTotalCount | 1 | 作业分片总数 【不能为0或者小于0】
override | false | 是否可覆盖
streamingProcess | false | 是否流式处理 注: 在DataflowJob 中才有效
jobStrategy | AverageAllocationJobShardingStrategy | 分片策略
jobEvent | false | 是否支持事件记录,需要配置数据源
jobListener | 无 | 监听器
enable | true | 是否启用定时任务

### 例子
@ElasticJob 自动识别SimpleJob和DataflowJob，如果继承的并非这两个类会无效
```java
@ElasticJob(name = "my-SimpleJob",cron = "0/10 * * * * ?",shardingTotalCount = 2,override = true,jobEvent = true)
public class MySimpleJob implements SimpleJob {
    
    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.printf("SimpleJob %s 当前分片项 %d,总分片项 %d\n", LocalTime.now(),shardingContext.getShardingItem(),shardingContext.getShardingTotalCount());
    }
}
```
## 常见问题
- 1.zookeeper 未配置或者参数缺少
- 2.zookeeper 未启动
- 3.启用事件记录，未配置数据源并不会启用事件记录
- 4.streamingProcess仅在DataflowJob下有效
