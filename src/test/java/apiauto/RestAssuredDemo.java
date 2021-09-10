package apiauto;

import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

/**
 * @author balala
 * @data 2021/8/22
 **/
public class RestAssuredDemo {
   // @Test //注册
//    public void register(){
//        String json1 ="{\"mobile_phone\": \"18224417716\",\"pwd\": \"12345678\",\"type\": 0}";
//        Response registerres =
//                given().
//                        body(json1).
//                        header("Content-Type","application/json").
//                        header("X-Lemonban-Media-Type","lemonban.v2").
//
//                when().
//                        post("http://api.lemonban.com/futureloan/member/register").
//                then().
//                        log().all().extract().response();
//        System.out.println(registerres.body());
//        //响应结果断言：
//        int code1 =registerres.jsonPath().get("code");
//        Assert.assertEquals(code1,2);
//    }

    //把充值接口要用的memberid和token弄成全局变量
        int memberid;
        String token;
    @Test       //登录
    public void login() {
        String json2 = "{\"mobile_phone\": \"18224417716\",\"pwd\": \"12345678\"}";
        //这个配置是让json小数返回返回类型是BigDecimal：
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
        //配置全局url：
        RestAssured.baseURI = "http://api.lemonban.com/futureloan";


        Response loginres =
                given().
                        //config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).
                        body(json2).
                        header("Content-Type", "application/json").
                        header("X-Lemonban-Media-Type", "lemonban.v2").
                when().
                        post("/member/login").
                then().
                        log().all().extract().response();
        System.out.println(loginres.body());
        //响应结果断言：
        int code = loginres.jsonPath().get("code");
        Assert.assertEquals(code, 0);
        String msg = loginres.jsonPath().get("msg");
        Assert.assertEquals(msg, "OK");
        //注意：restassured里面如果返回json小数，那么其类型是float
//        但是如果用float接收，会有丢失精度的问题，解解决方案是：
//        声明restassured返回json小数的类型为bigDecimal（float和double用于接收一般的小数，但是如果是对精度要求较高的就用bigDecimal）
        BigDecimal actualloginleaveAmount = loginres.jsonPath().get("data.leave_amount");
        BigDecimal expectedloginleaveAmount = BigDecimal.valueOf(7.96);//把期望的数值传入进去，转成BigDecimal类型
        Assert.assertEquals(actualloginleaveAmount, expectedloginleaveAmount);

        //1、充值前先从登录接口获取返回的memberid
        memberid = loginres.jsonPath().get("data.id");
        System.out.println("memberid:" + memberid);

        //2、再获取token
        token = loginres.jsonPath().get("data.token_info.token");
        System.out.println("token:" + token);
    }

        //充值
        @Test(dependsOnMethods = "login")
        public void recharge() {

            String json3 = "{\"member_id\": " + memberid + ", \"amount\": 1.99 }";
            Response rechargeres =
                    given().
                            body(json3).
                            header("Content-Type", "application/json").
                            header("X-Lemonban-Media-Type", "lemonban.v2").
                            header("Authorization", "Bearer " + token).
                    when().
                            post("/member/recharge").
                    then().
                            log().all().extract().response();
            System.out.println("当前余额：" + rechargeres.jsonPath().get("data.leave_amount"));
            BigDecimal actualrechargeleaveamount = rechargeres.jsonPath().get("data.leave_amount");
            BigDecimal expectedrechargeleaveamount = BigDecimal.valueOf(9.95);
            Assert.assertEquals(actualrechargeleaveamount, expectedrechargeleaveamount);
        }

   }


