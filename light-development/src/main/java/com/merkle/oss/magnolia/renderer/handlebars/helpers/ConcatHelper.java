package com.merkle.oss.magnolia.renderer.handlebars.helpers;

import com.github.jknack.handlebars.Options;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This helper concatenates strings together and can be used if a dynamic
 * concatenation like for the icons is needed.
 */
public class ConcatHelper implements NamedHelper<String> {

	@Override
	public Object apply(final String value, final Options options) {
		return StringUtils.defaultIfEmpty(value, StringUtils.EMPTY) +
				Stream.of(options.params)
						.map(Object::toString)
						.collect(Collectors.joining());
	}

	@Override
	public Set<String> names() {
		return Set.of("concat");
	}
}
