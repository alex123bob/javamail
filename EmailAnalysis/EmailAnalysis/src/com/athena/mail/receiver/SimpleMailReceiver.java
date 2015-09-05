package com.athena.mail.receiver;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
 
/**
 * �򵥵��ʼ�������
 * 
 * @author Cinderella Huang
 * 
 */
public class SimpleMailReceiver {
 
    /**
     * ��ȡ�ռ�������ʼ�
     * 
     * @param props
     *            Ϊ�ʼ���������ı�Ҫ����
     * @param authenticator
     *            �û���֤��
     * @return Message���飨�ʼ����飩
     */
    public static Message[] fetchInbox(Properties props, Authenticator authenticator) {
        return fetchInbox(props, authenticator, null);
    }
 
    /**
     * ��ȡ�ռ�������ʼ�
     * 
     * @param props
     *            ��ȡ�ռ�������ʼ�
     * @param authenticator
     *            �û���֤��
     * @param protocol
     *            ʹ�õ���ȡ�ʼ�Э�飬������ֵ"pop3"����"imap"
     * @return Message���飨�ʼ����飩
     */
    public static Message[] fetchInbox(Properties props, Authenticator authenticator, String protocol) {
        Message[] messages = null;
        Session session = Session.getDefaultInstance(props, authenticator);
        // session.setDebug(true);
        Store store = null;
        Folder folder = null;
        try {
            store = protocol == null || protocol.trim().length() == 0 ? session.getStore() : session.getStore(protocol);
            store.connect();
            folder = store.getFolder("INBOX");
//            folder = store.getDefaultFolder();// fetch inbox, sentbox, draftbox
//            Folder[] allFolder = folder.list();
//            for (Folder fd:allFolder) {
//            	System.out.println(fd.getName());
//            }
            folder.open(Folder.READ_ONLY); // ��ֻ����ʽ��
            messages = folder.getMessages();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return messages;
    }
}