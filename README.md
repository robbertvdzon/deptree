# kotlin-mojo-javadoc
Shows how Kotlin 1.0.3 ignores Mojo Javadoc.

Install the artifact:
```
mvn clean install
```

Describe it in detail:
```
mvn help:describe '-Dplugin=${project.groupId}:${project.artifactId}:${project.version}' -Ddetail
```

Note that the Kotlin goal and parameters have no description, while Java ones do.