package com.scottlogic.deg.generator.restrictions;

import com.scottlogic.deg.generator.constraints.atomic.*;
import com.scottlogic.deg.generator.generation.IStringGenerator;
import com.scottlogic.deg.generator.generation.RegexStringGenerator;
import com.scottlogic.deg.generator.utils.NumberUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FieldSpecFactory {
    public FieldSpec construct(AtomicConstraint constraint) {
        return construct(constraint, false);
    }

    public FieldSpec toMustContainRestrictionFieldSpec(Collection<AtomicConstraint> constraints) {
        return FieldSpec.Empty.withMustContainRestriction(
            new MustContainRestriction(
                constraints.stream().map(this::construct).collect(Collectors.toSet())
            )
        );
    }

    private FieldSpec construct(AtomicConstraint constraint, boolean negate) {
        if (constraint instanceof NotConstraint) {
            return construct(((NotConstraint) constraint).negatedConstraint, !negate);
        } else if (constraint instanceof IsInSetConstraint) {
            return construct((IsInSetConstraint) constraint, negate);
        } else if (constraint instanceof IsEqualToConstantConstraint) {
            return construct((IsEqualToConstantConstraint) constraint, negate);
        } else if (constraint instanceof IsGreaterThanConstantConstraint) {
            return construct((IsGreaterThanConstantConstraint) constraint, negate);
        } else if (constraint instanceof IsGreaterThanOrEqualToConstantConstraint) {
            return construct((IsGreaterThanOrEqualToConstantConstraint) constraint, negate);
        } else if (constraint instanceof IsLessThanConstantConstraint) {
            return construct((IsLessThanConstantConstraint) constraint, negate);
        } else if (constraint instanceof IsLessThanOrEqualToConstantConstraint) {
            return construct((IsLessThanOrEqualToConstantConstraint) constraint, negate);
        } else if (constraint instanceof IsAfterConstantDateTimeConstraint) {
            return construct((IsAfterConstantDateTimeConstraint) constraint, negate);
        } else if (constraint instanceof IsAfterOrEqualToConstantDateTimeConstraint) {
            return construct((IsAfterOrEqualToConstantDateTimeConstraint) constraint, negate);
        } else if (constraint instanceof IsBeforeConstantDateTimeConstraint) {
            return construct((IsBeforeConstantDateTimeConstraint) constraint, negate);
        } else if (constraint instanceof IsBeforeOrEqualToConstantDateTimeConstraint) {
            return construct((IsBeforeOrEqualToConstantDateTimeConstraint) constraint, negate);
        } else if (constraint instanceof IsGranularToConstraint) {
            return construct((IsGranularToConstraint) constraint, negate);
        } else if (constraint instanceof IsNullConstraint) {
            return constructIsNull(negate, constraint);
        } else if (constraint instanceof MatchesRegexConstraint) {
            return construct((MatchesRegexConstraint) constraint, negate);
        } else if (constraint instanceof ContainsRegexConstraint) {
            return construct((ContainsRegexConstraint) constraint, negate);
        } else if (constraint instanceof MatchesStandardConstraint) {
            return construct((MatchesStandardConstraint) constraint, negate);
        } else if (constraint instanceof IsOfTypeConstraint) {
            return construct((IsOfTypeConstraint) constraint, negate);
        } else if (constraint instanceof FormatConstraint) {
            return construct((FormatConstraint) constraint, negate);
        } else if (constraint instanceof StringHasLengthConstraint) {
            return construct((StringHasLengthConstraint) constraint, negate);
        } else if (constraint instanceof IsStringLongerThanConstraint) {
            return construct((IsStringLongerThanConstraint) constraint, negate);
        } else if (constraint instanceof IsStringShorterThanConstraint) {
            return construct((IsStringShorterThanConstraint) constraint, negate);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private FieldSpec construct(IsEqualToConstantConstraint constraint, boolean negate) {
        return construct(
            new IsInSetConstraint(
                constraint.field,
                Collections.singleton(constraint.requiredValue)
            ),
            negate);
    }

    private FieldSpec construct(IsInSetConstraint constraint, boolean negate) {
        return FieldSpec.Empty.withSetRestrictions(
                negate
                    ? SetRestrictions.fromBlacklist(constraint.legalValues)
                    : SetRestrictions.fromWhitelist(constraint.legalValues),
            FieldSpecSource.fromConstraint(constraint, negate));
    }

    private FieldSpec constructIsNull(boolean negate, AtomicConstraint constraint) {
        final NullRestrictions nullRestrictions = new NullRestrictions();

        nullRestrictions.nullness = negate
            ? NullRestrictions.Nullness.MUST_NOT_BE_NULL
            : NullRestrictions.Nullness.MUST_BE_NULL;

        return FieldSpec.Empty.withNullRestrictions(
            nullRestrictions,
            FieldSpecSource.fromConstraint(constraint, negate));
    }

    private FieldSpec construct(IsOfTypeConstraint constraint, boolean negate) {
        final TypeRestrictions typeRestrictions;

        if (negate) {
            typeRestrictions = DataTypeRestrictions.all.except(constraint.requiredType);
        } else {
            typeRestrictions = DataTypeRestrictions.createFromWhiteList(constraint.requiredType);
        }

        return FieldSpec.Empty.withTypeRestrictions(
            typeRestrictions,
            FieldSpecSource.fromConstraint(constraint, negate));
    }

    private FieldSpec construct(IsGreaterThanConstantConstraint constraint, boolean negate) {
        return constructGreaterThanConstraint(constraint.referenceValue, false, negate, constraint);
    }

    private FieldSpec construct(IsGreaterThanOrEqualToConstantConstraint constraint, boolean negate) {
        return constructGreaterThanConstraint(constraint.referenceValue, true, negate, constraint);
    }

    private FieldSpec constructGreaterThanConstraint(Number limitValue, boolean inclusive, boolean negate, AtomicConstraint constraint) {
        final NumericRestrictions numericRestrictions = new NumericRestrictions();

        final BigDecimal limit = NumberUtils.coerceToBigDecimal(limitValue);
        if (negate) {
            numericRestrictions.max = new NumericLimit<>(
                limit,
                !inclusive);
        } else {
            numericRestrictions.min = new NumericLimit<>(
                limit,
                inclusive);
        }

        return FieldSpec.Empty.withNumericRestrictions(
            numericRestrictions,
            FieldSpecSource.fromConstraint(constraint, negate));
    }

    private FieldSpec construct(IsLessThanConstantConstraint constraint, boolean negate) {
        return constructLessThanConstraint(constraint.referenceValue, false, negate, constraint);
    }

    private FieldSpec construct(IsLessThanOrEqualToConstantConstraint constraint, boolean negate) {
        return constructLessThanConstraint(constraint.referenceValue, true, negate, constraint);
    }

    private FieldSpec constructLessThanConstraint(Number limitValue, boolean inclusive, boolean negate, AtomicConstraint constraint) {
        final NumericRestrictions numericRestrictions = new NumericRestrictions();
        final BigDecimal limit = NumberUtils.coerceToBigDecimal(limitValue);
        if (negate) {
            numericRestrictions.min = new NumericLimit<>(
                limit,
                !inclusive);
        } else {
            numericRestrictions.max = new NumericLimit<>(
                limit,
                inclusive);
        }

        return FieldSpec.Empty.withNumericRestrictions(
            numericRestrictions,
            FieldSpecSource.fromConstraint(constraint, negate));
    }

    private FieldSpec construct(IsGranularToConstraint constraint, boolean negate) {
        if (negate) {
            // it's not worth much effort to figure out how to negate a formatting constraint - let's just make it a no-op
            return FieldSpec.Empty;
        }

        return FieldSpec.Empty.withGranularityRestrictions(
            new GranularityRestrictions(constraint.granularity),
            FieldSpecSource.fromConstraint(constraint, false));
    }

    private FieldSpec construct(IsAfterConstantDateTimeConstraint constraint, boolean negate) {
        return constructIsAfterConstraint(constraint.referenceValue, false, negate, constraint);
    }

    private FieldSpec construct(IsAfterOrEqualToConstantDateTimeConstraint constraint, boolean negate) {
        return constructIsAfterConstraint(constraint.referenceValue, true, negate, constraint);
    }

    private FieldSpec constructIsAfterConstraint(LocalDateTime limit, boolean inclusive, boolean negate, AtomicConstraint constraint) {
        final DateTimeRestrictions dateTimeRestrictions = new DateTimeRestrictions();

        if (negate) {
            dateTimeRestrictions.max = new DateTimeRestrictions.DateTimeLimit(limit, !inclusive);
        } else {
            dateTimeRestrictions.min = new DateTimeRestrictions.DateTimeLimit(limit, inclusive);
        }

        return FieldSpec.Empty.withDateTimeRestrictions(
            dateTimeRestrictions,
            FieldSpecSource.fromConstraint(constraint, negate));
    }

    private FieldSpec construct(IsBeforeConstantDateTimeConstraint constraint, boolean negate) {
        return constructIsBeforeConstraint(constraint.referenceValue, false, negate, constraint);
    }

    private FieldSpec construct(IsBeforeOrEqualToConstantDateTimeConstraint constraint, boolean negate) {
        return constructIsBeforeConstraint(constraint.referenceValue, true, negate, constraint);
    }

    private FieldSpec constructIsBeforeConstraint(LocalDateTime limit, boolean inclusive, boolean negate, AtomicConstraint constraint) {
        final DateTimeRestrictions dateTimeRestrictions = new DateTimeRestrictions();

        if (negate) {
            dateTimeRestrictions.min = new DateTimeRestrictions.DateTimeLimit(limit, !inclusive);
        } else {
            dateTimeRestrictions.max = new DateTimeRestrictions.DateTimeLimit(limit, inclusive);
        }

        return FieldSpec.Empty.withDateTimeRestrictions(
            dateTimeRestrictions,
            FieldSpecSource.fromConstraint(constraint, negate));
    }

    private FieldSpec construct(MatchesRegexConstraint constraint, boolean negate) {
        return constructPattern(constraint.regex, negate, true, constraint);
    }

    private FieldSpec construct(ContainsRegexConstraint constraint, boolean negate) {
        return constructPattern(constraint.regex, negate, false, constraint);
    }

    private FieldSpec construct(MatchesStandardConstraint constraint, boolean negate) {
        return constructGenerator(constraint.standard, negate, constraint);
    }

    private FieldSpec construct(FormatConstraint constraint, boolean negate) {
        if (negate) {
            // it's not worth much effort to figure out how to negate a formatting constraint - let's just make it a no-op
            return FieldSpec.Empty;
        }

        final FormatRestrictions formatRestrictions = new FormatRestrictions();
        formatRestrictions.formatString = constraint.format;

        return FieldSpec.Empty.withFormatRestrictions(
            formatRestrictions,
            FieldSpecSource.fromConstraint(constraint, false));
    }

    private FieldSpec construct(StringHasLengthConstraint constraint, boolean negate) {
        final Pattern regex = Pattern.compile(String.format(".{%s}", constraint.referenceValue));
        return constructPattern(regex, negate, true, constraint);
    }

    private FieldSpec construct(IsStringShorterThanConstraint constraint, boolean negate) {
        final Pattern regex = Pattern.compile(String.format(".{0,%d}", constraint.referenceValue - 1));
        return constructPattern(regex, negate, true, constraint);
    }

    private FieldSpec construct(IsStringLongerThanConstraint constraint, boolean negate) {
        final Pattern regex = Pattern.compile(String.format(".{%d,}", constraint.referenceValue + 1));
        return constructPattern(regex, negate, true, constraint);
    }

    private FieldSpec constructPattern(Pattern pattern, boolean negate, boolean matchFullString, AtomicConstraint constraint) {
        return constructGenerator(new RegexStringGenerator(pattern.toString(), matchFullString), negate, constraint);
    }

    private FieldSpec constructGenerator(IStringGenerator generator, boolean negate, AtomicConstraint constraint) {
        final StringRestrictions stringRestrictions = new StringRestrictions();

        stringRestrictions.stringGenerator = negate
            ? generator.complement()
            : generator;

        return FieldSpec.Empty.withStringRestrictions(
            stringRestrictions,
            FieldSpecSource.fromConstraint(constraint, negate));
    }
}
