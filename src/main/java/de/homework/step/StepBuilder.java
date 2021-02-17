package de.homework.step;

import java.util.function.Function;

import de.homework.step.executor.DefaultStepExecutor;
import de.homework.step.executor.StepExecutor;
import io.vavr.control.Either;

public class StepBuilder<T, P> {

    private Function<T, Either<P, T>> action;
    private StepExecutor<T, P> stepExecutor;
    private Function<P, Either<P, T>> recoverAction;

    public StepBuilder<T, P> addAction(Function<T, Either<P, T>> action) {
        this.action = action;

        return this;
    }

    public StepBuilder<T, P> addRecoverAction(Function<P, Either<P, T>> recoverAction) {
        this.recoverAction = recoverAction;

        return this;
    }

    public StepBuilder<T, P> addStepExecutor(StepExecutor<T, P> stepExecutor) {
        this.stepExecutor = stepExecutor;

        return this;
    }

    public Step<T, P> build() {
        return new Step<>(this.action, //
                this.stepExecutor == null ? new DefaultStepExecutor<>() : this.stepExecutor, //
                this.recoverAction);
    }
}
