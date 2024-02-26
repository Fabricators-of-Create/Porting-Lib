package io.github.fabricators_of_create.porting_lib.transfer.callbacks;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * A helper callback for use with Transactions that only runs on failure.
 * Useful for rolling back changes. Uses:
 * <pre>{@code
 * try (Transaction t = Transaction.openOuter()) {
 *     TransactionFailCallback callback = new TransactionFailCallback(t);
 *     callback.addCallback(() -> System.out.println("fail :(");
 *     callback.addCallback() -> world.setBlockState(...));
 * }
 * }</pre>
 * @see TransactionCallback
 * @see TransactionSuccessCallback
 */
public class TransactionFailCallback extends TransactionCallback {

	public TransactionFailCallback(TransactionContext ctx) {
		super(ctx);
	}

	public TransactionFailCallback(TransactionContext ctx, Runnable callback) {
		super(ctx, callback);
	}

	@Override
	protected boolean shouldRunCallbacks() {
		return failed;
	}
}
