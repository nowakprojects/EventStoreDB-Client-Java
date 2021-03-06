package com.eventstore.dbclient;

import org.junit.Rule;
import org.junit.Test;
import testcontainers.module.EventStoreTestDBContainer;
import testcontainers.module.EventStoreStreamsClient;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;

public class DeleteTests {
    @Rule
    public final EventStoreTestDBContainer server = new EventStoreTestDBContainer(false);

    @Rule
    public final EventStoreStreamsClient client = new EventStoreStreamsClient(server);

    @Test
    public void testCanDeleteStream() throws Throwable {
        CompletableFuture<DeleteResult> future = client.instance.softDelete("dataset20M-1800",
                new StreamRevision(1999));
        DeleteResult result = future.get();

        // The ideal actual test here would be that the stream does not exist, but since this
        // client does not currently have reads we can't do that. In lieu, we ensure that the
        // log record which actually soft deleted this was written at some late point in the
        // database log given the size of the database. The _actual_ position is non-deterministic
        // so we cannot specify an exact match here.
        Position nonZero = new Position(20000000, 20000000);
        assertTrue(result.getLogPosition().compareTo(nonZero) > 0);
    }
}
