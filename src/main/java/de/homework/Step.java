package de.homework;

import io.vavr.control.Either;

import java.util.Optional;
import java.util.function.Function;

public class Step<T, P> {

    private final Function<T, Either<P, T>> action;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<RecoveryStep<P, T>> recoverStep;

    public Step(Function<T, Either<P, T>> action) {
        this.action = action;
        this.recoverStep = Optional.empty();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Step(Function<T, Either<P, T>> action, Optional<RecoveryStep<P, T>> recoverStep) {
        this.action = action;
        this.recoverStep = recoverStep;
    }

    public Function<T, Either<P, T>> getAction() {
        return action;
    }

    public Either<P, T> run(T t) {
        return action.apply(t);
    }

    public boolean isRecoverable() {
        return recoverStep.isPresent();
    }

    public Either<P, T> recover(P p) {
        return recoverStep.get().run(p);
    }
}
