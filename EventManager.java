import java.util.*;

public class EventManager {
    public static enum State {
        CREATED, READY, RUNNING, COMPLETED;
    }

    LinkedList<Event> eventQueue = new LinkedList<>();

    public void initializeQueue(LinkedList<Event> q) {
        eventQueue.clear();
        for (Event e : q) {
            eventQueue.add(e);
        }
    }

    public void addEvent(Event e) {
        int i = 0;

        for (Event eve : eventQueue) {
            if (eve.timestamp > e.timestamp) {
                break;
            } else if (eve.timestamp == e.timestamp && eve.pid > e.pid) {
                break;
            }
            i++;
        }
        eventQueue.add(i, e);
    }

    public void removeEvent(int pid, State oldState, State newState) {
        int i = 0;

        for (Event eve : eventQueue) {
            if (eve.pid == pid && eve.newState == newState && eve.oldState == oldState) {
                break;
            }
            i++;
        }
        eventQueue.remove(i);
    }

    public Event getNextEvent() {
        Event e = eventQueue.get(0);
        eventQueue.remove(0);
        return e;
    }

    public int getNextEventTime() {
        if (eventQueue.size() == 0)
            return -1;
        Event e = eventQueue.get(0);
        return e.timestamp;
    }
}