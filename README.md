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
| Module                   | Description                                                                          |
|--------------------------|--------------------------------------------------------------------------------------|
| `Porting-Lib`            | Fat jar including all modules                                                        |
| `asm`                    | Utils for doing asm                                                                  |
| `accessors`              | Accessor mixins aplenty                                                              |
| `attributes`             | Additional Entity Attributes; step height, gravity, swim speed                       |
| `base`                   | Code that has not yet been split into modules                                        |
| `brewing`                | A potion recipe api                                                                  |
| `client_events`          | Useful client-side events                                                            |
| `common`                 | Miscellaneous utilities for other modules                                            |
| `core`                   | Core functionality used across other modules                                         |
| `config`                 | A minimal port of forge's config api                                                 |
| `chunk_loading`          | Custom chunk loading api                                                             |
| `data`                   | Additional data generation providers                                                 |
| `entity`                 | Multipart entities, extra spawn data, removal listening                              |
| `extensions`             | Extensions to vanilla classes for additional functionality                           |
| `fluids`                 | Api that provides additional fluid attributes for fluids                             |
| `gametest`               | Tools to make GameTest creation as easy as possible                                  |
| `items`                  | Adds extra item extensions                                                           |
| `lazy_registration`      | A implementation of forge's DeferredRegister system rewritten for fabric             |
| `loot`                   | A small library to modify mob loot                                                   |
| `mixin_extensions`       | More features for Mixins                                                             |
| `model_builders`         | Additional model builders for data generation                                        |
| `model_generators`       | Forge model generators                                                               |
| `model_loader`           | Base loader for custom model types                                                   |
| `model_materials`        | Material data for use in rendering                                                   |
| `models`                 | Model implementations, ModelData, RenderTypes                                        |
| `networking`             | A Forge-like packet system                                                           |
| `obj_loader`             | Loading .obj models                                                                  |
| `recipe_book_categories` | Allows mods to add additional recipe book categories                                 |
| `registries`             | Custom datapack registries and registry utils                                        |
| `tags`                   | Forge tags                                                                           |
| `tool_actions`           | Utilities for tool interactions                                                      |
| `transfer`               | Storage implementations, client-side lookup, FluidStack, assorted transfer utilities |
| `utility`                | Miscellaneous utilities that are too niche for other modules                         |

### Contributing
See [the contribution information](CONTRIBUTING.md).

### Related APIs
Some APIs (some in-house) we've found to also be useful with porting mods.

| Name                                                                                        | Description                                                          |
|---------------------------------------------------------------------------------------------|----------------------------------------------------------------------|
| [Forge Config API Port](https://github.com/Fuzss/forgeconfigapiport-fabric)                 | A port of the Forge config API to Fabric                             |
| [Registrate Refabricated](https://github.com/Fabricators-of-Create/Registrate-Refabricated) | A port of Registrate to Fabric                                       |
| [Reach Entity Attributes](https://github.com/JamiesWhiteShirt/reach-entity-attributes)      | Provides Entity Attributes for reach distance                        |
| [Milk Lib](https://github.com/TropheusJ/milk-lib)                                           | Provides a Milk fluid as well as other milk items used often by mods |
| [Cardinal Components API](https://github.com/OnyxStudios/Cardinal-Components-API)           | Provides Components, which can replace Capabilities                  |
| [Trinkets](https://github.com/emilyploszaj/trinkets)                                        | Accessories, replacing Curios                                        |
| [Here be no Dragons](https://github.com/Parzivail-Modding-Team/HereBeNoDragons)             | Hides the Experimental World Settings screen                         |
| [Mixin Extras](https://github.com/LlamaLad7/MixinExtras)                                    | For when Mixin just isn't enough                                     |
| [Fabric ASM](https://github.com/Chocohead/Fabric-ASM)                                       | For when Mixin Extras just isn't enough                              |
