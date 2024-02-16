# Porting Lib Contributing Guidelines
Thank you for your interest in contributing to Porting Lib! We have some guidelines you should follow along the way.

### Ideology
Porting Lib's goal is not a 1-to-1 port of Forge features. While these may happen occasionally, they should be
the exception, not the rule.
If an API has room for improvement, don't just port it over. The functionality should be provided another way.
You should be able to do the same things with Porting Lib as you can with Forge; not necessarily in the same way though.

### Organization
Porting Lib is split into modules. Each module has its own functionality and features. The separation allows for mods
to only use the modules they need.

Mixins should be sorted by environment. Ex. have `mixin.common` and `mixin.client` subpackages.

Accessor mixins should be further sorted into `accessor` subpackages. Ex. `mixin.common.accessor`.

Extension interfaces adding functionality to Minecraft classes should be injected onto their targets and stored
in an `extensions` package.

### Mixin Standards
#### Class Names
All mixin classes should be named after the class that they mixin to followed by either `Mixin` if the class is
a regular mixin, or `Accessor` if it is an accessor mixin.

When mixing into inner classes, use a `$` symbol to separate the outer and inner class names. For example, when
making a regular mixin for the class `ClassA.ClassB`, the mixin class name would be `ClassA$ClassBMixin`.

#### Behavior and Style
- All mixin-added fields and methods must be private and annotated with `@Unique`. This will prefix them with the
mod ID, which is critical for mod compatibility.

- Mixins are to be designed to be as compatible as possible. No Overwrites, Redirects, or Inject Head Unconditional
Cancels. Make use of Mixin Extras and our own Mixin Extensions module.

- All methods in extension interfaces must be prefixed with `port_lib$`. This is because it is not possible for
this to be done automatically by mixin, and it is critical to avoid conflicting with other mods.

- Accessors and Invokers should be named `get`/`set`/`call` + the target name. For example, an accessor for a field
named `level` would be called `getLevel`, and an invoker for `setBlockState` would be `callSetBlockState`.

- Mixins that mixin to a class that only exists in a certain environment type should have an
`@Environment(EnvType.{type})` annotation to improve clarity. This annotation should go above the `@Mixin`
annotation.

- All mixin classes should be abstract. Regular mixins should have the `abstract` modifier. Accessors should be
interfaces, which are already technically abstract.

- Shadowed methods, invokers, and accessors should always be abstract, unless they are static. If static, they
must throw an exception. They should also be placed towards the top of the class.

- Accessor methods should always go before Invoker methods.

- The mixin config JSON file should have all mixins in alphabetical order, with accessor mixins being listed before
regular mixins.

### Adding Modules
- Modules can be added when a new feature doesn't fit in an existing one.
- Module names should use lower_snake_case.
- Start by creating the directory. It should match the module's name.
- Add a build.gradle file, which will likely be empty.
- Include the module in the root `settings.gradle` file, in alphabetical order with the others.
- To depend on another module, call `portingLib.addModuleDependency("project_name")` in the buildscript.
- All code should be in the `io.github.fabricators_of_create` package. Exceptions are made when a significant
portion of the code is copied from elsewhere. See `networking` and `tags` modules.
- A `fabric.mod.json` file is the only needed metadata. An icon will be added at build time.
  - The mod JSON will be filled with additional values on build as well. The only required fields are
  `id`, `name`, `description`, `version`, and `schemaVersion`. Add anything else as needed.
  - `id` should be `porting_lib_` plus the module name.
  - `version` should be `${version}`, it'll be filled in at build.
  - `schemaVersion` should be `1`. It's only there to make the IDE be quiet.
  - When in doubt, reference another module.

### Common Gradle Errors
   - Since Gradle is a quality piece of software, here's some common errors and solutions.
     - **Failed to download version manifest:**
       - Find which project it fails in. Delete the .gradle folder from that project and try again.
     - **Any FileSystemException:**
       - Run `./gradlew --stop` to stop all running daemons and try again.

### Asking Questions
Not sure about something? Want feedback on an API? You can find us either here on GitHub, or in
the [official Create discord server](https://discord.gg/hmaD7Se) in #devchat.
