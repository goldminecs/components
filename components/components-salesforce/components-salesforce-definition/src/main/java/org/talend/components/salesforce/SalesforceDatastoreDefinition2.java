
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
package org.talend.components.salesforce;

import org.talend.components.common.dataset.DatasetProperties;
import org.talend.components.common.datastore.DatastoreDefinition;
import org.talend.components.salesforce.dataprep.SalesforceInputDefinition;
import org.talend.daikon.definition.DefinitionImageType;
import org.talend.daikon.definition.I18nDefinition;
import org.talend.daikon.runtime.RuntimeInfo;

/**
 * the salesforce data store work for dataprep
 *
 */
public class SalesforceDatastoreDefinition2 extends I18nDefinition
        implements DatastoreDefinition<SalesforceDatastoreProperties2> {

    public static final String NAME = "SalesforceDatastore";

    public SalesforceDatastoreDefinition2() {
        super(NAME);
    }

    @Override
    public RuntimeInfo getRuntimeInfo(SalesforceDatastoreProperties2 properties) {
        return SalesforceDefinition.getCommonRuntimeInfo(SalesforceDefinition.DATASTORE_RUNTIME_CLASS);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DatasetProperties createDatasetProperties(SalesforceDatastoreProperties2 storeProp) {
        SalesforceModuleProperties datasetProperties = new SalesforceModuleProperties("dataset");
        datasetProperties.init();
        datasetProperties.setDatastoreProperties(storeProp);
        return datasetProperties;
    }

    @Override
    public String getInputCompDefinitionName() {
        return SalesforceInputDefinition.NAME;
    }

    @Override
    public String getOutputCompDefinitionName() {
        // no output component now
        return null;
    }

    @Deprecated
    @Override
    public String getImagePath() {
        return NAME + "_icon32.png";
    }

    @Override
    public String getImagePath(DefinitionImageType type) {
        switch (type) {
        case PALETTE_ICON_32X32:
            return NAME + "_icon32.png";
        case SVG_ICON:
            return null;
        }
        return null;
    }

    @Override
    public String getIconKey() {
        return null;
    }

    @Override
    public Class<SalesforceDatastoreProperties2> getPropertiesClass() {
        return SalesforceDatastoreProperties2.class;
    }

}
