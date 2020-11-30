# Component Recipe Reference
Component Recipe is a single YAML or JSON file for the component author to define component deployment and runtime
characteristics in the AWS Greengrass ecosystem.
 
## Reference last updated date: 2020.09.21
The date is here for easier reference. Look at git commit time for the accurate timestamp instead.

## Guidelines
### Unknown or unrecognized Property
1. Currently, unknown properties will not be parsed so that it gives team members clear message if a field is mis-spelled, etc.

### Backward compatibility for the version 2020-01-25
1. Property names are case-insensitive. Please use Pascal case if possible. See below.
2. Enum names are case-insensitive.
3. New fields like "ComponentDependencies" will not break existing recipe parsing.

## Recipe file structure and examples
Here is a sample recipe file in yaml format which defines a simple HelloWorld application can run on AWS Greengrass
 managed devices. It defines a manifest for x86_64 windows as well as a manifest for arm32 linux.
 
 > recipe key name use [PascalCase](https://wiki.c2.com/?PascalCase), and is case-sensitive
 
```yaml
---
RecipeFormatVersion: 2020-01-25
ComponentName: com.aws.greengrass.HelloWorld
ComponentVersion: 1.0.0
ComponentDescription: hello world from greengrass!
ComponentPublisher: Amazon
ComponentType: aws.greengrass.generic
ComponentConfiguration:
  DefaultValue: # object: the default configuration
    FirstItem:
      message: 'hello'
    SecondItem:
      message: 'goodbye'
Manifests:
  - Platform:
      os: windows
      architecture: amd64
    # Friendly name of platform (not of component)
    Name: Windows AMD64
    # parameter is deprecating, the section is undocumented to external customers
    Parameters:
      - Name: repeat
        Value: 10
        Type: NUMBER
      - Name: greeting
        Value: 'Hello Windows'
        Type: STRING
      - Name: print
        Value: false
        Type: BOOLEAN
    # lifecycle can be passed per manifest, or passed common to all manifests
    Lifecycle:
      Run:
        python3 {{artifacts:path}}/hello_windows_server.py '{{params:greeting.value}}'
    # If lifecycle not specified for multi-platform, selections will typically be specified instead
    Selections: [windows]
    Artifacts:
      - URI: s3://some-bucket/hello_windows.zip
        Unarchive: ZIP
        # digest and algorithm are not provided by customer
        Digest: d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f
        Algorithm: SHA-256
    Dependencies:
      variant.Python3:
        VersionRequirement: ^3.5
        DependencyType: SOFT
  - Platform:
      os: linux
      architecture: arm
    Parameters:
      - Name: repeat
        Value: 10
        Type: NUMBER
      - Name: greeting
        Value: 'Hello World'
        Type: STRING
    Lifecycle:
      Run:
        python3 {{artifacts:path}}/hello_world.py '{{params:greeting.value}}'
    Artifacts:
      - URI: s3://some-bucket/hello_world.py
        Digest: d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f
        Algorithm: SHA-256
    Dependencies:
      variant.Python3:
        VersionRequirement: ^3.5
# alternative lifecycle specification
Lifecycle:
  Run:
    # Selection keyword (see detailed notes)
    windows:
        python3 {{artifacts:path}}/hello_windows_server.py '{{params:greeting.value}}'

```
The topics on this reference are organized by top-level keys in terms of providing component metadata or
 defining platform specific manifest. Top-level keys can have options that support them as sub-topics. This
  maps to the `<key>: <options>: <value>` indent structure of recipe file.

### Recipe Format Version
Define the version of recipe format
```yaml
RecipeFormatVersion: '2020-01-25'
```
### Component Name
Component name identifier, reverse DNS notation is recommended. Component name is unique within a private component
 registry. A private component which has same name occludes public available component.
> note: component name is also used as service name, since component to service is 1:1 mapping.

```yaml
ComponentName: com.aws.greengrass.HelloWorld
```
### Component Version
Component version, use [semantic versioning](https://semver.org/) standard
```yaml
ComponentVersion: 1.0.0
```
### Component Description
Text description of component
```yaml
ComponentDescription: Hello World App for Evergreen
```
### Component Publisher
Publisher of component
```yaml
ComponentPublisher: Amazon
```
### Component Type
Describe component runtime mode, support values: `aws.greengrass.plugin`, `aws.greengrass.lambda`, `aws.greengrass.generic`, default is `aws.greengrass.generic`
```yaml
ComponentType: aws.greengrass.generic
```

### Component Configuration
Describe the configuration of the component

```yaml
ComponentConfiguration:
  DefaultConfiguration:
    FirstItem:
      message: 'hello'
    SecondItem:
      message: 'goodbye'
```

#### ComponentConfiguration.DefaultConfiguration

An optional default configuration that takes effect when the component is first deployed. If an operator resets any
configuration key, the value of that key and any sub-keys will be reset to the default value specified here. The
component should operate normally using only this set of default values.

### Component Dependencies
Don't use!!! Under active development...

### Manifests
Define a list of manifests, a manifest is specific to one platform or default to every other platform.
#### Manifests.Platform
Define the platform the manifest is specifically for.
```yaml
Platform:
  os: windows
  architecture: amd64
  keyword3: label
  keyword4: *
  keyword5: /a|b/
```
- Example operating system:
    - linux
    - windows
    - darwin
    - \*   
- Example architecture:
    - amd64
    - arm
    - \*
This section consists of key/value pairs. The key can have any name, although "os" and "architecture" will always be
defined. Additional keys will depend on the OS and Architecture. It is also possible to override and/or define
additional keys via system configuration. When a label (not beginning with a symbol) is specified, an exact match
is assumed. The symbol "*" means any (including no) value. A Java-style regular expression can be specified, e.g.
"/windows|linux/". The regular expression "/.+/" will match any non-blank value. 

A platform matches if (and only if) all conditions hold. Thus the above platform would match if Nucleus specifies:
```yaml
os: windows
architecture: amd64
keyword3: label
keyword5: b
```
Keyword3 and Keyword4 would have to be provided by platform overrides in configuration in this case.
It would fail if Nucleus specifies only:
```yaml
os: windows
architecture: amd64
```

#### Manifests.Name
If specified, provides a friendly name representing the platform in UX. If not specified, a name is created based
off of os and architecture. 

#### Manifests.Parameters 
> deprecating, moving towards JSON schema definition, prepare to migrate after beta 2

Define the parameter list can be used by component at runtime
```yaml
Parameters:
  - Name: repeat
    Value: 10
    Type: NUMBER
  - Name: greeting
    Value: 'Hello Windows'
    Type: STRING
  - Name: print
    Value: false
    Type: BOOLEAN
```

#### Manifests.Lifecycle
Specify lifecycle management scripts for component represented service, specific to given platform. See also
"Manifest.Selections" and (global) "Lifecycle" further down.
```yaml
Lifecycle:
  Setenv: # apply to all commands to the service.
    <key>: <defaultValue>
        
  Install:
    Skipif: onpath <executable>|exists <file>
    Script:
    Timeout: # default to be 120 seconds.
    Environment: # optional
      <key>: <overrideValue>
    
  Startup: # mutually exclusive from 'run'
    Script: # eg: brew services start influxdb
    Timeout: # optional
    Environment:  # optional, override
      
  Run: # mutually exclusive from 'startup'
    Script:
    Environment: # optional, override
    Timeout: # optional
    Periodicity: # perodically run the command
    
  Shutdown: # can co-exist with both startup/run
    Script:
    Environment: # optional, override
    Timeout: # optional
  
  Healthcheck: # do health check when service is in Running
    Script: # non-zero exit trigger error
    RecheckPeriod: # optional, default to be 0
    Environment: # override
    
  Recover:
    Script: # will be run every time service enters error.
    Environment: # optional, override
    # referring to https://docs.docker.com/v17.12/compose/compose-file/#restart_policy
    RetryPolicy:
      Delay: # default to be 0. Time to wait between retry.
      MaxAttempts: # default to be infinite. After N times of error, service enter Broken state.
      Window: # how long to wait before deciding if a restart has succeeded
  
  CheckIfSafeToUpdate:
    RecheckPeriod: 5
    Script: 
    
  UpdatesCompleted:
    Script:
```
#### Manifests.Selections
An optional list of selection keys to apply to the global lifecycle section (described further down).
```yaml
Selections: [debian, linux, all]
```
Keys are used to select sub-sections of the global lifecycle in order of precedence. For example, if the Lifecycle
section contains "debian", "linux" and "all" keys at a given level, only "debian" key is used. However if the Lifecycle
section contains only "linux" and "all", the "linux" key is used. "all" is optional and is assumed. Thus "[linux]" is
the same as "[linux, all]". If no selections are specified, "[all]" is assumed.
#### Manifests.Artifacts
A list of artifacts that component uses as resources, such as binary, scripts, images etc.
```yaml
Artifacts:
    - URI: s3://some-bucket/hello_windows.zip
      Unarchive: ZIP
      Digest: d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f
      Algorithm: SHA-256
```
##### URI
Artifacts are referenced by artifact URIs. Currently Greengrass supports Greengrass repository and s3 as artifact
 storage location.
##### Unarchive
Indicate whether automatically unarchive artifact.
##### Digest
artifact cryptographic hash for integrity check, calculated by component cloud service.
##### Algorithm
algorithm used for calculating cryptographic hash.
#### Manifest.Dependencies
Describe component dependencies, the versions of dependencies will be resolved during deployment.
> note: Services represented by components will be started/stopped with respect to dependency order.

```yaml
Dependencies:
    shared.python:
      VersionRequirement: ~3.6
      DependencyType: SOFT
```
##### Version Requirement
Specify dependency version requirements, the requirements use NPM-style syntax.
##### Dependency Type
Specify if dependency is `HARD` or `SOFT` dependency. `HARD` means dependent service will be restarted if the dependency
 service changes state. In the opposite, `SOFT` means the service will wait the dependency to start when first
  starting, but will not be restarted if the dependency changes state.

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
