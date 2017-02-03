// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.adapter.beam.kv;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.avro.AvroUtils;

public class KeyValueUtils {

    /**
     * Generate a new Index Record which is the filtered result of the input record.
     * 
     * The user can freely remove column, add empty column or change the place of column in the same hierarchical level.
     * 
     * @return the new record
     */
    public static IndexedRecord extractIndexedRecord(IndexedRecord inputRecord, Schema outputSchema) {
        GenericRecordBuilder outputRecord = new GenericRecordBuilder(outputSchema);
        Schema inputSchema = getUnwrappedSchema(inputRecord);
        for (Field field : outputSchema.getFields()) {
            if (inputSchema.getField(field.name()) != null) {
                // The column was existing on the input record, we forward it to the output record.
                Object inputValue = inputRecord.get(inputSchema.getField(field.name()).pos());

                // The current column can be a Record (an hierarchical sub-object) or directly a value.
                // If we are on a record, we need to recursively do the process
                // if we are on a object, we save it to the output.
                if (inputValue instanceof Record) {
                    // The sub-schema at this level is a union of "empty" and a record,
                    // so we need to get the true sub-schema
                    Schema inputChildSchema = getUnwrappedSchema(inputSchema.getField(field.name()));
                    Schema outputChildSchema = getUnwrappedSchema(outputSchema.getField(field.name()));
                    if (inputChildSchema.getType().equals(Type.RECORD) && outputChildSchema.getType().equals(Type.RECORD)) {
                        Object childRecord = extractIndexedRecord((IndexedRecord) inputValue, outputChildSchema);
                        outputRecord.set(field.name(), childRecord);
                    }
                } else {
                    outputRecord.set(field.name(), inputValue);
                }
            } else {
                // element not found => set to the value and its hierarchy to null
                outputRecord.set(field.name(), KeyValueUtils.generateEmptyRecord(outputSchema, field.name()));
            }
        }
        return outputRecord.build();
    }

    /**
     * Transform a indexedRecord to match the associated key-value schema
     *
     * @param record a indexed record
     * @param kvSchema its associated key value schema
     * @return the key-value
     */
    public static IndexedRecord transformToKV(IndexedRecord record, Schema kvSchema) {
        Schema keySchema = kvSchema.getField("key").schema();
        IndexedRecord keyIndexRecord = extractIndexedRecord(record, keySchema);
        Schema valueSchema = kvSchema.getField("value").schema();
        IndexedRecord valueIndexRecord = extractIndexedRecord(record, valueSchema);

        GenericRecordBuilder outputRecord = new GenericRecordBuilder(kvSchema);
        outputRecord.set("key", keyIndexRecord);
        outputRecord.set("value", valueIndexRecord);

        return outputRecord.build();
    }

    /**
     * Merge a two IndexedRecords in order to match the outputSchema.
     * @param keyRecord an indexedRecord
     * @param valueRecord an indexedRecord
     * @param outputSchema a schema
     * @return a merged IndexedRecord
     */
    public static IndexedRecord mergeIndexedRecord(IndexedRecord keyRecord, IndexedRecord valueRecord, Schema outputSchema) {
        GenericRecordBuilder outputRecord = new GenericRecordBuilder(outputSchema);
        Schema keySchema = getUnwrappedSchema(keyRecord);
        Schema valueSchema = getUnwrappedSchema(valueRecord);
        for (Field field : outputSchema.getFields()) {
            if (keySchema.getField(field.name()) != null && valueSchema.getField(field.name()) != null) {
                // The field is present in key and shcema => we are on a record an need to merge its subschema
                Object keyValue = keyRecord.get(keySchema.getField(field.name()).pos());
                Object valueValue = valueRecord.get(valueSchema.getField(field.name()).pos());
                if (keyValue instanceof Record) {
                    Schema keyChildSchema = getUnwrappedSchema(keySchema.getField(field.name()));
                    Schema valueChildSchema = getUnwrappedSchema(valueSchema.getField(field.name()));
                    Schema outputChildSchema = getUnwrappedSchema(outputSchema.getField(field.name()));
                    if (keyChildSchema.getType().equals(Type.RECORD) && valueChildSchema.getType().equals(Type.RECORD)
                            && outputChildSchema.getType().equals(Type.RECORD)) {
                        Object childRecord = mergeIndexedRecord((IndexedRecord) keyValue, (IndexedRecord) valueValue,
                                outputChildSchema);
                        outputRecord.set(field.name(), childRecord);
                    }
                } else {
                    outputRecord.set(field.name(), keyValue);
                }
            } else if (keySchema.getField(field.name()) != null) {
                outputRecord.set(field.name(), keyRecord.get(keySchema.getField(field.name()).pos()));
            } else if (valueSchema.getField(field.name()) != null) {
                outputRecord.set(field.name(), valueRecord.get(valueSchema.getField(field.name()).pos()));
            } else {
                // element not found => set to the value and its hierarchy to null
                outputRecord.set(field.name(), KeyValueUtils.generateEmptyRecord(outputSchema, field.name()));
            }
        }

        return outputRecord.build();
    }

