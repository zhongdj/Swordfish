package net.madz.lifecycle.meta.instance;

import java.lang.reflect.Method;

import net.madz.lifecycle.meta.Instance;
import net.madz.lifecycle.meta.template.StateMetadata;

public interface StateInst extends Instance<StateMetadata> {

    Method stateGetter();

    Method stateSetter();
}
