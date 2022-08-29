package ir.sahab.zookeeperrule;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.*;

import java.io.IOException;

/**
 * JUnit 5 extension which provides an embedded ZooKeeper server.
 */
class ZooKeeperExtension extends ZooKeeperBase
        implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

    private final TestInstance.Lifecycle lifecycle;

    public ZooKeeperExtension(int port, TestInstance.Lifecycle lifecycle) {
        super(port);
        this.lifecycle = lifecycle;
    }

    /**
     * Creates an extension to set up an embedded Test ZooKeeper.
     *
     * @param port a port on which the embedded ZooKeeper should be setup.
     */
    public ZooKeeperExtension(int port) {
        this(port, TestInstance.Lifecycle.PER_CLASS);
    }

    public ZooKeeperExtension() {
        this(TestInstance.Lifecycle.PER_CLASS);
    }

    public ZooKeeperExtension(TestInstance.Lifecycle lifecycle) {
        super();
        this.lifecycle = lifecycle;
    }

    @Override
    public void afterAll(ExtensionContext context) throws IOException {
        if (lifecycle == TestInstance.Lifecycle.PER_CLASS) {
            teardown();
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws IOException {
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
