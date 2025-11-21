package com.merkle.oss.magnolia.renderer.handlebars.renderer;

import com.github.jknack.handlebars.ValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;

import java.util.stream.Stream;

public class DefaultValueResolversProvider implements HandlebarsRenderer.ValueResolversProvider {
	@Override
	public Stream<ValueResolver> get() {
		return Stream.of(
				MapValueResolver.INSTANCE,
				MethodValueResolver.INSTANCE,
				JavaBeanValueResolver.INSTANCE
		).map(OptionalValueResolverWrapper::new);
	}
}
