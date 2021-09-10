package lemon.testcases;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alibaba.fastjson.JSONObject;
import common.BaseTest;
import data.Constants;
import data.Environment;
import groovy.transform.Undefined;
import io.restassured.RestAssured;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.kohsuke.rngom.parse.host.Base;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pojo.ExcelPojo;
import util.JDBCUtils;
import util.PhoneRandomUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.config.JsonConfig.jsonConfig;

/**
 * @author balala
 * @data 2021/8/28
 **/
public class InvestFlowTest  extends BaseTest {
    @BeforeClass
    public void setup() throws InterruptedException {
        //这个配置是让json小数返回返回类型是BigDecimal：
        //RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
        //配置全局url：
        //RestAssured.baseURI = Constants.Baseurl;
        //-------在BaseTest父类中配置了全局配置，那么以上代码在这儿就不需要了-------//

        //投资接口需要生成三个角色的手机号（管理员+投资人+借款人）,并放到环境变量中
        String adminPhone = PhoneRandomUtils.getUnregisterPhone();
        Thread.sleep(500);//有时数据库可能承受不了快速查询，可以加个等待
        String investPhone = PhoneRandomUtils.getUnregisterPhone();
        Thread.sleep(500);
        String borrowerPhone = PhoneRandomUtils.getUnregisterPhone();
        Thread.sleep(500);
        Environment.envData.put("admin_phone", adminPhone);
        Environment.envData.put("invest_phone", investPhone);
        Environment.envData.put("borrower_phone", borrowerPhone);

        //读取前置用例数据，从第一条~第九条
        List<ExcelPojo> list = readSpecifyExcelData(5, 0, 9);
        //因为一共有9条用例要执行，并且每条用例都有返回数据要提取数据用到下个接口
        //所以这里要用循环将九条用例遍历
        for (int i = 0; i < list.size(); i++) {
            //list.get(i)用excelPojo变量接收，后面使用起来方便些
            ExcelPojo excelPojo = list.get(i);
            //参数替换
            excelPojo = casesReplace(excelPojo);
            //发送请求
            Response res = request(excelPojo,"InvestFlow");
            //判断提取返回数据是不是为空，如果不为空就要进行提取,并保存进环境变量中
            if (list.get(i).getExtract() != null) {
                extractToEnvironment(excelPojo, res);

            }

        }
    }


    @Test
    public void InvestTest() {
        List<ExcelPojo> list = readSpecifyExcelData(5, 9);
        ExcelPojo excelPojo = list.get(0);
        //替换
        excelPojo = casesReplace(excelPojo);
        //发送投资请求
        Response investRes = request(excelPojo,"InvestFlow");
        //响应断言：
        assertResponse(excelPojo,investRes);
        //数据库断言：
        assertSQL(excelPojo);

        //.getClass()可以把变量的数据类型打印出来，
        //System.out.println("exceptedValue类型"+exceptedValue.getClass());
        //System.out.println("actualValue类型"+actualValue.getClass());
    }


    @AfterTest
    public void teardown(){

    }

}
