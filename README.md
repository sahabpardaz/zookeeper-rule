# zookeeper-rule
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
