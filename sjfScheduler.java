import java.util.*;

public class sjfScheduler extends Scheduler {

    sjfScheduler(int quantum) {
        this.quantum = quantum;
        name = "SJF";
    }

    public void addJob(Job job) {
        int i = 0;
        for (Job j : jobQueue) {
            if (j.burst > job.burst) {
                break;
            }
            if (j.burst == job.burst) {
                if (j.pid > job.pid) {
                    break;
                }
            }
            i++;
        }
        jobQueue.add(i, job);
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