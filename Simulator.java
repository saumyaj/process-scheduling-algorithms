import java.io.*;
import java.lang.*;
import java.util.*;

public class Simulator {
    static int currTime = 0;
    static int currJobPid = -1;
    static String inputName = "";

    private static void resetJobs(HashMap<Integer, Job> map) {
        for (Integer pid : map.keySet()) {
            Job j = map.get(pid);
            j.reset();
        }
    }

    private static String extractInputFileName(String inputName) {
        String[] arr = inputName.split("/");
        int l = arr.length - 1;
        return arr[l];
    }

    private static void writeOutput(HashMap<Integer, Job> map, String name) {
        String outputPath = extractInputFileName(inputName) + "_" + name;
        List<Integer> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
            for (Integer key : keys) {
                Job j = map.get(key);

                int turnAroundTime = -j.arrivalTime + j.finishTime;
                int waitTime = turnAroundTime - j.burst;

                String s = j.pid + " " + j.finishTime + " " + waitTime + " " + turnAroundTime;
                writer.append(s);
                writer.append("\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateRemainingTime(int pid, HashMap<Integer, Job> map) {
        Job j = map.get(pid);
        j.remainingTime -= (currTime - j.startTime);
    }

    private static int getRemainingTime(int pid, HashMap<Integer, Job> map) {
        Job j = map.get(pid);
        return j.remainingTime;
    }

    private static void updateFinishTime(int pid, HashMap<Integer, Job> map) {
        Job j = map.get(pid);
        j.finishTime = currTime;
        // System.out.println(currTime);
    }

    private static boolean checkForPreemption(Event e, HashMap<Integer, Job> map) {
        Job currJob = map.get(currJobPid);
        int currRemainingTime = currJob.remainingTime - (currTime - currJob.startTime);
        Job newJob = map.get(e.pid);
        int newRemainingTime = newJob.remainingTime;
        if (currRemainingTime > newRemainingTime) {
            return true;
        }
        return false;
    }

    private static void runSimulation(Scheduler sch, LinkedList<Event> eventQueue, EventManager emo,
            HashMap<Integer, Job> map, boolean isSRTF) {
        currJobPid = -1;
        currTime = 0;
        while (emo.eventQueue.size() > 0) {
            Event e = emo.getNextEvent();
            currTime = e.timestamp;
            switch (e.newState) {
            case CREATED:
                // System.out.println("process " + e.pid + " created at " + e.timestamp);
                emo.addEvent(new Event(e.timestamp, e.pid, EventManager.State.CREATED, EventManager.State.READY));
                break;
            case READY:
                if (e.oldState == EventManager.State.RUNNING) {
                    // System.out.println("process " + e.pid + " paused at " + e.timestamp);
                    updateRemainingTime(e.pid, map);
                    currJobPid = -1;
                    // System.out.println(getRemainingTime(e.pid, map));
                    if (getRemainingTime(e.pid, map) == 0) {
                        // System.out.println("process " + e.pid + " finished at " + e.timestamp);
                        updateFinishTime(e.pid, map);
                        currJobPid = -1;
                    } else {
                        // System.out.println("process " + e.pid + " ready at " + e.timestamp);
                        Job j = map.get(e.pid);
                        j.queueArrival = currTime;
                        sch.addJob(j);
                    }
                } else {
                    // System.out.println("process " + e.pid + " initialized at " + e.timestamp);
                    if (isSRTF) {
                        boolean preempt = false;
                        if (currJobPid != -1) {
                            preempt = checkForPreemption(e, map);
                        }
                        if (preempt) {
                            emo.removeEvent(currJobPid, EventManager.State.RUNNING, EventManager.State.READY);
                            emo.addEvent(new Event(currTime, currJobPid, EventManager.State.RUNNING,
                                    EventManager.State.READY));
                        }

                    }
                    Job j = map.get(e.pid);
                    j.queueArrival = currTime;
                    sch.addJob(j);

                }
                break;
            case COMPLETED:
                // System.out.println("process " + e.pid + " finished at " + e.timestamp);
                Job j = map.get(e.pid);
                j.finishTime = currTime;
                currJobPid = -1;
                break;
            default:
                break;
            }
            int nextEventTime = emo.getNextEventTime();
            if (nextEventTime != -1 && currTime >= nextEventTime) {
                continue;
            }
            if (currJobPid == -1) {
                Job j = sch.getNextJob();
                if (j != null) {

                    currJobPid = j.pid;
                    j.startTime = currTime;
                    // System.out.println("process " + j.pid + " started at " + currTime);
                    if (sch.preempt) {
                        emo.addEvent(new Event(currTime + Math.min(sch.quantum, j.remainingTime), j.pid,
                                EventManager.State.RUNNING, EventManager.State.READY));
                    } else {
                        emo.addEvent(new Event(currTime + j.remainingTime, j.pid, EventManager.State.RUNNING,
                                EventManager.State.READY));
                    }
                }
            }
        }
        writeOutput(map, sch.name);
    }

    public static void main(String[] args) {
        BufferedReader reader;
        int quantum = -1;
        // BufferedWriter writer;
        inputName = args[0];
        String path = args[0];

        HashMap<Integer, Job> map = new HashMap<>();
        LinkedList<Event> eventQueue = new LinkedList<>();
        LinkedList<Event> copy = new LinkedList<>();

        try {
            reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            quantum = Integer.parseInt(line);
            line = reader.readLine();
            while (line != null) {
                String arr[] = line.split("\\s+");
                // System.out.println(Integer.parseInt(arr[0]));
                Job job = new Job(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                map.put(job.pid, job);
                Event e = new Event(job.arrivalTime, job.pid, null, EventManager.State.CREATED);
                eventQueue.add(e);
                // System.out.println(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        EventManager emo = new EventManager();
        emo.initializeQueue(eventQueue);

        Scheduler fcfs = new fcfsScheduler(quantum);
        runSimulation(fcfs, eventQueue, emo, map, false);

        resetJobs(map);
        emo.initializeQueue(eventQueue);

        Scheduler sjf = new sjfScheduler(quantum);
        runSimulation(sjf, eventQueue, emo, map, false);

        resetJobs(map);
        emo.initializeQueue(eventQueue);

        Scheduler rr = new rrScheduler(quantum);
        runSimulation(rr, eventQueue, emo, map, false);

        resetJobs(map);
        emo.initializeQueue(eventQueue);

        Scheduler srtf = new srtfScheduler(quantum);
        runSimulation(srtf, eventQueue, emo, map, true);

        return;

    }
}