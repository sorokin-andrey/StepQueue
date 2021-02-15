package de.homework;

import java.util.function.Function;

import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.Map;
import io.vavr.control.Either;

public class StepExecutor<T, P> {

    private final ContextHolder<T, P> contextHolder;

    private StepExecutor(ContextHolder<T, P> contextHolder) {
        this.contextHolder = contextHolder;
    }

    public Either<P, T> apply(T input) {

        Either<P, T> result = null;

        while (contextHolder.hasNext()) {
            if (result != null && result.isLeft()) {
                return result;
            }

            final Step<T, P> step = contextHolder.next();
            result = step.run(result == null ? input : result.get());
            contextHolder.updateStepContext(step, result, 0);
        }

        return result;
    }

    public static final class Builder<T, P> {

        private Map<Step<T, P>, StepContext<T, P>> contextMap = LinkedHashMap.empty();

        public Builder<T, P> addStep(Function<T, Either<P, T>> nextStep) {
            final Step<T, P> step = new Step<>(nextStep);
            this.contextMap = this.contextMap.put(step, new StepContext<>(step));
            return this;
        }

        public StepExecutor<T, P> build() {
            return new StepExecutor<>(new ContextHolder<>(this.contextMap));
        }
    }
}
