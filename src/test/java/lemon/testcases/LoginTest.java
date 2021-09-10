package lemon.testcases;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import common.BaseTest;
import data.Constants;
import data.Environment;
import io.restassured.RestAssured;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pojo.ExcelPojo;
import util.PhoneRandomUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;

/**登录接口测试
 * @author balala
 * @data 2021/8/24
 **/
public class LoginTest extends BaseTest {

    @BeforeClass
    public void setup(){
        //这个配置是让json小数返回返回类型是BigDecimal：
        //RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
        //配置全局url：
        //RestAssured.baseURI = Constants.Baseurl;
        //-------在BaseTest父类中配置了全局配置，那么以上代码在这儿就不需要了-------//

        //前置条件
        //生成没有注册过的手机号码
        String phone=PhoneRandomUtils.getUnregisterPhone();
        //再保存到环境变量中去,注意：get 中的“XXX”一定要和用例中的变量名保持一致
        Environment.envData.put("phone",phone);
        //从excel中读取到注册这个前提条件
        List<ExcelPojo> dataList=readSpecifyExcelData(2,0,1);
        //并将这个结果用excelPojo接收
        ExcelPojo excelPojo=dataList.get(0);
        //然后替换到原始数据中去
        excelPojo=casesReplace(excelPojo);
        //获取dataList集合中的Header，并转成Map
        //Map requestHeaderMap=JSON.parseObject(dataList.get(0).getRequestHeader());
        //执行注册接口请求
        Response res=request(excelPojo,"Login");
        //提取注册的手机号保存到环境变量中去
        extractToEnvironment(excelPojo,res);

    }


    //登录(EXCEL数据驱动)
    @Test(dataProvider = "getLoginData")
    public void login(ExcelPojo excelPojo) {
        //替换用例数据
        excelPojo =casesReplace(excelPojo);
        //发送登录请求
        Response loginRes=request(excelPojo,"Login");
        //响应断言：
        assertResponse(excelPojo,loginRes);
        //数据库断言：
        assertSQL(excelPojo);

        //----------------------分割线---------------------------//
        //经过方法封装后，代码可以简化成以上部分，以下部分是不使用封装方法时的步骤

//        //接口入参
//        String InputParams = excelPojo.getInputParams();
//        //接口地址
//        String url=excelPojo.getUrl();
//        //接口请求头
//        String requestHeader = excelPojo.getRequestHeader();
//        //把请求头转成map，（用例一般设计成json格式，方便转成map）
//        Map requestHeaderMap=(Map) JSON.parse(requestHeader);
//
////        Response loginRes =
////        given().
////                //有了全局配置，这行代码就不需要了
////                // config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).
////                body(InputParams).
////                headers(requestHeaderMap).
////        when().
////                post(url).
////        then().
////                log().all().extract().response();
//        Response loginRes=request(excelPojo);
//        //响应断言：
        //1、读取excel里的期望响应结果，获取到的是json格式
//        String expected=excelPojo.getExpected();
//        //并把获取得到的结果转成map,并声明map中的key是String类型，value是Object类型：
//        //Map<String,Object> exceptedMap= (Map) JSON.parse(expected);  这行写法和下面这个写法一样，都可以
//        Map<String,Object> exceptedMap= JSONObject.parseObject(expected,Map.class);
//       // 2、循环遍历map，用.keySet()取到期望结果里面的每一个key
//        for (String exceptedKey : exceptedMap.keySet()){
//            //通过获取到的key来获取map里面的期望结果值value
//            Object exceptedValue=exceptedMap.get(exceptedKey);
//            //3、通过jsonPath表达式,从请求结果中取到key对应的实际结果value --- loginRes.jsonPath.get(key)
//            //注意：exceptedValue是Object类型，所以actualValue也应用Object类型，否则下一步无法做比较
//            Object actualValue=loginRes.jsonPath().get(exceptedKey);
//            //4、把实际值actualValue与期望值exceptedValue做对比
//            Assert.assertEquals(actualValue,exceptedValue);
//        }

    }

    @DataProvider   //读取excel作为数据驱动
    public Object[] getLoginData(){

        //调用封装的readSpecifyExcelData()方法，读取EXCEL文件,指定sheet、开始行、需要读取的行数，放到集合datalist中
        List<ExcelPojo> dataList=readSpecifyExcelData(2,2,13);
        //dataList是一个集合,但当前访问类型是个一维数组，因此要把集合转换成数组，用.toArray()即可:
        return dataList.toArray();
        //dataProvide支持一维和二维数组，当是一维数组时，就是一数组中的每一个元素作为数据驱动
        //当是二维数组时，就是以每一个一维数组作为驱动，注入到测试方法中去，实现数据驱动

    }


}
