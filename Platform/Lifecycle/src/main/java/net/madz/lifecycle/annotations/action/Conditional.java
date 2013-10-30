package net.madz.lifecycle.annotations.action;


public @interface Conditional {

    Class<? extends ConditionalTransition<?>> judger();

    boolean postValidate() default false;

    Class<?> condition();
}
