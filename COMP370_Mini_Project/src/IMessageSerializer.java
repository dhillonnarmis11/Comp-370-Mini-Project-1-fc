
// This interface defines how messages are converted
// to and from strings before being sent over the network.
// Using an interface makes it easy to change formats later
// without changing server logic.

public interface IMessageSerializer {

    // Converts a message into a string
    String serialize(String message);

    // Converts a received string back into a message
    String deserialize(String message);
}
