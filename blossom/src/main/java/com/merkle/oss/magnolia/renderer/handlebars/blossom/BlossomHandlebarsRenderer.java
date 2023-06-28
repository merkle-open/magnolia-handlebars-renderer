package com.merkle.oss.magnolia.renderer.handlebars.blossom;

import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.merkle.oss.magnolia.renderer.handlebars.helpers.NamedHelper;
import com.merkle.oss.magnolia.renderer.handlebars.renderer.HandlebarsRenderer;
import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.module.blossom.render.RenderContext;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.RenderableDefinition;

import javax.inject.Inject;
import javax.jcr.Node;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BlossomHandlebarsRenderer extends HandlebarsRenderer {

	@Inject
	private BlossomHandlebarsRenderer(
			final RenderingEngine renderingEngine,
			final Set<NamedHelper> helpers,
			final ClassPathTemplateLoader classPathTemplateLoader,
			final ValueResolversProvider valueResolversProvider,
			final MagnoliaConfigurationProperties magnoliaConfigurationProperties
	) {
		super(renderingEngine, helpers, classPathTemplateLoader, valueResolversProvider, magnoliaConfigurationProperties);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void setupContext(
			final Map<String, Object> context,
			final Node content,
			final RenderableDefinition definition,
			final RenderingModel<?> model,
			final Object actionResult
	) {
		super.setupContext(context, content, definition, model, actionResult);
		Optional.ofNullable(RenderContext.get().getModel()).ifPresent(context::putAll);
	}

	@Override
	protected String resolveTemplateScript(
			final Node content,
			final RenderableDefinition definition,
			final RenderingModel<?> model,
			final String actionResult
	) {
		return Optional
				.ofNullable(super.resolveTemplateScript(content, definition, model, actionResult))
				//TemplateDefinitionBuilder.buildAreaDefinition
				.filter(templateScript -> !templateScript.equals("<area-script-placeholder>"))
				.orElseGet(() -> RenderContext.get().getTemplateScript());
	}
}
