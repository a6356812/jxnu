package org.jxnu.stu.util;

import com.alibaba.druid.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;

@Slf4j
public class JsonHelper {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //把序列化对象的所有字段全部注入
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);

        //取消默认转换timestamps格式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);

        //忽略空bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);

        //忽略在json字符串中存在，但在Java对象中不存在对应属性的情况。防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        //所有日期格式统一为 yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeHelper.STANDARD_PATTERN));
    }

    public static <T> String obj2string(T obj){
        if(obj == null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj : objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("Parse Object to String error",e);
            return null;
        }
    }

    public static <T> String obj2stringPretty(T obj){
        if(obj == null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("Parse Object to String error",e);
            return null;
        }
    }

    public static <T> T string2obj(String str,Class<T> clazz){
        if(StringUtils.isEmpty(str) || clazz == null){
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T)str : objectMapper.readValue(str,clazz);
        } catch (IOException e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

    //以上三个都是简单对象的转换，如若碰见Map<K,V>类型的则方显力不从心
    //现在开始编写复杂集合对象的反序列化方法

    /**
     *
     * @param str
     * @param typeReference 例如 new TypeReference<List<User>>()
     * @param <T> 返回类型
     * @return
     */
    public static <T> T string2obj(String str, TypeReference<T> typeReference){
        if(StringUtils.isEmpty(str) || typeReference == null){
            return null;
        }
        try {
            return typeReference.getType().equals(String.class) ? (T)str : objectMapper.readValue(str,typeReference);
        } catch (IOException e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

    /**
     *
     * @param str   序列化字符串
     * @param collectionsType 返回集合的类型
     * @param elements  集合形参类型
     * @param <T>
     * @return
     */
    public static <T> T string2obj(String str, Class<?> collectionsType, Class<?>... elements){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionsType,elements);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (IOException e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }


}
