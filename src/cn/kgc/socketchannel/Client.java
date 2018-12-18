package cn.kgc.socketchannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Client {
	
	private SocketChannel socketChannel = null;// �ͻ���socketͨ��
	private ByteBuffer outBuffer = ByteBuffer.allocate(1024);// ���ڶ��Ļ�����
	private ByteBuffer inBuffer = ByteBuffer.allocate(1024);// ����д�Ļ�����
	private Selector selector = null;
	private int keys = 0;
	
	public void initClient() throws IOException {
		// �򿪿ͻ���SocketChannel
		socketChannel = SocketChannel.open();
		// ��ѡ����
		selector = Selector.open();
		// ���ÿͻ���SocketChannelΪ������ʽ
		socketChannel.configureBlocking(false);
		// ���ӷ�����
		socketChannel.connect(new InetSocketAddress("localhost", 6666));
		// ע��ͻ���SocketChannel��selector
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
	}
	
	/**
	 * ����ͨ���Ͻ���ע����¼�
	 * @throws IOException 
	 */
	private void listen() throws IOException {
		// ��ѯ
		while(true) {
			keys = selector.select();
			// ����ѡ��ļ�
			if(keys>0) {
				// ����ѡ��ļ�
				Iterator iterator = selector.selectedKeys().iterator();
				
				while(iterator.hasNext()) {
					SelectionKey key = (SelectionKey) iterator.next();
					// �������ӷ������¼�
					if(key.isConnectable()) {
						// ��ȡ�������������ͨ��
						SocketChannel channel = (SocketChannel) key.channel();
						// ����������ӣ����������
						if(channel.isConnectionPending()) {
							channel.finishConnect();
							System.out.println("������ӣ�");
						}
						// ע��һ���������д���¼�
						channel.register(selector, SelectionKey.OP_WRITE);
					}
					// �����������д��Ϣ�¼�
					else if(key.isWritable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						outBuffer.clear();
						System.out.println("�ͻ�������д����...");
						channel.write(outBuffer.wrap("hello server!".getBytes()));
						// ע��һ������������Ϣ�¼�
						channel.register(selector, SelectionKey.OP_READ);
						System.out.println("�ͻ���д�������...");
					}
					// ���Ǵӷ�������ȡ��Ϣ�¼�
					else if(key.isReadable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						inBuffer.clear();
						System.out.println("�ͻ������ڶ�����...");
						int len = channel.read(inBuffer);
						String str = "������������Ϣ==>" + new String(inBuffer.array(), 0, len);
						System.out.println(str);
						System.out.println("�ͻ��˶��������...");
					}
				}
				
			}
			/*else {
				System.out.println("û���ҵ�����Ȥ���¼�");
			}*/
		}
	}
	
	public void start() {
		try {
			// ��ʼ���ͻ���socket
			initClient();
			// ����
			listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	


	public static void main(String[] args) {
		new Client().start();
	}
	
}
