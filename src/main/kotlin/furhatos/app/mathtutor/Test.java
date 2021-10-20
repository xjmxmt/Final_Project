package furhatos.app.mathtutor;

public class Test {

    public static void main(String[] args) {

        SocketClient client = new SocketClient();
        String emo = client.callServer();
        System.out.println(emo);
    }
}
