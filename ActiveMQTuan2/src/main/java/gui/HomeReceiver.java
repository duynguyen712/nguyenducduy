package gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.BasicConfigurator;

public class HomeReceiver extends JFrame implements ActionListener {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtField;
	private JTextArea txtArea;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					HomeReceiver frame = new HomeReceiver();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public HomeReceiver() {
		setResizable(false);
		setAlwaysOnTop(true);
		getContentPane().setLayout(null);
		setSize(650, 550);
		setTitle("DuyHAHAHA");
		txtArea = new JTextArea();
		txtArea.setEditable(false);
		txtArea.setBounds(20, 41, 589, 386);
		getContentPane().add(txtArea);
		
		txtField = new JTextField();
		txtField.setBounds(20, 437, 500, 39);
		getContentPane().add(txtField);
		txtField.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnSend.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		btnSend.setBounds(524, 437, 85, 39);
		getContentPane().add(btnSend);
		btnSend.addActionListener(this);
		try {
			receiver();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void receiver() throws Exception {
		BasicConfigurator.configure();
		
		Properties settings = new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
		
		Context ctx = new InitialContext(settings);
		
		Object obj = ctx.lookup("ConnectionFactory");
		ConnectionFactory factory = (ConnectionFactory) obj;
		
		Destination destination = (Destination) ctx.lookup("dynamicQueues/nguyenduy");
		
		Connection con = factory.createConnection("admin", "admin");
		
		con.start();
		
		Session session = con.createSession(false, Session.CLIENT_ACKNOWLEDGE);
		
		MessageConsumer receiver = session.createConsumer(destination);
		
		System.out.println("Ty was listened on queue...");
		receiver.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message msg) {
				try {
					if (msg instanceof TextMessage) {
						TextMessage tm = (TextMessage) msg;
						String txt = tm.getText();
						System.out.println("Nhan duoc: " + txt);
						
						int indexStart = txt.indexOf("<hoten>");
						int indexEnd = txt.indexOf("</hoten>");
						int indexMSStart = txt.indexOf("<mssv>");
						int indexMSEnd = txt.indexOf("</mssv>");
						System.out.println("index " + indexMSStart);
						System.out.println(txt);
						String textMS = txt.substring(indexMSStart, indexMSEnd);
						String text = txt.substring(indexStart + 6, indexEnd);
						text.replaceAll("<hoten>", "");
						textMS.replaceAll("<mssv>", "");
						if (txtArea.getText().indexOf(textMS) == -1) {
							txtArea.append("\nMssv: " + textMS);
						}
						txtArea.append("\nContent: " + text);
						
						msg.acknowledge();
					} else if (msg instanceof ObjectMessage) {
						ObjectMessage om = (ObjectMessage) msg;
						System.out.println("om");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
