package com.merkle.oss.magnolia.renderer.handlebars.renderer;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.ValueResolver;
import com.github.jknack.handlebars.cache.ConcurrentMapTemplateCache;
import com.github.jknack.handlebars.cache.NullTemplateCache;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.merkle.oss.magnolia.renderer.handlebars.helpers.NamedHelper;
import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.engine.RenderException;
import info.magnolia.rendering.engine.RenderingEngine;
import info.magnolia.rendering.renderer.AbstractRenderer;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.rendering.util.AppendableWriter;

import jakarta.inject.Inject;
import javax.jcr.Node;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class HandlebarsRenderer extends AbstractRenderer {
	public static final String NAME = "handlebars";
	private final Handlebars handlebars;
	private final ValueResolversProvider valueResolversProvider;
	private final MagnoliaConfigurationProperties magnoliaConfigurationProperties;

	/*
	 *	Hint for usage with blossom:
	 *  This class is created by RendererRegistry, when spring context is not yet available.
	 *	Each constructor param, must be injectable by guice!!
	 */
	@Inject
	public HandlebarsRenderer(
			final RenderingEngine renderingEngine,
			final Set<NamedHelper> helpers,
			final ClassPathTemplateLoader classPathTemplateLoader,
			final ValueResolversProvider valueResolversProvider,
			final MagnoliaConfigurationProperties magnoliaConfigurationProperties
	) {
		super(renderingEngine);
		this.handlebars = new Handlebars(classPathTemplateLoader).with(new ConcurrentMapTemplateCache());
		this.valueResolversProvider = valueResolversProvider;
		this.magnoliaConfigurationProperties = magnoliaConfigurationProperties;
		if(isDevMode()) {
			handlebars.with(NullTemplateCache.INSTANCE);
		}
		helpers.forEach(this::registerHelper);
	}

	private boolean isDevMode() {
		final String devMode = magnoliaConfigurationProperties.getProperty("magnolia.develop");
		return "true".equalsIgnoreCase(devMode);
	}

	private <H> void registerHelper(final NamedHelper<H> helper) {
		helper.names().forEach(name ->
				handlebars.registerHelper(name, helper)
		);
	}

	@Override
	protected Map<String, Object> newContext() {
		return new HashMap<>();
	}

	@Override
	protected void onRender(
			final Node content,
			final RenderableDefinition definition,
			final RenderingContext renderingContext,
			final Map<String, Object> context,
			final String templateScript
	) throws RenderException {
		try {
			final AppendableWriter out = renderingContext.getAppendable();
			final Context combinedContext = Context.newBuilder(context)
					.resolver(valueResolversProvider.get().toArray(ValueResolver[]::new))
					.build();
			try {
				final Template template = handlebars.compile(templateScript);
				template.apply(combinedContext, out);
			} finally {
				combinedContext.destroy();
			}
		} catch (Exception e) {
			throw new RenderException("Failed to render template", e);
		}
	}

	public interface ValueResolversProvider {
		Stream<ValueResolver> get();
	}
}