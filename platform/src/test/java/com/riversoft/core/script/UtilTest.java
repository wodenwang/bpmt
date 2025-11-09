package com.riversoft.core.script;

import org.junit.Assert;
import org.junit.Test;

import com.riversoft.platform.script.function.Util;

import java.util.Calendar;

/**
 * Created by exizhai on 12/02/2015.
 */
public class UtilTest {

    @Test
    public void testCompareDate() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.YEAR, 2015);
        now.set(Calendar.MONTH, 2);
        now.set(Calendar.DAY_OF_MONTH, 3);

        Calendar before = Calendar.getInstance();
        before.set(Calendar.YEAR, 2014);
        before.set(Calendar.MONTH, 5);
        before.set(Calendar.DAY_OF_MONTH, 3);

        Assert.assertEquals(9, Util.compareDate(now.getTime(), before.getTime(), "M").longValue());
        Assert.assertEquals(1, Util.compareDate(now.getTime(), before.getTime(), "Y").longValue());

        now.set(Calendar.YEAR, 2015);
        now.set(Calendar.MONTH, 2);
        now.set(Calendar.DAY_OF_MONTH, 3);

        before.set(Calendar.YEAR, 2015);
        before.set(Calendar.MONTH, 2);
        before.set(Calendar.DAY_OF_MONTH, 3);

        Assert.assertEquals(0, Util.compareDate(now.getTime(), before.getTime(), "M").longValue());
        Assert.assertEquals(0, Util.compareDate(now.getTime(), before.getTime(), "Y").longValue());

        now.set(Calendar.YEAR, 2015);
        now.set(Calendar.MONTH, 2);
        now.set(Calendar.DAY_OF_MONTH, 3);

        before.set(Calendar.YEAR, 2013);
        before.set(Calendar.MONTH, 2);
        before.set(Calendar.DAY_OF_MONTH, 3);

        Assert.assertEquals(24, Util.compareDate(now.getTime(), before.getTime(), "M").longValue());
        Assert.assertEquals(2, Util.compareDate(now.getTime(), before.getTime(), "Y").longValue());

    }
}
