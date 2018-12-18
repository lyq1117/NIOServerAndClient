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
		// ��ѡ����
		selector = Selector.open();
		// �򿪷�����socketͨ��
		serverSocketChannel = ServerSocketChannel.open();
		// ������socketͨ���󶨶�Ӧ��ip���˿�
		serverSocketChannel.socket().bind(new InetSocketAddress("localhost", 6666));
		// ���÷�����socketͨ��Ϊ������ģʽ
		serverSocketChannel.configureBlocking(false);
		// �ѷ�����socketͨ��ע�ᵽselector�У����пͻ�����ʱ������ͨ��
		SelectionKey key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	/**
	 * ����selector
	 * @throws IOException
	 */
	public void listen() throws IOException {
		System.out.println("�������������ɹ���");
		while(true) {
			// ��ͨ��ѡ��������ѡ��һ��ͨ����select()��������������������select(����)�����ǲ�����
			// ָ��ʱ����û������Ļ���Ҳ�᷵�أ�Ϊ0
			keys = selector.select();
			Iterator iterator = selector.selectedKeys().iterator();
			if(keys > 0) {// ��ѡ�е�ͨ������������
				//������ѯ
				while(iterator.hasNext()) {
					SelectionKey key = (SelectionKey) iterator.next();
					iterator.remove();
					// ���ǿͻ������������¼�
					if(key.isAcceptable()) {
						System.out.println("һ���¿ͻ�������");
						// �Ȼ�ȡ������socketͨ��
						serverSocketChannel = (ServerSocketChannel) key.channel();
						// �ٻ�ȡ�ͻ���ͨ��
						SocketChannel socketChannel = serverSocketChannel.accept();
						// ���ÿͻ���ͨ��Ϊ������ʽ
						socketChannel.configureBlocking(false);
						// ���ͻ��˷�����Ϣ
						socketChannel.write(ByteBuffer.wrap("hello client!".getBytes()));
						// �ٰѿͻ���ͨ��Ҳע�ᵽselector�У�����ȡ�ͻ��˷�������Ϣ
						socketChannel.register(selector, SelectionKey.OP_READ);
					}
					// ���Ƕ�ȡ�¼�
					else if(key.isReadable()) {
						read(key);
					}
					
				}
			}
			
		}
	}
	
	/**
	 * ��ȡ��Ϣ
	 * @param key
	 * @throws IOException 
	 */
	private void read(SelectionKey key) throws IOException {
		// ͨ��key��ȡ�ͻ���socketͨ��
		SocketChannel socketChannel = (SocketChannel) key.channel();
		// ����������
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int len = socketChannel.read(buffer);
		if(len > 0) {
			String msg = "�ͻ��˷�������Ϣ��" + new String(buffer.array(), 0, len);
			System.out.println(msg);
		}else {
			System.out.println("�ͻ��˹ر�");
			key.cancel();
		}
	}

	public void start() {
		try {
			// ��ʼ��������socketͨ��
			initServer();
			// ����
			listen();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public static void main(String[] args) {
		new Server().start();
	}

}
