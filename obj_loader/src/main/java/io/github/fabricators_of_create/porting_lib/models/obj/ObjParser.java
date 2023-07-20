package io.github.fabricators_of_create.porting_lib.models.obj;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import io.github.fabricators_of_create.porting_lib.models.obj.ObjModel.ModelGroup;
import io.github.fabricators_of_create.porting_lib.models.obj.ObjModel.ModelMesh;
import io.github.fabricators_of_create.porting_lib.models.obj.ObjModel.ModelObject;
import io.github.fabricators_of_create.porting_lib.models.obj.ObjModel.ModelSettings;
import joptsimple.internal.Strings;

import org.joml.Vector3f;
import org.joml.Vector4f;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

public class ObjParser {
	public static ObjModel parse(ObjTokenizer tokenizer, ModelSettings settings) throws IOException {
		if (!ObjModel.ENABLED) {
			throw new IllegalStateException("The Fabric Rendering API is not available. If you have Sodium, install Indium!");
		}
		var modelLocation = settings.modelLocation();
		var materialLibraryOverrideLocation = settings.mtlOverride();
		var model = new ObjModel(settings);

		// for relative references to material libraries
		String modelDomain = modelLocation.getNamespace();
		String modelPath = modelLocation.getPath();
		int lastSlash = modelPath.lastIndexOf('/');
		if (lastSlash >= 0) {
			modelPath = modelPath.substring(0, lastSlash + 1); // include the '/'
		} else {
			modelPath = "";
		}

		ObjMaterialLibrary mtllib = ObjMaterialLibrary.EMPTY;
		ObjMaterialLibrary.Material currentMat = null;
		String currentSmoothingGroup = null;
		ModelGroup currentGroup = null;
		ModelObject currentObject = null;
		ModelMesh currentMesh = null;

		boolean objAboveGroup = false;

		if (materialLibraryOverrideLocation != null) {
			String lib = materialLibraryOverrideLocation;
			if (lib.contains(":")) {
				mtllib = ObjLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(lib));
			} else {
				mtllib = ObjLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(modelDomain, modelPath + lib));
			}
		}

		String[] line;
		while ((line = tokenizer.readAndSplitLine(true)) != null) {
			switch (line[0]) {
				case "mtllib": // Loads material library
				{
					if (materialLibraryOverrideLocation != null) {
						break;
					}

					String lib = line[1];
					if (lib.contains(":")) {
						mtllib = ObjLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(lib));
					} else {
						mtllib = ObjLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(modelDomain, modelPath + lib));
					}
					break;
				}

				case "usemtl": // Sets the current material (starts new mesh)
				{
					String mat = Strings.join(Arrays.copyOfRange(line, 1, line.length), " ");
					ObjMaterialLibrary.Material newMat = mtllib.getMaterial(mat);
					if (!Objects.equals(newMat, currentMat)) {
						currentMat = newMat;
						if (currentMesh != null && currentMesh.mat == null && currentMesh.faces.size() == 0) {
							currentMesh.mat = currentMat;
						} else {
							// Start new mesh
							currentMesh = null;
						}
					}
					break;
				}

				case "v": // Vertex
					model.positions.add(parseVector4To3(line));
					break;
				case "vt": // Vertex texcoord
					model.texCoords.add(parseVector2(line));
					break;
				case "vn": // Vertex normal
					model.normals.add(parseVector3(line));
					break;
				case "vc": // Vertex color (non-standard)
					model.colors.add(parseVector4(line));
					break;

				case "f": // Face
				{
					if (currentMesh == null) {
						currentMesh = model.new ModelMesh(currentMat, currentSmoothingGroup);
						if (currentObject != null) {
							currentObject.meshes.add(currentMesh);
						} else {
							if (currentGroup == null) {
								currentGroup = model.new ModelGroup("");
								model.parts.put("", currentGroup);
							}
							currentGroup.meshes.add(currentMesh);
						}
					}

					int[][] vertices = new int[line.length - 1][];
					for (int i = 0; i < vertices.length; i++) {
						String vertexData = line[i + 1];
						String[] vertexParts = vertexData.split("/");
						int[] vertex = Arrays.stream(vertexParts).mapToInt(num -> Strings.isNullOrEmpty(num) ? 0 : Integer.parseInt(num)).toArray();
						if (vertex[0] < 0) {
							vertex[0] = model.positions.size() + vertex[0];
						} else {
							vertex[0]--;
						}
						if (vertex.length > 1) {
							if (vertex[1] < 0) {
								vertex[1] = model.texCoords.size() + vertex[1];
							} else {
								vertex[1]--;
							}
							if (vertex.length > 2) {
								if (vertex[2] < 0) {
									vertex[2] = model.normals.size() + vertex[2];
								} else {
									vertex[2]--;
								}
								if (vertex.length > 3) {
									if (vertex[3] < 0) {
										vertex[3] = model.colors.size() + vertex[3];
									} else {
										vertex[3]--;
									}
								}
							}
						}
						vertices[i] = vertex;
					}

					currentMesh.faces.add(vertices);

					break;
				}

				case "s": // Smoothing group (starts new mesh)
				{
					String smoothingGroup = "off".equals(line[1]) ? null : line[1];
					if (!Objects.equals(currentSmoothingGroup, smoothingGroup)) {
						currentSmoothingGroup = smoothingGroup;
						if (currentMesh != null && currentMesh.smoothingGroup == null && currentMesh.faces.size() == 0) {
							currentMesh.smoothingGroup = currentSmoothingGroup;
						} else {
							// Start new mesh
							currentMesh = null;
						}
					}
					break;
				}

				case "g": {
					String name = line[1];
					if (objAboveGroup) {
						currentObject = model.new ModelObject(currentGroup.name() + "/" + name);
						currentGroup.parts.put(name, currentObject);
					} else {
						currentGroup = model.new ModelGroup(name);
						model.parts.put(name, currentGroup);
						currentObject = null;
					}
					// Start new mesh
					currentMesh = null;
					break;
				}

				case "o": {
					String name = line[1];
					if (objAboveGroup || currentGroup == null) {
						objAboveGroup = true;

						currentGroup = model.new ModelGroup(name);
						model.parts.put(name, currentGroup);
						currentObject = null;
					} else {
						currentObject = model.new ModelObject(currentGroup.name() + "/" + name);
						currentGroup.parts.put(name, currentObject);
					}
					// Start new mesh
					currentMesh = null;
					break;
				}
			}
		}
		return model;
	}

	public static Vector3f parseVector4To3(String[] line) {
		Vector4f vec4 = parseVector4(line);
		return new Vector3f(
				vec4.x() / vec4.w(),
				vec4.y() / vec4.w(),
				vec4.z() / vec4.w()
		);
	}

	public static Vec2 parseVector2(String[] line) {
		return switch (line.length) {
			case 1 -> new Vec2(0, 0);
			case 2 -> new Vec2(Float.parseFloat(line[1]), 0);
			default -> new Vec2(Float.parseFloat(line[1]), Float.parseFloat(line[2]));
		};
	}

	public static Vector3f parseVector3(String[] line) {
		return switch (line.length) {
			case 1 -> new Vector3f();
			case 2 -> new Vector3f(Float.parseFloat(line[1]), 0, 0);
			case 3 -> new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), 0);
			default -> new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]));
		};
	}

	public static Vector4f parseVector4(String[] line) {
		return switch (line.length) {
			case 1 -> new Vector4f();
			case 2 -> new Vector4f(Float.parseFloat(line[1]), 0, 0, 1);
			case 3 -> new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), 0, 1);
			case 4 -> new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]), 1);
			default ->
					new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]), Float.parseFloat(line[4]));
		};
	}
}
