package com.kil;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class Event {
    public static List<Event> eventBox = new ArrayList<>();

    Client eventClient;
    int eventTime;
    String event;

    public static void add(Event event) {
//        for (Event e : eventBox) {
//            if (e.eventTime > event.eventTime)
//                e.setEventTime(e.getEventTime() - event.eventTime);
//            else
//                e.setEventTime(0);
//        }
        eventBox.add(event);
    }
}
