package net.madz.lifecycle.syntax;

import static org.junit.Assert.assertEquals;

import net.madz.lifecycle.SyntaxErrors;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.utils.BundleUtils;
import net.madz.verification.VerificationFailure;

public class BaseMetaDataTest {

    public BaseMetaDataTest() {
        super();
    }

    protected String getMessage(String errorCode, Object... args) {
        return BundleUtils.getBundledMessage(StateMachineMetaBuilder.class, SyntaxErrors.SYNTAX_ERROR_BUNDLE, errorCode, args);
    }

    protected void assertFailure(VerificationFailure failure, String errorCode, Object... args) {
        System.out.println("ExpectedErrorCode:" + errorCode + ",ActualErrorCode:" + failure.getErrorCode());
        assertEquals(errorCode, failure.getErrorCode());
        final String expectedMessage = getMessage(errorCode, args);
        System.out.println("ExpectedMessages:" + expectedMessage + "\n" + "  FailureMessage:" + failure.getErrorMessage(null));
        assertEquals(expectedMessage, failure.getErrorMessage(null));
    }
}