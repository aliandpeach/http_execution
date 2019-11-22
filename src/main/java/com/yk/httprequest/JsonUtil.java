package com.yk.httprequest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.IntStream;

public class JsonUtil
{

    public static void main(String[] args)
    {
        List<TestUser> users = new ArrayList<>();
        Map<Integer, TestUser> mapUsers = new HashMap<>();

        Map<Integer, String> mapStrings = new HashMap<>();
        IntStream.range(0, 10).forEach((i) -> {
            users.add(new TestUser("name" + i, 20, "email" + i));

            mapUsers.put(i, new TestUser("name" + i, 20, "email" + i));

            mapStrings.put(i, i + "");
        });

        System.out.println(users);
        String json = toJson(users);
        String mapuser_json = toJson(mapUsers);
        Optional.of(json).ifPresent(System.out::println);
        CurParameterizedType type = new CurParameterizedType(List.class, new Type[]{TestUser.class});

        List<TestUser> _usersx = JsonUtil.fromJson(json, new TypeReference<List<TestUser>>()
        {
        });
        System.out.println(_usersx);

        List<TestUser> __users = JsonUtil.fromJson(json, new CurTypeReference2<List<TestUser>>());
        List<TestUser> _users = JsonUtil.fromJson(json, new CurTypeReference<List<TestUser>>(type));
        System.out.println(_users);


        CurParameterizedType _type = new CurParameterizedType(Map.class, new Type[]{Integer.class, TestUser.class});
        Map<Integer, TestUser> _mapUsers = JsonUtil.fromJson(mapuser_json, new CurTypeReference<Map<Integer, TestUser>>(_type));
        System.out.println(_mapUsers);

        Map<Integer, TestUser> __mapUsers = JsonUtil.fromJson(mapuser_json, new TypeReference<Map<Integer, TestUser>>()
        {
        });
        System.out.println(__mapUsers);

        CurParameterizedType __type = new CurParameterizedType(Map.class, new Type[]{Integer.class, String.class});
        Map<Integer, TestUser> _mapStrings = JsonUtil.fromJson(toJson(mapStrings), new CurTypeReference<Map<Integer, TestUser>>(__type));
        System.out.println(_mapStrings);


        TestUser user = new TestUser("mail", 21, "email");
        String j = toJson(user);
        TestUser t = fromJson(j, TestUser.class);
        TestUser t2 = fromJson(j, new TypeReference<TestUser>()
        {
        });
        System.out.println(t2);
    }

    public static <T> T fromJsonFilter(String json, Class<T> clazz, String... params)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();

            String[] beanProperties = params;
            String nonPasswordFilterName = "myFilter";//需要跟 TestUser类上的注解@JsonFilter("myFilter")里面的一致
            FilterProvider filterProvider = new SimpleFilterProvider()
                    .addFilter(nonPasswordFilterName, SimpleBeanPropertyFilter.serializeAllExcept(beanProperties));
            //serializeAllExcept 表示序列化全部，除了指定字段
            //filterOutAllExcept 表示过滤掉全部，除了指定的字段
            objectMapper.setFilterProvider(filterProvider);
            T result = objectMapper.readValue(json, clazz);
            return result;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJsonFilter(String json, TypeReference<T> type, String... params)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            String[] beanProperties = params;
            String nonPasswordFilterName = "myFilter";//需要跟TestUser类上的注解@JsonFilter("myFilter")里面的一致
            FilterProvider filterProvider = new SimpleFilterProvider()
                    .addFilter(nonPasswordFilterName, SimpleBeanPropertyFilter.serializeAllExcept(beanProperties));
            //serializeAllExcept 表示序列化全部，除了指定字段
            //filterOutAllExcept 表示过滤掉全部，除了指定的字段
            objectMapper.setFilterProvider(filterProvider);
            T result = objectMapper.readValue(json, type);
            return result;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String json, Class<T> clazz)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            T result = objectMapper.readValue(json, clazz);
            return result;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String json, TypeReference<T> type)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            T result = objectMapper.readValue(json, type);
            return result;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static String toJson(Object object)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static class CurParameterizedType implements ParameterizedType
    {
        private Class<?> clazz;

        private Type[] types;

        public CurParameterizedType(Class<?> clazz, Type[] types)
        {
            this.clazz = clazz;
            this.types = types;
        }

        public Type[] getActualTypeArguments()
        {
            return null == types ? new Type[0] : types;
        }

        public Type getRawType()
        {
            return clazz;
        }

        public Type getOwnerType()
        {
            return clazz;
        }
    }

    public static class CurTypeReference<T> extends TypeReference<T>
    {

        private CurParameterizedType curParameterizedType;

        public CurTypeReference(CurParameterizedType curParameterizedType)
        {
            super();
            this.curParameterizedType = curParameterizedType;
        }

        public Type getType()
        {
            return curParameterizedType;
        }
    }

    public static class CurTypeReference2<T> extends TypeReference<T>
    {

        public CurTypeReference2()
        {
            super();
        }
    }
}
