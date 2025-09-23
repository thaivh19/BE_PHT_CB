package com.pht.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.jayway.jsonpath.JsonPath;
import com.pht.common.constants.CommonConstants;
import com.pht.common.model.TreeData;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings({"unchecked"})
public class ObjectUtils {

    private static final ObjectMapper OM = getObjectMapper();

    /**
     * Default method to get ObjectMapper for global configuration<br/>
     * Set setVisibility = false to prevent: <b>*** java.lang.instrument ASSERTION FAILED ***</b> (JobRunr)
     */
    public static ObjectMapper getObjectMapper() {
        return getObjectMapper(true, false);
    }

    public static ObjectMapper getObjectMapper(boolean addJavaTimeModule, boolean setVisibility) {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
                .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
                .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
                .configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .build();

        if (setVisibility) {
            mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                    .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.DEFAULT)
                    .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        }

        // format datetime
        if (addJavaTimeModule) {
            JavaTimeModule javaTimeModule = getJavaTimeModule(CommonConstants.DateTimePattern.FORMAT_DATE_MONTH_YEAR, CommonConstants.DateTimePattern.FORMAT_24H);
            mapper.registerModule(javaTimeModule);
        }

        mapper.registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setDateFormat(new SimpleDateFormat(CommonConstants.DateTimePattern.FORMAT_24H))
                .setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));

        return mapper;
    }

    public static JavaTimeModule getJavaTimeModule(String localDateFormat, String localDateTimeFormat) {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(localDateFormat)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(localDateFormat)));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(localDateTimeFormat)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(localDateTimeFormat)));

        return javaTimeModule;
    }

    public static String toJson(Object object) {
        if (object == null) {
            return "";
        }

        try {
            return OM.writeValueAsString(object);
        } catch (Exception e) {
            return "";
        }
    }

    public static String toJson(ObjectMapper objectMapper, Object object) {
        if (objectMapper == null || object == null) {
            return "";
        }

        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return "";
        }
    }

    public static Map<String, Object> toMap(Object object) {
        if (object == null) {
            return null;
        }

        try {
            return OM.convertValue(object, new TypeReference<>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            return OM.readValue(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T toObject(String json, TypeReference<T> type) {
        try {
            return OM.readValue(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T convertObject(Object fromObject, Class<T> toClass) {
        try {
            return OM.convertValue(fromObject, toClass);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T convertObject(Object fromObject, TypeReference<T> toType) {
        try {
            return OM.convertValue(fromObject, toType);
        } catch (Exception e) {
            return null;
        }
    }

    public static String writeValueAsString(Object value) {
        try {
            return OM.writeValueAsString(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isValidJSON(String json) {
        try {
            OM.readTree(json);
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    /**
     * Deep copy
     */
    public static <T extends Serializable> T clone(T object) {
        return SerializationUtils.clone(object);
    }

    /**
     * Copy properties
     */
    public static void copyProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target);
    }

    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    // then use Spring BeanUtils to copy and ignore null using our function
    public static void myCopyProperties(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    public static String jsonArrayToString(String jsonArray, String delimiter) {
        try {
            List<Object> strings = OM.readValue(jsonArray, List.class);
            return strings.stream().map(String::valueOf).collect(Collectors.joining(delimiter));
        } catch (Exception e) {
            return jsonArray;
        }
    }

    public static String jsonArrayToString(List<?> jsonArray, String delimiter) {
        if (!ValidationUtils.isNullOrEmpty(jsonArray)) {
            return jsonArray.stream().map(String::valueOf).collect(Collectors.joining(delimiter));
        }

        return jsonArray != null ? jsonArray.toString() : "";
    }

    public static String minifyJson(String json) {
        String regex = "((?<![\\w}])\\s(?=[\\s\":]*))|(\\s+(?=\"))";
        try {
            if (!ValidationUtils.isNullOrEmpty(json)) {
                Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.UNICODE_CHARACTER_CLASS);
                Matcher matcher = pattern.matcher(json);
                return matcher.replaceAll("");
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return json;
    }

    public static String handleNullableJson(String data, String handleValue) {
        return ValidationUtils.isNullOrEmpty(data) ? handleValue : data;
    }

    public static String handleNullableJsonToEmtpyObject(String data) {
        return handleNullableJson(data, "{}");
    }

    public static String handleInvalidJson(String data) {
        return isValidJSON(data) ? data : writeValueAsString(data);
    }

    public static boolean hasNullOrEmptyFields(Object obj) {
        if (obj == null) {
            return true;
        }

        return Arrays.stream(obj.getClass().getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .map(field -> {
                    try {
                        return field.get(obj);
                    } catch (IllegalAccessException e) {
                        log.debug(e.getMessage(), e);
                        return null;
                    }
                })
                .anyMatch(value -> value == null || (value instanceof String && ((String) value).trim().isEmpty()));
    }

    /**
     * Parse a flat list to list tree data.
     */
    public static <T extends TreeData<ID>, ID> List<T> toTree(List<T> flatList) {
        List<T> treeList = new ArrayList<>();
        if (flatList != null) {
            Map<ID, T> dataMap = flatList.stream().collect(Collectors.toMap(TreeData::getNodeId, Function.identity()));

            flatList.forEach(node -> {
                ID nodeId = node.getNodeId();
                dataMap.put(nodeId, node);

                if (node.isRoot()) {
                    treeList.add(node);
                } else {
                    T parentNode = dataMap.get(node.getParentNodeId());
                    if (parentNode != null) {
                        parentNode.addChild(node);
                    }
                }
            });
        }

        return treeList;
    }

    /**
     * Get value of object by field name path
     *
     * @param object
     * @param path   ex: "$.addressList[0].province.code", "$.addressList[*].province.name", "$.birthDate.year"
     * @return
     * @throws JsonProcessingException
     * @Ex: getValueByPath(student, " $.birthDate.year ")
     */
    public static String getValueByPath(Object object, String path) throws JsonProcessingException {
        String json = OM.writeValueAsString(object);
        return JsonPath.parse(json).read(path, Object.class).toString();
    }

    public static String getValueByPath(String jsonString, String path) {
        return JsonPath.parse(jsonString).read(path, Object.class).toString();
    }

    /**
     * Get value of object by field name path with Object type defined
     *
     * @param object
     * @param path
     * @param clazz
     * @param <T>
     * @return
     * @throws JsonProcessingException
     * @Ex getValueByPath(student, " $.addressList[0].province.code ", String.class)
     * getValueByPath(student, "$.name", String.class)
     * getValueByPath(student, "$.birthDate", Object.class)
     * getValueByPath(student, "$.birthDate.year", String.class)
     * getValueByPath(student, "$.addressList[*].province.code", List.class)
     */
    public static <T> T getValueByPath(Object object, String path, Class<T> clazz) throws JsonProcessingException {
        String json = OM.writeValueAsString(object);
        return JsonPath.parse(json).read(path, clazz);
    }

    public static <T> T getValueByPath(String jsonString, String path, Class<T> clazz) {
        return JsonPath.parse(jsonString).read(path, clazz);
    }
}
