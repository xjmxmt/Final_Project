package furhatos.app.mathtutor;

public class Test {

    public static void main(String[] args) {

        SocketClient client = new SocketClient();
        String action = client.callServer(0, "proceed", 1, "gaze");
        System.out.println(action);
    }
}
