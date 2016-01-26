package rapanui.core;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a source that asynchronously emits objects. This is used by asynchronous
 * methods that, if they were synchronous, would return an array or @see Iterable.
 *
 * It is similar to a "promise" in other languages or a Java @see java.util.concurrent.Future,
 * but differs in that it produces multiple instances and in that it cannot be determined if
 * all objects have been emitted or more will be emitted in the future.
 *
 * @param <T> The type of object that is emitted.
 */
public abstract class Emitter<T> {
	private boolean isStopped = false;
	private final List<T> results = new LinkedList<T>();
	private final List<Consumer<T>> actions = new LinkedList<Consumer<T>>();

	/**
	 * Executes a given action for each emitted object. The action may (and most likely will)
	 * be executed in another thread.
	 *
	 * @param action The action to perform. As parameter, it receives the emitted object. Must not be null.
	 */
	public synchronized void onEmit(Consumer<T> action) {
		assert action != null;
		for (T result : results)
			action.accept(result);
		actions.add(action);
	}

	/**
	 * Retrieve all results that have been produced so far.
	 *
	 * @return All previous results. The return value must not be modified. Guaranteed to be non-null.
	 */
	public Iterable<T> getResults() {
		return results;
	}

	/**
	 * Used by subclasses to insert new objects that are then emitted.
	 *
	 * @param result The object to emit. May be null.
	 */
	protected synchronized void acceptResult(T result) {
		if (!isStopped()) {
			results.add(result);
			for (Consumer<T> action : actions)
				action.accept(result);
		}
	}

	/**
	 * Stops the emitter. Once this has been called, no further objects will be emitted.
	 */
	public synchronized void stop() {
		isStopped = true;
	}

	/**
	 * Test if this emitter has been explicitly stopped by means of @see stop().
	 *
	 * @return True if @see stop() has been previously called, false otherwise.
	 */
	public boolean isStopped() {
		return isStopped;
	}

	/**
	 * Creates a new emitter that executes code in a new thread.
	 *
	 * @param computation A consumer that computes objects to emit and passes them to its argument. Must not be null.
	 *
	 * @return A new Emitter instance that emits objects from the computation. Guaranteed to be non-null.
	 */
	public static <T> Emitter<T> fromResultComputation(Consumer<Consumer<T>> computation) {
		return new ThreadedEmitter<T>(computation);
	}

	/**
	 * Creates an emitter that collects results from all given emitters.
	 *
	 * @param emitters The sources for the combined emitter. Must not be null nor contain null-values.
	 *
	 * @return A new @see Emitter instance. Guaranteed to be non-null.
	 */
	@SafeVarargs
	public static <T> Emitter<T> combine(Emitter<? extends T>... emitters) {
		return combine(Arrays.asList(emitters));
	}

	/**
	 * Creates an empty emitter that emits no items.
	 *
	 * @return A new @see Emitter instance. Guaranteed to be non-null.
	 */
	public static <T> Emitter<T> empty() {
		return new Emitter<T>() {};
	}

	/**
	 * Creates an emitter that collects results from all given emitters.
	 *
	 * @param emitters The sources for the combined emitter. Must not be null nor contain null-values.
	 *
	 * @return A new @see Emitter instance. Guaranteed to be non-null.
	 */
	public static <T> Emitter<T> combine(Collection<Emitter<? extends T>> emitters) {
		return new AggregateEmitter<T>(emitters);
	}

	/**
	 * Creates an emitter that only relays the first object emitted by its source
	 *
	 * @return A new @see Emitter instance. Guaranteed to be non-null.
	 */
	public Emitter<T> first() {
		return first(1);
	}

	/**
	 * Creates an emitter that only relays the first few objects emitted by its source
	 *
	 * @param count How many objects emitted by the source should be emitted by the new emitter.
	 *
	 * @return A new @see Emitter instance. Guaranteed to be non-null.
	 */
	public Emitter<T> first(int count) {
		return new HeadEmitter<T>(this, count);
	}

	/**
	 * Creates an emitter that applies the given conversion on each emitted object
	 *
	 * @param conversion A function that is applied to objects emitted by this instance before they are re-emitted. Must not be null.
	 *
	 * @return A new @see Emitter instance. Guaranteed to be non-null.
	 */
	public <R> Emitter<R> map(Function<T,R> conversion) {
		return new MapEmitter<T,R>(this, conversion);
	}

	protected static class HeadEmitter<T> extends Emitter<T> {
		private int count;
		private final Emitter<T> source;

		public HeadEmitter(Emitter<T> source, int count) {
			this.count = count;
			source.onEmit(this::acceptResult);
			this.source = source;
		}

		@Override
		protected synchronized void acceptResult(T result) {
			if (count > 0) {
				super.acceptResult(result);
				count--;
				if (count <= 0)
					source.stop();
			}
		}
	}

	protected static class MapEmitter<T,R> extends Emitter<R> {
		private final Emitter<T> source;

		public MapEmitter(Emitter<T> source, Function<T,R> converter) {
			this.source = source;
			source.onEmit(s -> acceptResult(converter.apply(s)));
		}

		@Override
		public synchronized void stop() {
			source.stop();
			super.stop();
		}
	}

	protected static class AggregateEmitter<T> extends Emitter<T> {
		private final Collection<Emitter<? extends T>> generators;

		public AggregateEmitter(Collection<Emitter<? extends T>> generators) {
			this.generators = generators;
			for (Emitter<? extends T> generator : generators)
				generator.onEmit(this::acceptResult);
		}

		@Override
		public synchronized void stop() {
			for (Emitter<? extends T> generator : generators)
				generator.stop();
			super.stop();
		}
	}

	protected static class ThreadedEmitter<T> extends Emitter<T> {
		protected static final ExecutorService executor = Executors.newCachedThreadPool();

		protected final Future<?> underlyingFuture;

		public ThreadedEmitter(Consumer<Consumer<T>> generator) {
			underlyingFuture = executor.submit(() -> generator.accept(this::acceptResult));
		}

		@Override
		public void stop() {
			if (!underlyingFuture.isDone())
				underlyingFuture.cancel(true);
			super.stop();
		}
	}
}
