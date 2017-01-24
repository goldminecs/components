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

package org.talend.components.elasticsearch;

import java.net.MalformedURLException;
import java.net.URL;

import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.JarRuntimeInfo;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.common.dataset.DatasetProperties;
import org.talend.components.common.datastore.DatastoreDefinition;
import org.talend.components.elasticsearch.input.ElasticsearchInputDefinition;
import org.talend.components.elasticsearch.output.ElasticsearchOutputDefinition;
import org.talend.daikon.definition.I18nDefinition;
import org.talend.daikon.runtime.RuntimeInfo;

public class ElasticsearchDatastoreDefinition extends I18nDefinition
        implements DatastoreDefinition<ElasticsearchDatastoreProperties> {

    public static final String RUNTIME_2_4 = "org.talend.components.elasticsearch.runtime_2_4.ElasticsearchDatastoreRuntime";

    public static final String NAME = ElasticsearchComponentFamilyDefinition.NAME + "Datastore";

    public ElasticsearchDatastoreDefinition() {
        super(NAME);
    }

    @Override
    public Class<ElasticsearchDatastoreProperties> getPropertiesClass() {
        return ElasticsearchDatastoreProperties.class;
    }

    @Override
    public RuntimeInfo getRuntimeInfo(ElasticsearchDatastoreProperties properties) {
        try {
            switch (properties.version.getValue()) {
            case V_2_4:
            default:
                return new JarRuntimeInfo(new URL("mvn:org.talend.components/elasticsearch-runtime"),
                        DependenciesReader.computeDependenciesFilePath("org.talend.components", "elasticsearch-runtime"),
                        RUNTIME_2_4);
            }
        } catch (MalformedURLException e) {
            throw new ComponentException(e);
        }
    }

    @Override
    public String getImagePath() {
        return NAME + "_icon32.png";
    }

    @Override
    public DatasetProperties createDatasetProperties(ElasticsearchDatastoreProperties storeProp) {
        ElasticsearchDatasetProperties setProp = new ElasticsearchDatasetProperties(ElasticsearchDatasetDefinition.NAME);
        setProp.init();
        setProp.setDatastoreProperties(storeProp);
        return setProp;
    }

    @Override
    public String getInputCompDefinitionName() {
        return ElasticsearchInputDefinition.NAME;
    }

    @Override
    public String getOutputCompDefinitionName() {
        return ElasticsearchOutputDefinition.NAME;
    }

}
