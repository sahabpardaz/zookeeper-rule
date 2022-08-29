# zookeeper-rule

[![Tests](https://github.com/sahabpardaz/zookeeper-rule/actions/workflows/maven-verify.yml/badge.svg?branch=master)](https://github.com/sahabpardaz/zookeeper-rule/actions/workflows/maven-verify.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=sahabpardaz_zookeeper-rule&metric=coverage)](https://sonarcloud.io/dashboard?id=sahabpardaz_zookeeper-rule)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=sahabpardaz_zookeeper-rule&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=sahabpardaz_zookeeper-rule)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=sahabpardaz_zookeeper-rule&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=sahabpardaz_zookeeper-rule)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=sahabpardaz_zookeeper-rule&metric=security_rating)](https://sonarcloud.io/dashboard?id=sahabpardaz_zookeeper-rule)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=sahabpardaz_zookeeper-rule&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=sahabpardaz_zookeeper-rule)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=sahabpardaz_zookeeper-rule&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=sahabpardaz_zookeeper-rule)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=sahabpardaz_zookeeper-rule&metric=sqale_index)](https://sonarcloud.io/dashboard?id=sahabpardaz_zookeeper-rule)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sahabpardaz_zookeeper-rule&metric=alert_status)](https://sonarcloud.io/dashboard?id=sahabpardaz_zookeeper-rule)
[![JitPack](https://jitpack.io/v/sahabpardaz/zookeeper-rule.svg)](https://jitpack.io/#sahabpardaz/zookeeper-rule)

This library provides Junit 4 Rule and Junit 5 Extension which is an embedded ZooKeeper server.
The rule and extension has also helper methods to get curator clients which are initialized to work with that ZooKeeper.

## Sample Usage
 
 ```java
 @Rule
 public ZooKeeperRule zkRule = new ZooKeeperRule();
 
 @Test
 public void test() throws Exception {
     try (CuratorFramework client = zkRule.newClient()) {  // It is also possible to create a client on a specific namespace
        client.create().forPath("/path", "data".toBytes());
        Assert.assertArrayEquals("data".toBytes(), client.getData().forPath("/path"));
     }
 }
 ```
## JUnit 5 Support

Sample usage:

```java
@RegisterExtension
static ZooKeeperExtension zooKeeperExtension = new ZooKeeperExtension();

@Test
public void test() throws Exception {
    try (CuratorFramework client = zooKeeperExtension.newClient()) {  // It is also possible to create a client on a specific namespace
        client.create().forPath("/path", "data".getBytes());
        assertArrayEquals("data".getBytes(), client.getData().forPath("/path"));
    }
}
```

 ## Add it to your project
 You can reference to this library by either of java build systems (Maven, Gradle, SBT or Leiningen) using snippets from this jitpack link:
 [![](https://jitpack.io/v/sahabpardaz/zookeeper-rule.svg)](https://jitpack.io/#sahabpardaz/zookeeper-rule)

JUnit 4 and 5 dependencies are marked as optional, so you need to provide JUnit 4 or 5 dependency
(based on what version you need, and you use) in you project to make it work.
