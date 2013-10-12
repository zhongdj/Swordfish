package net.madz.test.stochastic.core.impl.explorers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.madz.test.stochastic.core.DimensionValuePair;
import net.madz.test.stochastic.core.DynamicCaseContext;
import net.madz.test.stochastic.core.ICombinationHandler;
import net.madz.test.stochastic.core.IConstraint;
import net.madz.test.stochastic.core.IDimension;
import net.madz.test.stochastic.core.IExploreStrategy;
import net.madz.test.stochastic.core.IFilter;

public class ExhaustiveExploreStrategy implements IExploreStrategy {

    public class DimensionIterator {

        public final int size;
        private int index;
        private final IDimension dimension;
        private final IFilter filter;
        private final String[] filteredValues;

        public DimensionIterator(IDimension dimension) {
            this(dimension, null);
        }

        public DimensionIterator(IDimension dimension, IFilter filter) {
            this.filter = filter;
            this.dimension = dimension;
            if ( null == this.filter ) {
                this.filteredValues = dimension.values();
            } else {
                final String[] includes = this.filter.includes();
                final String[] excludes = this.filter.excludes();
                final ArrayList<String> includeList = new ArrayList<String>();
                if ( 0 < includes.length ) {
                    for ( String include : includes ) {
                        String includeValue = include;
                        // challenge if the includeValue is invalid
                        includeList.add(includeValue);
                    }
                    this.filteredValues = includeList.toArray(new String[0]);
                } else if ( 0 < excludes.length ) {
                    NEXT_VALUE: for ( String value : dimension.values() ) {
                        for ( String exclude : excludes ) {
                            String excludeValue = exclude;
                            if ( excludeValue.equals(value) ) {
                                continue NEXT_VALUE;
                            } else {
                                continue;
                            }
                        }
                        includeList.add(value);
                    }
                    this.filteredValues = includeList.toArray(new String[0]);
                } else {
                    this.filteredValues = dimension.values();
                }
            }
            this.size = filteredValues.length;
            this.index = -1;
        }

        public boolean hasNext() {
            if ( index + 1 < size ) {
                return true;
            } else {
                return false;
            }
        }

        public void next() {
            index++;
        }

        public String getValue() {
            return filteredValues[index];
        }

        public IDimension getDimension() {
            return dimension;
        }

        public void reset() {
            index = -1;
        }
    }

    private ICombinationHandler callback;

    @Override
    public void setCombinationHandler(ICombinationHandler handler) {
        this.callback = handler;
    }

    @Override
    public void doExplore(List<IDimension> dimensionsList, IFilter[] filters, IConstraint constraint, final Map<String, Integer> dimensionIndex) {
        final ArrayList<DimensionIterator> iterators = new ArrayList<DimensionIterator>();
        final HashMap<IDimension, IFilter> filterMap = new HashMap<IDimension, IFilter>();
        NEXT_FILTER: for ( IFilter filter : filters ) {
            for ( IDimension dimension : dimensionsList ) {
                String dimensionDottedName = dimension.getDottedName();
                if ( filter.dimension().equals(dimensionDottedName) ) {
                    filterMap.put(dimension, filter);
                    continue NEXT_FILTER;
                } else {
                    continue;
                }
            }
        }
        Collections.sort(dimensionsList, new Comparator<IDimension>() {

            @Override
            public int compare(IDimension o1, IDimension o2) {
                return dimensionIndex.get(o1.getDottedName()) - dimensionIndex.get(o2.getDottedName());
            }
        });
        for ( IDimension dimension : dimensionsList ) {
            final IFilter filter = filterMap.get(dimension);
            if ( null == filter ) {
                iterators.add(new DimensionIterator(dimension));
            } else {
                iterators.add(new DimensionIterator(dimension, filter));
            }
        }
        // final ArrayList<TargetState> firstTargetStateSet = new
        // ArrayList<TargetState>();
        // generateAllLeft(firstTargetStateSet, iterators, constraint);
        doExplore(new ArrayList<DimensionValuePair>(), iterators.listIterator(), constraint);
    }

