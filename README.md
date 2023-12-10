sbhx
----

Spring Boot with Htmx and Thymeleaf

This is only a "babies first steps" project, Htmx has many [extensions](https://htmx.org/extensions/)
which will be needed for a full-featured web-site experience.

Use the `run` profile to enable live-reloads of Thymeleaf templates. 
The `run` profile will activate the `test` profile and load test-resources.

```
java17
mvn spring-boot:run -Prun
```

and open http://localhost:8080/

Documentation:
  - https://htmx.org/docs/
  - https://picocss.com/docs/
  - https://www.thymeleaf.org/documentation.html

This project uses the older Sping Boot and Java versions.
For high-performance, the latest Spring Boot and Java 21 version should be used,
together with the "virutal threads" option. See for example [sbtreeconf](https://github.com/fwi/sbtreeconf/compare/security...spring-boot-3)

Credits:
  - SVG loaders courtesy of https://github.com/SamHerbert/SVG-Loaders
  - Favicon courtesy of https://eclipse.org
