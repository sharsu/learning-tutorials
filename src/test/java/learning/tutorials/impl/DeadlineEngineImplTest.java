package learning.tutorials.impl;

import learning.tutorials.DeadlineEngine;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DeadlineEngineImplTest {

    @Test
    public void testSchedulingAndPolling()
    {
        DeadlineEngine deadlineEngine = new DeadlineEngineImpl<>();
        deadlineEngine.schedule(1800489600000L);
        deadlineEngine.schedule(1000489600000L);
        deadlineEngine.schedule(1100489600000L);
        deadlineEngine.schedule(1700489600000L);
        deadlineEngine.schedule(1704489600000L);
        deadlineEngine.schedule(1707489600000L);
        deadlineEngine.schedule(1207489600000L);
        deadlineEngine.schedule(1307489600000L);

        assertEquals(8, deadlineEngine.size());

        assertEquals(0, deadlineEngine.poll(1000089600000L, l -> System.out.println(l), 4));
        assertEquals(8, deadlineEngine.size());


        assertEquals(4, deadlineEngine.poll(1900089600000L, l -> System.out.println(l), 4));
        assertEquals(4, deadlineEngine.size());
    }

    @Test
    public void testSchedulingAndCancelling()
    {
        DeadlineEngine deadlineEngine = new DeadlineEngineImpl<>();
        deadlineEngine.schedule(1800489600000L);
        deadlineEngine.schedule(1000489600000L);
        deadlineEngine.schedule(1100489600000L);
        deadlineEngine.schedule(1700489600000L);
        deadlineEngine.schedule(1704489600000L);
        deadlineEngine.schedule(1707489600000L);
        deadlineEngine.schedule(1207489600000L);
        deadlineEngine.schedule(1307489600000L);

        assertEquals(8, deadlineEngine.size());

        List<Long> requestIds = new ArrayList<>();
        assertEquals(1, deadlineEngine.poll(1000589600000L, l -> requestIds.add(l), 4));

        assertEquals(1, deadlineEngine.poll(1100589600000L, l -> requestIds.add(l), 4));

        assertEquals(1, deadlineEngine.poll(1208489600000L, l -> requestIds.add(l), 4));

        assertEquals(1, deadlineEngine.poll(1308489600000L, l -> requestIds.add(l), 4));

        assertEquals(3, deadlineEngine.poll(1708489600000L, l -> requestIds.add(l), 4));

        assertEquals(1, deadlineEngine.poll(1801489600000L, l -> requestIds.add(l), 4));

        assertEquals(false, deadlineEngine.cancel(1000489600000L));
    }
}
