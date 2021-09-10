package util;

import java.util.Random;

/**工具类，生成随机手机号
 * @author balala
 * @data 2021/9/1
 **/
public class PhoneRandomUtils {
    public static String getRandomPhone (){

        Random random=new Random();
        //nextInt随机生成一个整数，范围是0~你的参数范围之内
        //设置手机号以133开头
        String phonePrefix="133";
        //用循环生成8个范围在0-9之间的数字--循环拼接
        for (int i=0;i<8;i++){
            //生成一个0-9的随机数
            int num= random.nextInt(9);
            phonePrefix=phonePrefix+num;
        }
        System.out.println(phonePrefix);
        return phonePrefix;


    }

    /**
     * 通过与数据库查询对比，生成没有注册过的手机号
     * @return
     */
    public static String getUnregisterPhone() {
        //循环体外要返回phone，但是phone 调用getRandomPhone()在循环体内，所以先在外面定义一下phone
        String phone = "";
        while (true) {
            phone = getRandomPhone();
            //在数据库中查询该随机手机号的个数，（如果是 0 则表示改手机号没有被注册）
            Object result = JDBCUtils.querySingle("select count(*) from member where mobile_phone =" + phone);
            //System.out.println(result);

            if ((Long) result == 0) {
                //表示没有被注册，符合需求
                break;
            } else {
                //表示已经被注册，还需继续执行上述过程
                //else其实可以不加，加上便于理解
                continue;
            }
        }
        return phone;
    }

//    public static void main(String[] args) {
//        //System.out.println(getUnregisterPhone());
//    }



}
