package net.madz.scheduling;

import net.madz.core.exceptions.AppServiceException;
import net.madz.scheduling.to.ServiceOrderTO;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class OperationBeanTest extends OperationBeanTestBase {

    @BeforeClass
    public static void prepareData() {
    }

    @AfterClass
    public static void deleteData() {
    }

    @Test
    public void testAllocateResourceTo() throws AppServiceException {
        ServiceOrderTO orderTO = bean.allocateResourceTo(1L, 1L, 1L, 30D);
        System.out.println(orderTO);
    }
}
