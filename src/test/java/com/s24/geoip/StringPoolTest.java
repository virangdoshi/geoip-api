package com.s24.geoip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assume;
import org.junit.Test;

public class StringPoolTest {

    @SuppressWarnings({ "RedundantStringConstructorCall", "StringEquality" })
    @Test
    public void testStringPool() throws Exception {
        StringPool pool = new StringPool();
        // deliberately create different instances of equal strings
        String s1 = new String("foobar");
        String s2 = new String("foobar");
        String different = "baz";

        Assume.assumeTrue("Must use different instances of equal strings for this test to work",
                s1 != s2 && s1.equals(s2));

        String s1Pooled = pool.pool(s1);
        String s2Pooled = pool.pool(s2);
        String differentPooled = pool.pool(different);

        // The pooled instance should be the first instance of the string that was pooled (i.e., the pool should not
        // create new instances of strings)
        assertTrue(s1 == s1Pooled);
        assertTrue(different == differentPooled);
        // Equal strings should be pooled to use the same instance
        assertTrue(s1Pooled == s2Pooled);

        assertEquals(2, pool.size());
    }
}
