import java.util.*;

public class srtfScheduler extends Scheduler {

    srtfScheduler(int quantum) {
        this.quantum = quantum;
        name = "SRTF";
    }

    public void addJob(Job job) {
        int i = 0;
        for (Job j : jobQueue) {
            if (j.remainingTime > job.remainingTime) {
                break;
            }
            if (j.remainingTime == job.remainingTime) {
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