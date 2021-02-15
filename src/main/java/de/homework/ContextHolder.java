package de.homework;

import io.vavr.collection.Iterator;
import io.vavr.collection.Map;
import io.vavr.control.Either;

public class ContextHolder<T, P> implements Iterator<Step<T, P>> {

    private Map<Step<T, P>, StepContext<T, P>> contextMap;

    public ContextHolder(Map<Step<T, P>, StepContext<T, P>> contextMap) {
        this.contextMap = contextMap;
    }

    public Step<T, P> next() {
        return this.contextMap.find(step -> step._2.getOutput().isEmpty() || step._2.getOutput().get().isLeft()).get()._1;
    }

    public boolean hasNext() {
        return !this.contextMap.filter(step -> step._2.getOutput().isEmpty() || step._2.getOutput().get().isLeft()).isEmpty();
    }

    public void updateStepContext(Step<T, P> step, Either<P, T> recoveryOutput, int timeTakenNanos) {
        this.contextMap = this.contextMap.put(step, new StepContext<>(step, recoveryOutput, timeTakenNanos));
    }

    public void updateStepContext(Step<T, P> step, Either<P, T> output, Either<P, T> recoveryOutput, int timeTakenNanos) {
        this.contextMap = this.contextMap.put(step, new StepContext<>(step, output, recoveryOutput, timeTakenNanos));
    }
}
