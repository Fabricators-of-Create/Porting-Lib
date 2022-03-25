package io.github.fabricators_of_create.porting_lib.transfer.callbacks;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext.Result;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Base class for {@link TransactionSuccessCallback} and {@link TransactionFailCallback}
 */
public abstract class TransactionCallback implements TransactionContext.CloseCallback {
	protected final List<Runnable> callbacks = new ArrayList<>();
	protected boolean failed = false;

	public TransactionCallback(TransactionContext ctx) {
		ctx.addCloseCallback(this);
	}

	public TransactionCallback(TransactionContext ctx, Runnable callback) {
		this(ctx);
		addCallback(callback);
	}

	public TransactionCallback addCallback(Runnable callback) {
		callbacks.add(callback);
		return this;
	}

	protected abstract boolean shouldRunCallbacks();

	@Override
	public void onClose(TransactionContext transaction, Result result) {
		failed |= result.wasAborted();
		if (transaction.nestingDepth() == 0) {
			if (shouldRunCallbacks()) {
				for (ListIterator<Runnable> itr = callbacks.listIterator(); itr.hasNext();) {
					Runnable callback = itr.next();
					callback.run();
					itr.remove();
				}
			}
		} else {
			transaction.getOpenTransaction(transaction.nestingDepth() - 1).addCloseCallback(this);
		}
	}

	public static void onSuccess(TransactionContext ctx, Runnable r) {
		new TransactionSuccessCallback(ctx, r);
	}

	public static void onFail(TransactionContext ctx, Runnable r) {
		new TransactionFailCallback(ctx, r);
	}

	public static void setBlockAndUpdate(TransactionContext ctx, Level level, BlockPos pos, BlockState state) {
		setBlock(ctx, level, pos, state, Block.UPDATE_ALL);
	}

	public static void setBlock(TransactionContext ctx, Level level, BlockPos pos, BlockState state, int flags) {
		BlockState old = level.getBlockState(pos);
		BlockEntity be = old.hasBlockEntity() ? level.getBlockEntity(pos) : null;
		onFail(ctx, () -> {
			level.setBlock(pos, old, 0); // reset back to old on fail
			if (be != null && level.getBlockEntity(pos) != be) level.setBlockEntity(be); // restore any BEs
		});
		level.setBlock(pos, state, 0); // set silently
		onSuccess(ctx, () -> level.setBlock(pos, state, flags)); // send update on new
	}
}
