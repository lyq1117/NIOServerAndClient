package cn.kgc.socketchannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {
	
	private Selector selector = null;
	private ServerSocketChannel serverSocketChannel = null;
	private int keys = 0;
	
	public void initServer() throws IOException {
		// 打开选择器
		selector = Selector.open();
		// 打开服务器socket通道
		serverSocketChannel = ServerSocketChannel.open();
		// 服务器socket通道绑定对应的ip、端口
		serverSocketChannel.socket().bind(new InetSocketAddress("localhost", 6666));
		// 设置服务器socket通道为非阻塞模式
		serverSocketChannel.configureBlocking(false);
		// 把服务器socket通道注册到selector中，当有客户连接时除法该通道
		SelectionKey key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	/**
	 * 监听selector
	 * @throws IOException
	 */
	public void listen() throws IOException {
		System.out.println("服务器已启动成功！");
		while(true) {
			// 让通道选择器至少选择一个通道，select()是阻塞方法，带参数的select(毫秒)可以是不阻塞
			// 指定时间内没有请求的话，也会返回，为0
			keys = selector.select();
			Iterator iterator = selector.selectedKeys().iterator();
			if(keys > 0) {// 有选中的通道，即有请求
				//进行轮询
				while(iterator.hasNext()) {
					SelectionKey key = (SelectionKey) iterator.next();
					iterator.remove();
					// 若是客户端请求连接事件
					if(key.isAcceptable()) {
						System.out.println("一个新客户端连接");
						// 先获取服务区socket通道
						serverSocketChannel = (ServerSocketChannel) key.channel();
						// 再获取客户端通道
						SocketChannel socketChannel = serverSocketChannel.accept();
						// 设置客户端通道为非阻塞式
						socketChannel.configureBlocking(false);
						// 给客户端发送消息
						socketChannel.write(ByteBuffer.wrap("hello client!".getBytes()));
						// 再把客户端通道也注册到selector中，来读取客户端发来的消息
						socketChannel.register(selector, SelectionKey.OP_READ);
					}
					// 若是读取事件
					else if(key.isReadable()) {
						read(key);
					}
					
				}
			}
			
		}
	}
	
	/**
	 * 读取消息
	 * @param key
	 * @throws IOException 
	 */
	private void read(SelectionKey key) throws IOException {
		// 通过key读取客户端socket通道
		SocketChannel socketChannel = (SocketChannel) key.channel();
		// 创建缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int len = socketChannel.read(buffer);
		if(len > 0) {
			String msg = "客户端发来的消息：" + new String(buffer.array(), 0, len);
			System.out.println(msg);
		}else {
			System.out.println("客户端关闭");
			key.cancel();
		}
	}

	public void start() {
		try {
			// 初始化服务器socket通道
			initServer();
			// 监听
			listen();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public static void main(String[] args) {
		new Server().start();
	}

}
