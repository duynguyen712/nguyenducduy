package main;

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

import org.apache.log4j.BasicConfigurator;

import data.Person;
import helper.XMLConvert;

public class QueueSender {
	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		Properties settings = new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
		
		Context ctx = new InitialContext(settings);
		
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		
		Destination destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");
		
		Connection con = factory.createConnection("admin", "admin");
		
		con.start();
		
		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		MessageProducer producer = session.createProducer(destination);
		
		Message msg = session.createTextMessage("Hello mesage from ActiveMQ");
		producer.send(msg);
		Scanner scan = new Scanner(System.in);
		try {
			while (true) {
				String name = scan.nextLine();
				Person p = new Person(1001, name, new Date());
				String xml = new XMLConvert<Person>(p).object2XML(p);
				msg = session.createTextMessage(xml);
				producer.send(msg);
			}
		} finally {
			session.close();
			con.close();
			System.out.println("Finished...");
		}
	}
}
