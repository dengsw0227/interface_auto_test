package prac;

import com.alibaba.fastjson.JSONObject;

/**
 * @author balala
 * @data 2021/8/9
 **/
public class JsonDemo {
    public static void main(String[] args) {
        String json="{\"name\":\"张三\",\"age\":\"18\",\"scort\":\"90\"}";
        Student s1= JSONObject.parseObject(json,Student.class);
        System.out.println(s1);

    }
}
