package de.homework.step;

import de.homework.step.executor.StepExecutor;
import io.vavr.control.Either;

import java.util.function.Function;

public class Step<T, P> {

    private final Function<T, Either<P, T>> action;

    private final StepExecutor<T, P> stepExecutor;

    private final Function<P, Either<P, T>> recoverAction;

    private StepContext<T, P> context;

    Step(Function<T, Either<P, T>> action, StepExecutor<T, P> stepExecutor, Function<P, Either<P, T>> recoverAction) {
        this.action = action;
        this.stepExecutor = stepExecutor;
        this.recoverAction = recoverAction;
    }

    public Either<P, T> execute(T t) {
        final Either<P, T> result = stepExecutor.execute(this.action, this.recoverAction, t);
        this.context = new StepContext<>(result, 0, action.getClass().getSimpleName());

        return result;
    }

    public boolean isExecutedSuccessfully() {
        return context != null && context.isExecutedSuccessfully();
    }
}
