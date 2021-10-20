package furhatos.app.mathtutor;

import java.net.*;
import java.io.*;

public class SocketClient {

    public String callServer() {

        try {

            URL url = new URL("http://127.0.0.1:5000/emotion?data=HelloPython");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output = "null";
            String tmp;
            System.out.println("Output from Server... \n");
            while ((tmp = br.readLine()) != null) {
                output = tmp;
            }

            conn.disconnect();

            return output;

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return "null";
    }
}