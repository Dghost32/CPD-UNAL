package co.edu.unal.paralela;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import co.edu.unal.paralela.utils.ReciprocalArraySumTask;

public final class ReciprocalArraySum {
    private ReciprocalArraySum() {
    }

    protected static double seqArraySum(final double[] input) {
        return DoubleStream.of(input)
                .map(x -> 1 / x)
                .sum();
    }

    private static int getChunkSize(final int chunks, final int elements) {
        // Función techo entera
        return (elements + chunks - 1) / chunks;
    }

    private static int getChunkStartInclusive(final int chunk,
            final int nChunks, final int nElements) {
        final int chunkSize = getChunkSize(nChunks, nElements);
        return chunk * chunkSize;
    }

    private static int getChunkEndExclusive(final int chunk, final int nChunks,
            final int nElements) {

        final int chunkSize = getChunkSize(nChunks, nElements);
        final int end = (chunk + 1) * chunkSize;

        if (end > nElements)
            return nElements;

        return end;
    }

    protected static double parArraySum(final double[] input) {
        assert input.length % 2 == 0;

        int numTasks = 2;

        List<ReciprocalArraySumTask> tasks = new ArrayList<>(numTasks);

        IntStream.range(0, numTasks).forEach(i -> {
            int start = getChunkStartInclusive(i, numTasks, input.length);
            int end = getChunkEndExclusive(i, numTasks, input.length);
            ReciprocalArraySumTask task = new ReciprocalArraySumTask(start, end, input);
            tasks.add(task);
        });

        try (ForkJoinPool pool = ForkJoinPool.commonPool()) {
            tasks.forEach(task -> pool.invoke(task));
        }

        return tasks.stream().reduce(0.0, (sum, task) -> sum + task.getValue(), Double::sum);
    }

    protected static double parManyTaskArraySum(final double[] input,
            final int numTasks) {
        assert input.length % 2 == 0;

        List<ReciprocalArraySumTask> tasks = new ArrayList<>(numTasks);

        IntStream.range(0, numTasks).forEach(i -> {
            int start = getChunkStartInclusive(i, numTasks, input.length);
            int end = getChunkEndExclusive(i, numTasks, input.length);
            ReciprocalArraySumTask task = new ReciprocalArraySumTask(start, end, input);
            tasks.add(task);
        });

        try (ForkJoinPool pool = ForkJoinPool.commonPool()) {
            tasks.forEach(task -> pool.invoke(task));
        }

        return tasks.stream().reduce(0.0, (sum, task) -> sum + task.getValue(), Double::sum);
    }
}