package com.dong.github.db;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Created by dongjiangpeng on 2019/7/11 0011.
 */
public class GithubTypeConverters {
    @TypeConverter
    public List<Integer> stringToIntList(String data) {
        return Arrays.asList(data.split(","))
                .stream()
                .flatMap(new Function<String, Stream<Integer>>() {
                    @Override
                    public Stream<Integer> apply(String s) {
                        try {
                            return Stream.of(Integer.valueOf(s));
                        } catch (NumberFormatException e) {
                            return Stream.of(null);
                        }
                    }
                })
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) {
                        return integer != null;
                    }
                })
                .collect(Collectors.toList());
    }

    @TypeConverter
    public String intListToString(List<Integer> integers) {
        if (integers != null) {
            return integers
                    .stream()
                    .map(new Function<Integer, String>() {
                        @Override
                        public String apply(Integer integer) {
                            return String.valueOf(integer);
                        }
                    })
                    .collect(Collectors.joining(","));
        } else {
            return null;
        }
    }
}
