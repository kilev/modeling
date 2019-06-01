package com.kil;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class Event {
    public Client eventClient;
    public int eventTime;
    public String event;
}
