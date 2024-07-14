package io.github.fabricators_of_create.porting_lib.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

import io.github.fabricators_of_create.porting_lib.util.BlockEntityHelper;
import net.minecraft.util.datafix.fixes.References;

public class ForgeBlockEntityFix extends DataFix {
	public static final String LEGACY_DATA_KEY = "ForgeData";
	public static final String NEO_DATA_KEY = "NeoForgeData";

	public ForgeBlockEntityFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	public Dynamic<?> fix(Dynamic<?> original) {
		return original.renameField(LEGACY_DATA_KEY, BlockEntityHelper.EXTRA_DATA_KEY).renameField(NEO_DATA_KEY, BlockEntityHelper.EXTRA_DATA_KEY);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped("jigsaw_rotation_fix", this.getInputSchema().getType(References.BLOCK_ENTITY), (typed) -> {
			return typed.update(DSL.remainderFinder(), this::fix);
		});
	}
}
