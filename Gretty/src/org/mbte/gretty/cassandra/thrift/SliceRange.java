/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.mbte.gretty.cassandra.thrift;

import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;

import org.apache.thrift.*;
import org.apache.thrift.meta_data.*;
import org.apache.thrift.protocol.*;

/**
 * A slice range is a structure that stores basic range, ordering and limit information for a query that will return
 * multiple columns. It could be thought of as Cassandra's version of LIMIT and ORDER BY
 * 
 * @param start. The column name to start the slice with. This attribute is not required, though there is no default value,
 *               and can be safely set to '', i.e., an empty byte array, to start with the first column name. Otherwise, it
 *               must a valid value under the rules of the Comparator defined for the given ColumnFamily.
 * @param finish. The column name to stop the slice at. This attribute is not required, though there is no default value,
 *                and can be safely set to an empty byte array to not stop until 'count' results are seen. Otherwise, it
 *                must also be a valid value to the ColumnFamily Comparator.
 * @param reversed. Whether the results should be ordered in reversed order. Similar to ORDER BY blah DESC in SQL.
 * @param count. How many keys to return. Similar to LIMIT 100 in SQL. May be arbitrarily large, but Thrift will
 *               materialize the whole result into memory before returning it to the client, so be aware that you may
 *               be better served by iterating through slices by passing the last value of one call in as the 'start'
 *               of the next instead of increasing 'count' arbitrarily large.
 */
public class SliceRange implements TBase<SliceRange, SliceRange._Fields>, java.io.Serializable, Cloneable {
  private static final TStruct STRUCT_DESC = new TStruct("SliceRange");

  private static final TField START_FIELD_DESC = new TField("start", TType.STRING, (short)1);
  private static final TField FINISH_FIELD_DESC = new TField("finish", TType.STRING, (short)2);
  private static final TField REVERSED_FIELD_DESC = new TField("reversed", TType.BOOL, (short)3);
  private static final TField COUNT_FIELD_DESC = new TField("count", TType.I32, (short)4);

