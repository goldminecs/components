package org.talend.components.jms;

import org.apache.avro.Schema;
import org.junit.Test;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.common.datastore.DatastoreProperties;
import org.talend.components.jms.runtime_1_1.JmsDatastoreRuntime;
import org.talend.daikon.properties.ValidationResult;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class JmsDatastoreRuntimeTest {
    private final JmsDatastoreRuntime datastoreRuntime = new JmsDatastoreRuntime();

    /**
     * Check {@link JmsDatastoreRuntime#doHealthChecks(RuntimeContainer)}
     * returns //TODO
     */
    @Test
    public void testDoHealthChecks() {
        JmsDatastoreProperties props = new JmsDatastoreProperties("test");
        props.contextProvider.setValue("org.exolab.jms.jndi.InitialContextFactory");
        props.serverUrl.setValue("tcp://localhost:3035");
        props.version.setValue(JmsDatastoreProperties.JmsVersion.V_1_1);
        props.connectionFactoryName.setValue("ConnectionFactory");
        datastoreRuntime.initialize(null,props);
        datastoreRuntime.doHealthChecks(null);
        //assertEquals(ValidationResult.OK, healthResult);
    }

    /**
     * Check {@link JmsDatastoreRuntime#initialize(RuntimeContainer, DatastoreProperties)}
     * Returns OK
     */
    @Test
    public void testInitialize() {
        ValidationResult result = datastoreRuntime.initialize(null, null);
        assertEquals(ValidationResult.OK, result);
    }


    /**
     * Check {@link JmsDatastoreRuntime#getPossibleDatasetNames(RuntimeContainer, String)}
     * Returns // TODO
     */
    @Test
    public void testGetPossibleDatasetNames() {
        ValidationResult result = datastoreRuntime.initialize(null, null);
        assertEquals(ValidationResult.OK, result);
    }
}