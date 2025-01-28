package com.merkle.oss.magnolia.renderer.handlebars.generator.reference;

import java.util.Objects;

public abstract class AbstractTemplateScript {
	private final String location;
	private final String key;
	private final String rendererName;

	protected AbstractTemplateScript(
			final String location,
			final String key,
			final String rendererName
	) {
		this.location = location;
		this.key = key;
		this.rendererName = rendererName;
	}

	public String getLocation() {
		return location;
	}

	public String getKey() {
		return key;
	}

	public String getRendererName() {
		return rendererName;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		AbstractTemplateScript that = (AbstractTemplateScript) o;
		return Objects.equals(location, that.location) && Objects.equals(key, that.key) && Objects.equals(rendererName, that.rendererName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(location, key, rendererName);
	}

	@Override
	public String toString() {
		return "AbstractTemplateScript{" +
				"location='" + location + '\'' +
				", key='" + key + '\'' +
				", rendererName='" + rendererName + '\'' +
				'}';
	}
}
