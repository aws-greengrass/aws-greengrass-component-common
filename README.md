# Standard Operation Procedure
To keep device/cloud consistency, whenever changing this brazil pkg, remember to run 
`bb publish` after `git push` to mainline. 

We’ve made the decision before not investing more to automate it with pipeline + todworker for now.

## Steps
**Step 1.** Export Isengard credentials for account 698947471564 aws-iot-evergreen-dev@amazon.com

**Step 2.** in `build.gradle`'s dependencies section, uncomment the following section
```
//  implementation 'com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.10.1'
//  implementation 'com.fasterxml.jackson.core:jackson-databind:2.10.1'
//  implementation 'com.vdurmont:semver4j:3.1.0'
...
```
And comment out the brazil dependency:
```
  implementation brazilGradle.build()
  testImplementation brazilGradle.testbuild()
  ...
```

Final state before run `bb publish`:  
```
  implementation 'com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.10.1'
  implementation 'com.fasterxml.jackson.core:jackson-databind:2.10.1'
  implementation 'com.vdurmont:semver4j:3.1.0'
  testImplementation 'org.junit.jupiter:junit-jupiter-engine:5.6.2'
  testImplementation 'org.hamcrest:hamcrest-core:2.2'
  ...

//  implementation brazilGradle.build()
//  testImplementation brazilGradle.testbuild()
```
This is to tell Maven to find dependency from Maven central. Otherwise, it tries to find dependency from Brazil and 
fails at device side because device side can't access Brazil.

**Step 3.** Run `bb publish`.

**Step 4.** Verify device side has latest change by `mvn -U clean [compile/verify/...] 

#What?

This package is an example Java library package using the BrazilGradle build system. It provides a basic gradle script having Brazil quality plugins applied.

#References
* Gradle User Guide Command-Line Interface (options you can use with `brazil-build`): https://docs.gradle.org/current/userguide/command_line_interface.html
* Gradle User Guide Java Plugin: https://docs.gradle.org/current/userguide/java_plugin.html
* Gradle User Guide Checkstyle Plugin: https://docs.gradle.org/current/userguide/checkstyle_plugin.html
* Gradle User Guide SpotBugs Plugin: http://spotbugs.readthedocs.io/en/latest/gradle.html
* Gradle User Guide JaCoCo Plugin: https://docs.gradle.org/current/userguide/jacoco_plugin.html
* Authoring Gradle Tasks: https://docs.gradle.org/current/userguide/more_about_tasks.html
* BrazilGradle and IDE: https://w.amazon.com/bin/view/BrazilGradle/#ide
* Executing tests using JUnit5 Platform: https://junit.org/junit5/docs/current/user-guide/#running-tests-build-gradle and https://docs.gradle.org/4.6/release-notes.html#junit-5-support
