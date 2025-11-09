package com.riversoft.core.script;

import com.riversoft.core.BeanFactory;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

public class JsonUtilTest {

    private static String ARRAY = "[{\"email\":\"name1@mail.com\",\"alias\":\"name one\",\"phone\":\"3456789\"},\n" +
            "{\"email\":\"name2@mail.com\",\"alias\":\"name two\",\"phone\":\"1234567\"},\n" +
            "{\"email\":\"name3@mail.com\",\"alias\":\"name three\",\"phone\":\"2345678\"}]";

    private static String OBJECT = "{ \"store\": {\n" +
            "    \"book\": [ \n" +
            "      { \"category\": \"reference\",\n" +
            "        \"author\": \"Nigel Rees\",\n" +
            "        \"title\": \"Sayings of the Century\",\n" +
            "        \"price\": 8.95\n" +
            "      },\n" +
            "      { \"category\": \"fiction\",\n" +
            "        \"author\": \"Evelyn Waugh\",\n" +
            "        \"title\": \"Sword of Honour\",\n" +
            "        \"price\": 12\n" +
            "      },\n" +
            "      { \"category\": \"fiction\",\n" +
            "        \"author\": \"Herman Melville\",\n" +
            "        \"title\": \"Moby Dick\",\n" +
            "        \"isbn\": \"0-553-21311-3\",\n" +
            "        \"price\": 8.99\n" +
            "      },\n" +
            "      { \"category\": \"fiction\",\n" +
            "        \"author\": \"J. R. R. Tolkien\",\n" +
            "        \"title\": \"The Lord of the Rings\",\n" +
            "        \"isbn\": \"0-395-19395-8\",\n" +
            "        \"price\": 22.99\n" +
            "      }\n" +
            "    ],\n" +
            "    \"bicycle\": {\n" +
            "      \"color\": \"red\",\n" +
            "      \"price\": 19.95,\n" +
            "      \"atoms\": " + Long.MAX_VALUE + ",\n" +
            "    }\n" +
            "  }\n" +
            "}";

    private static Map<String, Object> CONTEXT = new HashMap<>();

    @BeforeClass
    public static void beforeClass() {
        BeanFactory.init("classpath:applicationContext-scripts-test.xml");
        CONTEXT.put("array", ARRAY);
        CONTEXT.put("object", OBJECT);
    }

    @Test
    public void testObject(){
        String groovy = "json.from(object).get()";
        Object o = eval(groovy);
        Assert.assertTrue(o instanceof Map);

        groovy = "json.from(object).get('store.book.category')";
        List<String> categories = (List<String>) eval(groovy);
        assertThat(categories.size(), equalTo(4));
        assertThat(categories, hasItems("reference", "fiction"));

        groovy = "json.from(object).get('store.book[0].category')";
        String category = (String) eval(groovy);
        assertThat(category, equalTo("reference"));

        groovy = "json.from(object).get('store.book[-1].title')";
        String title = (String) eval(groovy);
        assertThat(title, equalTo("The Lord of the Rings"));

        groovy = "json.from(object).param('author', 'Herman Melville').get('store.book.findAll { book -> book.author == author }')";
        List<Map<String, ?>> books = (List<Map<String, ?>>) eval(groovy);
        assertThat(books.size(), equalTo(1));
        String authorActual = (String) books.get(0).get("author");
        assertThat(authorActual, equalTo("Herman Melville"));

        groovy = "json.from(object).get('store.book.findAll { book -> book.price >= 5 && book.price <= 15 }')";
        books = (List<Map<String, ?>>) eval(groovy);
        assertThat(books.size(), equalTo(3));
        String author = (String) books.get(0).get("author");
        assertThat(author, equalTo("Nigel Rees"));
        int price = (Integer) books.get(1).get("price");
        assertThat(price, equalTo(12));

        groovy = "json.from(object).get('store.book.size()')";
        Integer size = (Integer)eval(groovy);
        assertThat(size, equalTo(4));

        groovy = "json.from(object).get('store')";
        Map<String, Map> store = (Map<String, Map>)eval(groovy);
        assertThat(store.size(), equalTo(2));
        Map<String, Object> bicycle = store.get("bicycle");
        String color = (String) bicycle.get("color");
        float p = (Float) bicycle.get("price");
        assertThat(color, equalTo("red"));
        assertThat(p, equalTo(19.95f));

        groovy = "json.from(object).prettify()";
        o = eval(groovy);
        Assert.assertTrue(o instanceof String);
        System.out.println(o);
    }

    @Test
    public void testArray(){
        String groovy = "json.from(array).get()";
        Object o = eval(groovy);
        Assert.assertTrue(o instanceof List);

        groovy = "json.from(array).get().size()";
        o = eval(groovy);
        Assert.assertTrue(o instanceof Number);
        Assert.assertEquals(3, ((Number) o).intValue());

        groovy = "json.from(array).get()[0]";
        o = eval(groovy);
        Assert.assertTrue(o instanceof Map);

        groovy = "json.from(array).get()[0].email";
        o = eval(groovy);
        Assert.assertTrue(o instanceof String);
        Assert.assertEquals("name1@mail.com", o);

        groovy = "json.from(array).get('email')";
        o = eval(groovy);
        Assert.assertTrue(o instanceof List);

        groovy = "json.from(array).getList('email')";
        o = eval(groovy);
        Assert.assertTrue(o instanceof List);

        groovy = "json.from(array).get('email').size()";
        o = eval(groovy);
        Assert.assertTrue(o instanceof Number);
        Assert.assertEquals(3, ((Number) o).intValue());

        groovy = "json.from(array).get('email')[2]";
        o = eval(groovy);
        Assert.assertTrue(o instanceof String);
        Assert.assertEquals("name3@mail.com", o);

        groovy = "json.from(array).prettify()";
        o = eval(groovy);
        Assert.assertTrue(o instanceof String);
        System.out.println(o);
    }

    private Object eval(String groovy) {
        return ScriptHelper.evel(ScriptTypes.GROOVY, groovy, CONTEXT);
    }

}
