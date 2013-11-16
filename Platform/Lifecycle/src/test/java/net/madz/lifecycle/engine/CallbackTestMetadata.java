package net.madz.lifecycle.engine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.madz.lifecycle.LifecycleContext;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.Condition;
import net.madz.lifecycle.annotations.action.ConditionSet;
import net.madz.lifecycle.annotations.action.Conditional;
import net.madz.lifecycle.annotations.action.ConditionalTransition;
import net.madz.lifecycle.annotations.callback.PostStateChange;
import net.madz.lifecycle.annotations.callback.PreStateChange;
import net.madz.lifecycle.annotations.relation.InboundWhile;
import net.madz.lifecycle.annotations.relation.RelateTo;
import net.madz.lifecycle.annotations.relation.Relation;
import net.madz.lifecycle.annotations.relation.RelationSet;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.engine.CallbackTestMetadata.InvoiceStateMachineMeta.Conditions;
import net.madz.lifecycle.engine.CallbackTestMetadata.InvoiceStateMachineMeta.Conditions.Payable;
import net.madz.lifecycle.engine.CallbackTestMetadata.InvoiceStateMachineMeta.States.PaidOff;
import net.madz.lifecycle.engine.CallbackTestMetadata.InvoiceStateMachineMeta.States.PartialPaid;
import net.madz.lifecycle.engine.CallbackTestMetadata.InvoiceStateMachineMeta.Utilities.PayableJudger;
import net.madz.verification.VerificationException;

import org.junit.BeforeClass;

public class CallbackTestMetadata extends EngineTestBase {

    @BeforeClass
    public static void setup() throws VerificationException {
        registerMetaFromClass(CallbackTestMetadata.class);
    }

