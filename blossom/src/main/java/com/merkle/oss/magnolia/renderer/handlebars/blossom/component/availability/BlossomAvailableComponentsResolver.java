package com.merkle.oss.magnolia.renderer.handlebars.blossom.component.availability;

import com.merkle.oss.magnolia.renderer.handlebars.helpers.magnolia.CmsAreaTemplateHelper;
import info.magnolia.context.WebContext;
import info.magnolia.rendering.template.AreaDefinition;
import info.magnolia.rendering.template.ComponentAvailability;
import info.magnolia.templating.elements.AreaElement;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BlossomAvailableComponentsResolver implements CmsAreaTemplateHelper.AvailableComponentsResolver {
	private final WebContext webContext;

	@Inject
	public BlossomAvailableComponentsResolver(final WebContext webContext) {
		this.webContext = webContext;
	}

	@Override
	public Optional<String> getValue(final AreaElement area) {
		if (StringUtils.isNotEmpty(area.getAvailableComponents())) {
			return Optional.of(StringUtils.remove(area.getAvailableComponents(), " "));
		}
		return Optional.of(resolveAvailableComponents(area));
	}

	private String resolveAvailableComponents(final AreaElement area) {
		final Collection<String> userRoles = webContext.getUser().getAllRoles();
		return Optional
				.ofNullable(area.getAreaDefinition())
				.map(AreaDefinition::getAvailableComponents)
				.map(Map::values)
				.stream()
				.flatMap(Collection::stream)
				.filter(componentAvailability -> isAvailable(area, componentAvailability, userRoles))
				.map(ComponentAvailability::getId)
				.collect(Collectors.joining(","));
	}

	private boolean isAvailable(final AreaElement area, final ComponentAvailability availableComponent, final Collection<String> userRoles) {
		return isUserRoleAvailable(availableComponent, userRoles)
				&& evaluatePredicate(area, availableComponent);
	}

	private boolean isUserRoleAvailable(final ComponentAvailability availableComponent, final Collection<String> userRoles) {
		return availableComponent.getRoles().isEmpty() || CollectionUtils.containsAny(userRoles, availableComponent.getRoles());
	}

	private boolean evaluatePredicate(final AreaElement area, final ComponentAvailability availableComponent) {
		if(availableComponent instanceof ComponentAvailabilityWithPredicate) {
			final ComponentAvailabilityWithPredicate availabilityWithPredicate = (ComponentAvailabilityWithPredicate) availableComponent;
			return availabilityWithPredicate.getPredicate().test(
					new ComponentAvailabilityWithPredicate.AreaAndComponentId() {
						@Override
						public AreaElement getArea() {
							return area;
						}
						@Override
						public String getComponentId() {
							return availableComponent.getId();
						}
						@Override
						public ComponentAvailabilityWithPredicate.TemplateUtil getTemplateUtil() {
							return availabilityWithPredicate.getTemplateUtil();
						}
					}
			);
		}
		return true;
	}
}
