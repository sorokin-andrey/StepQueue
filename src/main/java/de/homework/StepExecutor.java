package de.homework;

import java.util.Optional;
import java.util.function.Function;

import io.vavr.control.Either;

public class StepExecutor<T, P> {

    private final Step<T, P> firstStep;

    private StepExecutor(Step<T, P> firstStep) {
        this.firstStep = firstStep;
    }

    public Either<P, T> apply(T input) {

        Step<T, P> step = firstStep;
        Either<P, T> result = null;

        while (hasNext(step)) {
            step = next(step);
            result = apply(step, result != null ? result.get() : input);

            if (result.isLeft()) {
                if (step.isRecoverable()) {
                    step.recover(result.getLeft());
                }

                return result;
            }
        }

        return result;
    }

    private Either<P, T> apply(Step<T, P> step, T input) {
        return step.run(input);
    }

    private boolean hasNext(Step<T, P> currentStep) {
        if (currentStep.isExecutedSuccessfully()) {
            return true;
        }

        if (!currentStep.hasNext()) {
            return false;
        }

        if (currentStep.next().isExecutedSuccessfully()) {
            return true;
        }

        return hasNext(currentStep.next());
    }

    private Step<T, P> next(Step<T, P> currentStep) {
        if (currentStep.isExecutedSuccessfully()) {
            return currentStep;
        }

        if (currentStep.next().isExecutedSuccessfully()) {
            return currentStep.next();
        }

        return next(currentStep.next());
    }

    public static final class Builder<T, P> {

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private Optional<Step<T, P>> firstStep = Optional.empty();
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private Optional<Step<T, P>> lastStep = Optional.empty();

        public Builder<T, P> addStep(Function<T, Either<P, T>> nextStep) {
            return addStep(new Step<>(nextStep));
        }

        public Builder<T, P> addStep(Function<T, Either<P, T>> nextStep, Function<P, Either<P, T>> recover) {
            return addStep(new Step<>(nextStep, recover));
        }

        public Builder<T, P> addStep(Step<T, P> step) {
            if (this.firstStep.isEmpty()) {
                this.firstStep = Optional.of(step);
            } else if (this.lastStep.isPresent()) {
                this.lastStep.get().setNextStep(step);
                this.lastStep = Optional.of(step);
            } else {
                this.firstStep.get().setNextStep(step);
                this.lastStep = Optional.of(step);
            }

            return this;
        }

        public StepExecutor<T, P> build() {
            return new StepExecutor<>(this.firstStep.orElseThrow());
        }
    }
}
