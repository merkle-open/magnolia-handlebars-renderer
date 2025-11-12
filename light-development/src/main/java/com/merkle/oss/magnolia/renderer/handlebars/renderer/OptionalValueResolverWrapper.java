package com.merkle.oss.magnolia.renderer.handlebars.renderer;

import com.github.jknack.handlebars.ValueResolver;

import jakarta.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class OptionalValueResolverWrapper implements ValueResolver {
	private final ValueResolver valueResolver;

	public OptionalValueResolverWrapper(final ValueResolver valueResolver) {
		this.valueResolver = valueResolver;
	}

	@Override
	public Object resolve(Object o, String s) {
		return unwrap(valueResolver.resolve(o, s));
	}

	@Override
	public Object resolve(Object o) {
		return unwrap(valueResolver.resolve(o));
	}

	@Override
	public Set<Map.Entry<String, Object>> propertySet(Object o) {
		return valueResolver.propertySet(o);
	}

	@Nullable
	private Object unwrap(Object object) {
		if(object instanceof Optional<?> optional) {
			return optional.orElse(null);
		}
		return object;
	}
}
