package cn.kgc.filechannel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelDemo {
	
	public static void main(String[] args) {
		testFileChannelDemo();
	}
	
	public static void testFileChannelDemo() {
		File file = new File("c:/Users/Administrator/Desktop/abc.txt");
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		try {
			// ׼��������
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			// �ļ������ֽ���
			fis = new FileInputStream(file);
			// �ļ�����ֽ�����׷��д
			fos = new FileOutputStream(file, true);
			// ��ȡ����ͨ��
			inChannel = fis.getChannel();
			// ��ȡ���ͨ��
			outChannel = fos.getChannel();
			
			// ��ȡ����
			buffer.clear();
			int len = inChannel.read(buffer);
			System.out.println(len);
			System.out.println(new String(buffer.array(), 0, len));
			
			// д����
			ByteBuffer buffer2 = ByteBuffer.wrap("add...".getBytes());
			outChannel.write(buffer2);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// �ر���Դ
			try {
				outChannel.close();
				inChannel.close();
				fos.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
