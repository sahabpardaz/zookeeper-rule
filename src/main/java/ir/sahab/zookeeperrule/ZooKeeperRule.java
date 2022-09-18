package ir.sahab.zookeeperrule;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * JUnit 4 rule which provides an embedded ZooKeeper server.
 */
public class ZooKeeperRule extends ZooKeeperBase implements TestRule {

    public ZooKeeperRule() {
        super();
    }

    /**
     * Creates a rule to set up an embedded ZooKeeper.
     *
     * @param address the local address on which the embedded ZooKeeper should be setup. It should
     *                be in format of "IP:PORT" and the IP should be one of the IPs of the local system.
     */
    public ZooKeeperRule(String address) {
        super(address);
    }

    //copied from org.junit.rules.ExternalResource
    public Statement apply(Statement base, Description description) {
        return statement(base);
    }

    private Statement statement(final Statement base) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                setup();

                List<Throwable> errors = new ArrayList<>();
                try {
                    base.evaluate();
                } catch (Throwable t) {
                    errors.add(t);
                } finally {
                    try {
                        teardown();
                    } catch (Throwable t) {
                        errors.add(t);
                    }
                }
                MultipleFailureException.assertEmpty(errors);
            }
        };
    }

}
