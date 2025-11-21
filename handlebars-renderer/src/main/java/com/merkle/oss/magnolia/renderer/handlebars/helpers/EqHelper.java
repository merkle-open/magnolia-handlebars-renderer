package com.merkle.oss.magnolia.renderer.handlebars.helpers;

import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.helper.ConditionalHelpers;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

/**
 * Handlebars helper to check if two values are equal.
 * @deprecated use ifCond helper instead: <br>
 * {@code {{eq 'a' 'b'}} } --> {@code {{ifCond 'a' '==' 'b'}} }
 */
@Deprecated
public class EqHelper implements NamedHelper<Object> {

	@Override
	public Object apply(final Object context, final Options options) throws IOException {
		return ConditionalHelpers.eq.apply(context, options);
	}

	@Override
	public Set<String> names() {
		return Set.of("eq");
	}
}
