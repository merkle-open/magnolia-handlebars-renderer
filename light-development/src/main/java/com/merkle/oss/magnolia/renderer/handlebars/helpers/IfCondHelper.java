package com.merkle.oss.magnolia.renderer.handlebars.helpers;

import com.github.jknack.handlebars.Options;

import java.io.IOException;
import java.util.Set;

public class IfCondHelper implements NamedHelper<Object> {

	public Object apply(final Object context, final Options options) throws IOException {
		final Object lhs = context;
		final Object operator = options.param(0, null);
		final Object rhs = options.param(1, null);
		final Options.Buffer buffer = options.buffer();

		if (!(operator instanceof String)) {
			return buffer;
		}

		if (lhs != null && rhs != null) {
			String op = (String) operator;
			boolean result = false;
			switch (op) {
				case "==":
					result = lhs.equals(rhs);
					break;
				case "===":
					result = lhs.equals(rhs);
					break;
				case "!=":
					result = !lhs.equals(rhs);
					break;
				case "!==":
					result = !lhs.equals(rhs);
					break;
				case "<":
					if (areNums(lhs, rhs)) {
						result = ((Number) lhs).doubleValue() < ((Number) rhs).doubleValue();
					}
					break;
				case ">":
					if (areNums(lhs, rhs)) {
						result = ((Number) lhs).doubleValue() > ((Number) rhs).doubleValue();
					}
					break;
				case "<=":
					if (areNums(lhs, rhs)) {
						result = ((Number) lhs).doubleValue() <= ((Number) rhs).doubleValue();
					}
					break;
				case ">=":
					if (areNums(lhs, rhs)) {
						result = ((Number) lhs).doubleValue() >= ((Number) rhs).doubleValue();
					}
					break;
				case "typeof":
					result = lhs.getClass().equals(rhs.getClass());
					break;
				default:
					result = false;
			}
			if (result) {
				buffer.append(options.fn(context));
				return buffer;
			}
		}
		buffer.append(options.inverse(context));
		return buffer;
	}

	private boolean areNums(final Object lhs, final Object rhs) {
		return (lhs instanceof Number) && (rhs instanceof Number);
	}

	@Override
	public Set<String> names() {
		return Set.of("ifCond");
	}
}
