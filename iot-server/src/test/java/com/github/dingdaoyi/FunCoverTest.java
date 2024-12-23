package com.github.dingdaoyi;

import com.github.dingdaoyi.iot.rule.FunCover;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunCoverTest {
    private static FunCover funCover;
    @BeforeAll
    public static void init() {
        funCover = new FunCover();
    }
    @Test
    void testMath() {
        Object parse = funCover.parse(10, "#value-2*3");
        assertEquals(4,parse);
    }

    @Test
    void testEql() {
        Object parse = funCover.parse("yanbing", "#value=='yanbing'");
        assertEquals(true,parse);
    }
}
