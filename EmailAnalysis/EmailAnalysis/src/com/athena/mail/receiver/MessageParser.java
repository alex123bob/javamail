package com.athena.mail.receiver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageParser;

import com.javamail.mysql.mysql;

import java.lang.reflect.*;

/**
 * �ʼ�������
 * 
 * @author athena
 * 
 */
public class MessageParser {
	/**
	 * �ʼ��������λ��
	 */
	private static final String folder = "D:\\upload";
	
	private static String addSlashes (String input) {
		if (input == null) {
			input = "";
		}
		if (input.indexOf("'") >= 0) {
			input = input.replaceAll("'", "\\\\'");
		}
		if (input.indexOf("\"") >= 0) {
			input = input.replaceAll("\"", "\\\\\"");
		}
		return input;
	}

	private static void parse(Message message) {
		try {
			MimeMessageParser parser = new MimeMessageParser(
					(MimeMessage) message).parse();
			String from = parser.getFrom(); // ��ȡ�����˵�ַ
			List<Address> cc = parser.getCc();// ��ȡ�����˵�ַ
			List<Address> to = parser.getTo(); // ��ȡ�ռ��˵�ַ
			String replyTo = parser.getReplyTo();// ��ȡ�ظ��ʼ�ʱ���ռ���
			String subject = parser.getSubject(); // ��ȡ�ʼ�����
			String htmlContent = parser.getHtmlContent(); // ��ȡHtml����
			String plainContent = parser.getPlainContent(); // ��ȡ���ı��ʼ����ݣ�ע����Щ�ʼ���֧��html��
			java.util.Date sent = null;//��ȡ����ʱ��
			sent = message.getSentDate();
			java.util.Date received = message.getReceivedDate();  //��ȡ����ʱ��
			//System.out.println(received);
			//System.out.println(sent);
			// System.out.println(subject);
			// System.out.println(from);
			// System.out.println(replyTo);
			System.out.println(plainContent);

			// connectMysql(from, replyTo, subject);
			mysql mysqlObj = new mysql();
			SimpleDateFormat frm = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String sql = "insert into test(`from`, `to`,`sentDate`,`receivedDate`, `subject`,`content`) values('"
					+ addSlashes(from) + "', '" + addSlashes(to.get(0).toString()) + "', '" 
					+ addSlashes(sent == null ? "" : frm.format(sent).toString()) 
					+ "','" 
					+ addSlashes(received == null ? "" : frm.format(received).toString())
					+ "', '" 
					+ addSlashes(subject) + "', '" + addSlashes(plainContent) + "')";
			mysqlObj.insertData(sql);
			mysqlObj.deConnectMysql();

			List<DataSource> attachments = parser.getAttachmentList(); // ��ȡ��������д�����
			for (DataSource ds : attachments) {
				BufferedOutputStream outStream = null;
				BufferedInputStream ins = null;
				try {
					String fileName = folder + File.separator + ds.getName();
					outStream = new BufferedOutputStream(new FileOutputStream(
							fileName));
					ins = new BufferedInputStream(ds.getInputStream());
					byte[] data = new byte[2048];
					int length = -1;
					while ((length = ins.read(data)) != -1) {
						outStream.write(data, 0, length);
					}
					outStream.flush();
					System.out.println("����:" + fileName);
				} finally {
					if (ins != null) {
						ins.close();
					}
					if (outStream != null) {
						outStream.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void parse(Message... messages) throws Exception {
		java.util.Date date = new java.util.Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = sdf.format(date);
		
		Class EmailAnalysisClass = Class.forName("EmailAnalysis");
		Method func = EmailAnalysisClass.getDeclaredMethod("getUserName", String.class);
		Object obj = EmailAnalysisClass.newInstance();
		String userName = (String)func.invoke(obj);
		
		String sql = "delete from `test`";
		mysql mysqlObj = new mysql();
		mysqlObj.deleteData(sql);
		mysqlObj.deConnectMysql();
		if (messages == null) {
			throw new Exception("��¼ʧ�ܣ�");
		}
		else if (messages.length == 0) {
			System.out.println("û���κ��ʼ�");
		}
		else {
			for (Message m : messages) {
				parse(m);
			}
			// ���ر�����
			if (messages[0] != null) {
				Folder folder = messages[0].getFolder();
				if (folder != null) {
					try {
						Store store = folder.getStore();
						folder.close(false);// ����false��ʾ���ʼ����޸Ĳ����͵���������
						if (store != null) {
							store.close();
						}
					} catch (MessagingException e) {
						// ignore
					}
				}
			}
		}

	}
}