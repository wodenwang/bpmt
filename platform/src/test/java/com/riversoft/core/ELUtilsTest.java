package com.riversoft.core;

import com.riversoft.core.web.ELUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.Map;

/**
 * @borball on 5/30/2016.
 */
public class ELUtilsTest {

    @Test
    public void testBean2Map(){
        Map<String, Object> map = new LinkedCaseInsensitiveMap<>();
        map.put("id", 100);
        map.put("name", "name");
        map.put("mail", "mail");
        map.put("address", "address");

        String json = ELUtils.json(map, "name");
        Assert.assertNotNull(json);
    }

}
