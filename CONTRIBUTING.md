# Porting Lib Contributing Guidelines
Thank you for your interest in contributing to Porting Lib! We have some guidelines you should follow along the way.

1. **Ideology**:
   - Porting Lib's goal is not a 1-to-1 port of Forge features. While these may happen occasionally, they should be
   the exception, not the rule. If an API has room for improvement, don't just port it over. The functionality
   should be provided another way. You should be able to do the same things with Porting Lib as you can with Forge; not
   necessarily in the same way though.

2. **Organization:**
    - Porting Lib is split into modules. Each module has its own functionality and features. The separation allows for
    mods to only use the modules they need.
    - Mixins should be sorted by environment. Ex. have `mixin.common` and `mixin.client` subpackages.
    - Accessor mixins should be further sorted into `accessor` subpackages. Ex. `mixin.common.accessor`.
    - Extension interfaces adding functionality to Minecraft classes should be injected onto their targets and stored
    in an `extensions` package.

3. **Utility Classes:**
    - All utility classes should be final and have a private nullary (empty) constructor. This constructor should go
   at the very end before inner classes, if there are any.

4. **Mixin Naming**:
    - **Class Names:**
   All mixin classes should be named after the class that they mixin to followed by either `Mixin` if the class is
   a regular mixin, or `Accessor` if it is an accessor mixin. When mixing into inner classes, use a `$` symbol to
   separate the outer and inner class names. For example, when making a regular mixin for the class `ClassA.ClassB`,
   the mixin class name would be `ClassA$ClassBMixin`.
    - **Field and Method Names:**
       All non-shadowed fields and methods, including those in extension interfaces, should be prefixed with
   `port_lib$`. Accessor and invoker methods' base name should be the same as their target, plus the prefix. For
   example, the invoker method name for `renderBakedItemModel` would be `port_lib$renderBakedItemModel`, and the
   accessor method name for `itemRenderer` should be `port_lib$itemRenderer`.

5. **Mixin Misc:**
    - Mixins that mixin to a class that only exists in a certain environment type should have an
   `@Environment(EnvType.{type})` annotation. This annotation should go above the `@Mixin` annotation.
    - All mixin classes should be abstract. Regular mixins should have the `abstract` modifier. Accessors should be
   interfaces, which are already technically abstract.
    - Shadowed methods, invokers, and accessors should always be abstract, unless they are static. If static, they
   should throw an exception.
    - `@Accessor` methods should always go before `@Invoker` methods.
    - The mixin config JSON file should have all mixins in alphabetical order, with accessor mixins being listed before
   regular mixins.

6. **Adding Modules:**
    - Modules can be added when a new feature doesn't fit in an existing one.
    - Start by creating the directory. It should match the module's name.
    - Add a build.gradle file, using another module as a template.
    - Include the module in the root `settings.gradle` file, in alphabetical order with the others.
    - The `moduleDependencies` method may be used to make a module depend on another one.
    - All code should be in the `io.github.fabricators_of_create` package. Exceptions are made when a significant
   portion of the code is copied from elsewhere. See `networking` and `tags` modules.
    - A `fabric.mod.json` file is the only needed metadata. An icon will be added at build time.
      - The mod JSON will be filled with additional values on build as well. The only required fields are
      `id`, `name`, `description`, `version`, and `schemaVersion`. Add anything else as needed.
        - `id` should be `porting_lib_` plus the module name.
        - `version` should be `${version}`, it'll be filled in at build.
        - `schemaVersion` should be `1`. It's only there to make the IDE be quiet.
    - When in doubt, reference another module.

7. **Common Gradle Errors:**
   - Since Gradle is a quality piece of software, here's some common errors and solutions.
     - **Failed to download version manifest:**
       - Find which project it fails in. Delete the .gradle folder from that project and try again.
     - **Any FileSystemException:**
       - Run `./gradlew --stop` to stop all running daemons and try again.

8. **Asking Questions:**
   - Not sure about something? Want feedback on an API? You can find us either here on GitHub, or in
   the [official Create discord server](https://discord.gg/hmaD7Se) in #devchat.
