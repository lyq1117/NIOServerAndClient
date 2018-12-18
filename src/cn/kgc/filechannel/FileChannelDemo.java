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
			// 准备缓冲区
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			// 文件输入字节流
			fis = new FileInputStream(file);
			// 文件输出字节流，追加写
			fos = new FileOutputStream(file, true);
			// 获取输入通道
			inChannel = fis.getChannel();
			// 获取输出通道
			outChannel = fos.getChannel();
			
			// 读取数据
			buffer.clear();
			int len = inChannel.read(buffer);
			System.out.println(len);
			System.out.println(new String(buffer.array(), 0, len));
			
			// 写数据
			ByteBuffer buffer2 = ByteBuffer.wrap("add...".getBytes());
			outChannel.write(buffer2);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭资源
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
