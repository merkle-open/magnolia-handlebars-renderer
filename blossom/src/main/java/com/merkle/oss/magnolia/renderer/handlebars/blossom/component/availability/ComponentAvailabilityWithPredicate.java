package com.merkle.oss.magnolia.renderer.handlebars.blossom.component.availability;

import info.magnolia.rendering.template.ComponentAvailability;
import info.magnolia.templating.elements.AreaElement;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface ComponentAvailabilityWithPredicate extends ComponentAvailability {
	Predicate<AreaAndComponentId> getPredicate();
	TemplateUtil getTemplateUtil();

	interface AreaAndComponentId {
		AreaElement getArea();
		String getComponentId();
		TemplateUtil getTemplateUtil();
	}

	interface TemplateUtil {
		Optional<Class<?>> getHandlerClass(String templateId);
		List<String> getTemplateIdsInCategory(Class<? extends Annotation> category);
	}
}