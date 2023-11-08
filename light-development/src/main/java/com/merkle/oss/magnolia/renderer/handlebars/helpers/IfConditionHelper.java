package com.merkle.oss.magnolia.renderer.handlebars.helpers;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.helper.IfHelper;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class IfConditionHelper extends IfHelper implements NamedHelper<Object> {
	private final TypeOfHelper typeOfHelper = new TypeOfHelper();

	@Override
	public Object apply(final Object context, final Options options) throws IOException {
		if(options.params.length == 0) {
			return super.apply(context, options);
		}
		final Operator operator = Operator.fromKeyOrThrow(options.param(0, null));
		final Options optionsWithoutOperator = new Options
				.Builder(options.handlebars, options.helperName, options.tagType, options.context, options.fn)
				.setInverse(options.inverse)
				.setParams(Arrays.stream(options.params).skip(1).toArray())
				.build();
		switch (operator) {
			case EQUAL:
				return ConditionalHelpers.eq.apply(context, optionsWithoutOperator);
			case NOT_EQUAL:
				return ConditionalHelpers.neq.apply(context, optionsWithoutOperator);
			case LESS:
				return ConditionalHelpers.lt.apply(context, optionsWithoutOperator);
			case LESS_OR_EQUAL:
				return ConditionalHelpers.lte.apply(context, optionsWithoutOperator);
			case GREATER:
				return ConditionalHelpers.gt.apply(context, optionsWithoutOperator);
			case GREATER_OR_EQUAL:
				return ConditionalHelpers.gte.apply(context, optionsWithoutOperator);
			case TYPE_OF:
				return typeOfHelper.apply(context, optionsWithoutOperator);
			default:
				throw new IllegalArgumentException("Unsupported operator "+ operator);
		}
	}

	private enum Operator {
		EQUAL("==", "==="),
		NOT_EQUAL("!=", "!=="),
		TYPE_OF("typeof"),
		LESS( "<"),
		LESS_OR_EQUAL( "<="),
		GREATER( ">"),
		GREATER_OR_EQUAL( ">=");

		private final Set<String> keys;

		Operator(final String... keys) {
			this.keys = Set.of(keys);
		}

		private static Operator fromKeyOrThrow(@Nullable final String key) {
			return Operator.fromKey(key).orElseThrow(() ->
					new IllegalArgumentException("Unknown operator "+key+". Must be one of "+Operator.keys())
			);
		}

		private static Optional<Operator> fromKey(@Nullable final String key) {
			return Arrays
					.stream(values())
					.filter(operator -> operator.keys.contains(key))
					.findFirst();
		}

		private static Collection<String> keys() {
			return Arrays.stream(Operator.values()).flatMap(op -> op.keys.stream()).collect(Collectors.toList());
		}
	}

	@Override
	public Set<String> names() {
		return Set.of(
				"ifCond",
				"if"
		);
	}

	private static class TypeOfHelper implements Helper<Object> {
		@Override
		public Object apply(Object context, Options options) throws IOException {
			final boolean result = Objects.equals(
					Optional.ofNullable(context).map(Object::getClass),
					Optional.ofNullable(options.param(0)).map(Object::getClass)
			);
			if (options.tagType == TagType.SECTION) {
				return result ? options.fn() : options.inverse();
			}
			return result
					? options.hash("yes", true)
					: options.hash("no", false);
		}
	}

	public static class OrHelper extends BuildInHelperWrapper<ConditionalHelpers> {
		public OrHelper() {
			super(ConditionalHelpers.or);
		}
	}

	public static class AndHelper extends BuildInHelperWrapper<ConditionalHelpers> {
		public AndHelper() {
			super(ConditionalHelpers.and);
		}
	}

	public static class NotHelper extends BuildInHelperWrapper<ConditionalHelpers> {
		public NotHelper() {
			super(ConditionalHelpers.not);
		}
	}
}
