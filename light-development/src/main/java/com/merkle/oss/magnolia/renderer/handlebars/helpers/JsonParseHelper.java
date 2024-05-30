package com.merkle.oss.magnolia.renderer.handlebars.helpers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.jknack.handlebars.Options;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonParseHelper implements NamedHelper<String> {
	private final Gson gson = new GsonBuilder().create();

	public Object apply(final String json, final Options options) throws IOException {
		final JsonElement jsonElement = JsonParser.parseString(json);
		if(jsonElement.isJsonArray()) {
			return gson.fromJson(jsonElement.getAsJsonArray(), List.class);
		}
		if(jsonElement.isJsonObject()) {
			return gson.fromJson(jsonElement.getAsJsonObject(), Map.class);
		}
		if(jsonElement.isJsonPrimitive()) {
			return json;
		}
		return null;
	}

	@Override
	public Set<String> names() {
		return Set.of("parse-json");
	}
}
