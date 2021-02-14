package de.homework;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Queue;
import io.vavr.control.Either;

import java.util.function.Function;

public class StepQueue<T, P> {

    private final Queue<Step<T, P>> stepQueue;
    private Map<Step<T, P>, StepContext<T, P>> contextMap;

    private StepQueue(Queue<Step<T, P>> stepQueue, Map<Step<T, P>, StepContext<T, P>> contextMap) {
        this.stepQueue = stepQueue;
        this.contextMap = contextMap;
    }

    public Either<P, T> apply(T input) {

        Either<P, T> result = stepQueue.head().run(input);
        for (int i = 1; i < stepQueue.length(); i++) {
            if (result.isLeft()) {
                return result;
            }

            final Step<T, P> step = stepQueue.get(i);
            result = step.run(result.get());
            contextMap = contextMap.put(step, new StepContext<>(step, result, 0));
        }

        return result;
    }

    public Map<Step<T, P>, StepContext<T, P>> getContextMap() {
        return contextMap;
    }

    public static class Builder<T, P> {

        private Queue<Step<T, P>> stepQueue = Queue.empty();

        public Builder<T, P> addStep(Function<T, Either<P, T>> nextStep) {
            this.stepQueue = this.stepQueue.append(new Step<T, P>(nextStep));
            return this;
        }

        public StepQueue<T, P> build() {
            return new StepQueue<T, P>(stepQueue, HashMap.empty());
        }
    }
}
