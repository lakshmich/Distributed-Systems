//package com.client;
//
///**
// * Created by sahitya on 4/9/2015.
// */
//
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.ServerSocket;
//import java.net.Socket;
//
///**
// * Created by anushree99 on 3/31/2015.
// */
//class socketThread implements Runnable{
//
//    private String host;
//    private int port;
//    private Thread t;
//
//
//    socketThread(String hostparam, int portParam)
//    {
//        host=hostparam;
//        port=portParam;
//    }
//
//    public void run()
//    {
//
//        System.out.println("Starting socket on "+ host + " "+ port);
//        Socket kkSocket = null;
//        PrintWriter out = null;
//        BufferedReader in = null;
//        String inputLine;
//
//        ServerSocket serverSocket = null;
//        try {
//            serverSocket = new ServerSocket(port);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        while(true)
//        {
//            System.out.println("Waiting for request");
//
//            Socket s = null;
//            try {
//                s = serverSocket.accept();
//                System.out.println("Processing request");
////                executorService.submit(new ServiceRequest(s));
////                out = new PrintWriter(s.getOutputStream(), true);
//                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
//
//                while(true) {
//                    {
//                        while ((inputLine = in.readLine()) != null) {
////                        String inputLine, outputLine;
//                            if(inputLine.startsWith("election"))
//                            {
//
//                                int portNumber=Integer.parseInt(inputLine.split(" ")[1]);
//                                Socket electionRequester = new Socket("localhost", portNumber);
//                                out = new PrintWriter(electionRequester.getOutputStream(), true);
//                                if(port>portNumber)
//                                    out.print("OK");
//
//                            }
//
//                            if(inputLine.startsWith("get_temp"))
//                            {
//
//                                int portNumber=Integer.parseInt(inputLine.split(" ")[1]);
//                                Socket electionRequester = new Socket("localhost", portNumber);
//                                out = new PrintWriter(kkSocket.getOutputStream(), true);
//                                out.print("time "+System.currentTimeMillis()+ " "+ port);
//
//                            }
//
//
//                        }
//                    }}}catch(IOException ioe) {
//                System.out.println("Error accepting connection");
//                ioe.printStackTrace();
//            }}}
//
//
////        try {
////            kkSocket = new Socket(host, port);
////            out = new PrintWriter(kkSocket.getOutputStream(), true);
////            BufferedReader in = new BufferedReader(
////                    new InputStreamReader(kkSocket.getInputStream()));
////            BufferedReader stdIn =
////                    new BufferedReader(new InputStreamReader(System.in));
////            String fromServer;
////            String fromUser;
////
//////            out.println("register,device,bulb");
////
////            out.print("election "+ Client_temp.id);
////
////            while ((fromServer = in.readLine()) != null) {
//////                out.println("register,sensor,temperature");
////
////                if (fromServer.startsWith("election")) {
////                    out.println(Client_temp.id);
////                }
////
////            }
////
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//
//    public void start()
//    {
//        t=new Thread(this);
//        t.start();
//    }
//
//}
