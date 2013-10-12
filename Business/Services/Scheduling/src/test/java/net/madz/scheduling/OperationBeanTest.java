package net.madz.scheduling;

import net.madz.core.exceptions.AppServiceException;
import net.madz.scheduling.to.ServiceOrderTO;
import net.madz.test.MadzTestRunner;
import net.madz.test.annotations.NewTenant;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(MadzTestRunner.class)
@NewTenant
public class OperationBeanTest extends OperationBeanTestBase {

    @BeforeClass
    public static void prepareData() {
        
    }

    @AfterClass
    public static void deleteData() {
    }

    @Test
    public void testAllocateResourceTo_positive() throws AppServiceException {
        long summaryPlanId = 1L;
        Long plantResourceId = 1L;
        Long concreteTruckId = 1L;
        ServiceOrderTO orderTO = bean.allocateResourceTo(summaryPlanId, plantResourceId, concreteTruckId, 30D);
        System.out.println(orderTO);
    }
}
