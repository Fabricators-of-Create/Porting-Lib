package io.github.fabricators_of_create.porting_lib.core.event;

/**
 * An event that is both cancellable and has a result.
 * @see CancellableEvent
 * @see EventWithResult
 */
public class CancellableEventWithResult extends CancellableEvent.Base implements EventWithResult {
	private Result result = Result.DEFAULT;

	@Override
	public Result getResult() {
		return result;
	}

	@Override
	public void setResult(Result result) {
		this.result = result;
	}
}
