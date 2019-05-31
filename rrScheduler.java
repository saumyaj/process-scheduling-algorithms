import java.util.*;

public class rrScheduler extends Scheduler {

    rrScheduler(int quantum) {
        this.quantum = quantum;
        preempt = true;
        name = "RR";
    }

    public void addJob(Job job) {
        jobQueue.add(job);
    }

    public Job getNextJob() {
        if (jobQueue.size() > 0) {
            Job j = jobQueue.get(0);
            jobQueue.remove(0);
            return j;
        }
        return null;
    }
}