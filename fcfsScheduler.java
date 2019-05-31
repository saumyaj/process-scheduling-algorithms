import java.util.*;

public class fcfsScheduler extends Scheduler {

    fcfsScheduler(int quantum) {
        this.quantum = quantum;
        name = "FCFS";
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