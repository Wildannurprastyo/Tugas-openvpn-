package com.pemrogramanJaringan.pemrogramanJaringan.Filter_Stream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class wildan {
    private static final int PORT_SEND = 12345;
    private static final int PORT_RECEIVE = 12345;
    private static final Object CONSOLE_LOCK = new Object();
    private static final String CLEAR_LINE = "\u001B[2K";
    private static final String CURSOR_UP = "\u001B[1A";
    private static final String NAMA_SENDIRI = "Wildan";
    private static final String LABEL_PROMPT = "pesan";
    private static boolean promptAktif = false;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String ipTarget;

        while (true) {
            System.out.print("Masukkan Ip Target: ");
            ipTarget = input.nextLine();

            if (ipTarget.isEmpty()) {
                System.out.println("Ip Target tidak boleh kosong");
                continue;
            }

            try {
                InetAddress inet = InetAddress.getByName(ipTarget);
                boolean status = inet.isReachable(3000);
                if (!status) {
                    System.out.println("Ip Target tidak ditemukan");
                    continue;
                }
                System.out.println("Koneksi berhasil Ketik 'exit' untuk keluar.\n");

            } catch (Exception e) {
                System.out.println("Terdapat ERRROR " + e);
                continue;
            }
            break;
        }

        Thread penerima = new Thread(() -> terimaPesan("Wisnu", PORT_RECEIVE), "wildan-receiver");
        penerima.setDaemon(true);
        penerima.start();

        while (true) {
            tampilkanPrompt(LABEL_PROMPT);
            String text = input.nextLine();

            if ("exit".equalsIgnoreCase(text)) {
                tampilkanInfo("Program selesai.");
                break;
            }

            tampilkanPesanKirim(NAMA_SENDIRI, text);

            try (Socket s = new Socket(ipTarget, PORT_SEND);
                    PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
                out.println(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void terimaPesan(String namaLawan, int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (Socket server = serverSocket.accept();
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(server.getInputStream()))) {

                    String pesan;
                    while ((pesan = in.readLine()) != null) {
                        if (!pesan.isBlank()) {
                            tampilkanPesanMasuk(namaLawan, pesan, LABEL_PROMPT);
                        }
                    }
                } catch (Exception e) {
                    tampilkanInfo("Koneksi penerima terputus: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            tampilkanInfo("Gagal membuka port " + port + ": " + e.getMessage());
        }
    }

    private static void tampilkanPrompt(String nama) {
        synchronized (CONSOLE_LOCK) {
            System.out.println();
            System.out.print(nama + ": ");
            promptAktif = true;
        }
    }

    private static void tampilkanPesanMasuk(String pengirim, String pesan, String namaPrompt) {
        synchronized (CONSOLE_LOCK) {
            if (promptAktif) {
                System.out.print("\r");
                System.out.print(CLEAR_LINE);
                System.out.print(CURSOR_UP);
                System.out.print("\r");
                System.out.print(CLEAR_LINE);
            }
            System.out.println(pengirim + ": " + pesan);
            System.out.println();
            System.out.print(namaPrompt + ": ");
            promptAktif = true;
        }
    }

    private static void tampilkanPesanKirim(String pengirim, String pesan) {
        synchronized (CONSOLE_LOCK) {
            if (promptAktif) {
                System.out.print("\r");
                System.out.print(CLEAR_LINE);
                System.out.print(CURSOR_UP);
                System.out.print("\r");
                System.out.print(CLEAR_LINE);
            }
            System.out.print(CURSOR_UP);
            System.out.print("\r");
            System.out.print(CLEAR_LINE);
            System.out.println(pengirim + ": " + pesan);
            promptAktif = false;
        }
    }

    private static void tampilkanInfo(String pesan) {
        synchronized (CONSOLE_LOCK) {
            System.out.println(pesan);
        }
    }
}