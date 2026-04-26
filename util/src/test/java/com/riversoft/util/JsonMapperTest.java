package com.riversoft.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.riversoft.util.jackson.JsonMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

/**
 * Created by exizhai on 4/16/2015.
 */
public class JsonMapperTest {

    private JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void testSingleQuote(){
        String json = "{'ID':1}";
        HashMap<String, Object> map = JsonMapper.defaultMapper().json2Map(json);
        Assert.assertTrue(map.size() == 1);
    }

    @Test
    public void fromPojo2Json(){
        JsonPOJO pojo = createPojo(1, "name", false, 1000f);
        String json = jsonMapper.toJson(pojo);
        System.out.println(json);
        Assert.assertTrue(json.contains("\"name\":\"name\","));
    }

    @Test
    public void fromJson2Pojo(){
        String jsonString = "{\"id\":1,\"name\":\"name\",\"time\":\"2015-04-16 14:33:57\"," +
                "\"calendar\":\"2015-04-16 14:33:57\",\"number\":1000.0,\"bool\":false}";
        JsonPOJO pojo = jsonMapper.fromJson(jsonString, JsonPOJO.class);
        Assert.assertEquals("name", pojo.getName());
        Assert.assertFalse(pojo.isBool());
    }

    @Test
    public void fromList2Json(){
        List<JsonPOJO> list = new ArrayList<>();
        JsonPOJO pojo1 = createPojo(1, "tom", false, 1000f);
        JsonPOJO pojo2 = createPojo(2, "kim", true, 2000f);
        list.add(pojo1);
        list.add(pojo2);

        String json = jsonMapper.toJson(list);
        System.out.println(json);
        Assert.assertTrue(json.contains("},{"));
    }

    @Test
    public void fromJson2UndefinedTypeList(){
        String json = "[{\"id\":1,\"name\":\"tom\",\"time\":\"2015-04-16 16:00:44\"," +
                "\"calendar\":\"2015-04-16 16:00:44\",\"number\":1000.0,\"bool\":false}," +
                "{\"id\":2,\"name\":\"kim\",\"time\":\"2015-04-16 16:00:44\"," +
                "\"calendar\":\"2015-04-16 16:00:44\",\"number\":2000.0,\"bool\":true}]\n";

        List<Map> list = jsonMapper.fromJson(json, List.class);
        Assert.assertEquals(2, list.size());

        Map map = list.get(0);
        Assert.assertTrue(map.containsKey("time"));
        System.out.println("    time->" + map.get("time").getClass().getName());
        System.out.println("    name->" + map.get("name").getClass().getName());
        System.out.println("      id->" + map.get("id").getClass().getName());
        System.out.println("    bool->" + map.get("bool").getClass().getName());
        System.out.println("calendar->" + map.get("calendar").getClass().getName());
        System.out.println("  number->" + map.get("number").getClass().getName());
    }

