package com.merkle.oss.magnolia.renderer.handlebars.configuration;

import com.google.inject.multibindings.Multibinder;
import com.merkle.oss.magnolia.renderer.handlebars.helpers.NamedHelper;
import com.merkle.oss.magnolia.renderer.handlebars.helpers.magnolia.CmsAreaTemplateHelper;
import com.merkle.oss.magnolia.renderer.handlebars.helpers.magnolia.CmsComponentTemplateHelper;
import com.merkle.oss.magnolia.renderer.handlebars.helpers.magnolia.CmsPageTemplateHelper;
import info.magnolia.objectfactory.guice.AbstractGuiceComponentConfigurer;

import java.util.Set;

public class HandlebarsRendererGuiceComponentConfigurer extends AbstractGuiceComponentConfigurer {
	private static final Set<Class<? extends NamedHelper<?>>> MAGNOLIA_HELPER = Set.of(
			CmsPageTemplateHelper.class,
			CmsAreaTemplateHelper.class,
			CmsComponentTemplateHelper.class
	);

	@Override
	protected void configure() {
		super.configure();
		final Multibinder<NamedHelper> namedHelperMultibinder = Multibinder.newSetBinder(binder(), NamedHelper.class);
		MAGNOLIA_HELPER.forEach(helper -> namedHelperMultibinder.addBinding().to(helper));
	}
}
