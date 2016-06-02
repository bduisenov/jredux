package org.js.redux;

import static org.js.redux.Redux.compose;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.function.Function;

import org.js.redux.helpers.Pair;
import org.junit.Test;

/**
 * Created by bduisenov on 02/06/16.
 */
public class ComposeTest {

    @Test
    public void testComposesFromRightToLeft() throws Exception {
        Function<Integer, Integer> _double = x -> x * 2;
        Function<Integer, Integer> square = x -> x * x;

        assertEquals(Integer.valueOf(25), compose(square).apply(5));
        assertEquals(Integer.valueOf(100), compose(square, _double).apply(5));
        assertEquals(Integer.valueOf(200), compose(_double, square, _double).apply(5));
    }

    @Test
    public void testComposesFunctionsFromRightToLeft() throws Exception {
        Function<Function<String, String>, Function<String, String>> a = next -> x -> next.apply(x + "a");
        Function<Function<String, String>, Function<String, String>> b = next -> x -> next.apply(x + "b");
        Function<Function<String, String>, Function<String, String>> c = next -> x -> next.apply(x + "c");
        Function<String, String> _final = x -> x;

        assertEquals("abc", compose(a, b, c).apply(_final).apply(""));
        assertEquals("bca", compose(b, c, a).apply(_final).apply(""));
        assertEquals("cab", compose(c, a, b).apply(_final).apply(""));
    }

    @Test
    public void testCanBeSeededWithMultipleArguments() throws Exception {
        Function<Integer, Integer> square = x -> x * x;
        Function<Pair<Integer, Integer>, Integer> add = (x) -> x._1() + x._2();

        assertEquals(Integer.valueOf(9), Redux.compose(square, add).apply(new Pair<>(1, 2)));
    }

    @Test
    public void testReturnsTheFirstGivenArgumentIfGivenNoFunctions() throws Exception {
        //expect(compose()(1, 2)).toBe(1)
        assertEquals(3, compose().apply(3));
        assertNull(compose().apply(null));
    }

    @Test
    public void testReturnsTheFirstFunctionIfGivenOnlyOne() throws Exception {
        Function<Integer, Integer> fn = (a) -> a;
        assertEquals(fn, compose(fn));
    }

}
