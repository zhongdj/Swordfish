package net.madz.scheduling;

import net.madz.core.exceptions.AppServiceException;
import net.madz.core.exceptions.BONotFoundException;
import net.madz.scheduling.to.ServiceOrderTO;
import net.madz.test.MadzTestRunner;
import net.madz.test.annotations.FreeTrialTenant;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(MadzTestRunner.class)
@FreeTrialTenant
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

    @Test(expected = BONotFoundException.class)
    public void testAllocateResourceTo_ne_summaryPlanId_invalid() throws AppServiceException {
        long summaryPlanId = 2L;
        Long plantResourceId = 1L;
        Long concreteTruckId = 1L;
        try {
            bean.allocateResourceTo(summaryPlanId, plantResourceId, concreteTruckId, 30D);
            fail("BONotFoundException expected");
        } catch (BONotFoundException be) {
            assertEquals("100-0001", be.getErrorCode());
            throw be;
        } 
    }

    @Test(expected = BONotFoundException.class)
    public void testAllocateResourceTo_ne_planResourceId_invalid() throws AppServiceException {
        long summaryPlanId = 1L;
        Long plantResourceId = 2L;
        Long concreteTruckId = 1L;
        try {
            bean.allocateResourceTo(summaryPlanId, plantResourceId, concreteTruckId, 30D);
            fail("BONotFoundException expected");
        } catch (BONotFoundException be) {
            assertEquals("100-0006", be.getErrorCode());
            throw be;
        }
    }

    @Test(expected = BONotFoundException.class)
    public void testAllocateResourceTo_ne_concreteResourceId_invalid() throws AppServiceException {
        long summaryPlanId = 1L;
        Long plantResourceId = 1L;
        Long concreteTruckId = 2L;
        try {
            bean.allocateResourceTo(summaryPlanId, plantResourceId, concreteTruckId, 30D);
            fail("BONotFoundException expected");
        } catch (BONotFoundException be) {
            assertEquals("100-0004", be.getErrorCode());
            throw be;
        }
    }
}
