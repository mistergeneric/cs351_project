package client;

public interface RoomListener {

    public void open(String roomName);
    public void closed(String roomName);
}
