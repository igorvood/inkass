package ru.sberbank.inkass.calc;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class PairCalcChanceServiceImplTest {

    public static <T> Predicate<T> dictinct(long chance) {
        Map<T, Long> map = new ConcurrentHashMap<>();
        return t -> map.merge(t, 1L, Long::sum) == chance;
    }

    @Test
    public void runOneAnt() {

        List<String> strings = Arrays.asList("a", "2", "3", "2", "2", "c", "c", "c", "c", "c", "a", "a");
        final String s = strings.stream().filter(dictinct(3)).peek(System.out::println).findFirst().get();
        System.out.println(s);
        Assert.assertEquals("2", s);

    }

}