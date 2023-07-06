package com.merkle.oss.magnolia.renderer.handlebars.generator.reference;


import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class AnnotationClass {
	private String className;
	private Set<String> locationMethods = Collections.emptySet();
	private Set<String> templateKeyMethods = Collections.emptySet();

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public void setLocationMethods(final Set<String> locationMethods) {
		this.locationMethods = locationMethods;
	}

	public Set<String> getLocationMethods() {
		return locationMethods;
	}

	public void setTemplateKeyMethods(final Set<String> templateKeyMethods) {
		this.templateKeyMethods = templateKeyMethods;
	}

	public Set<String> getTemplateKeyMethods() {
		return templateKeyMethods;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AnnotationClass that = (AnnotationClass) o;
		return Objects.equals(className, that.className) && Objects.equals(locationMethods, that.locationMethods) && Objects.equals(templateKeyMethods, that.templateKeyMethods);
	}

	@Override
	public int hashCode() {
		return Objects.hash(className, locationMethods, templateKeyMethods);
	}

	@Override
	public String toString() {
		return "AnnotationClass{" +
				"className=" + className +
				", locationMethods=" + locationMethods +
				", templateKeyMethods=" + templateKeyMethods +
				'}';
	}
}