  public ByteBuffer start;
  public ByteBuffer finish;
  public boolean reversed;
  public int count;

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements TFieldIdEnum {
    START((short)1, "start"),
    FINISH((short)2, "finish"),
    REVERSED((short)3, "reversed"),
    COUNT((short)4, "count");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // START
          return START;
        case 2: // FINISH
          return FINISH;
        case 3: // REVERSED
          return REVERSED;
        case 4: // COUNT
          return COUNT;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __REVERSED_ISSET_ID = 0;
  private static final int __COUNT_ISSET_ID = 1;
  private BitSet __isset_bit_vector = new BitSet(2);

  public static final Map<_Fields, FieldMetaData> metaDataMap;
  static {
    Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.START, new FieldMetaData("start", TFieldRequirementType.REQUIRED, 
        new FieldValueMetaData(TType.STRING)));
    tmpMap.put(_Fields.FINISH, new FieldMetaData("finish", TFieldRequirementType.REQUIRED, 
        new FieldValueMetaData(TType.STRING)));
    tmpMap.put(_Fields.REVERSED, new FieldMetaData("reversed", TFieldRequirementType.REQUIRED, 
        new FieldValueMetaData(TType.BOOL)));
    tmpMap.put(_Fields.COUNT, new FieldMetaData("count", TFieldRequirementType.REQUIRED, 
        new FieldValueMetaData(TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    FieldMetaData.addStructMetaDataMap(SliceRange.class, metaDataMap);
  }

  public SliceRange() {
    this.reversed = false;

    this.count = 100;

  }

  public SliceRange(
    ByteBuffer start,
    ByteBuffer finish,
    boolean reversed,
    int count)
  {
    this();
    this.start = start;
    this.finish = finish;
    this.reversed = reversed;
    setReversedIsSet(true);
    this.count = count;
    setCountIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public SliceRange(SliceRange other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetStart()) {
      this.start = ByteBuffer.wrap(new byte[other.start.limit() - other.start.arrayOffset()]);
      System.arraycopy(other.start.array(), other.start.arrayOffset(), start.array(), 0, other.start.limit() - other.start.arrayOffset());
    }
    if (other.isSetFinish()) {
      this.finish = ByteBuffer.wrap(new byte[other.finish.limit() - other.finish.arrayOffset()]);
      System.arraycopy(other.finish.array(), other.finish.arrayOffset(), finish.array(), 0, other.finish.limit() - other.finish.arrayOffset());
    }
    this.reversed = other.reversed;
    this.count = other.count;
  }

  public SliceRange deepCopy() {
    return new SliceRange(this);
  }

  @Deprecated
  public SliceRange clone() {
    return new SliceRange(this);
  }

  @Override
  public void clear() {
    this.start = null;
    this.finish = null;
    this.reversed = false;

    this.count = 100;

  }

  public ByteBuffer getStart() {
    return this.start;
  }

  public SliceRange setStart(ByteBuffer start) {
    this.start = start;
    return this;
  }

  public void unsetStart() {
    this.start = null;
  }

  /** Returns true if field start is set (has been asigned a value) and false otherwise */
  public boolean isSetStart() {
    return this.start != null;
  }

  public void setStartIsSet(boolean value) {
    if (!value) {
      this.start = null;
    }
  }

  public ByteBuffer getFinish() {
    return this.finish;
  }

  public SliceRange setFinish(ByteBuffer finish) {
    this.finish = finish;
    return this;
  }

  public void unsetFinish() {
    this.finish = null;
  }

  /** Returns true if field finish is set (has been asigned a value) and false otherwise */
  public boolean isSetFinish() {
    return this.finish != null;
  }

  public void setFinishIsSet(boolean value) {
    if (!value) {
      this.finish = null;
    }
  }

  public boolean isReversed() {
    return this.reversed;
  }

  public SliceRange setReversed(boolean reversed) {
    this.reversed = reversed;
    setReversedIsSet(true);
    return this;
  }

  public void unsetReversed() {
    __isset_bit_vector.clear(__REVERSED_ISSET_ID);
  }

  /** Returns true if field reversed is set (has been asigned a value) and false otherwise */
  public boolean isSetReversed() {
    return __isset_bit_vector.get(__REVERSED_ISSET_ID);
  }

  public void setReversedIsSet(boolean value) {
    __isset_bit_vector.set(__REVERSED_ISSET_ID, value);
  }

  public int getCount() {
    return this.count;
  }

  public SliceRange setCount(int count) {
    this.count = count;
    setCountIsSet(true);
    return this;
  }

  public void unsetCount() {
    __isset_bit_vector.clear(__COUNT_ISSET_ID);
  }

  /** Returns true if field count is set (has been asigned a value) and false otherwise */
  public boolean isSetCount() {
    return __isset_bit_vector.get(__COUNT_ISSET_ID);
  }

  public void setCountIsSet(boolean value) {
    __isset_bit_vector.set(__COUNT_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case START:
      if (value == null) {
        unsetStart();
      } else {
        setStart((ByteBuffer)value);
      }
      break;

    case FINISH:
      if (value == null) {
        unsetFinish();
      } else {
        setFinish((ByteBuffer)value);
      }
      break;

    case REVERSED:
      if (value == null) {
        unsetReversed();
      } else {
        setReversed((Boolean)value);
      }
      break;

    case COUNT:
      if (value == null) {
        unsetCount();
      } else {
        setCount((Integer)value);
      }
      break;

    }
  }

  public void setFieldValue(int fieldID, Object value) {
    setFieldValue(_Fields.findByThriftIdOrThrow(fieldID), value);
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case START:
      return getStart();

    case FINISH:
      return getFinish();

    case REVERSED:
      return new Boolean(isReversed());

    case COUNT:
      return new Integer(getCount());

    }
    throw new IllegalStateException();
  }

  public Object getFieldValue(int fieldId) {
    return getFieldValue(_Fields.findByThriftIdOrThrow(fieldId));
  }

  /** Returns true if field corresponding to fieldID is set (has been asigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    switch (field) {
    case START:
      return isSetStart();
    case FINISH:
      return isSetFinish();
    case REVERSED:
      return isSetReversed();
    case COUNT:
      return isSetCount();
    }
    throw new IllegalStateException();
  }

  public boolean isSet(int fieldID) {
    return isSet(_Fields.findByThriftIdOrThrow(fieldID));
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof SliceRange)
      return this.equals((SliceRange)that);
    return false;
  }

  public boolean equals(SliceRange that) {
    if (that == null)
      return false;

    boolean this_present_start = true && this.isSetStart();
    boolean that_present_start = true && that.isSetStart();
    if (this_present_start || that_present_start) {
      if (!(this_present_start && that_present_start))
        return false;
      if (!this.start.equals(that.start))
        return false;
    }

    boolean this_present_finish = true && this.isSetFinish();
    boolean that_present_finish = true && that.isSetFinish();
    if (this_present_finish || that_present_finish) {
      if (!(this_present_finish && that_present_finish))
        return false;
      if (!this.finish.equals(that.finish))
        return false;
    }

    boolean this_present_reversed = true;
    boolean that_present_reversed = true;
    if (this_present_reversed || that_present_reversed) {
      if (!(this_present_reversed && that_present_reversed))
        return false;
      if (this.reversed != that.reversed)
        return false;
    }

    boolean this_present_count = true;
    boolean that_present_count = true;
    if (this_present_count || that_present_count) {
      if (!(this_present_count && that_present_count))
        return false;
      if (this.count != that.count)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(SliceRange other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    SliceRange typedOther = (SliceRange)other;

    lastComparison = Boolean.valueOf(isSetStart()).compareTo(typedOther.isSetStart());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStart()) {      lastComparison = TBaseHelper.compareTo(this.start, typedOther.start);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetFinish()).compareTo(typedOther.isSetFinish());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFinish()) {      lastComparison = TBaseHelper.compareTo(this.finish, typedOther.finish);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetReversed()).compareTo(typedOther.isSetReversed());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetReversed()) {      lastComparison = TBaseHelper.compareTo(this.reversed, typedOther.reversed);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetCount()).compareTo(typedOther.isSetCount());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCount()) {      lastComparison = TBaseHelper.compareTo(this.count, typedOther.count);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public void read(TProtocol iprot) throws TException {
    TField field;
    iprot.readStructBegin();
    while (true)
    {
      field = iprot.readFieldBegin();
      if (field.type == TType.STOP) { 
        break;
      }
      switch (field.id) {
        case 1: // START
          if (field.type == TType.STRING) {
            this.start = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2: // FINISH
          if (field.type == TType.STRING) {
            this.finish = iprot.readBinary();
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 3: // REVERSED
          if (field.type == TType.BOOL) {
            this.reversed = iprot.readBool();
            setReversedIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 4: // COUNT
          if (field.type == TType.I32) {
            this.count = iprot.readI32();
            setCountIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        default:
          TProtocolUtil.skip(iprot, field.type);
      }
      iprot.readFieldEnd();
    }
    iprot.readStructEnd();

    // check for required fields of primitive type, which can't be checked in the validate method
    if (!isSetReversed()) {
      throw new TProtocolException("Required field 'reversed' was not found in serialized data! Struct: " + toString());
    }
    if (!isSetCount()) {
      throw new TProtocolException("Required field 'count' was not found in serialized data! Struct: " + toString());
    }
    validate();
  }

  public void write(TProtocol oprot) throws TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    if (this.start != null) {
      oprot.writeFieldBegin(START_FIELD_DESC);
      oprot.writeBinary(this.start);
      oprot.writeFieldEnd();
    }
    if (this.finish != null) {
      oprot.writeFieldBegin(FINISH_FIELD_DESC);
      oprot.writeBinary(this.finish);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(REVERSED_FIELD_DESC);
    oprot.writeBool(this.reversed);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(COUNT_FIELD_DESC);
    oprot.writeI32(this.count);
    oprot.writeFieldEnd();
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("SliceRange(");
    boolean first = true;

    sb.append("start:");
    if (this.start == null) {
      sb.append("null");
    } else {
      TBaseHelper.toString(this.start, sb);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("finish:");
    if (this.finish == null) {
      sb.append("null");
    } else {
      TBaseHelper.toString(this.finish, sb);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("reversed:");
    sb.append(this.reversed);
    first = false;
    if (!first) sb.append(", ");
    sb.append("count:");
    sb.append(this.count);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
    if (start == null) {
      throw new TProtocolException("Required field 'start' was not present! Struct: " + toString());
    }
    if (finish == null) {
      throw new TProtocolException("Required field 'finish' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'reversed' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'count' because it's a primitive and you chose the non-beans generator.
  }

}

