package net.madz.test.stochastic.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import net.madz.test.stochastic.core.impl.dimensions.AbstractDimension;
import net.madz.test.stochastic.core.report.Cell;
import net.madz.test.stochastic.core.report.MergeableCell;
import net.madz.test.stochastic.utilities.ScriptLexicalAnalyzer;

public class DynamicCaseContext {

    private static final ThreadLocal<HashMap<String, Object>> localVariables = new ThreadLocal<HashMap<String, Object>>();
    private final ArrayList<IExpectation> expectations = new ArrayList<IExpectation>();
    private final ArrayList<AbstractDimension> dimensions = new ArrayList<AbstractDimension>();
    private final List<DimensionValuePair> targetStateList = new ArrayList<DimensionValuePair>();
    private final long start;
    private long end;
    private boolean success;
    private Throwable failCause;
    private boolean skipped;
    private String caseName;
    private static Comparator<DimensionValuePair> comparator = new Comparator<DimensionValuePair>() {

        @Override
        public int compare(DimensionValuePair one, DimensionValuePair other) {
            return other.getDimension().getPriority() - one.getDimension().getPriority();
        }
    };

    public DynamicCaseContext(List<DimensionValuePair> set) {
        targetStateList.addAll(set);
        Collections.sort(targetStateList, comparator);
        start = System.currentTimeMillis();
    }

    public List<IExpectation> getExpectations() {
        return Collections.unmodifiableList(expectations);
    }

    public List<AbstractDimension> getDimensions() {
        return Collections.unmodifiableList(dimensions);
    }

    public List<DimensionValuePair> getTargetStateList() {
        return Collections.unmodifiableList(targetStateList);
    }

    public void addExpectation(IExpectation positive) {
        expectations.add(positive);
    }

    public void debug() {
        final StringBuilder buffer = new StringBuilder("\nPerforming test with following Target State Set:\n");
        for ( DimensionValuePair targetState : targetStateList ) {
            buffer.append(targetState.getDimension().getClass().getSimpleName());
            buffer.append("@[");
            buffer.append(targetState.getDimension().getDottedName()).append(":");
            buffer.append(targetState.getChoice()).append("]\t");
        }
        buffer.append("\n\tExpecting: \n");
        for ( IExpectation expect : expectations ) {
            buffer.append(expect);
        }
        System.out.println(buffer.toString());
    }

    public void pass() {
        end = System.currentTimeMillis();
        success = true;
        skipped = false;
    }

