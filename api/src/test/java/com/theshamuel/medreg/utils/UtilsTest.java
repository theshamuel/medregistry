package com.theshamuel.medreg.utils;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UtilsTest {
    @Test
    public void testDeleteNotNeedSymbol(){
        String[] badSymbols = new String[]{" ","-","\\\\","/","'"};
        assertThat(Utils.deleteNotNeedSymbol("17 06   -9",badSymbols),is("17069"));
        assertThat(Utils.deleteNotNeedSymbol("17   06",badSymbols),is("1706"));
        assertThat(Utils.deleteNotNeedSymbol(" 17   06",badSymbols),is("1706"));
        assertThat(Utils.deleteNotNeedSymbol("-17   06",badSymbols),is("1706"));
        assertThat(Utils.deleteNotNeedSymbol("17 06 --- 78\\123456-",badSymbols),is("170678123456"));
    }
}
