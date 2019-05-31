import java.util.*;

abstract class Scheduler {
    LinkedList<Job> jobQueue = new LinkedList<>();

    // abstract void schedule();
    int quantum;
    String name;
    boolean preempt = false;

    abstract Job getNextJob();

    abstract void addJob(Job job);

}