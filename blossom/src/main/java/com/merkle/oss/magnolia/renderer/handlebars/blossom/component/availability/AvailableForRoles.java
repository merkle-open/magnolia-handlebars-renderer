package com.merkle.oss.magnolia.renderer.handlebars.blossom.component.availability;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AvailableForRoles {
	String[] value() default {};
}
