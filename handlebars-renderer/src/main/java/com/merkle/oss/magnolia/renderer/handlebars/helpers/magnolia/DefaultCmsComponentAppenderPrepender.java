package com.merkle.oss.magnolia.renderer.handlebars.helpers.magnolia;

import javax.jcr.Node;
import java.util.Map;
import java.util.Optional;

public class DefaultCmsComponentAppenderPrepender implements CmsComponentTemplateHelper.Appender, CmsComponentTemplateHelper.Prepender {
	@Override
	public Optional<String> append(final Node content, final Map<String, Object> contextObjects) {
		return Optional.empty();
	}
	@Override
	public Optional<String> prepend(final Node content, final Map<String, Object> contextObjects) {
		return Optional.empty();
	}
}
