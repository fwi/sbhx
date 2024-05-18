sbhx
----

Spring Boot with Htmx and Thymeleaf

This is only a "babies first steps" project, Htmx has many [extensions](https://htmx.org/extensions/)
which will be needed for a full-featured web-site experience.

Use the `run` profile to enable live-reloads of Thymeleaf templates. 
The `run` profile will activate the `test` profile and load test-resources.

```
java21
mvn spring-boot:run -Prun
```

and open http://localhost:8080/

Documentation:
  - https://htmx.org/docs/
  - https://picocss.com/docs/
  - https://www.thymeleaf.org/documentation.html

A Spring Boot extension to support common HTMX functions is also [available](https://github.com/wimdeblauwe/htmx-spring-boot).

Credits:
  - SVG loaders courtesy of https://github.com/SamHerbert/SVG-Loaders
  - Favicon courtesy of https://eclipse.org
