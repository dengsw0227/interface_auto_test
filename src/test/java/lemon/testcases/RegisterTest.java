package lemon.testcases;

import com.alibaba.fastjson.JSONObject;
import common.BaseTest;
import data.Environment;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pojo.ExcelPojo;
import util.JDBCUtils;
import util.PhoneRandomUtils;

import java.sql.SQLOutput;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author balala
 * @data 2021/9/7
 **/
public class RegisterTest extends BaseTest {
    @BeforeClass
    public void setup(){
        //随机生成没有注册过的手机号（用例中有三条正常注册的用例，所以要生成三个手机号）
        String phone1= PhoneRandomUtils.getUnregisterPhone();
        String phone2= PhoneRandomUtils.getUnregisterPhone();
        String phone3= PhoneRandomUtils.getUnregisterPhone();
        //保存到环境变量中
        Environment.envData.put("phone1",phone1);
        Environment.envData.put("phone2",phone2);
        Environment.envData.put("phone3",phone3);


    }
    @Test(dataProvider = "getRegisterDatas")
    public void Register(ExcelPojo excelPojo){
        //将放进环境变量中的phone替换到实体对象excelPojo中
        excelPojo=casesReplace(excelPojo);
        //替换后则发起注册请求
        Response res= request(excelPojo,"Register");
        //响应断言
        assertResponse(excelPojo,res);
        //数据库断言：
        assertSQL(excelPojo);
    }
    @DataProvider
    public Object[] getRegisterDatas(){
        List<ExcelPojo> dataList= readSpecifyExcelData(1,0);
        //把读取到的集合转成一维数组
        return dataList.toArray();

    }

}
