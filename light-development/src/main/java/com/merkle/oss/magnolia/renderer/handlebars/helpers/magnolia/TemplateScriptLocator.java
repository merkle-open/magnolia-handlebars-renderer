package com.merkle.oss.magnolia.renderer.handlebars.helpers.magnolia;

public interface TemplateScriptLocator {

	String get(String key);

	String get(String key, String type);
}
