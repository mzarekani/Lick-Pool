package lickpool.records;

import java.util.Comparator;

public class LickPoolRecordsComparator implements Comparator<LickPoolRecords> {
    public int compare(LickPoolRecords a, LickPoolRecords b)
    {
        return a.getIteration() - b.getIteration();
    }
}
