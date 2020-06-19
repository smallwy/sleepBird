package lambda;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

public class Test01 {
    public static void main(String[] args) {
        Runnable runnable = () -> {
            System.out.println("lambda");
        };

        BinaryOperator<Integer> add = (x, y) -> x + y;

        System.out.println(add.apply(1, 2));


    }


    @Test
    public void testJudge() {
        Predicate<Integer> ins = x -> x > 5;
        System.out.println(ins.test(2));
    }


    @Test
    public void testCompare() {
        List<Track> tracks = new ArrayList<>();
        tracks.add(new Track("212", 12));
        tracks.add(new Track("asad", 16));
        tracks.add(new Track("123", 15));

        System.out.println(tracks.stream().min(Comparator.comparing(e -> e.getAge())).get());
    }


    @Test
    public void testCount() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        int count = list.stream().reduce(3, (acc, element) -> acc + element);
        System.out.println(count);
    }

}


class Track {
    private String name;
    private int age;

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

    public Track(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Track{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}