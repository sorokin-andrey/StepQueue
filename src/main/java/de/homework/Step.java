package de.homework;

import io.vavr.control.Either;

import java.util.function.Function;

public class Step<T, P> {

    private final Function<T, Either<P, T>> action;
//    private final Function<Problem, Either<Problem, T>> recover;


    public Step(Function<T, Either<P, T>> action) {
        this.action = action;
    }

//    public Step(Function<T, Either<Problem, T>> action, Function<Problem, Either<Problem, T>> recover) {
//        this.action = action;
//        this.recover = recover;
//    }

    public Function<T, Either<P, T>> getAction() {
        return action;
    }

    public Either<P, T> run(T t) {
        return action.apply(t);
    }

//    public Either<Problem, T> recover(Problem problem) {
//        return recover.apply(problem);
//    }


    @Override
    public String toString() {
        return "Step{" +
                "action=" + action +
                '}';
    }
}
