package ir.sahab.zookeeperrule;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static ir.sahab.zookeeperrule.ZooKeeperExtensionTest.anOpenPort;

public class ZooKeeperRuleTest {

    private static final int LOCAL_PORT = anOpenPort();
    private static final String LOCAL_ADDRESS = "127.0.0.1:" + LOCAL_PORT;

    @ClassRule
    public static ZooKeeperRule zkClassRule = new ZooKeeperRule(LOCAL_ADDRESS);

    @Rule
    public ZooKeeperRule zkRule = new ZooKeeperRule();

    @Test
    public void testZKClassRule() throws Exception {
        checkZkIsClean();

        // Check client without namespace
        try (CuratorFramework client = zkClassRule.newClient()) {
            checkWriteAndRead(client, "/no-namespace-path");
        }

        // Check client with specific namespace
        try (CuratorFramework client = zkClassRule.newClient("test-ns")) {
            checkWriteAndRead(client, "/namespace-path");
        }
        // Check external client
        try (CuratorFramework client = CuratorFrameworkFactory
                .newClient(zkClassRule.getAddress(), 30000, 3000, new RetryNTimes(3, 100))) {
            client.start();
            checkWriteAndRead(client, "/external-client-path");
        }

        makeZkDirty();
    }

    @Test
    public void testZkRule() throws Exception {
        checkZkIsClean();

        // Check client without namespace
        try (CuratorFramework client = zkRule.newClient()) {
            checkWriteAndRead(client, "/no-namespace-path");
        }

        // Check client with specific namespace
        try (CuratorFramework client = zkRule.newClient("test-ns")) {
            checkWriteAndRead(client, "/namespace-path");
        }
        // Check external client
        try (CuratorFramework client = CuratorFrameworkFactory
                .newClient(zkRule.getAddress(), 30000, 3000, new RetryNTimes(3, 100))) {
            client.start();
            checkWriteAndRead(client, "/external-client-path");
        }

        makeZkDirty();
    }

    @Test
    public void testExplicitAddress() throws Exception {
        checkZkIsClean();

        Assert.assertEquals(LOCAL_PORT, zkClassRule.getPort());

        makeZkDirty();
    }

    private void checkWriteAndRead(CuratorFramework client, String path) throws Exception {
        byte[] zNodeDataBytes = "data".getBytes();
        client.create().forPath(path, zNodeDataBytes);
        byte[] resultBytes = client.getData().forPath(path);
        Assert.assertArrayEquals(resultBytes, zNodeDataBytes);
    }

    private void checkZkIsClean() throws Exception {
        List<String> children;
        try (CuratorFramework client = zkRule.newClient()) {
            children = client.getChildren().forPath("/");
        }

        // By default, a node named "zookeeper" exists under root.
        // So we expect here the size 1 instead of size 0.
        Assert.assertEquals(1, children.size());
        Assert.assertEquals("zookeeper", children.get(0));
    }

    private void makeZkDirty() throws Exception {
        try (CuratorFramework client = zkRule.newClient()) {
            client.create().forPath("/dirtyPath", "dirtyData".getBytes());
        }
    }
}
