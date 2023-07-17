package com.merkle.oss.magnolia.renderer.handlebars.helpers;

import com.github.jknack.handlebars.Options;
import info.magnolia.context.MgnlContext;

import java.io.IOException;
import java.util.Set;

public class AppContextHelper implements NamedHelper<Object> {

	public CharSequence apply(final Object context, Options options) {
		return MgnlContext.getContextPath();
	}

	@Override
	public Set<String> names() {
		return Set.of("app-context");
	}
}
