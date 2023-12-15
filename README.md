# Magnolia handlebars renderer

Handlebars implementation of magnolia renderer using [jknack](https://github.com/jknack/handlebars.java) library.

See [this README](blossom-reference-generator/README.md) for documentation on the blosom reference generator maven plugin.

## Setup
### DI-Bindings
- com.github.jknack.handlebars.io.ClassPathTemplateLoader
- com.merkle.oss.magnolia.renderer.handlebars.utils.DateUtil
- com.merkle.oss.magnolia.renderer.handlebars.utils.LinkUtil
- com.merkle.oss.magnolia.renderer.handlebars.utils.LocaleProvider
- com.merkle.oss.magnolia.renderer.handlebars.helpers.PatternHelper$TemplateScriptLocator

### Dependencies
#### Light-Development
##### POM
```xml
<dependency>
    <groupId>com.namics.oss.magnolia</groupId>
    <artifactId>magnolia-handlebars-renderer</artifactId>
    <version>${merkle.oss.handlebars-renderer.version}</version>
</dependency>
```
##### Magnolia-Module
```xml
<dependency>
    <name>magnolia-handlebars-renderer</name>
    <version>${merkle.oss.handlebars-renderer.version}</version>
</dependency>
```
#### Blossom
```xml
<dependency>
    <groupId>com.namics.oss.magnolia</groupId>
    <artifactId>magnolia-handlebars-renderer-blossom</artifactId>
    <version>${merkle.oss.handlebars-renderer.version}</version>
</dependency>
```
```xml
<dependency>
    <name>magnolia-handlebars-renderer-blossom</name>
    <version>${merkle.oss.handlebars-renderer.version}</version>
</dependency>
```

## Helpers
See separate [Helpers](Helpers.md) documentation.

## Custom ValueResolver
Custom jknack value resolvers can be configured by implementing a custom ValueResolversProvider
```xml
<component>
    <type>com.merkle.oss.magnolia.renderer.handlebars.renderer.HandlebarsRenderer$ValueResolversProvider</type>
    <implementation>custom.valueResolverProvider</implementation>
</component>
```

## Blossom component availability
### Role
For the blossom renderer components can be restricted for certain roles using the AvailableForRoles annotation:
```java
@AvailableForRoles("superuser")
@ContentArea
@Template(
		id = Script.ID,
		title = "templates.components." + Script.NAME + ".title",
		dialog = ScriptDialog.ID
)
@Controller
@TemplateDescription("templates.components." + Script.NAME + ".description")
public class Script extends BaseComponent {
    ...
}
```
### Custom
Example to only allow SomeComponent to be added inside backgroundArea:
```java
@ContentArea
@Template(
        id = Background.ID,
        title = "templates.components." + Background.NAME + ".title",
        dialog = BackgroundDialog.ID
)
@Controller
@TemplateDescription("templates.components." + Background.NAME + ".description")
public class Background extends BaseComponent {
    public static final String NAME = "Background";
    public static final String ID = COMPONENT_PREFIX + NAME;

    ...

    @Area(value = Background.BackgroundColorItemArea.NAME, title = "templates.areas." + Background.BackgroundColorItemArea.NAME + ".title")
    @AvailableComponentClasses({BackgroundArea.class})
    @Controller
    public static class BackgroundColorItemArea extends BaseArea {
        public static final String NAME = "BackgroundColorItemArea";
        ...
    }
}
```
```java
@BackgroundArea
public class BackgroundAreaComponentAvailabilityPredicate implements AreaAndComponentIdPredicate {
     @Override
     public boolean test(final AreaAndComponentId areaAndComponentId) {
         return SomeComponent.ID.equals(areaAndComponentId.getComponentId());
     }
}
```
```java
import com.google.inject.multibindings.MapBinder;
import com.merkle.oss.magnolia.renderer.handlebars.blossom.component.availability.ComponentAvailabilityPredicateResolver.AreaAndComponentIdPredicate;
import info.magnolia.objectfactory.guice.AbstractGuiceComponentConfigurer;

public class CustomGuiceComponentConfigurer extends AbstractGuiceComponentConfigurer {
	@Override
	protected void configure() {
		super.configure();
		MapBinder.newMapBinder(binder(), String.class, AreaAndComponentIdPredicate.class)
				.addBinding(Background.BackgroundColorItemArea.NAME).to(BackgroundAreaComponentAvailabilityPredicate.class);
	}
}
```