package de.homework;

import io.vavr.control.Either;

import java.util.Optional;
import java.util.function.Function;

public class Step<T, P> {

    private final Function<T, Either<P, T>> action;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<RecoveryStep<P, T>> recoverStep;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Step<T, P>> nextStep;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<StepContext<T, P>> context;

    public Step(Function<T, Either<P, T>> action) {
        this.action = action;
        this.recoverStep = Optional.empty();
        this.nextStep = Optional.empty();
        this.context = Optional.empty();
    }

    public Step(Function<T, Either<P, T>> action, Function<P, Either<P, T>> recoverStep) {
        this.action = action;
        this.recoverStep = Optional.of(new RecoveryStep<>(recoverStep));
        this.nextStep = Optional.empty();
        this.context = Optional.empty();
    }

    public Either<P, T> run(T t) {
        final Either<P, T> result = action.apply(t);
        this.context = Optional.of(new StepContext<>(result, 0, action.getClass().getSimpleName()));

        return result;
    }

    public boolean isRecoverable() {
        return recoverStep.isPresent();
    }

    public boolean isExecutedSuccessfully() {
        return context.isPresent() && context.orElseThrow().isExecutedSuccessfully();
    }

    public Either<P, T> recover(P p) {
        return recoverStep.orElseThrow().run(p);
    }

    public boolean hasNext() {
        return nextStep.isPresent();
    }

    public Step<T, P> next() {
        return nextStep.orElseThrow();
    }

    public void setNextStep(Step<T, P> nextStep) {
        this.nextStep = Optional.of(nextStep);
    }
}
