package com.merkle.oss.magnolia.renderer.handlebars.helpers;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import java.io.IOException;
import java.util.Set;

public interface NamedHelper<T> extends Helper<T> {
	Set<String> names();

	abstract class BuildInHelperWrapper<T extends Enum<T> & Helper<Object>> implements NamedHelper<Object>{
		private final T helper;

		public BuildInHelperWrapper(final T helper) {
			this.helper = helper;
		}

		@Override
		public Set<String> names() {
			return Set.of(helper.name());
		}

		@Override
		public Object apply(final Object context, final Options options) throws IOException {
			return helper.apply(context, options);
		}
	}
}
