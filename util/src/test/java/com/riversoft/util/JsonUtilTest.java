/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.util;

import com.riversoft.util.jackson.JsonMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Borball
 * 
 */
public class JsonUtilTest {

    private JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void testPrimitive() {
        System.out.println(jsonMapper.toJson(1));
        System.out.println(jsonMapper.toJson(1));
        System.out.println(jsonMapper.toJson(1));
        System.out.println(jsonMapper.toJson(1));
        System.out.println(jsonMapper.toJson(1));

    }

    @Test
     public void testToJsonFromSimplePOJO() {
        POJO pojo = new POJO();
        pojo.setId(1);
        pojo.setName(null);
        pojo.setTime(new Date());
        pojo.setNumber(1000.00f);
        pojo.setBool(false);
        pojo.setCalendar(Calendar.getInstance());

        System.out.println(jsonMapper.toJson(pojo));

        pojo = new POJO();
        pojo.setId(1);
        pojo.setName("name");
        pojo.setTime(new Date());
        pojo.setNumber(2000.00f);
        pojo.setBool(true);
        pojo.setCalendar(Calendar.getInstance());

        System.out.println(jsonMapper.toJson(pojo));

    }

    @Test
    public void testFromJsonToSimplePOJO() {
        String jsonString = "{\"id\":1,\"name\":null,\"time\":\"2015-04-14 21:06:01\",\"calendar\":\"2015-04-14 21:06:01\",\"number\":1000.0,\"bool\":false}";

        POJO pojo = jsonMapper.fromJson(jsonString, POJO.class);

        Assert.assertEquals(1, pojo.getId());
        Assert.assertNull(pojo.getName());
        Assert.assertEquals("2015-04-14 21:06:01", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(pojo.getTime()));
        Assert.assertEquals(1000, pojo.getNumber(), 0);
        Assert.assertFalse(pojo.isBool());
        Assert.assertEquals("2015-04-14 21:06:01", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(pojo.getCalendar().getTime()));
    }

    @Test
    public void testNull() {
        POJO pojo1 = new POJO();
        pojo1.setId(1);
        pojo1.setName(null);
        pojo1.setTime(new Date());
        pojo1.setNumber(1000.00f);

        System.out.println(jsonMapper.toJson(pojo1));

        String jsonString = "{\"id\":1,\"name\":null,\"time\":\"2015-04-14 19:35:25\",\"number\":1000.0}";
        POJO pojo2 = jsonMapper.fromJson(jsonString, POJO.class);
        Assert.assertEquals(1, pojo2.getId());
        Assert.assertNull(pojo2.getName());
    }

    @Test
    public void testToJsonFromSimpleList() {
        List list = new ArrayList<>();
        list.add("hello");
        list.add(" ");
        list.add("world");
        list.add("!");

        System.out.println(jsonMapper.toJson(list));

        Map<String, Integer> map = new HashMap<>();
        map.put("James", 10);
        map.put("Tom", 20);
        map.put("Jim", -10);

        System.out.println(jsonMapper.toJson(map));
    }

    @Test
    public void testFromJsonToSimpleList() throws IOException {
        String jsonString = "[{\"id\":1,\"name\":null,\"time\":\"2015-04-14 21:23:46\",\"calendar\":null,\"number\":1000.0,\"bool\":false}," +
                "{\"id\":2,\"name\":\"James\",\"time\":\"2015-04-14 21:23:46\",\"calendar\":null,\"number\":2000.0,\"bool\":false}]";

        List<POJO> listJackson = jsonMapper.fromJsons(jsonString, POJO.class);

        Assert.assertEquals(2, listJackson.size());

        POJO pojo1 = listJackson.get(0);
        Assert.assertEquals(1, pojo1.getId());
        Assert.assertNull(pojo1.getName());
        Assert.assertEquals("2015-04-14 21:23:46", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(pojo1.getTime()));
        Assert.assertEquals(1000, pojo1.getNumber(), 0);
        Assert.assertFalse(pojo1.isBool());
        Assert.assertNull(pojo1.getCalendar());

    }


    @Test
    public void testFromJsonToJsonObject() throws IOException {
        POJO pojo1 = new POJO();
        pojo1.setId(1);
        pojo1.setName("name");
        pojo1.setTime(new Date());
        pojo1.setNumber(1000.00f);
        pojo1.setCalendar(Calendar.getInstance());

        Map<String, Object> o3 = jsonMapper.convert(pojo1, Map.class);

        Set<String> keys = o3.keySet();
        for (String key: keys) {
            System.out.println(key + "->" + o3.get(key).getClass().getName());
        }

    }

    @Test
    public void testComplexCollections(){
        List<POJO> lists = new ArrayList<>();

        POJO pojo1 = new POJO();
        pojo1.setId(1);
        pojo1.setName(null);
        pojo1.setTime(new Date());
        pojo1.setNumber(1000.00f);

        POJO pojo2 = new POJO();
        pojo2.setId(2);
        pojo2.setName("James");
        pojo2.setTime(new Date());
        pojo2.setNumber(2000.00f);

        lists.add(pojo1);
        lists.add(pojo2);

        System.out.println(jsonMapper.toJson(lists));
    }

    public static class POJO{

        private int id;
        private String name;
        private Date time;
        private Calendar calendar;
        private float number;
        private boolean bool;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }

        public float getNumber() {
            return number;
        }

        public void setNumber(float number) {
            this.number = number;
        }

        public boolean isBool() {
            return bool;
        }

        public void setBool(boolean bool) {
            this.bool = bool;
        }

        public Calendar getCalendar() {
            return calendar;
        }

        public void setCalendar(Calendar calendar) {
            this.calendar = calendar;
        }
    }


}
