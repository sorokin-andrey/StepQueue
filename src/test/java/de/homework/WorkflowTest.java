package de.homework;

import static io.vavr.API.Left;
import static io.vavr.API.Right;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import de.homework.step.executor.AsyncStepExecutor;
import de.homework.step.executor.StepExecutor;
import de.homework.workflow.Workflow;
import de.homework.workflow.WorkflowBuilder;
import de.homework.workflow.WorkflowExecutor;
import io.vavr.control.Either;

class WorkflowTest {

    Function<Document, Either<Problem, Document>> fun1 = mock(Function.class);
    Function<Document, Either<Problem, Document>> fun2 = mock(Function.class);
    Function<Document, Either<Problem, Document>> fun3 = mock(Function.class);
    Function<Document, Either<Problem, Document>> fun4 = mock(Function.class);
    Function<Problem, Either<Problem, Document>> fun5 = mock(Function.class);
    Function<Problem, Either<Problem, Document>> fun6 = mock(Function.class);
    StepExecutor<Document, Problem> executor = spy(new AsyncStepExecutor<>());

    @BeforeEach
    void setUp() {
        Mockito.reset();

        doAnswer(answer -> Right(answer.getArgument(0))).when(fun1).apply(any());
        doAnswer(answer -> Right(answer.getArgument(0))).when(fun2).apply(any());
        doAnswer(answer -> Right(answer.getArgument(0))).when(fun3).apply(any());
        doAnswer(answer -> Right(answer.getArgument(0))).when(fun4).apply(any());
        doAnswer(answer -> Right(answer.getArgument(0))).when(fun5).apply(any());
        doAnswer(answer -> Left(answer.getArgument(0))).when(fun6).apply(any());
    }

    @Test
    public void whenAllFunctionsReturnRight_allFunctionsAreExecutedInRightOrder() {
        final Workflow<Document, Problem> workflow = new WorkflowBuilder<Document, Problem>()
                .addStep(fun1)
                .addStep(fun2)
                .addStep(fun3)
                .addStep(fun4)
                .build();

        final WorkflowExecutor<Problem, Document> workflowExecutor = new WorkflowExecutor<>(workflow);
        final Either<Problem, Document> result = workflowExecutor.execute(new Document());

        assertThat(result.isRight()).isTrue();
        InOrder inOrder = inOrder(fun1, fun2, fun3, fun4);
        inOrder.verify(fun1).apply(any());
        inOrder.verify(fun2).apply(any());
        inOrder.verify(fun3).apply(any());
        inOrder.verify(fun4).apply(any());
    }

    @Test
    public void whenAllFunctionsReturnLeftExceptFirstOne_skipsAfterFailure() {
        doReturn(Left(new Problem())).when(fun2).apply(any());
        doReturn(Left(new Problem())).when(fun3).apply(any());

        final Workflow<Document, Problem> workflow = new WorkflowBuilder<Document, Problem>()
                .addStep(fun1)
                .addStep(fun2)
                .addStep(fun3)
                .build();

        final WorkflowExecutor<Problem, Document> workflowExecutor = new WorkflowExecutor<>(workflow);
        final Either<Problem, Document> result = workflowExecutor.execute(new Document());

        assertThat(result.isLeft()).isTrue();
        InOrder inOrder = inOrder(fun1, fun2, fun3);
        inOrder.verify(fun1).apply(any());
        inOrder.verify(fun2).apply(any());
        inOrder.verify(fun3, never()).apply(any());
    }

    @Test
    @SuppressWarnings("UnusedAssignment")
    public void whenFunctionsInitiallyReturnLeftAndThenRetry_duringRetrySkipsAllSuccessfullyExecutedSteps() {
        doReturn(Left(new Problem())).when(fun2).apply(any());
        doReturn(Left(new Problem())).when(fun3).apply(any());

        final Workflow<Document, Problem> workflow = new WorkflowBuilder<Document, Problem>()
                .addStep(fun1)
                .addStep(fun2)
                .addStep(fun3)
                .build();

        final WorkflowExecutor<Problem, Document> workflowExecutor = new WorkflowExecutor<>(workflow);

        Either<Problem, Document> result = workflowExecutor.execute(new Document());
        doReturn(Right(new Document())).when(fun2).apply(any());
        result = workflowExecutor.execute(new Document());
        result = workflowExecutor.execute(new Document());
        doReturn(Right(new Document())).when(fun3).apply(any());
        result = workflowExecutor.execute(new Document());

        assertThat(result.isRight()).isTrue();
        InOrder inOrder = inOrder(fun1, fun2, fun3);
        inOrder.verify(fun1, times(1)).apply(any());
        inOrder.verify(fun2, times(2)).apply(any());
        inOrder.verify(fun3, times(3)).apply(any());
    }

    @Test
    @SuppressWarnings("UnusedAssignment")
    public void whenFunctionReturnLeftAndThenRecover_recoveryOperationIsExecutedAfterEachFail() {
        doReturn(Left(new Problem())).when(fun2).apply(any());
        doReturn(Left(new Problem())).when(fun3).apply(any());

        final Workflow<Document, Problem> workflow = new WorkflowBuilder<Document, Problem>()
                .addStep(fun1)
                .addStep(fun2)
                .addStep(fun3, fun5)
                .build();

        final WorkflowExecutor<Problem, Document> workflowExecutor = new WorkflowExecutor<>(workflow);

        Either<Problem, Document> result = workflowExecutor.execute(new Document());
        doReturn(Right(new Document())).when(fun2).apply(any());
        result = workflowExecutor.execute(new Document());
        result = workflowExecutor.execute(new Document());
        doReturn(Right(new Document())).when(fun3).apply(any());
        result = workflowExecutor.execute(new Document());

        assertThat(result.isRight()).isTrue();
        InOrder inOrder = inOrder(fun1, fun2, fun3, fun5, fun3, fun5, fun3);
        inOrder.verify(fun1, times(1)).apply(any());
        inOrder.verify(fun2, times(2)).apply(any());
        inOrder.verify(fun3, times(1)).apply(any());
        inOrder.verify(fun5, times(1)).apply(any());
        inOrder.verify(fun3, times(1)).apply(any());
        inOrder.verify(fun5, times(1)).apply(any());
        inOrder.verify(fun3, times(1)).apply(any());
    }

    @Test
    public void whenExecutorSpecifiedForFunction_specifiedExecutorInvoked() {
        final Workflow<Document, Problem> workflow = new WorkflowBuilder<Document, Problem>()
                .addStep(fun1, executor)
                .addStep(fun2)
                .addStep(fun3)
                .build();

        final WorkflowExecutor<Problem, Document> workflowExecutor = new WorkflowExecutor<>(workflow);

        Either<Problem, Document> result = workflowExecutor.execute(new Document());

        assertThat(result.isRight()).isTrue();

        InOrder inOrder = inOrder(executor, fun1, fun2, fun3);
        inOrder.verify(executor).execute(any(), any(), any());
        inOrder.verify(fun1).apply(any());
        inOrder.verify(fun2).apply(any());
        inOrder.verify(fun3).apply(any());
    }

    static class Problem {
    }

    static class Document {
    }
}