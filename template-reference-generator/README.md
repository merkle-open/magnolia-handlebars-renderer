# Handlebars reference generator
This maven plugin generates java classes for handlebars templates defined in the frontend.

## Configuration 
```xml
<build>
	<plugins>
		<plugin>
			<groupId>com.namics.oss.magnolia</groupId>
			<artifactId>magnolia-handlebars-template-reference-generator</artifactId>
			<version>${merkle.oss.handlebars-renderer.version}</version>
			<configuration>
				<packageName>com.namics.blitzdings.web.theme.templates</packageName>
				<templateScriptClass>com.namics.blitzdings.web.theme.templates.HandlebarsTemplateScript</templateScriptClass>
				<handlebarsSrcRoot>${basedir}/src/main/resources/frontend/src</handlebarsSrcRoot>
				<includeHandlebarFiles>
					<includeHandlebarFile>patterns/**.hbs</includeHandlebarFile>
					<includeHandlebarFile>views/magnolia/**.hbs</includeHandlebarFile>
				</includeHandlebarFiles>
				<annotationClasses>
					<annotationClass>
						<className>org.springframework.stereotype.Component</className>
						<locationMethods>
							<locationMethod>value</locationMethod>
						</locationMethods>
					</annotationClass>
				</annotationClasses>
			</configuration>
			<executions>
				<execution>
					<goals>
						<goal>reference-generator</goal>
					</goals>
				</execution>
			</executions>
		</plugin>
	</plugins>
</build>
```

### Parameters

| Key                                | Type         | Mandatory | Description                                                                                                                                           
|------------------------------------|--------------|-----------|-------------------------------------------------------------------------------------------------------------------------------------------------------
| packageName                        | String       | x         | package name of the generated classes                                                                                                                 
| templateScriptClass                | String       | x         | fully qualified name of the template script class. The class constructor must have 2 parameters: String location, String templateKey.                 
| classNameSuffix                    | String       |           | suffix of the generated classes. Defaults to HbsTemplate                                                                                              
| outputDirectory                    | String       | 		    | output directory for generated classes. Defaults to ${project.build.directory}/generated-sources                                                      
| handlebarsSrcRoot                  | String       | x         | root directory of frontend hbs files                                                                                                                  
| includeHandlebarFiles              | Set<String>	| x		    | [glob](https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob) pattern definition for handlebar files to generate classes for         
| excludeHandlebarFiles              | Set<String>  | x	        | [glob](https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob) pattern definition for handlebar files to **not** generate classes for 
| annotationClasses                  | Set<Object>	| x         | at least one annotation class needs to be specified. E.g. Component for spring                                                                        
| annotationClass.className          | String       | x	        | fully qualified annotation class name
| annotationClass.locationMethods    | Set<String>  | x	        | annotation method that should be set to hbs location
| annotationClass.templateKeyMethods | Set<String>  | x         | annotation method that should be set to hbs templateKey

### Trigger generation
```shell
mvn generate-sources
```