public class Event {

    EventManager.State oldState, newState;
    int timestamp, pid;

    Event(int timestamp, int pid, EventManager.State oldState, EventManager.State newState) {
        this.oldState = oldState;
        this.newState = newState;
        this.pid = pid;
        this.timestamp = timestamp;
    }
}