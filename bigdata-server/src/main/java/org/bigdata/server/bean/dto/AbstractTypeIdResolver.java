package org.bigdata.server.bean.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.io.IOException;

public abstract class AbstractTypeIdResolver<T> extends TypeIdResolverBase {
    protected final BiMap<T, Class<?>> subTypes = HashBiMap.create();

    protected JavaType javaType;

    protected Class<?> defaultClass;

    protected void bind(T type, Class<?> subClass) {
        this.subTypes.put(type, subClass);
    }

    protected void bindDefault(Class<?> defaultClass) {
        this.defaultClass = defaultClass;
    }

    @Override
    public void init(JavaType baseType) {
        this.javaType = baseType;
    }

    @Override
    public String idFromValue(Object value) {
        return typeFromSubtype(value);
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return typeFromSubtype(value);
    }

    protected abstract String typeFromSubtype(Object obj);

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        Class<?> subType = this.subTypeFromType(id);
        return context.constructSpecializedType(this.javaType, subType);
    }

    protected abstract Class<?> subTypeFromType(String id);

    @Override
    public Id getMechanism() {
        return Id.NAME;
    }
}
