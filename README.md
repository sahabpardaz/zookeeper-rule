# zookeeper-rule
JUnit rule which provides an embedded ZooKeeper server.
This rule able to provide a new initialized CuratorFramework client which can work with or without specified namespace. The caller is responsible to close the created client .

## Sample Usage

```
@Rule
public ZooKeeperRule zkRule = new ZooKeeperRule();

@Test
public void WriteAndRead() throws Exception {
    String path = "/path";
    byte[] zNodeDataBytes = "data".getBytes();
    CuratorFramework client = zkRule.newClient();
    client.create().forPath(path, zNodeDataBytes);
    byte[] resultBytes = client.getData().forPath(path);
    Assert.assertArrayEquals(zNodeDataBytes, resultBytes);
}
```

## Add it to your project
You can reference to this library by either of java build systems (Maven, Gradle, SBT or Leiningen) using snippets from this jitpack link:
[![](https://jitpack.io/v/sahabpardaz/zookeeper-rule.svg)](https://jitpack.io/#sahabpardaz/zookeeper-rule)
