package com.merkle.oss.magnolia.renderer.handlebars.helpers.magnolia;

import com.github.jknack.handlebars.Options;
import com.machinezoo.noexception.Exceptions;
import com.merkle.oss.magnolia.renderer.handlebars.helpers.NamedHelper;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.templating.elements.AbstractContentTemplatingElement;
import info.magnolia.templating.functions.TemplatingFunctions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

public abstract class AbstractCmsTemplateHelper<T> implements NamedHelper<T> {
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private final TemplatingFunctions templatingFunctions;

	public AbstractCmsTemplateHelper(final TemplatingFunctions templatingFunctions) {
		this.templatingFunctions = templatingFunctions;
	}

	@Override
	public CharSequence apply(final T context, final Options options) {
		try {
			return applySafe(context, options);
		} catch (Exception e) {
			if (templatingFunctions.isAuthorInstance() && templatingFunctions.isEditMode()) {
				throw Exceptions.sneak().handle(e);
			} else {
				LOG.error("Failed to apply!", e);
			}
		}
		return StringUtils.EMPTY;
	}

	protected abstract CharSequence applySafe(T context, Options options) throws Exception;

	protected CharSequence render(final AbstractContentTemplatingElement templatingElement) throws RenderException, IOException {
		final StringBuilder buffer = new StringBuilder();
		templatingElement.begin(buffer);
		templatingElement.end(buffer);
		return buffer;
	}
}
