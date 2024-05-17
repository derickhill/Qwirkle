package capstone.networking.messages.client;


import capstone.networking.Games;
import capstone.networking.messages.Message;
import capstone.networking.QwirkleClient;
import capstone.networking.messages.server.HandleSet;

import java.util.Collection;

/**
 * The message received from a client, typically once logged on initially,
 * to set the handle that will be used when communicating.
 */
public class SetHandle extends Message<QwirkleClient> {
    private static final long serialVersionUID = 6L;

    public String handle;

    public SetHandle(String handle) {
        this.handle = handle;
    }

    @Override
    public String toString() {
        return String.format("SetHandle('%s')", handle);
    }

    @Override
    public void apply(QwirkleClient qwirkleClient) {
        // Check if the handle is already being used. If is, append client number.
        long count = Games.games.values()
                .stream()
                .flatMap(Collection::stream)
                .distinct()
                .filter(client -> client.handle.equalsIgnoreCase(handle))
                .count();
        if(count > 0) handle = String.format("%s#%d", handle, qwirkleClient.clientNum);

        // Set the handle.
        qwirkleClient.handle = handle;

        // Tell the client about the handle that was decided upon.
        qwirkleClient.send(new HandleSet(handle));
    }
}
