package furhatos.app.mathtutor;

public class Test {

    public static void main(String[] args) {

        DialogManagerClient client = new DialogManagerClient();
        String action = client.callServer(0, "proceed", 1, "gaze");
        System.out.println(action);

        SocketClient client2 = new SocketClient();
        String emo = client2.callServer();
        System.out.println(emo);
    }
}
