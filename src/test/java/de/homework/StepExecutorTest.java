package de.homework;

import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.function.Function;

import static io.vavr.API.Left;
import static io.vavr.API.Right;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StepExecutorTest {

    static class Problem {
    }

    static class Document {
    }

    Function<Document, Either<Problem, Document>> fun1 = mock(Function.class);
    Function<Document, Either<Problem, Document>> fun2 = mock(Function.class);
    Function<Document, Either<Problem, Document>> fun3 = mock(Function.class);
    Function<Document, Either<Problem, Document>> fun4 = mock(Function.class);

    @BeforeEach
    void setUp() {
        Mockito.reset();

        doAnswer(answer -> Right(answer.getArgument(0))).when(fun1).apply(any());
        doAnswer(answer -> Right(answer.getArgument(0))).when(fun2).apply(any());
        doAnswer(answer -> Right(answer.getArgument(0))).when(fun3).apply(any());
        doAnswer(answer -> Right(answer.getArgument(0))).when(fun4).apply(any());
    }

    @Test
    public void whenAllFunctionsReturnRight_allFunctionsAreExecutedInRightOrder() {
        final StepExecutor<Document, Problem> stepExecutor = new StepExecutor.Builder<Document, Problem>()
                .addStep(fun1)
                .addStep(fun2)
                .addStep(fun3)
                .addStep(fun4)
                .build();

        final Either<Problem, Document> result = stepExecutor.apply(new Document());

        assertThat(result.isRight()).isTrue();
        InOrder inOrder = inOrder(fun1, fun2, fun3, fun4);
        inOrder.verify(fun1).apply(any());
        inOrder.verify(fun2).apply(any());
        inOrder.verify(fun3).apply(any());
        inOrder.verify(fun4).apply(any());
    }

    @Test
    void whenAllFunctionsReturnLeftExceptFirstOne_skipsAfterFailure() {
        doReturn(Left(new Problem())).when(fun2).apply(any());
        doReturn(Left(new Problem())).when(fun3).apply(any());

        final StepExecutor<Document, Problem> stepExecutor = new StepExecutor.Builder<Document, Problem>()
                .addStep(fun1)
                .addStep(fun2)
                .addStep(fun3)
                .build();

        final Either<Problem, Document> result = stepExecutor.apply(new Document());

        assertThat(result.isLeft()).isTrue();
        InOrder inOrder = inOrder(fun1, fun2, fun3);
        inOrder.verify(fun1).apply(any());
        inOrder.verify(fun2).apply(any());
        inOrder.verify(fun3, never()).apply(any());
    }

    @Test
    @SuppressWarnings("UnusedAssignment")
    void whenSecondFunctionInitiallyReturnLeftAndThenRecover_skipsAllInitiallyExecutedSteps() {
        doReturn(Left(new Problem())).when(fun2).apply(any());
        doReturn(Left(new Problem())).when(fun3).apply(any());

        final StepExecutor<Document, Problem> stepExecutor = new StepExecutor.Builder<Document, Problem>()
                .addStep(fun1)
                .addStep(fun2)
                .addStep(fun3)
                .build();

        Either<Problem, Document> result = stepExecutor.apply(new Document());
        doReturn(Right(new Document())).when(fun2).apply(any());
        result = stepExecutor.apply(new Document());
        result = stepExecutor.apply(new Document());
        doReturn(Right(new Document())).when(fun3).apply(any());
        result = stepExecutor.apply(new Document());

        assertThat(result.isRight()).isTrue();
        InOrder inOrder = inOrder(fun1, fun2, fun3);
        inOrder.verify(fun1, times(1)).apply(any());
        inOrder.verify(fun2, times(2)).apply(any());
        inOrder.verify(fun3, times(3)).apply(any());
    }
}