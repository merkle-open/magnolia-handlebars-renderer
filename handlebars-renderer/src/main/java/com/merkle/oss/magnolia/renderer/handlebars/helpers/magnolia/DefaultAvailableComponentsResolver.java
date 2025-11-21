package com.merkle.oss.magnolia.renderer.handlebars.helpers.magnolia;

import info.magnolia.templating.elements.AreaElement;

import java.util.Optional;

public class DefaultAvailableComponentsResolver implements CmsAreaTemplateHelper.AvailableComponentsResolver {
	@Override
	public Optional<String> getValue(final AreaElement element) {
		return Optional.empty();
	}
}
