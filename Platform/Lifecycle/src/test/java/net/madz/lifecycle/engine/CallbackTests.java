package net.madz.lifecycle.engine;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class CallbackTests extends CallbackTestMetadata {

    @Test
    public void test_standalone_pre_state_change_callback_from_any_to_any() {
        final PreCallbackFromAnyToAny o = new PreCallbackFromAnyToAny();
        assertEquals(0, o.getCallbackInvokeCounter());
        o.start();
        assertEquals(1, o.getCallbackInvokeCounter());
        o.finish();
        assertEquals(2, o.getCallbackInvokeCounter());
    }

    @Test
    public void test_standalone_pre_state_change_callback_from_started_to_any() {
        final PreCallbackFromStartToAny o = new PreCallbackFromStartToAny();
        assertEquals(0, o.getCallbackInvokeCounter());
        o.start();
        assertEquals(0, o.getCallbackInvokeCounter());
        o.finish();
        assertEquals(1, o.getCallbackInvokeCounter());
    }

    @Test
    public void test_standalone_pre_state_change_callback_from_any_to_start() {
        final PreCallbackFromAnyToStart o = new PreCallbackFromAnyToStart();
        assertEquals(0, o.getCallbackInvokeCounter());
        o.start();
        assertEquals(1, o.getCallbackInvokeCounter());
        o.finish();
        assertEquals(1, o.getCallbackInvokeCounter());
    }

    @Test
    public void test_standalone_post_state_change_callback_from_any_to_any() {
        final PostCallbackFromAnyToAny o = new PostCallbackFromAnyToAny();
        assertEquals(0, o.getCallbackInvokeCounter());
        o.start();
        assertEquals(1, o.getCallbackInvokeCounter());
        o.finish();
        assertEquals(2, o.getCallbackInvokeCounter());
    }

    @Test
    public void test_standalone_post_state_change_callback_from_started_to_any() {
        final PostCallbackFromStartToAny o = new PostCallbackFromStartToAny();
        assertEquals(0, o.getCallbackInvokeCounter());
        o.start();
        assertEquals(0, o.getCallbackInvokeCounter());
        o.finish();
        assertEquals(1, o.getCallbackInvokeCounter());
    }

    @Test
    public void test_standalone_post_state_change_callback_from_any_to_start() {
        final PostCallbackFromAnyToStart o = new PostCallbackFromAnyToStart();
        assertEquals(0, o.getCallbackInvokeCounter());
        o.start();
        assertEquals(1, o.getCallbackInvokeCounter());
        o.finish();
        assertEquals(1, o.getCallbackInvokeCounter());
    }

    @Test
    public void test_non_relational_callback_post_state_change() {
        final InvoiceNonRelationalCallback invoice = new InvoiceNonRelationalCallback(new BigDecimal(10000.0D));
        final InvoiceItemNonRelationalCallback itemOne = new InvoiceItemNonRelationalCallback(invoice, new BigDecimal(
                4000.0D));
        final InvoiceItemNonRelationalCallback itemTwo = new InvoiceItemNonRelationalCallback(invoice, new BigDecimal(
                4000.0D));
        final InvoiceItemNonRelationalCallback itemThree = new InvoiceItemNonRelationalCallback(invoice,
                new BigDecimal(2000.0D));
        invoice.post();
        assertState(InvoiceStateMachineMeta.States.Posted.class, invoice);
        assertState(InvoiceItemStateMachineMeta.States.Unpaid.class, itemOne);
        itemOne.pay(new BigDecimal(4000.0D));
        assertState(InvoiceItemStateMachineMeta.States.Paid.class, itemOne);
        assertState(InvoiceStateMachineMeta.States.PartialPaid.class, invoice);
        itemTwo.pay(new BigDecimal(4000.0D));
        assertState(InvoiceItemStateMachineMeta.States.Paid.class, itemTwo);
        assertState(InvoiceStateMachineMeta.States.PartialPaid.class, invoice);
        itemThree.pay(new BigDecimal(2000.0D));
        assertState(InvoiceItemStateMachineMeta.States.Paid.class, itemThree);
        assertState(InvoiceStateMachineMeta.States.PaidOff.class, invoice);
    }

    @Test
    public void test_relational_callback_post_state_change() {
        final Invoice invoice = new Invoice(new BigDecimal(10000.0D));
        final InvoiceItem itemOne = new InvoiceItem(invoice, new BigDecimal(4000.0D));
        final InvoiceItem itemTwo = new InvoiceItem(invoice, new BigDecimal(4000.0D));
        final InvoiceItem itemThree = new InvoiceItem(invoice, new BigDecimal(2000.0D));
        invoice.post();
        assertState(InvoiceStateMachineMeta.States.Posted.class, invoice);
        assertState(InvoiceItemStateMachineMeta.States.Unpaid.class, itemOne);
        itemOne.pay(new BigDecimal(4000.0D));
        assertState(InvoiceItemStateMachineMeta.States.Paid.class, itemOne);
        assertState(InvoiceStateMachineMeta.States.PartialPaid.class, invoice);
        itemTwo.pay(new BigDecimal(4000.0D));
        assertState(InvoiceItemStateMachineMeta.States.Paid.class, itemTwo);
        assertState(InvoiceStateMachineMeta.States.PartialPaid.class, invoice);
        itemThree.pay(new BigDecimal(2000.0D));
        assertState(InvoiceItemStateMachineMeta.States.Paid.class, itemThree);
        assertState(InvoiceStateMachineMeta.States.PaidOff.class, invoice);
    }

    @Test
    public void test_order_object_callback_definition () {
        final OrderObject order = new OrderObject();
        assertEquals(0, order.getCount());
        order.pay();
        assertEquals(1, order.getCount());
        order.deliver();
        assertEquals(2, order.getCount());
    }
    @Test
    public void test_extends_callback_definition() {
        final BigProductOrderObjectWithExtendsCallbackDefinition bigOrder = new BigProductOrderObjectWithExtendsCallbackDefinition();
        assertEquals(0, bigOrder.getCount());
        bigOrder.pay();
        assertEquals(1, bigOrder.getCount());
        bigOrder.deliver();
        assertEquals(2, bigOrder.getCount());
        bigOrder.install();
        assertEquals(3, bigOrder.getCount());
    }

    @Test
    public void test_overrides_callback_definition() {
        final BigProductOrderObjectWithOverridesCallbackDefinition bigOrder = new BigProductOrderObjectWithOverridesCallbackDefinition();
        assertEquals(0, bigOrder.getCount());
        bigOrder.pay();
        assertEquals(1, bigOrder.getCount());
        bigOrder.deliver();
        assertEquals(1, bigOrder.getCount());
        bigOrder.install();
        assertEquals(2, bigOrder.getCount());
    }
}
