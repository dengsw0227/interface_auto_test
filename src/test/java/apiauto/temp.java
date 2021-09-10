package apiauto;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * @author balala
 * @data 2021/8/4
 **/
public class temp {
    @Test
    public  void getDemo() {
        given().
        when().
                get("www.baidu,com").
        then().log().all();
        }
    }

