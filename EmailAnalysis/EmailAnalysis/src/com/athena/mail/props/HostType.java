package com.athena.mail.props;
 
import java.util.Properties;
 
/**
 * ���������ࣺ�ṩ�����׺���Ѷ����ҵ����(�������Ѿ�����ͨ��)���͹ȸ裨����δͨ���� ��������Ҫ������չ
 * 
 * @author athena
 */
public enum HostType {
 
    NETEASE163 {
        @Override
        public Properties getProperties() {
            Properties defaults = new Properties();
            defaults.put("mail.pop3.host", "pop.163.com");
            defaults.put("mail.imap.host", "imap.163.com");
            defaults.put("mail.store.protocol", "pop3"); // Ĭ��ʹ��pop3��ȡ�ʼ�
            return defaults;
        }
 
    },
    NETEASE126 {
        @Override
        public Properties getProperties() {
            Properties defaults = new Properties();
            defaults.put("mail.pop3.host", "pop.126.com");
            defaults.put("mail.imap.host", "imap.126.com");
            defaults.put("mail.store.protocol", "pop3"); // Ĭ��ʹ��pop3��ȡ�ʼ�
            return defaults;
        }
 
    },
    TENCENT {
        @Override
        public Properties getProperties() {
            Properties defaults = new Properties();
            defaults.put("mail.pop3.host", "pop.exmail.qq.com");
            defaults.put("mail.imap.host", "imap.exmail.qq.com");
            defaults.put("mail.store.protocol", "pop3"); // Ĭ��ʹ��pop3��ȡ�ʼ�
            return defaults;
        }
    },
    GMAIL {
 
        @Override
        public Properties getProperties() {
            Properties defaults = new Properties();
            defaults.put("mail.pop3.host", "pop.gmail.com");
            defaults.put("mail.pop3.port", "995");
            defaults.put("mail.imap.host", "imap.gmail.com");
            defaults.put("mail.imap.port", "465");
            defaults.put("mail.store.protocol", "pop3"); // Ĭ��ʹ��pop3��ȡ�ʼ�
            return defaults;
        }
 
    };
 
    public abstract Properties getProperties();
 
}