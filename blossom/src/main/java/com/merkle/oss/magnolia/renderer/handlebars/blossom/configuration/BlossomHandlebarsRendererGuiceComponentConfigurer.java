package com.merkle.oss.magnolia.renderer.handlebars.blossom.configuration;

import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.merkle.oss.magnolia.renderer.handlebars.blossom.component.availability.ComponentAvailabilityPredicateResolver;
import com.merkle.oss.magnolia.renderer.handlebars.blossom.component.availability.ComponentAvailabilityPredicateResolver.AreaAndComponentIdPredicate;
import com.merkle.oss.magnolia.renderer.handlebars.blossom.component.availability.ComponentAvailabilityWithPredicate;
import com.merkle.oss.magnolia.renderer.handlebars.helpers.NamedHelper;
import com.merkle.oss.magnolia.renderer.handlebars.helpers.magnolia.CmsAreaTemplateHelper;
import com.merkle.oss.magnolia.renderer.handlebars.helpers.magnolia.CmsComponentTemplateHelper;
import com.merkle.oss.magnolia.renderer.handlebars.helpers.magnolia.CmsPageTemplateHelper;
import info.magnolia.objectfactory.guice.AbstractGuiceComponentConfigurer;

import java.util.Set;

public class BlossomHandlebarsRendererGuiceComponentConfigurer extends AbstractGuiceComponentConfigurer {

	@Override
	protected void configure() {
		super.configure();
		MapBinder.newMapBinder(binder(), String.class, AreaAndComponentIdPredicate.class);
	}
}
