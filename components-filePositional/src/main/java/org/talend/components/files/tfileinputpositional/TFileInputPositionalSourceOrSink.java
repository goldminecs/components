package org.talend.components.files.tfileinputpositional;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.avro.Schema;

import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;

import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;

/**
 * The TestInputSource provides the mechanism to supply data to other
 * components at run-time.
 *
 * Based on the Apache Beam project, the Source mechanism is appropriate to
 * describe distributed and non-distributed data sources and can be adapted
 * to scalable big data execution engines on a cluster, or run locally.
 *
 * This example component describes an input source that is guaranteed to be
 * run in a single JVM (whether on a cluster or locally), so:
 *
 * <ul>
 * <li>the simplified logic for reading is found in the {@link TestInputReader},
 *     and</li>
 * </ul>
 */
public class TFileInputPositionalSourceOrSink implements SourceOrSink {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Configuration extracted from the input properties. */
    private TFileInputPositionalProperties properties;

	private transient Schema schema;

    public void initialize(RuntimeContainer container, ComponentProperties properties) {
        this.properties = (TFileInputPositionalProperties) properties;
        // FIXME - this should be moved to the properties setup
        schema = new Schema.Parser().parse(this.properties.schema.schema.getStringValue()); 
    }

    public ValidationResult validate(RuntimeContainer adaptor) {
        // Check that the file exists.
        File f = new File(this.properties.filename.getStringValue());
        if (!f.exists()) {
            ValidationResult vr = new ValidationResult();
            vr.setMessage("The file '" + f.getPath() + "' does not exist."); //$NON-NLS-1$//$NON-NLS-2$
            vr.setStatus(ValidationResult.Result.ERROR);
            return vr;
        }
        // Check that there is exactly one column to contain the output.
/*        if (schema.getFields().size() != 1) {
            ValidationResult vr = new ValidationResult();
            vr.setMessage("The schema must have exactly one column."); //$NON-NLS-1$
            vr.setStatus(ValidationResult.Result.ERROR);
            return vr;
        } */
        
        return ValidationResult.OK;
    }

    public Schema getEndpointSchema(RuntimeContainer container, String schemaName) throws IOException {
        return null;
    }

    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        return null;
    }

    public Schema getSchemaFromProperties(RuntimeContainer adaptor) throws IOException {
        return schema;
    }

    public Schema getPossibleSchemaFromProperties(RuntimeContainer adaptor) throws IOException {
        return schema;
    }

     public long getEstimatedSizeBytes(RuntimeContainer adaptor) {
        // This will be ignored since the source will never be split.
        return 0;
     }

     public boolean producesSortedKeys(RuntimeContainer adaptor) {
        return false;
     }

     public TFileInputPositionalProperties getProperties() {
 		return properties;
 	}
}
