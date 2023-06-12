package io.github.fabricators_of_create.porting_lib.core.event;

/**
 * An event with an {@link Result}
 * @see CancellableEvent
 * @see CancellableEventWithResult
 */
public interface EventWithResult {
	Result getResult();
	void setResult(Result result);

	enum Result {
		DENY,
		DEFAULT,
		ALLOW
	}

	class Base implements EventWithResult {
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
}
