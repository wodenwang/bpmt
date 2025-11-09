package com.riversoft.core.script;

import com.jayway.restassured.path.json.JsonPath;
import com.riversoft.core.BeanFactory;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * GET https://api.github.com/users/borball
 * {
 "login": "borball",
 "id": 2533592,
 "avatar_url": "https://avatars.githubusercontent.com/u/2533592?v=3",
 "gravatar_id": "",
 "url": "https://api.github.com/users/borball",
 "html_url": "https://github.com/borball",
 "followers_url": "https://api.github.com/users/borball/followers",
 "following_url": "https://api.github.com/users/borball/following{/other_user}",
 "gists_url": "https://api.github.com/users/borball/gists{/gist_id}",
 "starred_url": "https://api.github.com/users/borball/starred{/owner}{/repo}",
 "subscriptions_url": "https://api.github.com/users/borball/subscriptions",
 "organizations_url": "https://api.github.com/users/borball/orgs",
 "repos_url": "https://api.github.com/users/borball/repos",
 "events_url": "https://api.github.com/users/borball/events{/privacy}",
 "received_events_url": "https://api.github.com/users/borball/received_events",
 "type": "User",
 "site_admin": false,
 "name": "borball",
 "company": null,
 "blog": null,
 "location": null,
 "email": "borball.zh@gmail.com",
 "hireable": null,
 "bio": null,
 "public_repos": 8,
 "public_gists": 0,
 "followers": 0,
 "following": 3,
 "created_at": "2012-10-11T03:15:16Z",
 "updated_at": "2016-01-10T18:22:05Z"
 }
 * @borball on 2/20/2016.
 */
@Ignore
public class HttpUtilTest {

    @BeforeClass
    public static void beforeClass() {
        BeanFactory.init("classpath:applicationContext-scripts-test.xml");
    }

    @Test
    public void testHttpGetStatusCode(){
        String groovy = "http.given().get('https://api.github.com/users/borball').statusCode()";
        int status = (Integer) eval(groovy);
        assertThat(status, equalTo(200));
    }

    @Test
    public void testHttpGetBody(){
        String groovy = "http.given().get('https://api.github.com/users/borball').jsonPath().get('email')";
        String email = (String) eval(groovy);
        assertThat(email, equalTo("borball.zh@gmail.com"));
    }

    @Test
    public void testHttpGetAuth(){
        String groovy = "http.given().auth().basic('borball', 'Borball123').get('https://api.github.com/user/emails').jsonPath()";
        JsonPath jsonPath = (JsonPath) eval(groovy);
        assertNotNull(jsonPath);
    }

    private Object eval(String groovy) {
        return ScriptHelper.evel(ScriptTypes.GROOVY, groovy);
    }

}
