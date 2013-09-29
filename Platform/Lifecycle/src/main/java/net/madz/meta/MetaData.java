package net.madz.meta;

import net.madz.common.DottedPath;
import net.madz.verification.VerificationFailureSet;

public interface MetaData {
    DottedPath getDottedPath();

    MetaData getParent();

    void verifyMetaData(VerificationFailureSet verificationSet);
}
