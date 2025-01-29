package observers;

import Nova.GameObject;
import observers.events.Event;

public interface Observer {
    void onNotify(GameObject gameObject, Event event);
}
