package de.homework;

import java.util.function.Function;

import io.vavr.control.Either;

public class RecoveryStep<P, T> {

    private final Function<P, Either<P, T>> action;

    private StepContext<T, P> context;

    public RecoveryStep(Function<P, Either<P, T>> action) {
        this.action = action;
        this.context = new StepContext<>(action.getClass().getSimpleName());
    }

    public Either<P, T> run(P p) {
        final Either<P, T> result = action.apply(p);
        this.context = new StepContext<>(result, 0, action.getClass().getSimpleName());

        return result;
    }

    public StepContext<T, P> getContext() {
        return context;
    }
}
