# Component Recipe Reference
## Reference and guidelines
This reference describes version 2020-01-25 of component recipe file format.

Component recipe is a single YAML or JSON file for the component author to define component deployment and runtime
 characteristics in the AWS Greengrass ecosystem.
## Recipe file structure and examples
Here is a sample recipe file in yaml format. It defines a simple HelloWorld application which can run on AWS Greengrass
 managed devices. It defines a manifest for x86_64 linux as well as a manifest for armv7 linux.
 
 > Recipe key names use [PascalCase](https://wiki.c2.com/?PascalCase).
 
```yaml
---
RecipeFormatVersion: 2020-01-25
ComponentName: aws.greengrass.HelloWorld
ComponentVersion: 1.0.0
ComponentDescription: Hello world from greengrass!
ComponentPublisher: Amazon
ComponentType: aws.greengrass.generic
ComponentConfiguration:
  DefaultConfiguration:
    singleLevelKey: default value of singleLevelKey
    args:
      x86Arg: Hello x86_64
      armArg: Hello armv7

ComponentDependencies:
  foo.bar:
    VersionRequirement: 2.0.*
    DependencyType: SOFT

Manifests:
    # Optional friendly name of the platform manifest
  - Name: Linux x86_64
    Platform:
      os: linux
      architecture: x86_64
      <customKey>: <customValue>

    # Primary way to define component lifecycle. If not specified, use the secondary way with Selections
    Lifecycle:
      Run:
        python3 {artifacts:decompressedPath}/hello_world.py {configuration:/args/x86Arg}

    # Secondary way to define component lifecycle by selecting keywords in Global Lifecycle
    # Only used when Lifecycle is not defined in the manifest.
    Selections: [<keyword1>]

    Artifacts:
      - URI: s3://some-bucket/hello_world_x86.zip
        Unarchive: ZIP

  - Name: Linux armv7
    Platform:
      os: linux
      architecture: armv7
    Selections: [<keyword2>]
    Lifecycle:
      Run:
        python3 {artifacts:path}/hello_world.py {configuration:/args/armArg}
    Artifacts:
      - URI: s3://some-bucket/hello_world.py
        Permission:
          Execute: ALL
          READ: ALL

# Global Lifecycle
Lifecycle:
  Run:
    <keyword1>: {}
    <keyword2>: {}
```

### Recipe Format Version
Define the version of recipe format.
```yaml
RecipeFormatVersion: 2020-01-25
```
### Component Name
Component name identifier. Reverse DNS notation is recommended. Component name is unique within a private component
 registry. A private component occludes the public component with the same name.
> note: component name is also used as service name, since component to service is 1:1 mapping.

```yaml
ComponentName: aws.greengrass.HelloWorld
```
### Component Version
Component version follows [semantic versioning](https://semver.org/) standard.
```yaml
ComponentVersion: 1.0.0
```
### Component Description
Text description of the component.
```yaml
ComponentDescription: Hello World App
```
### Component Publisher
Publisher of component
```yaml
ComponentPublisher: Amazon
```
### Component Type
Describe component runtime mode, support values: `aws.greengrass.plugin`, `aws.greengrass.lambda`, `aws.greengrass.generic`, `aws.greengrass.nucleus`, default is `aws.greengrass.generic`
```yaml
ComponentType: aws.greengrass.generic
```

#### Component Dependencies
Describe component dependencies. The versions of dependencies will be resolved during deployment.
> note: Services represented by components will be started/stopped with respect to dependency order.

```yaml
ComponentDependencies:
  foo.bar:
    VersionRequirement: 2.0.*
    DependencyType: SOFT
```
##### Version Requirement
Specify dependency version requirements, the requirements use NPM-style syntax.
##### Dependency Type
Specify if dependency is `HARD` or `SOFT` dependency. `HARD` means dependent service will be restarted if the dependency
 service changes state. In the opposite, `SOFT` means the service will make sure the dependency is present when first
  starting, but will not be restarted if the dependency changes state.

#### ComponentConfiguration
Describe the configuration of the component

```yaml
ComponentConfiguration:
  DefaultConfiguration:
    singleLevelKey: default value of singleLevelKey
    args:
      x86Arg: Hello x86_64
      armArg: Hello armv7
```

##### ComponentConfiguration.DefaultConfiguration
Each Greengrass V2 component could define its own default configuration which would be used by default.
The configuration is a free-form hierarchical structure. It could be used by the recipe's lifecycle section with dynamic interpolation 
as well as the component code and logic.

### Manifests
Define a list of manifests, a manifest is specific to one platform or default to every other platform.
#### Manifest.Platform
Define the platform the manifest is specifically for.
```yaml
Platform:
  os: linux
  architecture: x86_64
  keyword3: label
  keyword4: *
  keyword5: /a|b/
```
- Example operating systems: linux, darwin, *
- Example architecture: amd64, arm, *

This section consists of key/value pairs. The key can have any name, although "os" and "architecture" will always be
defined. Additional keys will depend on the OS and Architecture. It is also possible to override and/or define
additional keys via system configuration. When a label (not beginning with a symbol) is specified, an exact match
is assumed. The symbol `*` means any (including no) value. A Java-style regular expression can be specified, e.g.
"/darwin|linux/". The regular expression "/.+/" will match any non-blank value.
A platform matches if (and only if) all conditions hold. Thus the above platform would match if Nucleus specifies:
```yaml
os: linux
architecture: amd64
keyword3: label
keyword5: b
```
Keyword3 and Keyword4 would have to be provided by platform overrides in configuration in this case.
It would fail if Nucleus specifies only:
```yaml
os: linux
architecture: amd64
```

#### Manifests.Name
If specified, provides a friendly name representing the platform in AWS Console. If not specified, a name is created based
off of os and architecture.

#### Manifest.Lifecycle
Specify lifecycle management scripts for component represented service. See details in
 [README_CONFIG_SCHEMA](https://github.com/aws-greengrass/aws-greengrass-nucleus/blob/main/README_CONFIG_SCHEMA.md#lifecycle).

#### Manifest.Lifecycle with recipe variables
Recipe variables expose information from the component and Nucleus for you to use in your recipes. For example, you can
 use recipe variables to pass component configurations to a lifecycle script that exists as an artifact.

Recipe variables use {recipe_variable} syntax. The single curly braces indicate a recipe variable and will be replaced at runtime.

##### Use Component's own variables
{\<namespace\>:\<key\>}

The following recipe variables:

1. {configuration:\<json pointer\>} The value of a configuration at the provided JSON pointer location for the component.

For example, the {configuration:/path/list/0} recipe variable retrieves the value at the location of `/path/list/0` from the configuration.

Note a JSON pointer could point to 4 different possible node type, including:
1. Value node: the place holder will be replacedd by the **the text representation for that value**.
2. Container node: the place holder will be replacedd by the serialized JSON String representation for that container. Note the JSON string
usually contains double quotes. If you are using it in the command line, make sure you escape it appropriately.
3. `null`: the placeholder will be replaced as: **null**
4. Missing node: the placeholder **will remain** unchanged.

You can use this variable to provide a configuration value to a script that you run in the component lifecycle.

1. {artifacts:path}:
The root path of the artifacts for the component that this recipe defines. When a component installs, AWS IoT Greengrass copies the component's artifacts to the folder that this variable exposes.
You can use this variable to identify the location of a script to run in the component lifecycle.

1. {artifacts:decompressedPath}:
The root path of the decompressed archive artifacts for the component that this recipe defines. When a component installs, AWS IoT Greengrass unpacks the component's archive artifacts to the folder that this variable exposes.
You can use this variable to identify the location of a script to run in the component lifecycle.
Each artifact unzips to a folder within the decompressed path, where the folder has the same name as the artifact minus its extension. For example, a ZIP artifact named models.zip unpacks to the {artifacts:decompressedPath}/models folder

##### Use Direct Dependencies' variables
If a component has dependencies, it sometimes require reading its dependencies's info at runtime, such as configuration and artifact path.
Syntax: {\<componentName\>:\<namespace\>:\<key\>}

Similarly, you could use the variables above.
1. {\<componentName\>:configuration:\<json pointer\>}
1. {\<componentName\>:artifacts:path}
1. {\<componentName\>:artifacts:decompressedPath}

If you refer to a componentName that is not a direct dependency, **the placeholder will remain unchanged**.

#### Global recipe variables
Global recipe variables could be used by any component.

1. {kernel:rootPath}
The absolute root path that the Nucleus is running at runtime.

#### Manifest.Artifacts
A list of artifacts that component uses as resources, such as binary, scripts, images etc.
```yaml
Artifacts:
  - URI: s3://some-bucket/hello_world.zip
    Unarchive: NONE|ZIP
    Permission:
      Read: NONE|OWNER|ALL
      Execute: NONE|OWNER|ALL
```
##### URI
Artifacts are referenced by artifact URIs. Currently Greengrass supports Greengrass repository and s3 as artifact
 storage location.
##### Unarchive
Indicate whether or not to automatically unarchive artifact. Support ZIP files.
##### Permission
Specify whether the artifact should be readable and/or executable. By default, the artifact is
 readonly to the user running the component (the owner).
 If the artifact is unarchived automatically, the same permission setting applies to all files in the archive.

#### Manifests.Selections
An optional list of selection keys to apply to the global lifecycle section (described further down).
```yaml
Selections: [debian, linux, all]
```
Keys are used to select sub-sections of the global lifecycle in order of precedence. For example, if the Lifecycle
section contains "debian", "linux" and "all" keys at a given level, only "debian" key is used. However if the Lifecycle
section contains only "linux" and "all", the "linux" key is used. "all" is optional and is assumed. Thus "[linux]" is
the same as "[linux, all]". If no selections are specified, "[all]" is assumed.

### Global Lifecycle
If Lifecycle section exists outside of the Manifests section, it applies to all manifests that do not specify a
lifecycle. In this case, the Selections list is used to select sub-sections of the lifecycle section. Consider the
following yaml:
```yaml
Lifecycle:
  key1:
    Install:
      Skipif: onpath <executable>|exists <file>
      Script: command1
  key2:
    Install:
      Script: command2
  all:
    Install:
      Script: command3
```
In the above example, if the matching platform contained Selections [key1,all], then the lifecycle section will be
rewritten as:
```yaml
Lifecycle:
  Install:
    Skipif: onpath <executable>|exists <file>
    Script: command1
```
Similarly if the matching platform contained Selections [key4], then the lifecycle section would be rewritten as:
```yaml
Lifecycle:
  Install:
    Script: command3
```
The selections keywords can be applied at any level, so this is also valid:
```yaml
Lifecycle:
  Install:
    Script:
      key1: command1
      key2: command2
      all: command3
```
Selection keywords can also be mixed:
```yaml
Lifecycle:
  key1:
    Install:
      Skipif: onpath <executable>|exists <file>
      Script: command1
  key2:
    Install:
      Script: command2
  all:
    Install:
      Script:
        key3: command3
        key4: command4
        all: command5
```
