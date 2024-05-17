package capstone.networking;

import capstone.QwirkleModel;
import capstone.networking.messages.Message;
import capstone.networking.messages.server.Joined;
import capstone.networking.messages.server.Left;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * This class is responsible for managing chat games and sending capstone.messages to
 * clients in specific games.
 */
public class Games {
    // Lock to prevent multiple threads manipulating the games data
    // structure while busy with an operation.
    private static final ReentrantLock lock = new ReentrantLock();

    // [Game Name] -> {clients}
    public static final Map<String, Set<QwirkleClient>> games = new HashMap<>();
    public static final Map<String, QwirkleModel> models = new HashMap<>();

    /**
     * Join a new game. Before joining a new game, the client will leave
     * any games it is currently in. All other clients in the game are
     * notified of the client joining.
     * @param gameName The new game's name.
     * @param client The client joining.
     */
    public static void join(String gameName, QwirkleClient client) {
        // If already in a game, leave it.
        leave(client);

        // If no such game, create it.
        if(!games.containsKey(gameName))
            addGame(gameName);

        // Now join the new game.
        lock.lock();
        // Add client to game.
        games.get(gameName).add(client);
        client.gameName = gameName;

        // Tell all clients that client joined game.
        games.get(gameName)
                .forEach(qwirkleClient -> qwirkleClient.send(new Joined(gameName, client.handle)));
        lock.unlock();
    }

    /**
     * The client leaves all games currently a member of. All other clients
     * are sent a notification of this fact.
     * @param client The client leaving.
     */
    public static void leave(QwirkleClient client) {
        lock.lock();
        // Get games to which client belongs.
        List<String> gamesIn = games.entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(client))
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());

        // Remove client from these games and notify other clients of this.
        gamesIn.forEach(gameName -> {
            // Get game.
            Set<QwirkleClient> game = games.get(gameName);

            // Remove client from game.
            game.remove(client);
            client.gameName = "";

            // Send message to other clients in game.
            Left msg = new Left(gameName, client.handle);
            game.forEach(chatClient -> chatClient.send(msg));
        });
        lock.unlock();
    }

    public static void deleteGame(String gameName)
    {
        lock.lock();
        games.remove(gameName);
        models.remove(gameName);
        lock.unlock();
    }

    /**
     * A new (empty) game is added.
     * @param gameName The new game's name.
     */
    public static void addGame(String gameName) {
        lock.lock();
        games.put(gameName, new HashSet<>());
        models.put(gameName, new QwirkleModel());
        lock.unlock();
    }

    /**
     * A message is sent to all clients in the named game.
     * @param gameName The game's name.
     * @param message The message to be sent.
     */
    public static void send(String gameName, Message message) {
        lock.lock();
        // Is there a game with the given name? If not, exit.
        if(!games.containsKey(gameName)) return;

        // Get clients in game.
        Set<QwirkleClient> clients = games.get(gameName);

        // Send message to each client.
        for(QwirkleClient client : clients)
            client.send(message);
        lock.unlock();
    }

    public static int getGameSize(String gameName)
    {
        if(!games.containsKey(gameName))
        {
            addGame(gameName);
            return 0;
        }

        return games.get(gameName).size();
    }
}
