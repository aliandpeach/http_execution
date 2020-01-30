package com.yk;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.*;

public class LambdaTest {
    public static void main(String[] args) {
        Function<String, char[]> f3 = (s) -> s.toCharArray();
        Supplier<Integer> s = () -> 1;
        Predicate<Integer> p = (a) -> true;
        Consumer<Integer> c = (a) -> System.out.println(a);

        //类名::实例方法名
        // 若Lambda表达式的参数列表的第一个参数，是实例方法的调用者，
        // 第二个参数(或无参)是实例方法的参数时，就可以使用这种方法
        BiPredicate<String, String> bip = String::equals;
        //Predicate<List> psa = (x) -> x.isEmpty();
        Predicate<List> ps = List::isEmpty;
        Function<String, char[]> fsc = String::toCharArray;
        //BiFunction<String, String, Integer> bifssia = (x, y) -> x.indexOf(y);
        BiFunction<String, String, Integer> bifssi = String::indexOf;
        //Function<BigInteger, BigInteger> fffffa = (x) -> x.abs();
        Function<BigInteger, BigInteger> fffff = BigInteger::abs;
        //Consumer<ArrayList> cccccca = (x) -> x.clear();
        Consumer<ArrayList> cccccc = ArrayList::clear;
//        BiFunction<Person, Person, Integer> c2a = (p1, p2) -> p1.compareTo(p2);
        BiFunction<Person, Person, Integer> c2 = Person::compareTo;

        //类名::静态方法名
        Function<Integer, List> fil = LambdaTest::test2;//test2方法有Integer参数
        Supplier<String> ss = LambdaTest::test;//test是 LambdaTest 类的静态方法(返回值是String类型)

        //对象引用::实例方法名
//        Consumer<Integer> c1a = x -> System.out.println(x);
        Consumer<Integer> c1 = System.out::println;

        LambdaTest lambdaTest = new LambdaTest();
        Predicate<Integer> p1 = lambdaTest::instance;
        Function<String, ArrayList> p2 = lambdaTest::instancef;

        // 引用构造器
        Supplier<String> s2 = String::new;//String的构造方法

        Optional.of("a").ifPresent(System.out::println);
    }

    public static String test() {
        return "";
    }

    public static List test2(Integer a) {
        return null;
    }

    public boolean instance(Integer l) {
        return true;
    }

    public ArrayList instancef(String l) {
        return null;
    }

    static class Person implements Comparable {

        @Override
        public int compareTo(Object o) {
            return 0;
        }
    }
}
