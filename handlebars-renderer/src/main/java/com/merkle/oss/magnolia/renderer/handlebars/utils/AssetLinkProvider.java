package com.merkle.oss.magnolia.renderer.handlebars.utils;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import info.magnolia.context.MgnlContext;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoFactory(implementing = AssetLinkProvider.Factory.class)
public class AssetLinkProvider {
    private static final Pattern PATH_SUFFIX_PATTERN = Pattern.compile("^([^#]*)((#.*)|$)$");

    private final LinkUtil linkUtil;
    private final TimestampUtil timestampUtil;
    private final String frontendAssetsPath;

    public AssetLinkProvider(
        @Provided final LinkUtil linkUtil,
        @Provided final TimestampUtil timestampUtil,
        final String frontendAssetsPath
    ) {
        this.linkUtil = linkUtil;
        this.timestampUtil = timestampUtil;
        this.frontendAssetsPath = frontendAssetsPath;
    }

    public String getAssetLink(
        final String theme,
        final String assetPath,
        final boolean timestamp,
        final boolean absolute
    ) throws IOException {
        return toAbsolutePath(
            addContextPath(addResourcePrefix(getPath(theme, assetPath, timestamp))),
            absolute
        );
    }

    protected String addResourcePrefix(final String path) {
        return "/.resources" + path;
    }

    protected String addContextPath(final String path) {
        return MgnlContext.getContextPath() + path;
    }

    protected String toAbsolutePath(final String path, final boolean shouldApply) {
        if (shouldApply) {
            return linkUtil.toExternalLink(path);
        }
        return path;
    }

    protected String getPath(final String theme, final String path, final boolean shouldApplyTimestamp) throws IOException {
        final Matcher matcher = PATH_SUFFIX_PATTERN.matcher(path);
        if (matcher.find()) {
            final String filePath = Path.of(frontendAssetsPath, theme, matcher.group(1)).toString();
            return (shouldApplyTimestamp ? timestampUtil.addTimestamp(filePath) : filePath) + Optional.ofNullable(matcher.group(2)).orElse("");
        }
        throw new IllegalArgumentException("Couldn't match path"+path);
    }

    public interface Factory {
        AssetLinkProvider create(String frontendAssetsPath);
    }
}
