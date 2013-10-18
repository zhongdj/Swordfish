package net.madz.test.rest;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

import net.madz.test.rest.annotations.Extractor;
import net.madz.test.rest.annotations.MergeField;
import net.madz.test.rest.annotations.UriParam;

import org.eclipse.jetty.util.ajax.JSON;

import com.eclipsesource.restfuse.Response;

public class VariableContext {

    private static Logger logger = Logger.getLogger(VariableContext.class.getName());
    private static final ThreadLocal<Integer> indent = new ThreadLocal<Integer>();
    private static final ThreadLocal<Map<String, Object>> localVariables = new ThreadLocal<Map<String, Object>>();
    private static final ThreadLocal<Map<String, Object>> staticVariables = new ThreadLocal<Map<String, Object>>();
    private static final VariableContext INSTANCE = new VariableContext();

    public Object getVariable(String key) {
        final Map<String, Object> localVars = localVariables.get();
        if ( null != localVars ) {
            Object result = localVars.get(key);
            if ( null != result ) {
                return result;
            }
        }
        final Map<String, Object> staticVars = staticVariables.get();
        if ( null == staticVars ) {
            return null;
        }
        return staticVars.get(key);
    }

    public void registerLocalVariable(String key, Object value) {
        logger.info("registering local variable: key = " + key + ", value = " + value);
        Map<String, Object> localVars = localVariables.get();
        if ( null == localVars ) {
            localVars = new HashMap<String, Object>();
            localVariables.set(localVars);
        }
        localVars.put(key, value);
    }

    public void registerStaticVariable(String key, Object value) {
        logger.info("registering static variable: key = " + key + ", value = " + value);
        Map<String, Object> staticVars = staticVariables.get();
        if ( null == staticVars ) {
            staticVars = new HashMap<String, Object>();
            staticVariables.set(staticVars);
        }
        staticVars.put(key, value);
    }

    public static VariableContext getInstance() {
        return INSTANCE;
    }

    private VariableContext() {
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
        localVariables.remove();
    }

    public void clearStaticVariables() {
        staticVariables.remove();
    }

    private ThreadLocal<Extractor[]> extractors = new ThreadLocal<Extractor[]>();
    private ThreadLocal<MergeField[]> mergeFields = new ThreadLocal<>();
    private ThreadLocal<UriParam[]> uriParams = new ThreadLocal<>();
    private ThreadLocal<Stack<Boolean>> scopeStack = new ThreadLocal<Stack<Boolean>>();

    public void pushScope(boolean staticScope) {
        if ( null == scopeStack.get() ) {
            scopeStack.set(new Stack<Boolean>());
        }
        scopeStack.get().push(staticScope);
    }

    public void popScope() {
        if ( null == scopeStack.get() ) {
            return;
        }
        scopeStack.get().pop();
    }

    // Happen 1st
    public void setVariableBindings(UriParam[] uriParams, MergeField[] mergeFields, Extractor[] extractors) {
        this.uriParams.set(uriParams);
        this.mergeFields.set(mergeFields);
        this.extractors.set(extractors);
    }

    // Happen 2nd
    public void processResponse(Response response) {
        if ( null == response ) {
            logger.warning("response is null");
            return;
        }
        if ( null == this.extractors.get() ) {
            return;
        }
        for ( Extractor ex : this.extractors.get() ) {
            process(ex, response);
        }
    }

    // Happen 3rd
    public void clearVariableBindings() {
        this.extractors.remove();
    }

    private void process(Extractor ex, Response response) {
        Object object = JSON.parse(response.getBody());
        logger.info("evaluating expression: " + ex.key() + " from response: " + response.getBody());
        Object result = evaluate(ex.key(), object);
        logger.info("evaluating expression: " + ex.key() + " with result evaluated: " + result);
        if ( null == this.scopeStack.get() ) {
            throw new NullPointerException(
                    "Push test scope: true for static, false for non-static before running InnerHttpUnitRunner");
        }
        final Boolean staticScope = this.scopeStack.get().peek();
        if ( staticScope ) {
            registerStaticVariable(ex.var(), result);
        } else {
            registerLocalVariable(ex.var(), result);
        }
    }

    @SuppressWarnings("rawtypes")
    private Object evaluate(String expression, Object jsonResult) {
        if ( null == expression ) {
            throw new NullPointerException("Expression should not be null! ");
        }
        if ( !( jsonResult instanceof Map ) && ( !jsonResult.getClass().isArray() ) ) {
            // Ignore expression evaluation since jsonResult is a simple type
            return jsonResult;
        }
        if ( jsonResult instanceof Map ) {
            Map resultMap = (Map) jsonResult;
            if ( isSimple(expression) ) {
                if ( !resultMap.containsKey(expression) ) {
                    throw new IllegalStateException("JSON Result does not contain key: " + expression
                            + ", the expression is invalid.");
                }
                return resultMap.get(expression);
            } else if ( isNavigation(expression) ) {
                int firstDot = expression.indexOf(".");
                if ( expression.length() - 1 == firstDot ) {
                    throw new IllegalStateException("Expression: " + expression
                            + " is invalid. the '.' should not be the last character.");
                }
                String subExpression = expression.substring(0, firstDot);
                Object tmpResult = evaluate(subExpression, jsonResult);
                return evaluate(expression.substring(firstDot + 1), tmpResult);
            } else if ( isArray(expression) ) {
                final String arrayName = parseArrayName(expression);
                if ( 0 >= arrayName.length() ) {
                    throw new IllegalStateException("[index] ONLY support while JSON result is an array, but is: "
                            + resultMap);
                }
                int index = parseIndex(expression);
                // jsonResult should be Map
                Object array = resultMap.get(arrayName);
                if ( !array.getClass().isArray() ) {
                    throw new IllegalStateException("Array is expected, but returning with: " + array);
                }
                return Array.get(array, index);
            } else {
                throw new IllegalStateException();
            }
        } else {
            if ( !isArray(expression) ) {
                throw new IllegalStateException("JSON result is an array, but the expression is not. Expression: "
                        + expression);
            } else {
                int index = parseIndex(expression);
                // jsonResult should be Map
                return Array.get(jsonResult, index);
            }
        }
        // 1. simple value : id
        // 2. collection value : unitProjectIds[0]
        // 3. navigation value : contract.contact.name
    }

    private boolean isNavigation(String expression) {
        return expression.contains(".");
    }

    private int parseIndex(String expression) {
        final int left = expression.indexOf("[");
        final int right = expression.lastIndexOf("]");
        String index = expression.substring(left + 1, right);
        if ( left >= right - 1 ) {
            throw new IllegalStateException("For array expression, the '[' should be before ']'.");
        }
        return Integer.valueOf(index);
    }

    private String parseArrayName(String expression) {
        final int left = expression.indexOf("[");
        return expression.substring(0, left);
    }

    private boolean isArray(String expression) {
        return expression.contains("[");
    }

    private boolean isSimple(String expression) {
        return !isNavigation(expression) && !isArray(expression);
    }

    public UriParam[] getUriParams() {
        return this.uriParams.get();
    }

    public MergeField[] getMergeFields() {
        return this.mergeFields.get();
    }
}
