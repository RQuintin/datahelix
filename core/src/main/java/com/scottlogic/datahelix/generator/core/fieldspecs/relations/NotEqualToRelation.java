/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.scottlogic.datahelix.generator.core.fieldspecs.relations;

import com.scottlogic.datahelix.generator.common.profile.Field;
import com.scottlogic.datahelix.generator.core.fieldspecs.FieldSpec;
import com.scottlogic.datahelix.generator.core.fieldspecs.FieldSpecFactory;
import com.scottlogic.datahelix.generator.core.generation.databags.DataBagValue;
import com.scottlogic.datahelix.generator.core.profile.constraints.Constraint;

import java.util.Collections;

public class NotEqualToRelation implements FieldSpecRelation
{
    private final Field main;
    private final Field other;

    public NotEqualToRelation(Field main, Field other) {
        this.main = main;
        this.other = other;
    }

    @Override
    public FieldSpec createModifierFromOtherFieldSpec(FieldSpec otherFieldSpec) {
        return FieldSpecFactory.fromType(main.getType());
    }

    @Override
    public FieldSpec createModifierFromOtherValue(DataBagValue otherFieldGeneratedValue) {
        return FieldSpecFactory.fromType(main.getType()).withBlacklist(Collections.singleton(otherFieldGeneratedValue.getValue()));
    }

    @Override
    public FieldSpecRelation inverse() {
        return new NotEqualToRelation(other, main);
    }

    @Override
    public Field main() {
        return main;
    }

    @Override
    public Field other() {
        return other;
    }

    @Override
    public Constraint negate() {
        return new EqualToRelation(main, other);
    }
}
