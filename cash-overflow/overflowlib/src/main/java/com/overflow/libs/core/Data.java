package com.overflow.libs.core;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Data implements Map<String, Object>, Serializable{
    private Map<String, Object> map = new LinkedHashMap<>();
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    static {
        jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        jsonMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        jsonMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        jsonMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US));
    }

    public Data() {
    }

    public Data(Object object) {
        this(writeTo(object));
    }

    @SuppressWarnings("unchecked")
    public Data(String other) {
        try {
            this.map = (Map<String, Object>) jsonMapper.readValue(other, HashMap.class);
        } catch (JsonParseException e) {
            Log.d("should json >>>", other);
            throw new CoreException("failed.to.parse.json", e);
        } catch (JsonMappingException e) {
            throw new CoreException("failed.to.mapping.json", e);
        } catch (IOException e) {
            throw new CoreException("failed.to.read.json.data", e);
        }
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public Object get(Object key) {
        return map.get(key);
    }

    /**
     * Method to get boolean value
     * @param key is the key of data
     * @return boolean value
     */
    public Boolean getBoolean(String key) {
        if(!map.containsKey(key))
            return false;
        return Boolean.valueOf(getString(key));
    }

    /**
     * Method to get long value
     * @param key is the key of data
     * @return long value
     */
    public Long getLong(String key) {
        return Long.valueOf(getString(key));
    }

    /**
     * Method to get int value
     * @param key is the key of data
     * @return int value
     */
    public Integer getInt(String key) {
        return Integer.valueOf(getString(key));
    }

    /**
     * Method to get short value
     * @param key is the key of data
     * @return short value
     */
    public Short getShort(String key) {
        return Short.valueOf(getString(key));
    }

    public Double getDouble(String key) {
        return Double.valueOf(getString(key));
    }

    public Float getFloat(String key) {
        return Float.valueOf(getString(key));
    }

    public BigInteger getBigInt(String key) {
        return BigInteger.valueOf(getLong(key));
    }

    public BigDecimal getBigDecimal(String key) {
        return BigDecimal.valueOf(getDouble(key));
    }

    public String getString(String key) {
        if(map.containsKey(key))
            return String.valueOf(map.get(key));
        return "";
    }

    public Data put(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public Object remove(Object key) {
        return map.remove(key);
    }

    public void putAll(@NonNull Map<? extends String, ?> m) {
        map.putAll(m);
    }

    public void clear() {
        map.clear();
    }
    @NonNull
    @Override
    public Set<String> keySet() {
        return map.keySet();
    }
    @NonNull
    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @NonNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return this.map.entrySet();
    }

    public List<Data> getList(String key) {
        try {
            String result = jsonMapper.writeValueAsString(this.map.get(key));
            return jsonMapper.readValue(result, new TypeReference<List<Data>>() {
            });
        } catch (JsonProcessingException e) {
            throw new CoreException("the.value.should.be.json.list." + key, e);
        } catch (IOException e) {
            throw new CoreException("failed.to.read.json.data", e);
        }
    }
    public List<Map<Object, Object>> getListAsMap(String key) {
        try {
            String result = jsonMapper.writeValueAsString(this.map.get(key));
            return jsonMapper.readValue(result, new TypeReference<List<Map<Object, Object>>>() {
            });
        } catch (JsonProcessingException e) {
            throw new CoreException("the.value.should.be.json.list." + key, e);
        } catch (IOException e) {
            throw new CoreException("failed.to.read.json.data", e);
        }
    }

    public List<String> getListString(String key) {
        try {
            String result = jsonMapper.writeValueAsString(this.map.get(key));
            return jsonMapper.readValue(result, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            throw new CoreException("the.value.should.be.json.list." + key, e);
        } catch (IOException e) {
            throw new CoreException("failed.to.read.json.data", e);
        }
    }

    public List<String> getListInt(String key) {
        try {
            String result = jsonMapper.writeValueAsString(this.map.get(key));
            return jsonMapper.readValue(result, new TypeReference<List<Integer>>() {});
        } catch (JsonProcessingException e) {
            throw new CoreException("the.value.should.be.json.list." + key, e);
        } catch (IOException e) {
            throw new CoreException("failed.to.read.json.data", e);
        }
    }
    public Set<Data> getSet(String key) {
        try {
            String result = jsonMapper.writeValueAsString(this.map.get(key));
            return jsonMapper.readValue(result, new TypeReference<Set<Data>>() {
            });
        } catch (JsonProcessingException e) {
            throw new CoreException("the.value.should.be.json.set." + key, e);
        } catch (IOException e) {
            throw new CoreException("failed.to.read.json.data", e);
        }
    }

    public Data getData(String key) {
        try {
            String result = jsonMapper.writeValueAsString(this.map.get(key));
            return jsonMapper.readValue(result.getBytes(), Data.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new CoreException("the.value.should.be.json.object." + key, e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CoreException("failed.to.read.json.data", e);
        }
    }

    public static Data convertKeyToCamelCase(Data input){
        Data result = new Data();
        for (Entry<String, Object> entry: input.entrySet()) {
            result.put(SnakeCase.convertToCamel(entry.getKey()), entry.getValue());
        }
        return result;
    }

    public <T> T convertTo(Class<T> classToSerialize) {
        try {
            return jsonMapper.readValue(toString(), classToSerialize);
        } catch (IOException e) {
            throw new CoreException("failed.to.convert.object.from.json", e);
        }
    }

    public static String writeTo(Object object) {
        try {
            return jsonMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new CoreException(e);
        }
    }

    public String toString() {
        try {
            return jsonMapper.writeValueAsString(this.map);
        } catch (JsonProcessingException e) {
            throw new CoreException(e);
        }
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public Bundle toBundle(){
        Bundle bundle = new Bundle();
        for (Entry<String, Object> entry : map.entrySet()){
            Object value = entry.getValue();
            String key = entry.getKey();
            if(value != null){
                if(value.getClass() == Integer.class || value.getClass() == int.class){
                    bundle.putLong(key, Long.valueOf(value.toString()));
                }else if(value.getClass() == Float.class || value.getClass() == float.class){
                    bundle.putFloat(key, Float.valueOf(value.toString()));

                }else if(value.getClass() == Short.class || value.getClass() == short.class){
                    bundle.putShort(key, Short.valueOf(value.toString()));
                }else if(value.getClass() == Double.class || value.getClass() == double.class){
                    bundle.putDouble(key, Double.valueOf(value.toString()));
                }
                else if(value.getClass() == Long.class || value.getClass() == long.class){
                    bundle.putLong(key, Long.valueOf(value.toString()));
                }else if(value.getClass() == Character.class || value.getClass() == char.class){
                    bundle.putChar(key, (char)value);
                }else if(value.getClass() == Boolean.class || value.getClass() == boolean.class){
                    bundle.putBoolean(key, Boolean.valueOf(value.toString()));
                }
                else {
                    bundle.putString(key, String.valueOf(value));
                }
            }
        }
        return bundle;
    }

    public static Data fromBundle(Bundle bundle){
        Data data = new Data();
        for (String key : bundle.keySet()){
            data.put(key, bundle.get(key));
        }
        return data;
    }
}