    private void doExplore(List<DimensionValuePair> exploredList, ListIterator<DimensionIterator> allDimensionsIterator, IConstraint constraint) {
        if ( 0 < exploredList.size() ) {
            final String[] currentChoices = new String[exploredList.size()];
            for ( int i = 0; i < currentChoices.length; i++ ) {
                currentChoices[i] = exploredList.get(i).getChoice();
            }
            if ( null != constraint && constraint.skip(exploredList.size(), currentChoices) ) {
                return;
            }
        }
        if ( !allDimensionsIterator.hasNext() ) {
            try {
                callback.onCombinationFound(new DynamicCaseContext(new ArrayList<DimensionValuePair>(exploredList)));
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return;
        } else {
            final DimensionIterator currentDimensionIterator = allDimensionsIterator.next();
            while ( currentDimensionIterator.hasNext() ) {
                currentDimensionIterator.next();
                Object value = currentDimensionIterator.getValue();
                exploredList.add(new DimensionValuePair(currentDimensionIterator.getDimension(), String.valueOf(value)));
                doExplore(exploredList, allDimensionsIterator, constraint);
                exploredList.remove(exploredList.size() - 1);
            }
            currentDimensionIterator.reset();
            allDimensionsIterator.previous();
        }
    }

    private void generateAllLeft(ArrayList<DimensionValuePair> currentTargetStateSet, ArrayList<DimensionIterator> leftIterators, IConstraint constraint) {
        if ( 0 >= leftIterators.size() ) {
            return;
        }
        // Left dimension iterators.
        final ArrayList<DimensionIterator> iteratorsCopy = new ArrayList<DimensionIterator>(leftIterators);
        final DimensionIterator iterator = iteratorsCopy.remove(0);
        ArrayList<DimensionValuePair> copyToLoadNextValueOfCurrentDimensionValue = null;
        copyToLoadNextValueOfCurrentDimensionValue = handle(currentTargetStateSet, iteratorsCopy, iterator, constraint);
        while ( iterator.hasNext() ) {
            iterator.next();
            copyToLoadNextValueOfCurrentDimensionValue = handle(copyToLoadNextValueOfCurrentDimensionValue, iteratorsCopy, iterator, constraint);
        }
        iterator.reset();
    }

    private ArrayList<DimensionValuePair> handle(ArrayList<DimensionValuePair> currentTargetStateSet, ArrayList<DimensionIterator> leftIterators,
            DimensionIterator iterator, IConstraint constraint) {
        final ArrayList<DimensionValuePair> copyToLoadNextValueOfCurrentDimensionValue = new ArrayList<DimensionValuePair>(currentTargetStateSet);
        currentTargetStateSet.add(new DimensionValuePair(iterator.getDimension(), iterator.getValue()));
        final String[] currentChoices = new String[currentTargetStateSet.size()];
        for ( int i = 0; i < currentChoices.length; i++ ) {
            currentChoices[i] = currentTargetStateSet.get(i).getChoice();
        }
        if ( null != constraint && constraint.skip(currentTargetStateSet.size(), currentChoices) ) {
            return copyToLoadNextValueOfCurrentDimensionValue;
        }
        // challenge include and exclude constrains:
        // For exclude constraints, challenge happens iterator reaches the last
        // dimension defined in exclude constraint
        // For include constraints, challenge happens iterator reaches every
        // dimension defined in include constraint with combination.
        if ( 0 >= leftIterators.size() ) {
            // targetStateSetList.add(currentTargetStateSet);
            try {
                callback.onCombinationFound(new DynamicCaseContext(new ArrayList<DimensionValuePair>(currentTargetStateSet)));
            } catch (Throwable ignored) {
            }
        } else {
            generateAllLeft(currentTargetStateSet, leftIterators, constraint);
        }
        return copyToLoadNextValueOfCurrentDimensionValue;
    }
}
