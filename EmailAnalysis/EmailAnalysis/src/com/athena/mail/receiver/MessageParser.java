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
 * 邮件解析类
 * 
 * @author athena
 * 
 */
public class MessageParser {
	/**
	 * 邮件附件存放位置
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
			String from = parser.getFrom(); // 获取发件人地址
			List<Address> cc = parser.getCc();// 获取抄送人地址
			List<Address> to = parser.getTo(); // 获取收件人地址
			String replyTo = parser.getReplyTo();// 获取回复邮件时的收件人
			String subject = parser.getSubject(); // 获取邮件主题
			String htmlContent = parser.getHtmlContent(); // 获取Html内容
			String plainContent = parser.getPlainContent(); // 获取纯文本邮件内容（注：有些邮件不支持html）
			java.util.Date sent = null;//获取发件时间
			sent = message.getSentDate();
			java.util.Date received = message.getReceivedDate();  //获取接收时间
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

			List<DataSource> attachments = parser.getAttachmentList(); // 获取附件，并写入磁盘
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
					System.out.println("附件:" + fileName);
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
			throw new Exception("登录失败！");
		}
		else if (messages.length == 0) {
			System.out.println("没有任何邮件");
		}
		else {
			for (Message m : messages) {
				parse(m);
			}
			// 最后关闭连接
			if (messages[0] != null) {
				Folder folder = messages[0].getFolder();
				if (folder != null) {
					try {
						Store store = folder.getStore();
						folder.close(false);// 参数false表示对邮件的修改不传送到服务器上
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