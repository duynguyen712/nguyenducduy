package gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.BasicConfigurator;

import data.Person;
import helper.XMLConvert;

public class Home extends JFrame implements ActionListener {

	private JTextField txtField;
	private JTextArea txtArea;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					Home frame = new Home();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public Home() {
		setResizable(false);
		setAlwaysOnTop(true);
		getContentPane().setLayout(null);
		setSize(650,550);
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
		txtField.addActionListener(this);
	}
	
	public void send() throws Exception {
		BasicConfigurator.configure();
		Properties settings = new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
		
		Context ctx = new InitialContext(settings);
		
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		
		Destination destination = (Destination) ctx.lookup("dynamicQueues/nguyenduy");
		
		Connection con = factory.createConnection("admin", "admin");
		
		con.start();
		
		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		MessageProducer producer = session.createProducer(destination);
		
		Message msg = session.createTextMessage("Hello mesage from ActiveMQ");
		producer.send(msg);
		
		try {
			String name = txtField.getText();
			Person p = new Person(1001, name, new Date());
			String xml = new XMLConvert<Person>(p).object2XML(p);
			msg = session.createTextMessage(xml);
			producer.send(msg);
			txtField.setText("");
			txtArea.setText(txtArea.getText() + "\n" + name);
			System.out.println(name);
		} finally {
			session.close();
			con.close();
			System.out.println("Finished...");
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		try {
			send();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	
}
