package com.neure.agent.utils;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * JacksonUtils
 *
 * @author tc
 * @date 2022-07-25 21:19
 */
@Slf4j
public class JacksonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    //动态过滤属性相关
    static final String DYNC_INCLUDE = "DYNC_INCLUDE";
    static final String DYNC_FILTER = "DYNC_FILTER";

    @JsonFilter(DYNC_FILTER)
    interface DynamicFilter {

    }

    @JsonFilter(DYNC_INCLUDE)
    interface DynamicInclude {

    }

    public static ObjectMapper getNewObject() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper;
    }

    static {
        //序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);//属性为 空（""） 或者为 NULL 都不序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);//属性为默认值不序列化
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);//不使用默认的日期格式。默认为true显示的时间戳
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);//空对象不抛出异常
        // 字段保留，序列化时将null值转为""
        //        mapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>()
        //        {
        //            @Override
        //            public void serialize(Object o, JsonGenerator jsonGenerator,
        //                                  SerializerProvider serializerProvider)
        //                    throws IOException
        //            {
        //                jsonGenerator.writeString("");
        //            }
        //        });
        //反序列化时
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);//允许出现特殊字符和转义符、
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);//允许出现单引号
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//遇到未知属性不抛出JsonMappingException
    }

    public static <T> String ObjectToJsonStr(T t) {
        if (t == null) {
            return "";
        }

        try {
//            String str = StringUtils.replace(objectMapper.writeValueAsString(t), " ", "");
//            return StringUtils.replace(str, "\\n", "");
            return objectMapper.writeValueAsString(t);
        } catch (Exception e) {
            log.error("Failed cast object to json due to {}", e);
        }
        return "";
    }

    /**
     * 字符串转对象
     *
     * @param jsonString
     * @return
     * @throws JsonProcessingException
     */
    public static <T> T StrToObject(String jsonString, Class<T> classType) throws JsonProcessingException {

        return objectMapper.readValue(jsonString, classType);
    }

    public static <T> T StrToObject(String jsonString,TypeReference<T> valueTypeRef) throws JsonProcessingException {
        return objectMapper.readValue(jsonString,valueTypeRef);
    }


    public static <T> List<T> StrToObjectList(String jsonString, Class<T> classType) throws JsonProcessingException {

        return objectMapper.readValue(jsonString, new TypeReference<List<T>>() {
        });
    }

    /**
     * 字符串转jsonNode
     *
     * @param jsonString
     * @return
     * @throws JsonProcessingException
     */
    public static JsonNode StrToJsonNode(String jsonString) throws JsonProcessingException {

        return objectMapper.readTree(jsonString);
    }

    /**
     * JsonNode to Object
     *
     * @param jsonNode
     * @param classType
     * @param <T>
     * @return
     * @throws JsonProcessingException
     */
    public static <T> T NodeToObject(JsonNode jsonNode, Class<T> classType) throws JsonProcessingException {
        if (jsonNode == null || classType == null) {
            return null;
        }
        return objectMapper.treeToValue(jsonNode, classType);
    }

    /**
     * 对象转jsonNode
     *
     * @param object
     * @param <T>
     * @return
     */
    public static <T> JsonNode ObjToJsonNode(T object) {
        if (object == null) {
            return objectMapper.createObjectNode();
        }
        return objectMapper.valueToTree(object);
    }

    /**
     * 动态过滤属性的方法
     *
     * @param clazz   需要设置规则的class
     * @param include 转换时包含哪些字段
     * @param filter  转换时过滤那些字段
     */
    public static void filter(Class<?> clazz, String include, String filter) {
        if (clazz == null) { return; }
        if (include != null && include.length() > 0) {
            objectMapper.setFilterProvider(new SimpleFilterProvider().addFilter(DYNC_INCLUDE,
                    SimpleBeanPropertyFilter.filterOutAllExcept(include.split(","))));
            objectMapper.addMixIn(clazz, DynamicInclude.class);
        } else if (filter != null && filter.length() > 0) {
            objectMapper.setFilterProvider(new SimpleFilterProvider().addFilter(DYNC_FILTER,
                    SimpleBeanPropertyFilter.serializeAllExcept(filter.split(","))));
            objectMapper.addMixIn(clazz, DynamicFilter.class);
        }
    }
}
