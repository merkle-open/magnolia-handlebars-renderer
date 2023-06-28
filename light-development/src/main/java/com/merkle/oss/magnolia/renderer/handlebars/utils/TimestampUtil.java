package com.merkle.oss.magnolia.renderer.handlebars.utils;

import org.apache.commons.text.StringSubstitutor;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimestampUtil {
	private static final Pattern FILE_PATTERN = Pattern.compile("^(.+)\\.(.+)$");
	private static final DateTimeFormatter DEFAULT_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS");
	private final DateUtil dateUtil;

	@Inject
	public TimestampUtil(final DateUtil dateUtil) {
		this.dateUtil = dateUtil;
	}

	public String addTimestamp(final String filePath) throws IOException {
		return addTimestamp(filePath, DEFAULT_PATTERN);
	}

	public String addTimestamp(final String filePath, final DateTimeFormatter pattern) throws IOException {
		final Matcher matcher = FILE_PATTERN.matcher(filePath);
		if (!matcher.find()) {
			throw new IOException("Invalid file: " + filePath);
		}

		return new StringSubstitutor(Map.of(
				"timestamp", getFormattedLastModified(filePath, pattern),
				"withoutExt", matcher.group(1),
				"ext", matcher.group(2)
		)).replace("${withoutExt}~${timestamp}~cache.${ext}");
	}

	private String getFormattedLastModified(final String filePath, final DateTimeFormatter pattern) throws IOException {
		final File file = getFile(filePath);
		return dateUtil.asLocalDateTime(new Date(file.lastModified())).format(pattern);
	}

	private File getFile(final String filePath) throws IOException {
		final URL url = getClass().getClassLoader().getResource(filePath);
		if (url == null) {
			throw new IOException("File " + filePath + " not found!");
		}
		return getFile(url);
	}

	private File getFile(final URL url) throws IOException {
		if (url.openConnection() instanceof JarURLConnection) {
			final JarURLConnection jarUrl = (JarURLConnection) url.openConnection();
			return new File(jarUrl.getJarFile().getName());
		}
		return new File(url.getFile());
	}
}
