package com.merkle.oss.magnolia.renderer.handlebars.blossom.component.availability;

import com.google.auto.factory.AutoFactory;
import info.magnolia.cms.security.operations.ConfiguredAccessDefinition;
import info.magnolia.cms.security.operations.ConfiguredOperationPermissionDefinition;
import info.magnolia.cms.security.operations.OperationPermissionDefinition;
import info.magnolia.rendering.template.ComponentAvailability;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Usage:<br>
 * Annotate component AvailableForRoles.<br>
 * E.g:
 * <pre>{@code
 * @AvailableForRoles({"superuser"})
 * @Template(...)
 * public class SomeComponent {...}
 * }</pre>
 */
@AutoFactory
public class ComponentAvailabilityRoleResolver {
	private final Map<String, Class<?>> handlerClassMapping;

	@Inject
	public ComponentAvailabilityRoleResolver(final Map<String, Class<?>> handlerClassMapping) {
		this.handlerClassMapping = handlerClassMapping;
	}

	public ComponentAvailability addRoles(final ComponentAvailability componentAvailability) {
		return Optional.ofNullable(handlerClassMapping.get(componentAvailability.getId()))
				.map(handlerClass ->
						handlerClass.getAnnotation(AvailableForRoles.class)
				)
				.map(AvailableForRoles::value)
				.map(roles -> withRoles(componentAvailability, roles))
				.orElse(componentAvailability);
	}

	private ComponentAvailability withRoles(final ComponentAvailability componentAvailability, final String[] roles) {
		return new ComponentAvailability() {
			@Override
			public String getId() {
				return componentAvailability.getId();
			}
			@Override
			public Collection<String> getRoles() {
				return Stream.concat(
						componentAvailability.getRoles().stream(),
						Arrays.stream(roles)
				).collect(Collectors.toSet());
			}
			@Override
			public OperationPermissionDefinition getPermissions() {
				final ConfiguredAccessDefinition accessDefinition = new ConfiguredAccessDefinition();
				accessDefinition.setRoles(getRoles());

				final ConfiguredOperationPermissionDefinition readOnlyPermissions = new ConfiguredOperationPermissionDefinition();
				readOnlyPermissions.setRead(null);
				readOnlyPermissions.setWrite(accessDefinition);
				readOnlyPermissions.setAdd(accessDefinition);
				readOnlyPermissions.setDelete(accessDefinition);
				readOnlyPermissions.setMove(accessDefinition);
				readOnlyPermissions.setExecute(accessDefinition);
				return readOnlyPermissions;
			}
			@Override
			public boolean isEnabled() {
				return componentAvailability.isEnabled();
			}
		};
	}
}