    @StateMachine
    static interface CallbackStateMachine {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = Transitions.Start.class, value = { States.Started.class })
            static interface New {}
            @Function(transition = Transitions.Finish.class, value = { States.Finished.class })
            static interface Started {}
            @End
            static interface Finished {}
        }
        @TransitionSet
        static interface Transitions {

            static interface Start {}
            static interface Finish {}
        }
    }
    @LifecycleMeta(CallbackStateMachine.class)
    static class CallbackObjectBase extends ReactiveObject {

        public CallbackObjectBase() {
            initialState(CallbackStateMachine.States.New.class.getSimpleName());
        }

        protected int callbackInvokeCounter = 0;

        @Transition
        public void start() {}

        @Transition
        public void finish() {}

        public int getCallbackInvokeCounter() {
            return this.callbackInvokeCounter;
        }
    }
    @LifecycleMeta(CallbackStateMachine.class)
    static class PreCallbackFromAnyToAny extends CallbackObjectBase {

        @PreStateChange
        public void interceptPreStateChange(LifecycleContext<PreCallbackFromAnyToAny, String> context) {
            this.callbackInvokeCounter++;
        }
    }
    @LifecycleMeta(CallbackStateMachine.class)
    static class PreCallbackFromStartToAny extends CallbackObjectBase {

        @PreStateChange(from = CallbackStateMachine.States.Started.class)
        public void interceptPreStateChange(LifecycleContext<PreCallbackFromStartToAny, String> context) {
            this.callbackInvokeCounter++;
        }
    }
    @LifecycleMeta(CallbackStateMachine.class)
    static class PreCallbackFromAnyToStart extends CallbackObjectBase {

        @PreStateChange(to = CallbackStateMachine.States.Started.class)
        public void interceptPreStateChange(LifecycleContext<PreCallbackFromAnyToStart, String> context) {
            this.callbackInvokeCounter++;
        }
    }
    @StateMachine
    public static interface InvoiceStateMachineMeta {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = InvoiceStateMachineMeta.Transitions.Post.class, value = { InvoiceStateMachineMeta.States.Posted.class })
            static interface Draft {}
            @Functions({ @Function(transition = InvoiceStateMachineMeta.Transitions.Pay.class, value = { States.PartialPaid.class,
                    InvoiceStateMachineMeta.States.PaidOff.class }) })
            static interface Posted {}
            @Function(transition = InvoiceStateMachineMeta.Transitions.Pay.class, value = { States.PartialPaid.class,
                    InvoiceStateMachineMeta.States.PaidOff.class })
            static interface PartialPaid {}
            @End
            static interface PaidOff {}
        }
        @TransitionSet
        static interface Transitions {

            static interface Post {}
            @Conditional(condition = Payable.class, judger = PayableJudger.class, postEval = true)
            static interface Pay {}
        }
        @ConditionSet
        static interface Conditions {

            static interface Payable {

                BigDecimal getTotalAmount();

                BigDecimal getPayedAmount();
            }
        }
        static class Utilities {

            static class PayableJudger implements ConditionalTransition<Payable> {

                @Override
                public Class<?> doConditionJudge(Payable t) {
                    if ( 0 < t.getPayedAmount().compareTo(BigDecimal.ZERO) && 0 < t.getTotalAmount().compareTo(t.getPayedAmount()) ) {
                        return PartialPaid.class;
                    } else if ( 0 >= t.getTotalAmount().compareTo(t.getPayedAmount()) ) {
                        return PaidOff.class;
                    } else {
                        throw new IllegalStateException();
                    }
                }
            }
        }
    }
    @StateMachine
    public static interface InvoiceItemStateMachineMeta {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = InvoiceItemStateMachineMeta.Transitions.Pay.class, value = { InvoiceItemStateMachineMeta.States.Paid.class })
            static interface Unpaid {}
            @End
            @InboundWhile(on = { InvoiceStateMachineMeta.States.Posted.class, InvoiceStateMachineMeta.States.PartialPaid.class },
                    relation = InvoiceItemStateMachineMeta.Relations.ParentInvoice.class)
            static interface Paid {}
        }
        @TransitionSet
        static interface Transitions {

            static interface Pay {}
        }
        @RelationSet
        static interface Relations {

            @RelateTo(InvoiceStateMachineMeta.class)
            static interface ParentInvoice {}
        }
    }
    @LifecycleMeta(InvoiceStateMachineMeta.class)
    public static class Invoice extends ReactiveObject implements InvoiceStateMachineMeta.Conditions.Payable {

        private final BigDecimal totalAmount = new BigDecimal(0D);
        private final BigDecimal payedAmount = new BigDecimal(0D);
        private final List<InvoiceItem> items = new ArrayList<>();

        public Invoice(final BigDecimal totalAmount) {
            initialState(InvoiceStateMachineMeta.States.Draft.class.getSimpleName());
            this.totalAmount.add(totalAmount);
        }

        @Condition(InvoiceStateMachineMeta.Conditions.Payable.class)
        public InvoiceStateMachineMeta.Conditions.Payable getPayable() {
            return this;
        }

        @Override
        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        @Override
        public synchronized BigDecimal getPayedAmount() {
            return payedAmount;
        }

        @Transition
        public void post() {}

        @Transition(InvoiceStateMachineMeta.Transitions.Pay.class)
        @PostStateChange(to = InvoiceItemStateMachineMeta.States.Paid.class, relation = "items", mappedBy = "parent")
        public synchronized void onItemPaied(InvoiceItem item) {
            payedAmount.add(item.getPayedAmount());
        }

        public void addItem(InvoiceItem invoiceItem) {
            if ( !items.contains(invoiceItem) ) {
                items.add(invoiceItem);
            }
        }

        public List<InvoiceItem> getItems() {
            return Collections.unmodifiableList(items);
        }
    }
    @LifecycleMeta(InvoiceItemStateMachineMeta.class)
    public static class InvoiceItem extends ReactiveObject {

        private int seq;
        private BigDecimal amount;
        private BigDecimal payedAmount;
        private final Invoice parent;

        public InvoiceItem(Invoice parent, BigDecimal amount) {
            initialState(InvoiceItemStateMachineMeta.States.Unpaid.class.getSimpleName());
            this.amount = amount;
            this.parent = parent;
            this.seq = this.parent.getItems().size() + 1;
            this.parent.addItem(this);
        }

        @Transition
        public void pay(final BigDecimal amount) {
            if ( 0 < this.amount.compareTo(amount) ) {
                throw new IllegalArgumentException("paying amount is not enough to pay this item.");
            }
            this.payedAmount = amount;
        }

        public BigDecimal getPayedAmount() {
            return payedAmount;
        }

        @Relation(InvoiceItemStateMachineMeta.Relations.ParentInvoice.class)
        public Invoice getParent() {
            return this.parent;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( ( parent == null ) ? 0 : parent.hashCode() );
            result = prime * result + seq;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            InvoiceItem other = (InvoiceItem) obj;
            if ( parent == null ) {
                if ( other.parent != null ) return false;
            } else if ( !parent.equals(other.parent) ) return false;
            if ( seq != other.seq ) return false;
            return true;
        }
    }
    @LifecycleMeta(InvoiceItemStateMachineMeta.class)
    public static class InvoiceItemNonRelationalCallback extends ReactiveObject {

        private int seq;
        private BigDecimal amount;
        private BigDecimal payedAmount;
        private final InvoiceNonRelationalCallback parent;

        public InvoiceItemNonRelationalCallback(InvoiceNonRelationalCallback parent, BigDecimal amount) {
            initialState(InvoiceItemStateMachineMeta.States.Unpaid.class.getSimpleName());
            this.amount = amount;
            this.parent = parent;
            this.seq = this.parent.getItems().size() + 1;
            this.parent.addItem(this);
        }

        @Transition
        public void pay(BigDecimal amount) {
            if ( 0 < this.amount.compareTo(amount) ) {
                throw new IllegalArgumentException("paying amount is not enough to pay this item.");
            }
            this.payedAmount = amount;
            // parent.onItemPaied(this);
        }

        @PostStateChange(to = InvoiceItemStateMachineMeta.States.Paid.class)
        public void notifyParent(LifecycleContext<InvoiceItemNonRelationalCallback, String> context) {
            this.parent.onItemPaied(this);
        }

        public BigDecimal getPayedAmount() {
            return payedAmount;
        }

        @Relation(InvoiceItemStateMachineMeta.Relations.ParentInvoice.class)
        public InvoiceNonRelationalCallback getParent() {
            return this.parent;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( ( parent == null ) ? 0 : parent.hashCode() );
            result = prime * result + seq;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            InvoiceItemNonRelationalCallback other = (InvoiceItemNonRelationalCallback) obj;
            if ( parent == null ) {
                if ( other.parent != null ) return false;
            } else if ( !parent.equals(other.parent) ) return false;
            if ( seq != other.seq ) return false;
            return true;
        }
    }
    @LifecycleMeta(InvoiceStateMachineMeta.class)
    public static class InvoiceNonRelationalCallback extends ReactiveObject implements Conditions.Payable {

        private final BigDecimal totalAmount = new BigDecimal(0D);
        private final BigDecimal payedAmount = new BigDecimal(0D);
        private final List<InvoiceItemNonRelationalCallback> items = new ArrayList<>();

        public InvoiceNonRelationalCallback(final BigDecimal totalAmount) {
            initialState(InvoiceStateMachineMeta.States.Draft.class.getSimpleName());
            this.totalAmount.add(totalAmount);
        }

        @Condition(InvoiceStateMachineMeta.Conditions.Payable.class)
        public Conditions.Payable getPayable() {
            return this;
        }

        @Override
        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        @Override
        public synchronized BigDecimal getPayedAmount() {
            return payedAmount;
        }

        @Transition
        public void post() {}

        @Transition(InvoiceStateMachineMeta.Transitions.Pay.class)
        public synchronized void onItemPaied(InvoiceItemNonRelationalCallback item) {
            payedAmount.add(item.getPayedAmount());
        }

        public void addItem(InvoiceItemNonRelationalCallback invoiceItem) {
            if ( !items.contains(invoiceItem) ) {
                items.add(invoiceItem);
            }
        }

        public List<InvoiceItemNonRelationalCallback> getItems() {
            return Collections.unmodifiableList(items);
        }
    }
}
