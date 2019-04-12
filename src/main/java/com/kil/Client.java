package com.kil;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@RequiredArgsConstructor
public class Client {

    private int x = 125;
    private int y = 75;
    private boolean readyToDraw = true;
    private boolean finished = false;

    @NonNull
    private boolean ticket;

    @NonNull
    private boolean onlyToKassa;
}
