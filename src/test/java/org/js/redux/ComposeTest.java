package org.js.redux;

import static org.js.redux.Redux.compose;
import static org.js.redux.Redux.composeList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.junit.Test;

/**
 * Created by bduisenov on 05/06/16.
 */
public class ComposeTest {

    @Test
    public void composesFromRightToleft() {
        Function<Integer, Integer> _double = (x) -> x * 2;
        Function<Integer, Integer> square = (x) -> x * x;
        assertEquals(Integer.valueOf(25), compose(square).apply(5));
        assertEquals(Integer.valueOf(100), compose(square, _double).apply(5));
        assertEquals(Integer.valueOf(200), compose(_double, square, _double).apply(5));
    }

    @Test
    public void composesFunctionsFromRightToLeft() throws Exception {
        Function<Function<String, String>, Function<String, String>> a = next -> x -> next.apply(x + "a");
        Function<Function<String, String>, Function<String, String>> b = next -> x -> next.apply(x + "b");
        Function<Function<String, String>, Function<String, String>> c = next -> x -> next.apply(x + "c");
        Function<String, String> _final = x -> x;

        assertEquals("abc", compose(a, b, c).apply(_final).apply(""));
        assertEquals("bca", compose(b, c, a).apply(_final).apply(""));
        assertEquals("cab", compose(c, a, b).apply(_final).apply(""));
    }

    @Test
    public void canBeSeededWithMultipleArguments() throws Exception {
        Function<Integer, Integer> square = x -> x * x;
        Function<IntStream, Integer> add = IntStream::sum;

        assertEquals(Integer.valueOf(9), Redux.compose(square, add).apply(IntStream.of(1, 2)));
    }

    @Test
    public void returnsTheFirstGivenArgumentIfGivenNoFunctions() throws Exception {
        assertEquals(1, composeList().apply(Arrays.asList(1, 2)));
        assertEquals(3, compose().apply(3));
        assertNull(compose().apply(null));
    }

    @Test
    public void returnsTheFirstFunctionIfGivenOnlyOne() throws Exception {
        Function<Integer, Integer> fn = (a) -> a;

        assertEquals(fn, compose(fn));
    }

}
