package apiauto;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;

/**
 * @author balala
 * @data 2021/3/28
 **/
public class ApiTest {
    @Test
    public void  postform(){
        given().
                formParam("phone","18224417716").
                formParam("password","123456").
        when().
                post("http://www.httpbin.org/post").
        then().
                log().body();
    }
    @Test
    public void  postfile(){
        given().
                multiPart(new File("D:\\test.txt")).
                when().
                post("http://www.httpbin.org/post").

                then().
                log().body();
    }
    @Test
    public void  getResponsehtml(){
        Response res=
                given().
                when().
                get("http://www.baidu.com").

                then().
                        //获取响应值
                log().all().extract().response();
        //get(html.head.meta)只能获取到meta标签的文本，如果要获取属性，要用@符号加上属性名
        System.out.println((String) res.htmlPath().get("html.head.meta[0].@content"));
    }
    @Test
    public void  getResponsexml(){
        Response res=
                given().
                        when().
                        get("http://www.httpbin.org//xml").

                        then().
                        log().all().extract().response();
        //get(html.head.meta)只能获取到meta标签的文本，如果要获取属性，要用@符号加上属性名
        System.out.println((String) res.htmlPath().get("html.head.meta[0].@content"));
    }

}
