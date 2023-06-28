package com.merkle.oss.magnolia.renderer.handlebars.blossom.component.availability;

import info.magnolia.module.blossom.annotation.Template;
import info.magnolia.module.blossom.template.DetectedHandlersMetaData;
import info.magnolia.module.blossom.template.HandlerMetaData;
import info.magnolia.module.blossom.template.TemplateDefinitionBuilder;
import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.template.ComponentAvailability;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BlossomAvailableComponentsTemplateDefinitionBuilder extends TemplateDefinitionBuilder {
	private final ComponentAvailabilityRoleResolverFactory componentAvailabilityRoleResolverFactory = Components.getComponent(ComponentAvailabilityRoleResolverFactory.class);
	private final ComponentAvailabilityPredicateResolverFactory componentAvailabilityPredicateResolverFactory = Components.getComponent(ComponentAvailabilityPredicateResolverFactory.class);

	@Override
	protected Map<String, ComponentAvailability> resolveAvailableComponents(
			final DetectedHandlersMetaData detectedHandlers,
			final HandlerMetaData area
	) {
		final Map<String, Class<?>> handlerClassMapping = getHandlerClassMapping(detectedHandlers);
		final ComponentAvailabilityRoleResolver roleResolver = componentAvailabilityRoleResolverFactory.create(handlerClassMapping);
		final ComponentAvailabilityPredicateResolver predicateResolver = componentAvailabilityPredicateResolverFactory.create(detectedHandlers, handlerClassMapping);
		return super
				.resolveAvailableComponents(detectedHandlers, area)
				.entrySet().stream()
				.map(entry ->
						Map.entry(entry.getKey(), roleResolver.addRoles(entry.getValue()))
				)
				.map(entry ->
						Map.entry(entry.getKey(), predicateResolver.addPredicate(entry.getValue()))
				)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private Map<String, Class<?>> getHandlerClassMapping(final DetectedHandlersMetaData detectedHandlers) {
		return detectedHandlers
				.getTemplates().stream()
				.map(handlerMetaData ->
						Optional.ofNullable(handlerMetaData.getHandlerClass().getAnnotation(Template.class)).map(Template::id).map(id ->
								Map.entry(id, handlerMetaData.getHandlerClass())
						)
				)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (aClass, aClass2) -> aClass));
	}
}
