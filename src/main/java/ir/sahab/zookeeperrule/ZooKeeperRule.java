package ir.sahab.zookeeperrule;

import org.apache.curator.framework.CuratorFramework;
import org.junit.rules.ExternalResource;

import java.io.IOException;

/**
 * JUnit 4 rule which provides an embedded ZooKeeper server.
 */
public class ZooKeeperRule extends ExternalResource {

    private final ZooKeeperBase base;

    public ZooKeeperRule() {
        base = new ZooKeeperBase();
    }

    /**
     * Creates a rule to set up an embedded ZooKeeper.
     *
     * @param port a port on which the embedded ZooKeeper should be setup.
     */
    public ZooKeeperRule(int port) {
        base = new ZooKeeperBase(port);
    }

    @Override
    protected void before() throws Exception {
        base.setup();
    }

    @Override
    protected void after() {
        try {
            base.teardown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return address of this ZK server
     */
    public String getAddress() {
        return base.getAddress();
    }

    /**
     * @return the port on local IP where this Embedded ZK is located.
     */
    public int getPort() {
        return base.getPort();
    }

    /**
     * Returns a new initialized client which has address of this embedded ZK as its remote. The
     * caller is responsible to close the returned client.
     */
    public CuratorFramework newClient() {
        return base.newClient();
    }

    /**
     * Returns a new initialized client which has address of this embedded ZK as its remote and is
     * working in the specified namespace. The caller is responsible to close the returned client.
     * The caller is responsible to close it.
     */
    public CuratorFramework newClient(String namespace) {
        return base.newClient(namespace);
    }
}
