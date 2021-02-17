package de.homework.step.executor;

import java.util.function.Function;

import io.vavr.control.Either;

/**
 * Multi threaded StepExecutor execute Steps and recover them in case of problems
 */
public class AsyncStepExecutor<T, P> implements StepExecutor<T, P> {

    @Override
    public Either<P, T> execute(Function<T, Either<P, T>> action, Function<P, Either<P, T>> recoveryAction, T input) {

        final Either<P, T> result = action.apply(input);

        if (result.isLeft()) {
            if (recoveryAction != null) {
                recoveryAction.apply(result.getLeft());
            }

            return result;
        }

        return result;
    }
}
