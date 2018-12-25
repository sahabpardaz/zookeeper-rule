JUnit rule which provides an embedded ZooKeeper server. The rule has also helper methods to get curator clients which are initialized to work with that ZooKeeper.
 
 ## Sample Usage
 
 ```
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
 
 ## Add it to your project
 You can reference to this library by either of java build systems (Maven, Gradle, SBT or Leiningen) using snippets from this jitpack link:
 [![](https://jitpack.io/v/sahabpardaz/zookeeper-rule.svg)](https://jitpack.io/#sahabpardaz/zookeeper-rule)