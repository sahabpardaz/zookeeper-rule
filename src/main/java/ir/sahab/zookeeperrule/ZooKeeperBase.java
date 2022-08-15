package ir.sahab.zookeeperrule;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

class ZooKeeperBase {

    private ServerCnxnFactory factory;
    private File snapshotDir;
    private File logDir;
    private int port;
    private String localIp;

    public ZooKeeperBase(String address) {
        String[] splitAddress = address.split(":");
        if (splitAddress.length != 2) {
            throw new IllegalArgumentException("Address should be in the format of IP:PORT");
        }

        this.localIp = splitAddress[0];
        this.port = Integer.parseInt(splitAddress[1]);
    }

    public ZooKeeperBase() {
        this(newLocalAddress());
    }

    private static String newLocalAddress() {

        // Why we are going to use local IP and not just localhost or 127.0.0.1 constants?
        // Because we have encountered a problem when configured an KafkaServerStartable
        // to use this embedded ZooKeeper on 'localhost'.
        // But using local IP, solved the problem. See this:
        // https://www.ibm.com/support/knowledgecenter/SSPT3X_4.1.0/
        // com.ibm.swg.im.infosphere.biginsights.trb.doc/doc/trb_kafka_producer_localhost.html
        String localIp;
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new AssertionError(e);
        }

        return localIp + ":" + anOpenPort();
    }

    static Integer anOpenPort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new AssertionError("Unable to find an open port.", e);
        }
    }

    void setup() throws IOException, InterruptedException {
        snapshotDir = Files.createTempDirectory("zk-snapshot").toFile();
        logDir = Files.createTempDirectory("zk-logs").toFile();

        // ZooKeeperServer overrides DefaultUncaughtExceptionHandler
        // and we do not want anyone to override this behaviour.
        // So here, we are going to backup the DefaultUncaughtExceptionHandler before
        // creating ZkServer and restore it after.
        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();

        ZooKeeperServer zkServer = new ZooKeeperServer(snapshotDir, logDir, 500);
        factory = NIOServerCnxnFactory.createFactory();
        factory.configure(new InetSocketAddress(localIp, port), 100);
        factory.startup(zkServer);

        // Restore the DefaultUncaughtExceptionHandler.
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    void teardown() {
        factory.shutdown();
        try {
            Files.walk(Paths.get(logDir.getPath()))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);

            Files.walk(Paths.get(snapshotDir.getPath()))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return address of this ZK server
     */
    public String getAddress() {
        return localIp + ":" + port;
    }

    /**
     * @return the port on local IP where this Embedded ZK is located.
     */
    public int getPort() {
        return port;
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
