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

package org.talend.components.bigquery;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.talend.components.api.test.ComponentTestUtils;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

public class BigQueryDatastorePropertiesTest {

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    BigQueryDatastoreProperties properties;

    @Before
    public void reset() {
        properties = new BigQueryDatastoreProperties("test");
        properties.init();
    }

    @Test
    public void testI18N() {
        ComponentTestUtils.checkAllI18N(properties, errorCollector);
    }

    /**
     * Checks {@link BigQueryDatastoreProperties} sets correctly initial schema property
     */
    @Test
    public void testDefaultProperties() {
    }

    /**
     * Checks {@link BigQueryDatastoreProperties} sets correctly initial layout properties
     */
    @Test
    public void testSetupLayout() {
        Form main = properties.getForm(Form.MAIN);
        Collection<Widget> mainWidgets = main.getWidgets();

        List<String> ALL = Arrays.asList(properties.projectName.getName(), properties.serviceAccountFile.getName(),
                properties.tempGsFolder.getName());

        Assert.assertThat(main, notNullValue());
        Assert.assertThat(mainWidgets, hasSize(ALL.size()));

        for (String field : ALL) {
            Widget w = main.getWidget(field);
            Assert.assertThat(w, notNullValue());
        }
    }

    /**
     * Checks {@link BigQueryDatastoreProperties} sets correctly layout after refresh properties
     */
    @Test
    public void testRefreshLayout() {
        properties.refreshLayout(properties.getForm(Form.MAIN));
    }
}
