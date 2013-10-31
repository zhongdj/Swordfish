package net.madz.lifecycle.meta;

import net.madz.verification.VerificationException;

public interface Template<T> {

    T newInstance(Class<?> clazz) throws VerificationException;
}
