package de.homework.workflow;

import java.util.function.Function;

import de.homework.step.Step;
import de.homework.step.StepBuilder;
import de.homework.step.executor.DefaultStepExecutor;
import de.homework.step.executor.StepExecutor;
import io.vavr.collection.List;
import io.vavr.control.Either;

public class WorkflowBuilder<T, P> {

    private List<Step<T, P>> steps = List.empty();

    public WorkflowBuilder<T, P> addStep(Function<T, Either<P, T>> nextStep) {
        return addStep(nextStep, new DefaultStepExecutor<>(), null);
    }

    public WorkflowBuilder<T, P> addStep(Function<T, Either<P, T>> nextStep, StepExecutor<T, P> stepExecutor) {
        return addStep(nextStep, stepExecutor, null);
    }

    public WorkflowBuilder<T, P> addStep(Function<T, Either<P, T>> nextStep, Function<P, Either<P, T>> recoveryAction) {
        return addStep(nextStep, new DefaultStepExecutor<>(), recoveryAction);
    }

    public WorkflowBuilder<T, P> addStep(Function<T, Either<P, T>> nextStep, StepExecutor<T, P> stepExecutor, Function<P, Either<P, T>> recoveryAction) {
        return addStep(new StepBuilder<T, P>().addAction(nextStep).addStepExecutor(stepExecutor).addRecoverAction(recoveryAction).build());
    }

    public WorkflowBuilder<T, P> addStep(Step<T, P> step) {
        steps = steps.append(step);

        return this;
    }

    public Workflow<T, P> build() {
        return new Workflow<>(this.steps);
    }
}
