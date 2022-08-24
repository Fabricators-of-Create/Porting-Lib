# Porting Lib
### A collection of utilities for porting mods from Forge to Fabric.
## Use
Porting Lib is split into modules. All modules are available on this maven:
```groovy
maven { url = "https://mvn.devos.one/snapshots/" }
```
```groovy
modImplementation(include("io.github.fabricators_of_create.Porting-Lib:<module>:<version>"))
```
The latest major and minor versions can be found in the `gradle.properties` file as `mod_version`.
The latest patch can be found from GitHub Actions as the build number.
### Modules
| Module         | Description                                                                          |
|----------------|--------------------------------------------------------------------------------------|
| `porting_lib`  | Fat jar including all modules                                                        |
| `accessors`    | Accessor mixins aplenty                                                              |
| `attributes`   | Additional Entity Attributes; step height, gravity, swim speed                       |
| `base`         | Code that has not yet been split into modules                                        |
| `common`       | Miscellaneous utilities for other modules                                            |
| `constants`    | Internal constants used by Porting Lib                                               |
| `entity`       | Multipart entities, extra spawn data, removal listening                              |
| `extensions`   | Extensions to vanilla classes for additional functionality                           |
| `model-loader` | Base loader for custom model types                                                   |
| `models`       | Model implementations, ModelData, RenderTypes                                        |
| `networking`   | A Forge-like packet system                                                           |
| `obj-loader`   | Loading .obj models                                                                  |
| `tags`         | Forge tags                                                                           |
| `transfer`     | Storage implementations, client-side lookup, FluidStack, assorted transfer utilities |

### Contributing
See [the contribution information](CONTRIBUTING.md).
