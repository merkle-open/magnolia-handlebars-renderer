package com.merkle.oss.magnolia.renderer.handlebars.helpers.magnolia;

import com.github.jknack.handlebars.Options;
import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.templating.elements.PageElement;
import info.magnolia.templating.functions.TemplatingFunctions;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CmsPageTemplateHelper extends AbstractCmsTemplateHelper<Map<String, Object>> {

	@Inject
	public CmsPageTemplateHelper(final TemplatingFunctions templatingFunctions) {
		super(templatingFunctions);
	}

	@Override
	public Optional<CharSequence> applySafe(final Map<String, Object> pageModelMap, final Options options) throws RenderException, IOException {
		final PageElement pageElement = Components.getComponentProvider().newInstance(PageElement.class);
		return Optional.of(render(pageElement));
	}

	@Override
	public Set<String> names() {
		return Set.of("cms-page");
	}
}