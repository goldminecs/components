// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.components.mongodb.runtime;

import java.util.HashMap;
import java.util.Map;

import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.mongodb.MongoDBConnectionProperties;

public abstract class MongoDBTestBasic {

    private static final String USER_ID = "talend";

    private static final String PASSWORD = "talend";

    private static final Integer MONGODB_PORT;

    protected static final String CONNECTION_COMP_ID = "tMongoDBConnection_1";

    protected static final String DEFAULT_DB = "testdb";

    final Map<String, Object> globalMap = new HashMap<String, Object>();

    static {
        String portStr = System.getProperty("mongodb.port");
        MONGODB_PORT = portStr != null ? Integer.parseInt(portStr) : 27017;
    }

    /**
     * @return the properties for this dataset, fully initialized with the default values and the datastore credentials
     * from the System environment.
     */
    public static MongoDBConnectionProperties createConnectionProperties() {

        MongoDBConnectionProperties properties = new MongoDBConnectionProperties("properties");
        properties.init();

        properties.port.setValue(MONGODB_PORT);
        // FIXME when docker of integrate is ready need recheck this.
        properties.userPassword.userId.setValue(USER_ID);
        properties.userPassword.password.setValue(PASSWORD);

        return properties;
    }

    /**
     * Create a runtime container for current test
     * 
     * @param componentId current component id
     * @param clear whether clean the globalMap
     * @return runtime container
     */

    public RuntimeContainer getRuntimeContainer(final String componentId, boolean clear) {
        if (clear) {
            globalMap.clear();
        }
        return new RuntimeContainer() {

            public Object getComponentData(String componentId, String key) {
                return globalMap.get(componentId + "_" + key);
            }

            public void setComponentData(String componentId, String key, Object data) {
                globalMap.put(componentId + "_" + key, data);
            }

            public String getCurrentComponentId() {
                return componentId;
            }

            public Object getGlobalData(String key) {
                return globalMap.get(key);
            }
        };
    }

    protected MongoDBSourceOrSink getInitializedSourceOrSink() {
        MongoDBSourceOrSink sourceOrSink = new MongoDBSourceOrSink();
        MongoDBConnectionProperties properties = createConnectionProperties();
        RuntimeContainer container = getRuntimeContainer(CONNECTION_COMP_ID, true);
        sourceOrSink.initialize(container, properties);
        return sourceOrSink;
    }

    public abstract void prepareTestData();

}
