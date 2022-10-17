package ir.sahab.zookeeperrule;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ZooKeeperExtensionTest {
    private static final int LOCAL_PORT = anOpenPort();
    private static final String LOCAL_ADDRESS = "127.0.0.1:" + LOCAL_PORT;

    @RegisterExtension
    static ZooKeeperExtension zkClassWideExtension = new ZooKeeperExtension(LOCAL_ADDRESS);

    @RegisterExtension
    static ZooKeeperExtension zooKeeperExtension = new ZooKeeperExtension(TestInstance.Lifecycle.PER_METHOD);

    static Integer anOpenPort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new AssertionError("Unable to find an open port.", e);
        }
    }

    @Test
    void testZKClassRule() throws Exception {
        checkZkIsClean();

        // Check client without namespace
        try (CuratorFramework client = zkClassWideExtension.newClient()) {
            checkWriteAndRead(client, "/no-namespace-path");
        }

        // Check client with specific namespace
        try (CuratorFramework client = zkClassWideExtension.newClient("test-ns")) {
            checkWriteAndRead(client, "/namespace-path");
        }

        // Check external client
        try (CuratorFramework client = CuratorFrameworkFactory
                .newClient(zkClassWideExtension.getAddress(), 30000, 3000, new RetryNTimes(3, 100))) {
            client.start();
            checkWriteAndRead(client, "/external-client-path");
        }

        makeZkDirty();
    }

    @Test
    void testZkRule() throws Exception {
        checkZkIsClean();

        // Check client without namespace
        try (CuratorFramework client = zooKeeperExtension.newClient()) {
            checkWriteAndRead(client, "/no-namespace-path");
        }

        // Check client with specific namespace
        try (CuratorFramework client = zooKeeperExtension.newClient("test-ns")) {
            checkWriteAndRead(client, "/namespace-path");
        }

        // Check external client
        try (CuratorFramework client = CuratorFrameworkFactory
                .newClient(zooKeeperExtension.getAddress(), 30000, 3000, new RetryNTimes(3, 100))) {
            client.start();
            checkWriteAndRead(client, "/external-client-path");
        }

        makeZkDirty();
    }

    @Test
    void testExplicitAddress() throws Exception {
        checkZkIsClean();

        assertEquals(LOCAL_PORT, zkClassWideExtension.getPort());

        makeZkDirty();
    }

    private void checkWriteAndRead(CuratorFramework client, String path) throws Exception {
        byte[] zNodeDataBytes = "data".getBytes();
        client.create().forPath(path, zNodeDataBytes);
        byte[] resultBytes = client.getData().forPath(path);
        assertArrayEquals(resultBytes, zNodeDataBytes);
    }

    private void checkZkIsClean() throws Exception {
        List<String> children;
        try (CuratorFramework client = zooKeeperExtension.newClient()) {
            children = client.getChildren().forPath("/");
        }

        // By default, a node named "zookeeper" exists under root.
        // So we expect here the size 1 instead of size 0.
        assertEquals(1, children.size());
        assertEquals("zookeeper", children.get(0));
    }

    private void makeZkDirty() throws Exception {
        try (CuratorFramework client = zooKeeperExtension.newClient()) {
            client.create().forPath("/dirtyPath", "dirtyData".getBytes());
        }
    }
}
