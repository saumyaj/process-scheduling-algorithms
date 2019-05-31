public class Job {
    int arrivalTime, pid, burst;
    int finishTime = -1;
    int remainingTime = -1;
    int startTime = -1;
    int queueArrival = -1;

    Job(int arrivalTime, int pid, int burst) {
        this.arrivalTime = arrivalTime;
        this.pid = pid;
        this.burst = burst;
        this.remainingTime = burst;
    }

    public void reset() {
        finishTime = -1;
        remainingTime = burst;
        startTime = -1;
        queueArrival = -1;
    }
}