    @Test
    public void fromJson2DefinedTypeList(){
        String json = "[{\"id\":1,\"name\":\"tom\",\"time\":\"2015-04-16 16:00:44\"," +
                "\"calendar\":\"2015-04-16 16:00:44\",\"number\":1000.0,\"bool\":false}," +
                "{\"id\":2,\"name\":\"kim\",\"time\":\"2015-04-16 16:00:44\"," +
                "\"calendar\":\"2015-04-16 16:00:44\",\"number\":2000.0,\"bool\":true}]\n";

        List<JsonPOJO> list = null;
        try {
            list = jsonMapper.getMapper().readValue(json, new TypeReference<List<JsonPOJO>>() {
            });
            Assert.assertEquals(2, list.size());

            JsonPOJO pojo1 = list.get(0);
            Assert.assertEquals("tom", pojo1.getName());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void fromJson2List(){
        String json = "[{\"id\":1,\"name\":\"tom\",\"time\":\"2015-04-16 16:00:44\"," +
                "\"calendar\":\"2015-04-16 16:00:44\",\"number\":1000.0,\"bool\":false}," +
                "{\"id\":2,\"name\":\"kim\",\"time\":\"2015-04-16 16:00:44\"," +
                "\"calendar\":\"2015-04-16 16:00:44\",\"number\":2000.0,\"bool\":true}]\n";

        List<JsonPOJO> list = null;
        try {
            list = jsonMapper.fromJsons(json, JsonPOJO.class);
            Assert.assertEquals(2, list.size());

            JsonPOJO pojo1 = list.get(0);
            Assert.assertEquals("tom", pojo1.getName());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

        json = "{\"id\":1,\"name\":\"tom\",\"time\":\"2015-04-16 14:33:57\"," +
                "\"calendar\":\"2015-04-16 14:33:57\",\"number\":1000.0,\"bool\":false}";;
        try {
            list = jsonMapper.fromJsons(json, JsonPOJO.class);
            Assert.assertEquals(1, list.size());

            JsonPOJO pojo1 = list.get(0);
            Assert.assertEquals("tom", pojo1.getName());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void fromMap2Json(){
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("name", "tom");
        map.put("time", new Date());
        map.put("calendar", Calendar.getInstance());
        map.put("number", 1000f);
        map.put("bool", true);

        String json = jsonMapper.toJson(map);
        System.out.println(json);
        Assert.assertTrue(json.contains("\"name\":\"tom\""));
    }

    @Test
     public void fromJson2Map(){
        String json = "{\"id\":1,\"time\":\"2015-04-16 16:35:29\",\"name\":\"tom\",\"number\":1000.0,\"bool\":true,\"calendar\":\"2015-04-16 16:35:29\"}";
        Map<String, Object> map = jsonMapper.fromJson(json, Map.class);

        Assert.assertEquals(6, map.size());
        Assert.assertTrue(map.containsKey("time"));
        System.out.println("    time->" + map.get("time").getClass().getName());
        System.out.println("    name->" + map.get("name").getClass().getName());
        System.out.println("      id->" + map.get("id").getClass().getName());
        System.out.println("    bool->" + map.get("bool").getClass().getName());
        System.out.println("calendar->" + map.get("calendar").getClass().getName());
        System.out.println("  number->" + map.get("number").getClass().getName());
    }

    @Test
    public void from2levelsMapToJson(){
        Map<String, Object> first = new HashMap<>();
        Map<String, Object> second = new HashMap<>();
        second.put("ID", 1);
        first.put("_key", second);
        first.put("name", "name");

        Map<String, Object> params = new HashMap<>();
        params.put("key", "key");
        params.put("other", "other");
        first.put("params", params);

        String json = jsonMapper.toJson(first);
        System.out.println(json);

        Assert.assertTrue(json.contains("\"params\":{\""));
    }

    @Test
    public void fromJsonTo2leveslMap(){
        String json = "{\"_key\":{\"ID\":1},\"name\":\"name\",\"params\":{\"other\":\"other\",\"key\":\"key\"}}";
        Map<String, Object> map = jsonMapper.fromJson(json, Map.class);

        Assert.assertEquals(3, map.size());
        Assert.assertTrue(map.containsKey("name"));
        System.out.println("    name->" + map.get("name").getClass().getName());
        System.out.println("    _key->" + map.get("_key").getClass().getName());
        System.out.println("  params->" + map.get("params").getClass().getName());
    }

    @Test
    public void from3levelsMapToJson(){
        Map<String, Object> first = new HashMap<>();
        Map<String, Object> second = new HashMap<>();
        Map<String, Object> third = new HashMap<>();
        third.put("ID", 1);
        second.put("_key", third);
        second.put("name", "name");

        Map<String, Object> params = new HashMap<>();
        params.put("key", "key");
        params.put("other", "other");
        second.put("params", params);

        first.put("tree", second);
        first.put("other", "other");

        String json = jsonMapper.toJson(first);
        System.out.println(json);

        Assert.assertTrue(json.contains("\"params\":{\""));
    }

    @Test
    public void fromJsonTo3levelsMap(){
        String json = "{\"other\":\"other\",\"tree\":{\"_key\":{\"ID\":1},\"name\":\"name\",\"params\":{\"other\":\"other\",\"key\":\"key\"}}}";
        Map<String, Object> first = jsonMapper.fromJson(json, Map.class);

        Assert.assertEquals(2, first.size());
        Assert.assertTrue(first.containsKey("tree"));
        System.out.println("    tree->" + first.get("tree").getClass().getName());
        System.out.println("    other->" + first.get("other").getClass().getName());

        HashMap<String, Object> tree = (HashMap<String, Object>)first.get("tree");
        Assert.assertEquals(3, tree.size());
        Assert.assertTrue(tree.containsKey("name"));
        System.out.println("    name->" + tree.get("name").getClass().getName());
        System.out.println("    _key->" + tree.get("_key").getClass().getName());
        System.out.println("  params->" + tree.get("params").getClass().getName());

        HashMap<String, Object> params = (HashMap<String, Object>)tree.get("params");
        Assert.assertEquals(2, params.size());
        Assert.assertTrue(params.containsKey("key"));
    }

    @Test
    public void fromMapListMapToJson(){
        Map<String, Object> first = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> second = new HashMap<>();
        Map<String, Object> third = new HashMap<>();
        third.put("ID", 1);
        second.put("_key", third);
        second.put("name", "name");

        Map<String, Object> params = new HashMap<>();
        params.put("key", "key");
        params.put("other", "other");
        second.put("params", params);

        list.add(second);
        first.put("other", "other");
        first.put("list", list);

        String json = jsonMapper.toJson(first);
        System.out.println(json);

        Assert.assertTrue(json.contains("\"params\":{\""));
    }

    @Test
    public void fromJsonMapListMap(){
        String json = "{\"other\":\"other\",\"list\":[{\"_key\":{\"ID\":1},\"name\":\"name\",\"params\":{\"other\":\"other\",\"key\":\"key\"}}]}";
        Map<String, Object> first = jsonMapper.fromJson(json, Map.class);

        Assert.assertEquals(2, first.size());
        Assert.assertTrue(first.containsKey("list"));
        System.out.println("  list->" + first.get("list").getClass().getName());
        System.out.println(" other->" + first.get("other").getClass().getName());

        List<HashMap<String, Object>> list = (List<HashMap<String, Object>>)first.get("list");
        Assert.assertEquals(1, list.size());
        HashMap<String, Object> tree = list.get(0);
        System.out.println("  name->" + tree.get("name").getClass().getName());
        System.out.println("  _key->" + tree.get("_key").getClass().getName());
        System.out.println("params->" + tree.get("params").getClass().getName());

        HashMap<String, Object> params = (HashMap<String, Object>)tree.get("params");
        Assert.assertEquals(2, params.size());
        Assert.assertTrue(params.containsKey("key"));
    }

    @Test
    public void convertPojo2Map(){
        JsonPOJO pojo = createPojo(1, "name", false, 1000f);
        Map<String, Object> map = jsonMapper.convert(pojo, Map.class);

        Assert.assertEquals(6, map.size());
        Assert.assertTrue(map.containsKey("time"));
        System.out.println("    time->" + map.get("time").getClass().getName());
        System.out.println("    name->" + map.get("name").getClass().getName());
        System.out.println("      id->" + map.get("id").getClass().getName());
        System.out.println("    bool->" + map.get("bool").getClass().getName());
        System.out.println("calendar->" + map.get("calendar").getClass().getName());
        System.out.println("  number->" + map.get("number").getClass().getName());

    }

    @Test
    public void convertString2Date(){
        String dateString = "2015-04-16 16:35:29";
        Date date = jsonMapper.convert(dateString, Date.class);

        Assert.assertNotNull(date);

        dateString = jsonMapper.convert(date, String.class);
        Assert.assertEquals("2015-04-16 16:35:29", dateString);
    }


    private JsonPOJO createPojo(int id, String name, boolean bool, float number){
        JsonPOJO pojo = new JsonPOJO();
        pojo.setId(id);
        pojo.setName(name);
        pojo.setBool(bool);
        pojo.setNumber(number);
        pojo.setTime(new Date());
        pojo.setCalendar(Calendar.getInstance());
        return pojo;
    }

}
