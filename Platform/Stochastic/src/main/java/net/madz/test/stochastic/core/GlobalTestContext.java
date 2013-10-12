package net.madz.test.stochastic.core;

import java.lang.reflect.Method;
import java.util.HashMap;

import net.madz.test.stochastic.utilities.IFunction;
import net.madz.test.stochastic.utilities.ScriptLexicalAnalyzer;

public class GlobalTestContext {

    public static final String ASSERT_FUNCTIONS_PREFIX = "assert.functions";
    public static final String SDFC_CUSTOM_OBJECT_PREFIX = "sdfc.customobject";
    public static final String SFDC_CUSTOM_SETTING_PREFIX = "sfdc.customsetting";
    public static final String GLOABLE_FUNCTION_PREFIX = "global.function";
    public static final String CUSTOM_FUNCTION_PREFIX = "custom.function";
    public static final GlobalTestContext INSTANCE = new GlobalTestContext();
    private final ThreadLocal<HashMap<String, IFunction>> localFunctions = new ThreadLocal<HashMap<String, IFunction>>();
    private final HashMap<String, IFunction> functions = new HashMap<String, IFunction>();
    private static final ThreadLocal<Integer> indent = new ThreadLocal<Integer>();

    public Object getVariable(String key) {
        Object result = DynamicCaseContext.getVariable(key);
        return result;
    }

    public static GlobalTestContext getInstance() {
        return INSTANCE;
    }

    private GlobalTestContext() {
        loadFunctions();
    }

    public Integer getIndent() {
        if ( null == indent.get() ) {
            indent.set(1);
        }
        return indent.get();
    }

    public void decreaseIndent() {
        indent.set(getIndent() - 1);
    }

    public void increaseIndent() {
        indent.set(getIndent() + 1);
    }

    public void clearLocalVariables() {
        DynamicCaseContext.clearLocalVariables();
    }

    private synchronized void loadFunctions() {
        // final List<Class<? extends ITestFunctions>> actionClasses =
        // TestFunctionRegistry.getActionClasses();
        // Method[] actionMethods = null;
        // ActionMeta actionMeta = null;
        // String key = null;
        //
        // Object actionInstance = null;
        // for (Class<? extends ITestFunctions> actionClass : actionClasses) {
        // Class iterateClass = actionClass;
        // try {
        // actionInstance = actionClass.newInstance();
        // } catch (Exception ex) {
        // throw new IllegalStateException(ex);
        // }
        // for (; null != iterateClass; iterateClass =
        // iterateClass.getSuperclass()) {
        // actionMethods = actionClass.getMethods();
        // for (final Method method : actionMethods) {
        // actionMeta = method.getAnnotation(ActionMeta.class);
        // if (null == actionMeta) {
        // continue;
        // }
        // if (!actionMeta.zuoraType().equals(StandardObject.class)) {
        // key = actionMeta.zuoraType().getName() + "." +
        // actionMeta.operation().name();
        // } else if (0 < actionMeta.sfdcType().trim().length()) {
        // key = SDFC_CUSTOM_OBJECT_PREFIX + "." +
        // actionMeta.operation().name();
        // } else if (0 < actionMeta.customSettingType().trim().length()) {
        // key = SFDC_CUSTOM_SETTING_PREFIX + "." +
        // actionMeta.operation().name();
        // } else if (0 < actionMeta.customName().trim().length()) {
        // key = CUSTOM_FUNCTION_PREFIX + "." + actionMeta.customName();
        // } else {
        // switch (actionMeta.operation()) {
        // case Create:
        // case Get:
        // case Touch:
        // case Update:
        // case Delete:
        // case Upsert:
        // continue;
        // case Sync:
        // case Cleanup:
        // key = GLOABLE_FUNCTION_PREFIX + "." + actionMeta.operation().name();
        // break;
        // default:
        // key = ASSERT_FUNCTIONS_PREFIX + "." + actionMeta.operation().name();
        // break;
        // }
        // }
        //
        // final Object actionCopy = actionInstance;
        // final IFunction action = (IFunction)
        // Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {
        // IFunction.class }, new InvocationHandler() {
        //
        // @Override
        // public Object invoke(Object proxy, Method executeMethod, Object[]
        // args) throws Exception {
        // print(method);
        // if (null == args) {
        // return method.invoke(actionCopy);
        // }
        // Object[] realArgs = (Object[]) args[0];
        // if (1 == realArgs.length) {
        // return method.invoke(actionCopy, realArgs[0]);
        // } else if (2 == realArgs.length) {
        // return method.invoke(actionCopy, realArgs[0], realArgs[1]);
        // } else if (3 == realArgs.length) {
        // return method.invoke(actionCopy, realArgs[0], realArgs[1],
        // realArgs[2]);
        // } else if (4 == realArgs.length) {
        // return method.invoke(actionCopy, realArgs[0], realArgs[1],
        // realArgs[2], realArgs[3]);
        // } else if (5 == realArgs.length) {
        // return method.invoke(actionCopy, realArgs[0], realArgs[1],
        // realArgs[2], realArgs[3], realArgs[4]);
        // } else if (6 == realArgs.length) {
        // return method.invoke(actionCopy, realArgs[0], realArgs[1],
        // realArgs[2], realArgs[3], realArgs[4], realArgs[5]);
        // } else {
        // throw new
        // UnsupportedOperationException("Only 6 arguments accepted.");
        // }
        // }
        // });
        //
        // functions.put(key, action);
        // }
        // }
        // }
    }

    public IFunction lookupFunction(final String key) {
        if ( null == localFunctions.get() ) {
            localFunctions.set(new HashMap<String, IFunction>());
        } else {
            if ( localFunctions.get().containsKey(key) ) {
                return localFunctions.get().get(key);
            }
        }
        synchronized (functions) {
            return functions.get(key);
        }
    }

    public void registerLocalVariable(String key, Object value) {
        DynamicCaseContext.registerLocalVariable(key, value);
    }

    public Object evaluateVariableNavigation(String with) {
        with = ScriptLexicalAnalyzer.preprocessNavigationExpression(with);
        final int first = with.indexOf('.');
        if ( -1 >= first ) {
            return getVariable(with);
        } else {
            final String key = with.substring(0, first);
            final Object variableValue = getVariable(key);
            if ( null == variableValue ) {
                throw new NullPointerException("Variable: " + key + " is NULL or Undefined.");
            }
            return ScriptLexicalAnalyzer.evaluateVariableNavigation(variableValue, with.substring(first + 1));
        }
    }

    /* package */void print(Method method) {
        // System.out.println("Invoking Method: " + method);
    }

    public void removeLocalVariable(String key) {
        DynamicCaseContext.removeLocalVariable(key);
    }

    public void registerLocalFunctions(IFunctionRegistry functionRegistry) {
        if ( null == localFunctions.get() ) {
            localFunctions.set(new HashMap<String, IFunction>());
        }
        functionRegistry.onLoadFuctions(localFunctions.get());
    }

    public void clearLocalFunctions() {
        localFunctions.remove();
    }
}
