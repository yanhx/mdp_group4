package listeners;

public interface SocketListener {
	public void bluetoothConnected();
	public void receiverMessage(String message);
	public void bluetoothDisconnected();
}
