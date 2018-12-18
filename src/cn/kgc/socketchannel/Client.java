package cn.kgc.socketchannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Client {
	
	private SocketChannel socketChannel = null;// 客户端socket通道
	private ByteBuffer outBuffer = ByteBuffer.allocate(1024);// 用于读的缓冲区
	private ByteBuffer inBuffer = ByteBuffer.allocate(1024);// 用于写的缓冲区
	private Selector selector = null;
	private int keys = 0;
	
	public void initClient() throws IOException {
		// 打开客户端SocketChannel
		socketChannel = SocketChannel.open();
		// 打开选择器
		selector = Selector.open();
		// 设置客户端SocketChannel为非阻塞式
		socketChannel.configureBlocking(false);
		// 连接服务器
		socketChannel.connect(new InetSocketAddress("localhost", 6666));
		// 注册客户端SocketChannel到selector
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
	}
	
	/**
	 * 监听通道上进行注册的事件
	 * @throws IOException 
	 */
	private void listen() throws IOException {
		// 轮询
		while(true) {
			keys = selector.select();
			// 若有选择的键
			if(keys>0) {
				// 迭代选择的键
				Iterator iterator = selector.selectedKeys().iterator();
				
				while(iterator.hasNext()) {
					SelectionKey key = (SelectionKey) iterator.next();
					// 若是连接服务器事件
					if(key.isConnectable()) {
						// 获取与服务器相连的通道
						SocketChannel channel = (SocketChannel) key.channel();
						// 如果正在连接，就完成连接
						if(channel.isConnectionPending()) {
							channel.finishConnect();
							System.out.println("完成连接！");
						}
						// 注册一个向服务器写的事件
						channel.register(selector, SelectionKey.OP_WRITE);
					}
					// 若是向服务器写信息事件
					else if(key.isWritable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						outBuffer.clear();
						System.out.println("客户端正在写数据...");
						channel.write(outBuffer.wrap("hello server!".getBytes()));
						// 注册一个读服务器信息事件
						channel.register(selector, SelectionKey.OP_READ);
						System.out.println("客户端写数据完成...");
					}
					// 若是从服务器读取信息事件
					else if(key.isReadable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						inBuffer.clear();
						System.out.println("客户端正在读数据...");
						int len = channel.read(inBuffer);
						String str = "服务器发送信息==>" + new String(inBuffer.array(), 0, len);
						System.out.println(str);
						System.out.println("客户端读数据完成...");
					}
				}
				
			}
			/*else {
				System.out.println("没有找到感兴趣的事件");
			}*/
		}
	}
	
	public void start() {
		try {
			// 初始化客户端socket
			initClient();
			// 监听
			listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	


	public static void main(String[] args) {
		new Client().start();
	}
	
}
