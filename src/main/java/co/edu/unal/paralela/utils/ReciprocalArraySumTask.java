package co.edu.unal.paralela.utils;

import java.util.concurrent.RecursiveAction;
import java.util.stream.IntStream;

public class ReciprocalArraySumTask extends RecursiveAction {
  private final int startIndexInclusive;
  private final int endIndexExclusive;
  private final double[] input;
  private double value;

  public ReciprocalArraySumTask(final int setStartIndexInclusive,
      final int setEndIndexExclusive, final double[] setInput) {
    this.startIndexInclusive = setStartIndexInclusive;
    this.endIndexExclusive = setEndIndexExclusive;
    this.input = setInput;
  }

  public double getValue() {
    return value;
  }

  @Override
  protected void compute() {
    final int SEQUENTIAL_THRESHOLD = Math.max(10000, (endIndexExclusive - startIndexInclusive) / 2);

    if (endIndexExclusive - startIndexInclusive <= SEQUENTIAL_THRESHOLD) {
      IntStream.range(startIndexInclusive, endIndexExclusive)
          .forEach(i -> value += 1 / input[i]);
    } else {
      int mid = (startIndexInclusive + endIndexExclusive) / 2;
      ReciprocalArraySumTask leftTask = new ReciprocalArraySumTask(startIndexInclusive, mid, input);
      ReciprocalArraySumTask rightTask = new ReciprocalArraySumTask(mid, endIndexExclusive, input);
      leftTask.fork();
      rightTask.compute();
      leftTask.join();
      value = leftTask.getValue() + rightTask.getValue();
    }
  }
}
