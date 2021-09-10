package lemon.testcases;

import com.alibaba.fastjson.JSON;
import common.BaseTest;
import data.Constants;
import data.Environment;
import io.restassured.RestAssured;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.omg.CORBA.OBJ_ADAPTER;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pojo.ExcelPojo;
import util.PhoneRandomUtils;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.put;
import static io.restassured.config.JsonConfig.jsonConfig;

/**
 * 充值接口测试
 * 有两个前置条件：1.注册账号  2.登录账号
 * 充值接口需要从登录接口获取到 memberId 和 token
 * @author balala
 * @data 2021/8/24
 **/
public class RechargeTest extends BaseTest {
    //从登录接口获取的memberId 和 token 要写成全局变量，因为充值接口要用
    int memberId;
    String token;

    @BeforeClass //前置条件：
    public void setup() {
        //这个配置是让json小数返回返回类型是BigDecimal：
        //RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
        //配置全局url：
        //RestAssured.baseURI = Constants.Baseurl;
        //-------在BaseTest父类中配置了全局配置，那么以上代码在这儿就不需要了-------//

        //前置条件：
        //条件1：注册
        //先生成一个没有被注册过的手机号码
        String phone = PhoneRandomUtils.getUnregisterPhone();
        //将其保存到环境变量中
        Environment.envData.put("phone",phone);
        //调用下面封装的readSpecifyExcelData方法，传入参数，读取注册这行用例
        List<ExcelPojo> dataList = readSpecifyExcelData( 3, 0,2);

        //获取dataList集合中的Header，并转成Map
//        Map requestHeaderMap01= JSON.parseObject(dataList.get(0).getRequestHeader());//注册中的Header
//        Map requestHeaderMap02= JSON.parseObject(dataList.get(1).getRequestHeader());//登录中的Header

        //将生成的没有被注册的手机号参数进行替换
        // dataList读取了excel前两行数据，其中注册接口是第一条,所以dataList.get(0)
        ExcelPojo registerExcelPojo =casesReplace(dataList.get(0));
        // 注册接口,调用封装的request请求方法
        Response registerRes = request(registerExcelPojo,"Recharge");
        //提取注册接口的”提取返回数据“，并保存到环境变量中（登录接口的请求参数需要用到注册接口中返回的phone）
        //String registerExtract =dataList.get(0).getExtract();
        extractToEnvironment(registerExcelPojo,registerRes);
        //参数替换：替换{{phone}}  （现在要替换的是第二条登录请求中的参数{{phone}}，所以是get(1)）
        ExcelPojo loginExcelPojo=dataList.get(1);
        loginExcelPojo=casesReplace(loginExcelPojo);


        //条件2：登录
        // 上面把登录的请求参数（phone）替换好后就可以发起登录请求了
        Response loginRes = request(loginExcelPojo,"Recharge");
        //得到”提取返回数据“这列，并保存到环境变量中,调用extractToEnvironment方法
        extractToEnvironment(loginExcelPojo,loginRes);


//        menberId = loginres.jsonPath().get("data.id");
//        token = loginres.jsonPath().get("data.token_info.token");
        //参考postman的方式，将获取的 memberId 和 token 赋值到到环境变量中 Environment
        //Environment.memberid = menberId;
        //Environment.token = token;

    }

    //充值接口
    @Test(dataProvider = "getRechargeData")

    public void RechargeTest(ExcelPojo excelPojo) {
        //在执行充值用例之前，将放进环境变量中的变量（ memberId 和 token ）进行替换
        //参数替换已封装为方法，调用即可
        excelPojo =casesReplace(excelPojo);
        //发起请求，将请求结果用rechargeRes接收
        Response rechargeRes=request(excelPojo,"Recharge");
        //响应断言：
        assertResponse(excelPojo,rechargeRes);
        //数据库断言：
        assertSQL(excelPojo);


    }

    @DataProvider
    public Object[] getRechargeData() {

        List<ExcelPojo> dataList = readSpecifyExcelData(3, 2);
        //dataList是一个集合,但当前访问类型是个一维数组，因此要把集合转换成数组，用.toArray()即可:
        return dataList.toArray();

    }

}
