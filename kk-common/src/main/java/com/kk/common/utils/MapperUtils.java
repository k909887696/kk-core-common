package com.kk.common.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dozer.Mapper;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;


public class MapperUtils {

    @Resource
    private Mapper mapper;

    public <T> T map(Object source, Class<T> destinationClass) {
        if (source == null) {
            return null;
        }
        return mapper.map(source, destinationClass);
    }

    public <T> List<T> map(List source, Class<T> clz) {
        List target = new LinkedList();
        for (Object o : source) {
            Object to = mapper.map(o, clz);
            target.add(to);
        }
        return target;
    }

    public <T, V> Map<T, V> map(Map map, Class<T> keyClz, Class<V> valueClz) {
        Map<T, V> target = new HashMap<T, V>();
        Set<Map.Entry> entrySet = map.entrySet();
        for (Map.Entry entry : entrySet) {
            T key = mapper.map(entry.getKey(), keyClz);
            V value = mapper.map(entry.getValue(), valueClz);
            target.put(key, value);
        }
        return target;
    }

}
