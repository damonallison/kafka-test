/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package com.damonallison.kafka.test.model;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class PersonKey extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"PersonKey\",\"namespace\":\"com.damonallison.kafka.test.model\",\"fields\":[{\"name\":\"id\",\"type\":\"int\",\"doc\":\"The unique id.\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  /** The unique id. */
  @Deprecated public int id;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public PersonKey() {}

  /**
   * All-args constructor.
   */
  public PersonKey(java.lang.Integer id) {
    this.id = id;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return id;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: id = (java.lang.Integer)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'id' field.
   * The unique id.   */
  public java.lang.Integer getId() {
    return id;
  }

  /**
   * Sets the value of the 'id' field.
   * The unique id.   * @param value the value to set.
   */
  public void setId(java.lang.Integer value) {
    this.id = value;
  }

  /** Creates a new PersonKey RecordBuilder */
  public static com.damonallison.kafka.test.model.PersonKey.Builder newBuilder() {
    return new com.damonallison.kafka.test.model.PersonKey.Builder();
  }
  
  /** Creates a new PersonKey RecordBuilder by copying an existing Builder */
  public static com.damonallison.kafka.test.model.PersonKey.Builder newBuilder(com.damonallison.kafka.test.model.PersonKey.Builder other) {
    return new com.damonallison.kafka.test.model.PersonKey.Builder(other);
  }
  
  /** Creates a new PersonKey RecordBuilder by copying an existing PersonKey instance */
  public static com.damonallison.kafka.test.model.PersonKey.Builder newBuilder(com.damonallison.kafka.test.model.PersonKey other) {
    return new com.damonallison.kafka.test.model.PersonKey.Builder(other);
  }
  
  /**
   * RecordBuilder for PersonKey instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<PersonKey>
    implements org.apache.avro.data.RecordBuilder<PersonKey> {

    private int id;

    /** Creates a new Builder */
    private Builder() {
      super(com.damonallison.kafka.test.model.PersonKey.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(com.damonallison.kafka.test.model.PersonKey.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
    }
    
    /** Creates a Builder by copying an existing PersonKey instance */
    private Builder(com.damonallison.kafka.test.model.PersonKey other) {
            super(com.damonallison.kafka.test.model.PersonKey.SCHEMA$);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
    }

    /** Gets the value of the 'id' field */
    public java.lang.Integer getId() {
      return id;
    }
    
    /** Sets the value of the 'id' field */
    public com.damonallison.kafka.test.model.PersonKey.Builder setId(int value) {
      validate(fields()[0], value);
      this.id = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'id' field has been set */
    public boolean hasId() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'id' field */
    public com.damonallison.kafka.test.model.PersonKey.Builder clearId() {
      fieldSetFlags()[0] = false;
      return this;
    }

    @Override
    public PersonKey build() {
      try {
        PersonKey record = new PersonKey();
        record.id = fieldSetFlags()[0] ? this.id : (java.lang.Integer) defaultValue(fields()[0]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