    /**
     * Merge the key and the value of a KV IndexedRecord to match the provided Schema 
     * @param record a KV IndexedRecordf
     * @param schema the output schema
     * @return a merged IndexedRecord
     */
    public static IndexedRecord transformFromKV(IndexedRecord record, Schema schema) {
        IndexedRecord keyRecord = (IndexedRecord) record.get(record.getSchema().getField("key").pos());
        IndexedRecord valueRecord = (IndexedRecord) record.get(record.getSchema().getField("value").pos());
        return mergeIndexedRecord(keyRecord, valueRecord, schema);
    }

    /**
     * Use a Schema to generate a hierarchical GenericRecord that contains only null values.
     * 
     * @param schema the parent schema of the field to set as null
     * @param fieldName the name of the field to set as null
     * @return if fieldName is a Record of the schema, the method will return a GenericRecord with any leaf set as null,
     * otherwise return null
     */
    public static IndexedRecord generateEmptyRecord(Schema schema, String fieldName) {
        if (schema.getType().equals(Type.RECORD)) {
            Schema unwrappedSchema = getUnwrappedSchema(schema.getField(fieldName));
            if (unwrappedSchema.getType().equals(Type.RECORD)) {
                GenericRecordBuilder outputRecord = new GenericRecordBuilder(unwrappedSchema);
                for (Field field : unwrappedSchema.getFields()) {
                    IndexedRecord value = generateEmptyRecord(unwrappedSchema, field.name());
                    outputRecord.set(field.name(), value);
                }
                return outputRecord.build();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static Schema getUnwrappedSchema(Field field) {
        return AvroUtils.unwrapIfNullable(field.schema());
    }

    public static Schema getUnwrappedSchema(IndexedRecord record) {
        return AvroUtils.unwrapIfNullable(record.getSchema());
    }

    /**
     * Try to find a field in either the key index record or the value index record
     * 
     * @param fieldPath the field name. Can be a path for hierarchical element
     * @param keyRecord an Indexed record
     * @param valueRecord an another Indexed record
     * @return the Object matching to the fieldName if it was found, null otherwise
     */
    public static Object getFieldFromKV(String fieldPath, IndexedRecord keyRecord, IndexedRecord valueRecord) {
        // Start with the value record, there is an higher chance to find the field
        Object outputField = getField(fieldPath, valueRecord);
        if (outputField != null) {
            return outputField;
        } else {
            return getField(fieldPath, keyRecord);
        }
    }
    /**
     * Retrieve a field from on indexedRecord.
     * 
     * @param fieldPath the field name. Can be a path for hierarchical element
     * @param record an Indexed record
     * @return the Object matching to the fieldName if it was found, null otherwise
     */
    public static Object getField(String fieldPath, IndexedRecord record) {
        // TODO current implementation will only extract one element, but
        // further implementation may
        String[] path = fieldPath.split("\\.");
        Schema schema = record.getSchema();

        for (Integer i = 0; i < path.length; i++) {
            // The column was existing on the input record, we forward it to the
            // output record.
            Object inputValue = record.get(schema.getField(path[i]).pos());

            // The current column can be a Record (an hierarchical sub-object)
            // or directly a value.
            if (inputValue instanceof Record) {
                // If we are on a record, we need to recursively do the process
                record = (IndexedRecord) inputValue;

                // The sub-schema at this level is a union of "empty" and a
                // record, so we need to get the true
                // sub-schema
                if (schema.getField(path[i]).schema().getType().equals(Type.RECORD)) {
                    schema = schema.getField(path[i]).schema();
                } else if (schema.getField(path[i]).schema().getType().equals(Type.UNION)) {
                    for (Schema childSchema : schema.getField(path[i]).schema().getTypes()) {
                        if (childSchema.getType().equals(Type.RECORD)) {
                            schema = childSchema;
                            break;
                        }
                    }
                }

            } else {
                // if we are on a object, then this is or the expected value of
                // an error.
                if (i == path.length - 1) {
                    return inputValue;
                } else {
                    // No need to go further, return an empty list
                    return null;
                }
            }
        }
        // field not found, return an empty list
        return null;
    }
}
