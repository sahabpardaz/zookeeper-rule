package ir.sahab.zookeeperrule;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.*;

/**
 * JUnit 5 extension which provides an embedded ZooKeeper server.
 */
public class ZooKeeperExtension extends ZooKeeperBase
        implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

    private final TestInstance.Lifecycle lifecycle;

    public ZooKeeperExtension(String address, TestInstance.Lifecycle lifecycle) {
        super(address);
        this.lifecycle = lifecycle;
    }

    /**
     * Creates an extension to set up an embedded Test ZooKeeper.
     *
     * @param address the local address on which the embedded ZooKeeper should be setup. It should
     *                be in format of "IP:PORT" and the IP should be one of the IPs of the local system.
     */
    public ZooKeeperExtension(String address) {
        this(address, TestInstance.Lifecycle.PER_CLASS);
    }

    public ZooKeeperExtension() {
        this(TestInstance.Lifecycle.PER_CLASS);
    }

    public ZooKeeperExtension(TestInstance.Lifecycle lifecycle) {
        super();
        this.lifecycle = lifecycle;
    }

    @Override
    public void afterAll(ExtensionContext context) {
        if (lifecycle == TestInstance.Lifecycle.PER_CLASS) {
            teardown();
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (lifecycle == TestInstance.Lifecycle.PER_METHOD) {
            teardown();
        }
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (lifecycle == TestInstance.Lifecycle.PER_CLASS) {
            setup();
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (lifecycle == TestInstance.Lifecycle.PER_METHOD) {
            setup();
        }
    }
}
