package com.athena.mail.client;
 
import com.athena.mail.props.AuthenticatorGenerator;
import com.athena.mail.props.HostType;
import com.athena.mail.receiver.MessageParser;
import com.athena.mail.receiver.SimpleMailReceiver;
 
/**
 * ” º˛≤‚ ‘¿‡
 * 
 * @author athena
 * 
 */
public class MailTest {
	
	/**
	 * @desc [huangjavamail@163.com,687291] [329429237@qq.com, huang687291]
	 * @param userName
	 * @param password
	 */
    public void test(String userName, String password, HostType host) throws Exception{
    	try {
    		MessageParser.parse(SimpleMailReceiver.fetchInbox(host.getProperties(),
            		AuthenticatorGenerator.getAuthenticator(userName, password)
            		));
    	}
    	catch (Exception ex) {
    		throw new Exception(ex.getMessage());
    	}
    }
}