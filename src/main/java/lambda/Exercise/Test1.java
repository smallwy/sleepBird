package lambda.Exercise;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test1 {


    //  求出链表元素的和
    @Test
    public void sumAdd() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        System.out.println(addUp(list.stream()));
    }

    public int addUp(Stream<Integer> numbers) {
        return numbers.reduce(0, Integer::sum);
    }


    //找出用作家名字和国籍的信息
    @Test
    public void flatTest() {
        List<article> articles = new ArrayList<>();
        articles.add(new article("mike", "amer", 11));
        articles.add(new article("m2e", "ame11r", 2));
        articles.add(new article("1123i13e", "chaine", 50));
        System.out.println(flat(articles.stream()));
    }


    public List<String> flat(Stream<article> articleStream) {
        return articleStream.flatMap(e -> Stream.of(e.name, e.country)).collect(Collectors.toList());
    }


    //找出 作品不超过3的作家
    @Test
    public List<article> filterT(Stream<article> articleStream) {
        return articleStream.filter(e -> e.getArbum().size() < 3).collect(Collectors.toList());
    }


    //找出所有作家的作品的和
    @Test
    public int allAdd(Stream<article> articleStream) {
        return articleStream.map(e -> e.getArbum().size()).reduce(0, Integer::sum);
    }


}

class article {

    String name;

    String country;

    int age;

    public List<Arbum> arbum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public article(String name, String country, int age) {
        this.name = name;
        this.country = country;
        this.age = age;
    }


    public List<Arbum> getArbum() {
        return arbum;
    }

    public void setArbum(List<Arbum> arbum) {
        this.arbum = arbum;
    }
}

class Arbum {
    //歌曲名字
    public String ArbumName;

    public int age;

}