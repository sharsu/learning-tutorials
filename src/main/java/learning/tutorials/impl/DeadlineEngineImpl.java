package learning.tutorials.impl;

import learning.tutorials.DeadlineEngine;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class DeadlineEngineImpl<T extends Callable<T>> implements DeadlineEngine {

    private static final Logger LOG = Logger.getLogger(DeadlineEngineImpl.class.getName());

    private static final int INITIAL_CAPACITY = 500;

    private final PriorityBlockingQueue<Deadline> deadlines = new PriorityBlockingQueue<>(INITIAL_CAPACITY, (d1, d2) ->
            (int) (d1.getDeadlineMs() / 1000 - d2.getDeadlineMs() / 1000));

    private final Random random = new Random();

    @Override
    public long schedule(long deadlineMs) {
        Long requestId = random.nextLong();
        deadlines.add(new Deadline(requestId, deadlineMs));
        return requestId;
    }

    @Override
    public boolean cancel(long requestId) {
        LOG.info("Cancelling the request " + requestId);
        return deadlines.removeIf(d -> d.getRequestId() == requestId);
    }

    @Override
    public int poll(long nowMs, Consumer<Long> handler, int maxPoll) {
        int counter = 0;
        Deadline deadline = deadlines.peek();
        while (deadline != null && counter < maxPoll && deadline.getDeadlineMs() <= nowMs) {
            handler.accept(deadlines.poll().requestId);
            deadline = deadlines.peek();
            counter++;
        }
        LOG.info("Number of elements polled " + counter);
        return counter;
    }

    @Override
    public int size() {
        return deadlines.size();
    }


    private static class Deadline {

        public final long requestId;

        public final long deadlineMs;

        public Deadline(long requestId, long deadlineMs) {
            this.requestId = requestId;
            this.deadlineMs = deadlineMs;
        }

        public long getRequestId() {
            return requestId;
        }

        public long getDeadlineMs() {
            return deadlineMs;
        }

        @Override
        public String toString() {
            return "Deadline{" +
                    "requestId=" + requestId +
                    ", deadlineMs=" + deadlineMs +
                    '}';
        }

    }

}
