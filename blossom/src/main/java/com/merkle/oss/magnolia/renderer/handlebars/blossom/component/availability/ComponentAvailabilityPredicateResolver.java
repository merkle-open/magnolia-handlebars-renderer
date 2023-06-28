package com.merkle.oss.magnolia.renderer.handlebars.blossom.component.availability;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import com.merkle.oss.magnolia.renderer.handlebars.blossom.util.Lazy;
import info.magnolia.cms.security.operations.OperationPermissionDefinition;
import info.magnolia.module.blossom.annotation.ComponentCategory;
import info.magnolia.module.blossom.template.DetectedHandlersMetaData;
import info.magnolia.rendering.template.ComponentAvailability;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.merkle.oss.magnolia.renderer.handlebars.blossom.component.availability.ComponentAvailabilityWithPredicate.AreaAndComponentId;
import static com.merkle.oss.magnolia.renderer.handlebars.blossom.component.availability.ComponentAvailabilityWithPredicate.TemplateUtil;

@AutoFactory
public class ComponentAvailabilityPredicateResolver {
	private final Map<String, AreaAndComponentIdPredicate> areaComponentAvailabilityPredicates;
	private final Supplier<Map<String, Predicate<AreaAndComponentId>>> predicateMapping;
	private final TemplateUtil templateUtil;

	@Inject
	public ComponentAvailabilityPredicateResolver(
			@Provided final Map<String, AreaAndComponentIdPredicate> areaComponentAvailabilityPredicates,
			final DetectedHandlersMetaData detectedHandlers,
			final Map<String, Class<?>> handlerClassMapping) {
		this.areaComponentAvailabilityPredicates = areaComponentAvailabilityPredicates;
		this.templateUtil = new TemplateUtilImpl(detectedHandlers, handlerClassMapping);
		this.predicateMapping = Lazy.of(() -> getPredicateMapping(detectedHandlers));
	}

	public ComponentAvailabilityWithPredicate addPredicate(final ComponentAvailability componentAvailability) {
		return withPredicate(
				componentAvailability,
				Optional
						.ofNullable(predicateMapping.get().get(componentAvailability.getId()))
						.orElseGet(() -> areaAndComponentId -> true)
		);
	}

	private Map<String, Predicate<AreaAndComponentId>> getPredicateMapping(final DetectedHandlersMetaData detectedHandlers) {
		return areaComponentAvailabilityPredicates.entrySet().stream()
				.flatMap(entry ->
						Arrays
								.stream(entry.getValue().getClass().getAnnotations()).filter(annotation ->
										annotation.annotationType().isAnnotationPresent(ComponentCategory.class)
								)
								.map(annotation ->
										detectedHandlers.getTemplatesInCategory(annotation.annotationType())
								)
								.flatMap(Collection::stream)
								.map(id ->
										Map.entry(id, (Predicate<AreaAndComponentId>) areaAndComponentId ->
												filter(areaAndComponentId, entry.getKey(), entry.getValue())
										)
								)
				)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (predicate, predicate2) -> predicate));
	}

	private boolean filter(final AreaAndComponentId areaAndComponentId, final String templateId, final Predicate<AreaAndComponentId> predicate) {
		if(Objects.equals(areaAndComponentId.getArea().getAreaDefinition().getId(), templateId)){
			return predicate.test(areaAndComponentId);
		}
		return true;
	}

	private ComponentAvailabilityWithPredicate withPredicate(
			final ComponentAvailability componentAvailability,
			final Predicate<AreaAndComponentId> predicate) {
		return new ComponentAvailabilityWithPredicate() {
			@Override
			public Predicate<AreaAndComponentId> getPredicate() {
				return predicate;
			}
			@Override
			public TemplateUtil getTemplateUtil() {
				return templateUtil;
			}
			@Override
			public String getId() {
				return componentAvailability.getId();
			}
			@Override
			public Collection<String> getRoles() {
				return componentAvailability.getRoles();
			}
			@Override
			public OperationPermissionDefinition getPermissions() {
				return componentAvailability.getPermissions();
			}
			@Override
			public boolean isEnabled() {
				return componentAvailability.isEnabled();
			}
		};
	}

	private static class TemplateUtilImpl implements TemplateUtil {
		private final DetectedHandlersMetaData detectedHandlers;
		private final Map<String, Class<?>> handlerClassMapping;

		public TemplateUtilImpl(
				final DetectedHandlersMetaData detectedHandlers,
				final Map<String, Class<?>> handlerClassMapping
		) {
			this.detectedHandlers = detectedHandlers;
			this.handlerClassMapping = handlerClassMapping;
		}

		@Override
		public Optional<Class<?>> getHandlerClass(final String templateId) {
			return Optional.ofNullable(handlerClassMapping.get(templateId));
		}
		@Override
		public List<String> getTemplateIdsInCategory(Class<? extends Annotation> category) {
			return detectedHandlers.getTemplatesInCategory(category);
		}
	}

	public interface AreaAndComponentIdPredicate extends Predicate<AreaAndComponentId>{}
}
