package io.github.fabricators_of_create.porting_lib.transfer.callbacks;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext.Result;

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

	public static TransactionSuccessCallback onSuccess(TransactionContext ctx, Runnable r) {
		return new TransactionSuccessCallback(ctx, r);
	}

	public static TransactionFailCallback onFail(TransactionContext ctx, Runnable r) {
		return new TransactionFailCallback(ctx, r);
	}
}
