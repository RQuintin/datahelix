package com.scottlogic.deg.generator.profile.constraints.atomic;

import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;

import java.util.Collections;
import java.util.Objects;

public class NotEqualToConstraint implements AtomicConstraint {

    private final Field field;
    public final Object value;

    public NotEqualToConstraint(Field field, Object value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public AtomicConstraint negate() {
        return new EqualToConstraint(field, value);
    }

    @Override
    public FieldSpec toFieldSpec() {
        return FieldSpec.fromType(field.getType()).withBlacklist(Collections.singleton(value));
    }

    @Override
    public String toString(){
        return String.format("`%s` = %s", field.name, value);
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o instanceof ViolatedAtomicConstraint) {
            return o.equals(this);
        }
        if (o == null || getClass() != o.getClass()) return false;
        NotEqualToConstraint constraint = (NotEqualToConstraint) o;
        return Objects.equals(field, constraint.field) && Objects.equals(value, constraint.value);
    }

    @Override
    public int hashCode(){
        return Objects.hash(field, value);
    }
}