import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final int PORT = 8989;

    public static void main(String[] args) {

        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        Gson gson = new GsonBuilder().create();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter
                             (clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(
                             new InputStreamReader(clientSocket.getInputStream()))) {

                    String word = in.readLine();
                    out.println(gson.toJson(engine.search(word)));
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}
