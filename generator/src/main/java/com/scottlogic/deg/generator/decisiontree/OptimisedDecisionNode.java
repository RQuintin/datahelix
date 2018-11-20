package com.scottlogic.deg.generator.decisiontree;

import java.util.Collection;

public class OptimisedDecisionNode implements DecisionNode, OptimisedNode{
    private final DecisionNode underlying;

    public OptimisedDecisionNode(DecisionNode underlying) {
        this.underlying = underlying;
    }

    @Override
    public Collection<ConstraintNode> getOptions() {
        return underlying.getOptions();
    }

    @Override
    public DecisionNode setOptions(Collection<ConstraintNode> options) {
        return new OptimisedDecisionNode(underlying.setOptions(options));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof OptimisedDecisionNode)
            o = ((OptimisedDecisionNode)o).underlying;

        return underlying.equals(o);
    }

    @Override
    public int hashCode() {
        return underlying.hashCode();
    }
}