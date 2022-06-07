package com.example.dm564project;

import java.time.Instant;

public class DBEntity {
    public long seconds; // Creation time of this in seconds since the epoch of 1970-01-01T00:00:00Z

    public int nanos; // The nanosecond component of the creation time. Always between 0 and 999 999 999.

    /**
     * Returns the timestamp of the given entity.
     * @param entity The entity to read the timestamp from.
     * @return An java.time.Instant representing the timestamp.
     */
    public static Instant instant(DBEntity entity){
        Instant instant = Instant.ofEpochSecond(0);
        if(entity != null)
            instant = Instant.ofEpochSecond(entity.seconds, entity.nanos);
        return instant;
    }
}
