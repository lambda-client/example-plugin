# Lambda Example Addon
This repository contains a minimal example for a Minecraft mod that uses Lambda's API. \
It aims to be as self-explanatory as possible for developers with minimal experience.

## How do I add features ?
We use [ClassGraph](https://github.com/classgraph/classgraph) to scan Fabric's classloader and load resources at runtime. \
These resources include but are not limited to:
- Modules
- Commands
- Pre/Post processor for building
- Various Minecraft objects for which they don't have defined lists (list of entity, etc.)

If you know a little bit about classloaders and reflection, you will realize that as long as your class matches the signature \
we are looking for, the resource will be found by the reflection tool and it will just work.

### Adding modules
Create an object extending `com.lambda.module.Module`. \
You can create the object (or the class with an empty constructor) anywhere you want in your codebase. \
However, we recommend to keep your code structured by placing them in `group.modules` (ex: com.lambda.modules). \
Once this is done, you can start coding your module by adding logic and settings. See existing modules for examples.

### Adding commands
The process is very similar to adding modules. \
In `group.commands` (ex: com.lambda.commands), create an object extending `com.lambda.command.LambdaCommand` and implement `CommandBuilder.create`. \
This function provides a `CommandBuilder` scope which is collected by the super class and then dispatched to the game to register it.

## Updating your addon
In `gradle.properties`, there are two variables that control the version of Lambda and which release type to use.
<br>
<br>
If you wish to use a snapshot releases of Lambda, change `mavenType` to `releases` and `lambdaVersion` to either `version-SNAPSHOT` to get the latest snapshot or \
a specific snapshot date (ex: `0.1.0+1.21.11-20260405.194419-31`). \
For official releases, set `mavenType` to `releases` and `lambdaVersion` to the lates available version (ex: `0.1.0`)

Please note that snapshot releases offer no assurance of compatibility with the latest release. That being said, you are allowed to use snapshots \
for your addon for bug fixes.


