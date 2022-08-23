package ir.sahab.zookeeperrule;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.test.TestingServer;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;

/**
 * Base class for initializing an embeddable Zookeeper server. It also provides helper method for creating clients.
 */
abstract class ZooKeeperBase {

    private final int port;
    private TestingServer testingServer;

    protected ZooKeeperBase(int port) {
        this.port = port;
    }

    protected ZooKeeperBase() {
        this(anOpenPort());
    }

    static Integer anOpenPort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new AssertionError("Unable to find an open port.", e);
        }
    }

    void setup() throws Exception {

        // ZooKeeperServer overrides DefaultUncaughtExceptionHandler,
        // and we do not want anyone to override this behaviour.
        // So here, we are going to back up the DefaultUncaughtExceptionHandler before
        // creating ZkServer and restore it after.
        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();

        File tempDirectory = Files.createTempDirectory("zk-test").toFile();
        testingServer = new TestingServer(port, tempDirectory, true);

        // Restore  the DefaultUncaughtExceptionHandler.
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    void teardown() throws IOException {
        testingServer.close();
    }

    /**
     * @return address of this ZK server
     */
    public String getAddress() {
        return testingServer.getConnectString();
    }

    /**
     * @return the port on local IP where this Embedded ZK is located.
     */
    public int getPort() {
        return testingServer.getPort();
    }

    /**
     * Returns a new initialized client which has address of this embedded ZK as its remote. The
     * caller is responsible to close the returned client.
     */
    public CuratorFramework newClient() {
        return newClient(null);
    }

    /**
     * Returns a new initialized client which has address of this embedded ZK as its remote and is
     * working in the specified namespace. The caller is responsible to close the returned client.
     * The caller is responsible to close it.
     */
    public CuratorFramework newClient(String namespace) {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        builder = builder.connectString(getAddress());
        builder = builder.retryPolicy(new RetryNTimes(3, 100));
        if (namespace != null) {
            builder = builder.namespace(namespace);
        }

        CuratorFramework client = builder.build();
        client.start();
        return client;
    }

}
