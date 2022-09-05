# Porting Lib Contributing Guidelines

1. **Organization:**
    - Porting Lib is split into modules. Each module has its own functionality and features. The separation allows for
    mods to only use the modules they need.
    - Mixins should be sorted by environment. Ex. have `mixin.common` and `mixin.client` subpackages.
    - Accessor mixins should be further sorted into `accessor` subpackages. Ex. `mixin.common.accessor`.
    - Extension interfaces adding functionality to Minecraft classes should be injected onto their targets and stored
    in an `extensions` package.

2. **Utility Classes:**
    - All utility classes should be final and have a private nullary (empty) constructor. This constructor should go
   at the very end before inner classes, if there are any.

3. **Mixin Naming**
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

4. **Mixin Misc:**
    - Mixins that mixin to a class that only exists in a certain environment type should have an
   `@Environment(EnvType.{type})` annotation. This annotation should go above the `@Mixin` annotation.
    - All mixin classes should be abstract. Regular mixins should have the `abstract` modifier. Accessors should be
   interfaces, which are already technically abstract.
    - Shadowed methods, invokers, and accessors should always be abstract, unless they are static. If static, they
   should throw an exception.
    - `@Accessor` methods should always go before `@Invoker` methods.
    - The mixin config JSON file should have all mixins in alphabetical order, with accessor mixins being listed before
   regular mixins.

5. **Adding Modules:**
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
