package com.riversoft.dtask;

import com.riversoft.dtask.office.OfficeServiceHelper;

import org.apache.commons.io.FileUtils;
import org.artofsolving.jodconverter.office.OfficeUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by exizhai on 13/11/2014.
 */
public class OfficeServiceHelperTest {

	@Ignore
    public void testStart(){
        Assert.assertFalse(OfficeServiceHelper.getInstance().isRunning(2002));

        OfficeServiceHelper.getInstance().start(OfficeUtils.getDefaultOfficeHome(), FileUtils.getTempDirectory(), 2002);

        Assert.assertTrue(OfficeServiceHelper.getInstance().isRunning(2002));
    }

	@Ignore
    public void testStop(){
        OfficeServiceHelper.getInstance().start(OfficeUtils.getDefaultOfficeHome(), FileUtils.getTempDirectory(), 2002);

        Assert.assertTrue(OfficeServiceHelper.getInstance().isRunning(2002));

        OfficeServiceHelper.getInstance().stop(OfficeUtils.getDefaultOfficeHome(), FileUtils.getTempDirectory(), 2002);

        try {
            Thread.sleep(3000l);
        } catch (InterruptedException e) {
        }

        Assert.assertFalse(OfficeServiceHelper.getInstance().isRunning(2002));
    }

}
