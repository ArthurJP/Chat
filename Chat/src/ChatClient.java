import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


public class ChatClient extends Frame{
	Socket s=null;
	DataOutputStream dos=null;
	DataInputStream dis=null;
	private boolean beConnected=false;
	
	TextField tfTxt=new TextField();
	TextArea taContent=new TextArea();
	
	/*
	 * 为了控制线程的关闭，故将线程设置为独立的属性
	 */
	Thread tRecv=new Thread(new RecvThread());

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ChatClient().launchFrame();
	}
	
	public void launchFrame() {
		this.setLocation(200,100);
		this.setSize(550,500);
		add(tfTxt,BorderLayout.SOUTH);
		add(taContent,BorderLayout.NORTH);
		pack();//调整此窗口的大小，以适合其子组件的首选大小和布局。
		
		this.addWindowListener(new WindowAdapter(){

			@Override
			public void windowClosing(WindowEvent e) {
				disconnect();
				System.exit(0);
			}
			
		});
		
		tfTxt.addActionListener(new TFListener());
		
		setVisible(true);
		connect();
		tRecv.start();
	}
	
	public void connect(){
		try{
			s=new Socket("127.0.0.1",8888);
			dos=new DataOutputStream(s.getOutputStream());
			dis=new DataInputStream(s.getInputStream());
System.out.println("connected");
			beConnected=true;
		}catch(UnknownHostException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void disconnect(){
		try {
			beConnected=false;
			dis.close();
			dos.close();
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		try {
			beConnected =false;//关闭线程
		 	
		 	// 将线程合并，防止并发时tRecv在执行readUTF()阻塞，而主线程关闭stream
			tRecv.join();
			
		}catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				dis.close();
				dos.close();
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
	}
	
//	实现对textField的监听
	private class TFListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String str=tfTxt.getText().trim();
//			taContent.setText(str);
			tfTxt.setText("");
			
			try{
				dos.writeUTF(str);
				dos.flush();
				//dos.close();//此处关闭会导致只能进行一次IO传输
			}catch(IOException e1){
				e1.printStackTrace();
			}
		}
		
	}

	private class RecvThread implements Runnable{

		@Override
		public void run() {
			try {
				while(beConnected){
					String str=dis.readUTF();
//					System.out.println(str);
					taContent.setText(taContent.getText()+'\n'+str);
				}
			}catch(EOFException e){
				System.out.println("Server closed!");
			}catch(SocketException e){
				System.out.println("Client quit");
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}

}


/*
 * 可以改进的点：
 * Client退出机制：Client退出时发送消息，Server编写监听线程，移除已经推出的client
 * 命令端口和信息传输端口区分
 * 聊天系统里面的名字问题
 */
