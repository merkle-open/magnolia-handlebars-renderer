package com.merkle.oss.magnolia.renderer.handlebars.helpers;

import com.github.jknack.handlebars.Options;

import java.io.IOException;
import java.util.Set;

/**
 * Handlebars helper to check if two values are equal.
 * Implemented like the if helper: {@link com.github.jknack.handlebars.helper.IfHelper}
 * <p>
 * Usage:
 * <p>
 * {@code
 * {{#eq 'value01' 'value02'}}
 * value01 == value02
 * {{else}}
 * value01 != value02
 * {{/eq}}
 * }
 */
public class EqHelper implements NamedHelper<Object> {

	public Object apply(final Object context, final Options options) throws IOException {
		final Object lhs = context;
		final Object rhs = options.param(0, null);
		final Options.Buffer buffer = options.buffer();
		if (lhs != null && rhs != null) {
			if (lhs.equals(rhs)) {
				buffer.append(options.fn(context));
				return buffer;
			}
		}
		buffer.append(options.inverse(context));
		return buffer;
	}

	@Override
	public Set<String> names() {
		return Set.of("eq");
	}
}
