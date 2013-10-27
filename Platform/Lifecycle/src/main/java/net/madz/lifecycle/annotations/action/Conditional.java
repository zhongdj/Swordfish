package net.madz.lifecycle.annotations.action;


public @interface Conditional {
    Class<? extends ConditionalTransition<?>> condition();
    boolean postValidate() default false;
}
