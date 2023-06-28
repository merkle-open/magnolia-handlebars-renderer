package com.merkle.oss.magnolia.renderer.handlebars.helpers;

import com.github.jknack.handlebars.Options;
import info.magnolia.context.MgnlContext;

import java.io.IOException;
import java.util.Set;

public class AppContextHelper implements NamedHelper<String> {

	public CharSequence apply(final String key, Options options) throws IOException {
		return MgnlContext.getContextPath();
	}

	@Override
	public Set<String> names() {
		return Set.of("app-context");
	}
}
