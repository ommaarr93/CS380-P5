// Omar Rodriguez
// CS 380
// Professor Nima Davarpanah

import java.io.*;
import java.net.*;
import java.util.*;

public class UdpClient {

	public static void main(String[] args) throws UnknownHostException, IOException {

      Socket socket = new Socket("codebank.xyz", 38005);
			OutputStream out = socket.getOutputStream();
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
      BufferedReader br = new BufferedReader(isr);

      byte[] packet = new byte[24];

      packet[0] = 0x45;
			packet[1] = 0;
			packet[2] = (byte) (24 >>> 8);
			packet[3] = (byte) 24;
			packet[4] = 0;
			packet[5] = 0;
			packet[6] = 0x40;
			packet[7] = 0;
			packet[8] = 50;
			packet[9] = 17;
			packet[12] = (byte) 0x6a;
			packet[13] = (byte) 0x64;
			packet[14] = (byte) 0xf5;
			packet[15] = (byte) 0x0d;
			packet[16] = (byte) 0x34;
			packet[17] = (byte) 0x25;
			packet[18] = (byte) 0x58;
			packet[19] = (byte) 0x9a;

			short check = checksum(packet);

			packet[10] = (byte) (check >>> 8);
			packet[11] = (byte) check;

			packet[20] = (byte) 0xDE;
			packet[21] = (byte) 0xAD;
			packet[22] = (byte) 0xBE;
			packet[23] = (byte) 0xEF;

      out.write(packet);
      System.out.print("Handshake response: 0x");

			for (int i = 0; i < 4; i++) {
				System.out.printf("%X", is.read());
			}

			System.out.println("");
			byte[] port = new byte[2];

			port[0] = (byte)is.read();
			port[1] = (byte)is.read();
			int val = ((port[0] & 0xff) << 8) | (port[1] & 0xff);

			System.out.println("Port number received: " + val + "\n");

			int data = 1;
      double averageTime = 0.0;


			for (int j = 0; j < 12; j++) {
				data *= 2;

				int size = 28 + data;
				packet = new byte[size];
				System.out.println("Sending packet with " + data + " bytes of data");

				packet[0] = 0x45;
				packet[1] = 0;
				packet[2] = (byte)(size >>> 8);
				packet[3] = (byte)size;
				packet[4] = 0;
				packet[5] = 0;
				packet[6] = 0x40;
				packet[7] = 0;
				packet[8] = 50;
				packet[9] = 17;
				packet[12] = (byte)0x6a;
				packet[13] = (byte)0x64;
				packet[14] = (byte)0xf5;
				packet[15] = (byte)0x0d;
				packet[16] = (byte)0x34;
				packet[17] = (byte)0x25;
				packet[18] = (byte)0x58;
				packet[19] = (byte)0x9a;

				check = checksum(packet);

				packet[10] = (byte)(check >>> 8);
				packet[11] = (byte)check;
				packet[20] = 12;
				packet[21] = 13;
				packet[22] = port[0];
				packet[23] = port[1];
				packet[24] = (byte)((8 + data) >>> 8);
				packet[25] = (byte)(8 + data);

				Random random = new Random();
				byte[] randomData = new byte [size - 28];
				random.nextBytes(randomData);
				int k = 0;

				for (int l = 28; l < size; l++) {
					packet[l] = randomData[k];
					k++;
				}

				byte[] udp = new byte [data + 20];
				int m = 0;
				udp[0] = (byte) 0x6a;
				udp[1] = (byte) 0x64;
				udp[2] = (byte) 0xf5;
				udp[3] = (byte) 0x0d;
				udp[4] = (byte) 0x34;
				udp[5] = (byte) 0x25;
				udp[6] = (byte) 0x58;
				udp[7] = (byte) 0x9a;
				udp[8] = 0;
				udp[9] = 17;
				udp[10] = (byte)((8 + data) >>> 8);
				udp[11] = (byte)(8 + data);
				m = 12;

				for (int n = 20; n < size; n++) {
					udp[m] = packet[n];
					m++;
				}

				check = checksum(udp);
				packet[26] = (byte)(check >>> 8);
				packet[27] = (byte)check;

        long startTime = System.currentTimeMillis();
				out.write(packet);
				System.out.print("Response: 0x");
				System.out.printf("%X",is.read());
        long endTime = System.currentTimeMillis();

				for (int l = 0; l < 3; l++) {
					System.out.printf("%X", is.read());
				}
        
				averageTime += endTime - startTime;
				System.out.println("\nRTT: " + (endTime - startTime) + "ms\n");
		  }

      averageTime /= 12;
      System.out.print("Average RTT: ");
			System.out.printf("%.2f", averageTime);
      System.out.println("ms");
	}

  public static short checksum(byte[] b) {
		long sum = 0;
		int i = 0;
		int length = b.length;
		while(length > 1) {
			sum += ((b[i] << 8) & 0xFF00 | ((b[i + 1]) & 0xFF));

			if((sum & 0xFFFF0000) > 0) {
					sum = sum & 0xFFFF;
					sum++;
			}
			i += 2;
			length -= 2;
		}

		if(length > 0) {
			sum += (b[i] << 8 & 0xFF00);
			if((sum & 0xFFFF0000) > 0) {
				sum = sum & 0xFFFF;
				sum++;
			}
		}
		return (short)(~sum & 0xFFFF);
	}
}