    public void fail(Throwable ex) {
        end = System.currentTimeMillis();
        success = false;
        skipped = false;
        failCause = ex;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public boolean isSuccess() {
        return success;
    }

    public Throwable getFailCause() {
        return failCause;
    }

    public void report() {
        if ( skipped ) {
            System.out.println("Test Skipped.");
        } else if ( success ) {
            System.out.println("Test Passed.");
        } else {
            System.out.println("Test Failed.");
            if ( null != failCause ) {
                System.out.println("\t With Exception.");
                failCause.printStackTrace();
            }
        }
    }

    public void skip() {
        skipped = true;
    }

    public Cell[] getReportHeader() {
        final ArrayList<Cell> result = new ArrayList<Cell>();
        result.add(new MergeableCell("Result", 0, 0, 0, 1));
        result.add(new MergeableCell("Reason", 1, 0, 1, 1));
        int columnIndex = 2;
        for ( DimensionValuePair state : getEnvironmentLevelTargetStates() ) {
            final ArrayList<String> validationList = new ArrayList<String>();
            final String[] values = state.getDimension().values();
            for ( String validation : values ) {
                validationList.add(validation);
            }
            result.add(new MergeableCell(state.getDimension().getAlias(), validationList, columnIndex, 0, columnIndex, 1));
            columnIndex++;
        }
        for ( DimensionValuePair state : getRelationLevelTargetStates() ) {
            final ArrayList<String> validationList = new ArrayList<String>();
            final String[] values = state.getDimension().values();
            for ( String validation : values ) {
                validationList.add(validation);
            }
            result.add(new MergeableCell(state.getDimension().getAlias(), validationList, columnIndex, 0, columnIndex, 1));
            columnIndex++;
        }
        for ( List<DimensionValuePair> states : getObjectLevelTargetStates() ) {
            final int subColumns = states.size();
            if ( 0 >= subColumns ) {
                continue;
            }
            final IObjectDimension d = (IObjectDimension) states.get(0).getDimension();
            final String objectName = ScriptLexicalAnalyzer.stripVariablePlaceholder(d.getOneExpression());
            result.add(new MergeableCell(objectName, columnIndex, 0, columnIndex + subColumns - 1, 0));
            int subCounter = 0;
            for ( DimensionValuePair state : states ) {
                final ArrayList<String> validationList = new ArrayList<String>();
                final String[] values = state.getDimension().values();
                for ( String validation : values ) {
                    validationList.add(validation);
                }
                result.add(new Cell(state.getDimension().getAlias(), validationList, columnIndex + ( subCounter++ ), 1));
            }
            columnIndex += subColumns;
        }
        result.add(new MergeableCell("Expectation", columnIndex, 0, columnIndex, 1));
        return result.toArray(new Cell[0]);
    }

    public Cell[] getReportRow(int rowNumber) {
        final ArrayList<Cell> result = new ArrayList<Cell>();
        final String resultMessage;
        final String errorMessage;
        if ( skipped ) {
            resultMessage = "Skipped";
            errorMessage = "";
        } else if ( success ) {
            resultMessage = "Passed";
            errorMessage = "";
        } else {
            resultMessage = "Failed";
            errorMessage = formatError(this.failCause);
        }
        result.add(new Cell(resultMessage, 0, rowNumber));
        final Cell errorReasonCell = new Cell(errorMessage, 1, rowNumber);
        errorReasonCell.setWrapped(true);
        result.add(errorReasonCell);
        int columnIndex = 2;
        for ( DimensionValuePair state : getEnvironmentLevelTargetStates() ) {
            final ArrayList<String> validationList = new ArrayList<String>();
            final String[] values = state.getDimension().values();
            for ( String validation : values ) {
                validationList.add(validation);
            }
            result.add(new Cell(state.getChoice(), validationList, columnIndex, rowNumber));
            columnIndex++;
        }
        for ( DimensionValuePair state : getRelationLevelTargetStates() ) {
            final ArrayList<String> validationList = new ArrayList<String>();
            final String[] values = state.getDimension().values();
            for ( String validation : values ) {
                validationList.add(validation);
            }
            result.add(new Cell(state.getChoice(), validationList, columnIndex, rowNumber));
            columnIndex++;
        }
        for ( List<DimensionValuePair> states : getObjectLevelTargetStates() ) {
            final int subColumns = states.size();
            if ( 0 >= subColumns ) {
                continue;
            }
            final IObjectDimension d = (IObjectDimension) states.get(0).getDimension();
            final String objectName = ScriptLexicalAnalyzer.stripVariablePlaceholder(d.getOneExpression());
            result.add(new Cell(objectName, columnIndex, rowNumber));
            int subCounter = 0;
            for ( DimensionValuePair state : states ) {
                final ArrayList<String> validationList = new ArrayList<String>();
                final String[] values = state.getDimension().values();
                for ( String validation : values ) {
                    validationList.add(validation);
                }
                result.add(new Cell(state.getChoice(), validationList, columnIndex + ( subCounter++ ), rowNumber));
            }
            columnIndex += subColumns;
        }
        final Cell expectCell = new Cell(formatExpectation(), columnIndex, rowNumber);
        expectCell.setWrapped(true);
        result.add(expectCell);
        return result.toArray(new Cell[0]);
    }

    private String formatExpectation() {
        final StringBuilder builder = new StringBuilder();
        for ( IExpectation expect : expectations ) {
            builder.append(expect.getFormalizedString());
            builder.append("\n");
        }
        return builder.toString();
    }

    private String formatError(Throwable ex) {
        final StringBuffer buffer = new StringBuffer();
        for ( Throwable e = ex; e != null; e = ex.getCause() ) {
            buffer.append(ex.getMessage()).append("\n");
            StackTraceElement[] stackTrace = ex.getStackTrace();
            if ( null == stackTrace ) {
                continue;
            }
            for ( StackTraceElement stackTraceElement : stackTrace ) {
                buffer.append(stackTraceElement.toString()).append("\n");
            }
        }
        return buffer.toString();
    }

    private List<DimensionValuePair> getEnvironmentLevelTargetStates() {
        final ArrayList<DimensionValuePair> result = new ArrayList<DimensionValuePair>();
        for ( DimensionValuePair state : targetStateList ) {
            if ( state.getDimension() instanceof IGlobalDimension ) {
                result.add(state);
            }
        }
        return result;
    }

    private List<DimensionValuePair> getRelationLevelTargetStates() {
        final ArrayList<DimensionValuePair> result = new ArrayList<DimensionValuePair>();
        for ( DimensionValuePair state : targetStateList ) {
            if ( state.getDimension() instanceof IPairDimension ) {
                result.add(state);
            }
        }
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Collection<List<DimensionValuePair>> getObjectLevelTargetStates() {
        final HashMap<Object, List<DimensionValuePair>> resultMap = new HashMap<Object, List<DimensionValuePair>>();
        for ( DimensionValuePair state : targetStateList ) {
            if ( ( state.getDimension() instanceof IObjectDimension ) ) {
                final Object key = ( (IObjectDimension) state.getDimension() ).getDottedName();
                if ( !resultMap.containsKey(key) ) {
                    resultMap.put(key, new ArrayList<DimensionValuePair>());
                }
                final List<DimensionValuePair> list = resultMap.get(key);
                list.add(state);
            }
        }
        final Comparator c = new Comparator<DimensionValuePair>() {

            @Override
            public int compare(DimensionValuePair one, DimensionValuePair other) {
                return one.getDimension().getAlias().compareTo(other.getDimension().getAlias());
            }
        };
        for ( List<DimensionValuePair> list : resultMap.values() ) {
            Collections.sort(list, c);
        }
        return resultMap.values();
    }

    public static Object getVariable(String key) {
        final Object result;
        initLocalVariables();
        if ( getLocalVariableMap().containsKey(key) ) {
            result = getLocalVariableMap().get(key);
            // if (result instanceof StandardObject) {
            // Session session =
            // SpringHelper.getSessionFactory().getCurrentSession();
            // session.refresh(result);
            // }
            return result;
        }
        return null;
    }

    public static HashMap<String, Object> getLocalVariableMap() {
        return localVariables.get();
    }

    public static void registerLocalVariable(String key, Object value) {
        initLocalVariables();
        getLocalVariableMap().put(key, value);
    }

    public static void initLocalVariables() {
        if ( null == getLocalVariableMap() ) {
            localVariables.set(new HashMap<String, Object>());
        }
    }

    public static void removeLocalVariable(String key) {
        initLocalVariables();
        getLocalVariableMap().remove(key);
    }

    public static void clearLocalVariables() {
        localVariables.remove();
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        if ( 31 < caseName.length() ) {
            caseName = caseName.substring(caseName.length() - 31);
        }
        this.caseName = caseName;
    }

    public DimensionValuePair findDimensionValuePair(String dottedName) {
        for ( DimensionValuePair pair : targetStateList ) {
            if ( pair.getDimension().getDottedName().equalsIgnoreCase(dottedName) ) {
                return pair;
            }
        }
        throw new IllegalArgumentException("can't find dimension with given dotted name = " + dottedName);
    }

    public DimensionValuePair findDimensionValuePair(String dottedName, boolean ignoreException) {
        try {
            return findDimensionValuePair(dottedName);
        } catch (RuntimeException e) {
            if ( ignoreException ) {
                return null;
            } else {
                throw e;
            }
        }
    }
}
