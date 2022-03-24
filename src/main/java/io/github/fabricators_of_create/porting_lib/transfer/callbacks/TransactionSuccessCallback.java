package io.github.fabricators_of_create.porting_lib.transfer.callbacks;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * A helper callback for use with Transactions that only runs on success.
 * Useful for applying final changes. Uses:
 * <pre>{@code
 * try (Transaction t = Transaction.openOuter()) {
 *     TransactionSuccessCallback callback = new TransactionSuccessCallback(t);
 *     callback.addCallback(() -> System.out.println("success!");
 *     callback.addCallback() -> level.setBlock(...));
 *     ...
 *     TransactionCallback.setBlock(t, level, pos, state, 3);
 *     ...
 *     TransactionCallback.onSuccess(t, Example::doThing);
 * }
 * }</pre>
 * @see TransactionCallback
 * @see TransactionFailCallback
 */
public class TransactionSuccessCallback extends TransactionCallback {
	public TransactionSuccessCallback(TransactionContext ctx) {
		super(ctx);
	}

	public TransactionSuccessCallback(TransactionContext ctx, Runnable callback) {
		super(ctx, callback);
	}

	@Override
	protected boolean shouldRunCallbacks() {
		return !failed;
	}
}
