package prac;

/**
 * @author balala
 * @data 2021/8/9
 **/
public class Student {
    private String name;
    private int age;
    private int scort;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getScort() {
        return scort;
    }

    public void setScort(int scort) {
        this.scort = scort;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", scort=" + scort +
                '}';
    }
}
