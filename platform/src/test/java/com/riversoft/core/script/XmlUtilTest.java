package com.riversoft.core.script;

import com.jayway.restassured.path.xml.element.Node;
import com.jayway.restassured.path.xml.element.NodeChildren;
import com.riversoft.core.BeanFactory;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by exizhai on 2/17/2016.
 */
public class XmlUtilTest {

    private static final String XML = "<shopping>\n" +
            "      <category type=\"groceries\">\n" +
            "        <item>\n" +
            "\t   <name>Chocolate</name>\n" +
            "           <price>10</" +
            "price>\n" +
            "" +
            "   " +
            "\t</item>\n" +
            "        <item>\n" +
            "\t   <name>Coffee</name>\n" +
            "           <price>20</price>\n" +
            "\t</item>\n" +
            "      </category>\n" +
            "      <category type=\"supplies\">\n" +
            "        <item>\n" +
            "\t   <name>Paper</name>\n" +
            "           <price>5</price>\n" +
            "\t</item>\n" +
            "        <item quantity=\"4\">\n" +
            "           <name>Pens</name>\n" +
            "           <price>15.5</price>\n" +
            "\t</item>\n" +
            "      </category>\n" +
            "      <category type=\"present\">\n" +
            "        <item when=\"Aug 10\">\n" +
            "           <name>Kathryn's Birthday</name>\n" +
            "           <price>200</price>\n" +
            "        </item>\n" +
            "      </category>\n" +
            "</shopping>";

    private static Map<String, Object> CONTEXT = new HashMap<>();

    @BeforeClass
    public static void beforeClass() {
        BeanFactory.init("classpath:applicationContext-scripts.xml");
        CONTEXT.put("text", XML);
    }

    @Test
    public void initializeUsingCtorAndGetList() throws Exception {
        final String name = (String)eval("xml.from(text).get('shopping.category.item[0].name')");
        assertThat(name, equalTo("Chocolate"));
    }

    @Test
    public void initializeUsingGivenAndGetAttributes() throws Exception {
        final List<String> categories = (List<String>)eval("xml.from(text).get('shopping.category.@type')");
        assertThat(categories, hasItems("groceries", "supplies", "present"));
    }

    @Test
    public void initializeUsingWithAndGetList() throws Exception {
        final NodeChildren categories = (NodeChildren)eval("xml.from(text).get('shopping.category')");
        assertThat(categories.size(), equalTo(3));
    }

    @Test
    public void initializeUsingWithAndGetChildren() throws Exception {
        final List<String> categories = (List<String>)eval("xml.from(text).get('shopping.category.item.name.list()')");
        assertThat(categories, hasItems("Chocolate", "Coffee", "Paper", "Pens", "Kathryn's Birthday"));
    }

    @Test
    public void returnItems() throws Exception {
        final List<String> categories = (List<String>)eval("xml.from(text).get('shopping.category.item.children().list()')");
        assertThat(categories, hasItems("Chocolate", "Coffee", "Paper", "Pens", "Kathryn's Birthday"));
    }

    @Test
    public void itemsWithPriceBetweenTenAndTwenty() throws Exception {
        final List<Node> itemsBetweenTenAndTwenty =  (List<Node>)eval("xml.from(text).get('shopping.category.item.findAll { item -> def price = item.price.toFloat(); price >= 10 && price <= 20 }')");

        assertThat(itemsBetweenTenAndTwenty.size(), equalTo(3));

        final Node category1 = itemsBetweenTenAndTwenty.get(0);
        final NodeChildren categoryChildren = category1.children();
        assertThat(categoryChildren, hasItems("Chocolate", "10"));

        for (Node item : categoryChildren.nodeIterable()) {
            assertThat(item.name(), anyOf(equalTo("name"), equalTo("price")));
        }
    }

