import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	boolean started=false;
	ServerSocket ss=null;
	
//	����ͻ�����Ϣ
	List<Client> clientsList=new ArrayList<Client>();
	
	public static void main(String[] args) {
		new ChatServer().start();
	}
	
	public void start(){
		try{
			ss=new ServerSocket(8888);
			started=true;
		}catch(BindException e){
			System.out.println("�˿ڱ�ռ��...");
			System.out.println("��ر���س��򣬲�����������");
			System.exit(0);
		}catch(IOException e){
			e.printStackTrace();
		}
		
		try{
			while(started){
				
				Socket s=ss.accept();
				Client c=new Client(s);
System.out.println("a client connected!");
				new Thread(c).start();
				clientsList.add(c);
//				dis.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try {
				ss.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	class Client implements Runnable{
		private Socket s;
		private DataInputStream dis=null;
		private DataOutputStream dos=null;
		private boolean beConnected=false;
		Client c=null;
		/*
		 * beConnected���ƿͻ����Ƿ�����
		 */
		
		public Client(Socket s){
			this.s=s;
			try {
				dis=new DataInputStream(s.getInputStream());
				dos=new DataOutputStream(s.getOutputStream());
				beConnected = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void send(String str){
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				/*
				 * ��ֹ��;�пͻ����˳�������socket�쳣
				 */
				clientsList.remove(this);
				
				System.out.println("a client quit. remove from list");
			}
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				while(beConnected){
					String str=dis.readUTF();
System.out.println(str);
					/*
					 * �˴���ѡ��iterator,��Ϊ�÷����ڲ�������Ч�ʽϵ�
					 * ���������ڲ���
					 */
//					for(Iterator<Client> it=clientsList.iterator();it.hasNext();){
//						Client c=it.next();
//						c.send(str);
//					}
//��������
//					Iterator<Client> it=clientsList.iterator();
//					while(it.hasNext()){
//						Client c=it.next();
//						c.send(str);
//					}
					for(int i=0;i<clientsList.size();i++){
						c=clientsList.get(i);
						c.send(str);
					}
				}
			}catch(EOFException e){
				System.out.println("client closed!");
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				beConnected = false;
				try {
					if(dis!=null) dis.close();
					if(dos!=null) dos.close();
					if(s!=null) {
						s.close();
						s=null;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
	}
}
