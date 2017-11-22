package sample;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Bank;
import model.Order;
import model.Util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Main extends Application {
    private static int PORT = 33333;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Digital Cash Bank");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        //launch(args);

        //TODO generate rsa key set for signing
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Socket started and listening..");
            Socket socket = serverSocket.accept();
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            LinkedList<Order> orders = (LinkedList<Order>) objectInputStream.readObject();
            System.out.println(orders.size());

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String signature = "Here is the signature";
            bufferedWriter.write(signature);
            bufferedWriter.flush();
            socket.close();


            Bank bank = new Bank(Util.generateKeyPair());


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