    @Test
    public void multipleGetsWithOneInstanceOfXmlPath() throws Exception {
        assertThat((Integer)eval("xml.from(text).get('shopping.category.item.size()')"), equalTo(5));
        assertThat((List<String>)eval("xml.from(text).getList('shopping.category.item.children().list()', String.class)"), hasItem("Pens"));

    }

    @Test
    public void rootPathNotEndingWithDot() throws Exception {
        assertThat((Integer)eval("xml.from(text).setRoot('shopping.category.item').getInt('size()')"), equalTo(5));
        assertThat((List<String>)eval("xml.from(text).setRoot('shopping.category.item').getList('children().list()', String.class)"), hasItem("Pens"));
    }

    @Test
    public void rootPathEndingWithDot() throws Exception {
        assertThat((Integer)eval("xml.from(text).setRoot('shopping.category.item.').getInt('size()')"), equalTo(5));
        assertThat((List<String>)eval("xml.from(text).setRoot('shopping.category.item.').getList('children().list()', String.class)"), hasItem("Pens"));
    }

    @Test
    public void convertsNonRootObjectGraphToJavaObjects() throws Exception {
        NodeChildren categories = (NodeChildren)eval("xml.from(text).get('shopping.category')");
        assertThat(categories.size(), equalTo(3));
        assertThat(categories.toString(), equalTo("Chocolate10Coffee20Paper5Pens15.5Kathryn's Birthday200"));
    }

    @Test
    public void convertsRootObjectGraphToJavaObjects() throws Exception {
        Node objects = (Node)eval("xml.from(text).get('shopping')");
        assertThat(objects.toString(), equalTo("Chocolate10Coffee20Paper5Pens15.5Kathryn's Birthday200"));
    }

    @Test
    public void firstCategoryAttributeFromJava() throws Exception {
        Node node = (Node)eval("xml.from(text).get('shopping.category[0]')");
        assertThat(node.getAttribute("@type"), equalTo("groceries"));
        assertThat(node.getAttribute("type"), equalTo("groceries"));
        assertThat((String) node.get("@type"), equalTo("groceries"));
    }

    @Test
    public void gettingChildrenFromJava() throws Exception {
        Node category = (Node)eval("xml.from(text).get('shopping.category[0]')");
        final NodeChildren categoryChildren = category.children();
        assertThat(categoryChildren.size(), equalTo(2));
        for (Node item : categoryChildren.nodeIterable()) {
            assertThat(item.children().size(), equalTo(2));
            final Node name = item.get("name");
            final Node price = item.get("price");
            assertThat(name.value(), anyOf(equalTo("Chocolate"), equalTo("Coffee")));
            assertThat(price.value(), anyOf(equalTo("10"), equalTo("20")));
        }
    }

    @Test
    public void getFirstItemName() throws Exception {
        final String name = (String)eval("xml.from(text).get('shopping.category.item[0].name')");
        assertThat(name, equalTo("Chocolate"));
        assertThat((String)eval("xml.from(text).get('shopping.category.item[0].name')"), equalTo("Chocolate"));
    }

    @Test
    public void getSingleAttributes() throws Exception {
        final Map<String, String> categoryAttributes = (Map<String, String>)eval("xml.from(text).get('shopping.category[0].attributes()')");
        assertThat(categoryAttributes.size(), equalTo(1));
        assertThat(categoryAttributes.get("type"), equalTo("groceries"));
    }

    @Test
    public void getAllItemNames() throws Exception {
        final List<String> items = (List<String>)eval("xml.from(text).get(\"shopping.depthFirst().grep { it.name() == 'item' }.name\")");

        assertThat(items, hasItems("Chocolate", "Coffee", "Paper", "Pens", "Kathryn's Birthday"));
    }

    @Test
    public void getEntireObjectGraph() throws Exception {
        final Node node = (Node)eval("xml.from(text).get()");
        assertThat(node.name(), is("shopping"));
    }

    private Object eval(String groovy) {
        return ScriptHelper.evel(ScriptTypes.GROOVY, groovy, CONTEXT);
    }
}

