package com.itamnesia.qiwihackathon.service.time;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Service
@AllArgsConstructor
public class TimeServiceImpl implements TimeService {
    private Clock clock;

    @Override
    public OffsetDateTime now(int secondsOffset) {
        var offset = ZoneOffset.ofTotalSeconds(secondsOffset);
        var zoneId = ZoneId.ofOffset("UTC", offset);
        return OffsetDateTime.now(clock.withZone(zoneId));
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

}
