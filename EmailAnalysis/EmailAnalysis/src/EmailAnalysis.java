import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import com.athena.mail.client.MailObj;
import com.athena.mail.client.MailTest;
import com.athena.mail.props.HostType;
import com.javamail.mysql.mysql;

public class EmailAnalysis extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTabbedPane jTabbedPane = null;
	private JTable table = null;

	private JFrame frame = null;
	private static EmailAnalysis currentObj = null;

	private JTextField jTextFieldUser = null;
	private JTextField jTextFieldPassword = null;
	private JButton jButtonLogin = null;
	private JButton jButtonReset = null;
	
	public static String getUserName (){
		return currentObj.jTextFieldUser.getText().trim();
	}
	
	/**
	 * This is the default constructor
	 */
	public EmailAnalysis() {
		super();
		initialize();
		this.setTitle(" Email 社群分析 ");
	}
	
	public enum MyRelationshipTypes implements RelationshipType{
        RECEIVEFROM
    }

	// 模板
	static void addItem(JTabbedPane tabbedPane, String text) {
		JLabel label = new JLabel(text);

		JPanel panel = new JPanel();
		panel.add(label);

		tabbedPane.addTab(text, panel);
		tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, null);
	}

	// 邮箱界面显示表格
	static void addItem1(JTabbedPane tabbedPane, String text, final EmailAnalysis frame, String userName, String pwd) {
		
		ArrayList<MailObj> mailArr = new ArrayList<MailObj>();
		/* 
		 	{ 
		 		{ " 阿 呆 ", "xx", "oo", "dd" },
			 	{ " 阿 瓜 ", "xx", "oo", "dd" } 
			 };
		*/
		mysql objMysql = null;
		try {
			objMysql = new mysql();
			String sql = "select * from `test` where `to` like '%" + userName + "%'";
			ResultSet rs = objMysql.getData(sql);

			while (rs.next()) {
				mailArr.add(new MailObj(rs.getString(1), rs.getString(2), rs.getString(3),
						rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)));
			}
		} catch (SQLException ex) {
			System.err.println("sqlexception :" + ex.getMessage());
		}
		
		GraphDatabaseService graphDb=new EmbeddedGraphDatabase("var/base");
		Transaction tx=graphDb.beginTx();
		try{
			// This is the user who logged in
            Node centralNode=graphDb.createNode();
            centralNode.setProperty("name", "User");
            Hashtable<String, Integer> indicator = new Hashtable<String, Integer>();
            for (int i = 0; i < mailArr.size(); i++) {
            	if (indicator.get(mailArr.get(i).senderAddress) != null) {
            		indicator.put(indicator.get(mailArr.get(i).senderAddress).toString(), 
            				indicator.get(mailArr.get(i).senderAddress) + 1);
            	}
            	else {
            		indicator.put(mailArr.get(i).senderAddress, 1);
            	}
            }
            
            for (Iterator itr = indicator.keySet().iterator(); itr.hasNext();) {
            	String key = (String)itr.next();
            	String value = indicator.get(key).toString();
            	Node subNode = graphDb.createNode();
            	subNode.setProperty("senderAddress", key);
            	Relationship mailRelation = subNode.createRelationshipTo(centralNode, MyRelationshipTypes.RECEIVEFROM);
            	mailRelation.setProperty("count", value);
            }
            
            tx.success();
            
            System.out.print(centralNode.getProperty("name"));
            
        }
        finally{
            tx.finish();
            graphDb.shutdown();
        }
        
        Object mailInfo[][] = new Object[mailArr.size()][7];
        for (int i = 0; i < mailArr.size(); i++) {
        	mailInfo[i][0] = mailArr.get(i).id;
        	mailInfo[i][1] = mailArr.get(i).senderAddress;
        	mailInfo[i][2] = mailArr.get(i).receiverAddress;
        	mailInfo[i][3] = mailArr.get(i).sentTime;
        	mailInfo[i][4] = mailArr.get(i).receivedTime;
        	mailInfo[i][5] = mailArr.get(i).mailSubject;
        	mailInfo[i][6] = mailArr.get(i).mailContent;
        }
		String[] Names = {"编号", "发件人地址", "收件人地址", "发件时间", "收件时间","邮件主题","邮件正文" };
		DefaultTableModel dtm = new DefaultTableModel(mailInfo, Names);
		final JTable table = new JTable(dtm);
		table.setPreferredScrollableViewportSize(new Dimension(550, 30)); // 设置此表视口的首选大小
		table.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e)) {
					PopUpDemo menu = new PopUpDemo(frame, table);
			        menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		JScrollPane scrollPane = new JScrollPane(table);

		tabbedPane.addTab(text, scrollPane);
		tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, null);
		try {
			objMysql.stmt.close();
		}
		catch (SQLException ex) {
			System.err.println("sqlexception :" + ex.getMessage());
		}

	}

	// 登陆界面显示登录
	static void addItem0(JTabbedPane tabbedPane, String text, EmailAnalysis obj) {
		JPanel jpanel = new JPanel();
		jpanel.setLayout(null);
		jpanel.setSize(new Dimension(175, 144));
		JLabel ID = new JLabel();
		ID.setText(" 帐号 ");
		ID.setBounds(5, 7, 32, 18);
		JLabel passwd = new JLabel();
		passwd.setText(" 密码 ");
		passwd.setBounds(4, 29, 32, 18);
		jpanel.add(ID, null);
		JTextField TID = new JTextField();
		TID.setBounds(40, 4, 114, 22);
		// attaches user name component to current object's property.
		obj.jTextFieldUser = TID;
		jpanel.add(passwd, null);
		JTextField Tpasswd = new JPasswordField();
		Tpasswd.setBounds(new Rectangle(40, 29, 115, 22));
		// attaches password component to current object's property.
		obj.jTextFieldPassword = Tpasswd;
		jpanel.add(TID, null);
		jpanel.add(Tpasswd, null);
		jpanel.add(obj.getJButtonLogin(), null);
		jpanel.add(obj.getJButtonReset(), null);

		tabbedPane.addTab(text, jpanel);
		tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, null);

	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(426, 288);
		JTabbedPane tabbedPane = getJTabbedPane();
		this.setContentPane(tabbedPane);
		addItem0(tabbedPane, "登录", this);
//		addItem1(tabbedPane, "邮箱", this);
		addItem(tabbedPane, "可视化");
//		tabbedPane.setEnabledAt(1, false);
//		tabbedPane.setEnabledAt(2, false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("E-mail社群分析");
		currentObj = this;
	}

	/**
	 * This method initializes jTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes jButtonLogin
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonLogin() {
		if (jButtonLogin == null) {
			jButtonLogin = new JButton();
			jButtonLogin.setBounds(new Rectangle(22, 93, 64, 23));
			jButtonLogin.setText("登录");
			final JTextField userField = this.jTextFieldUser;
			final JTextField passField = this.jTextFieldPassword;
			final JTabbedPane tabbedPane = this.jTabbedPane;
			jButtonLogin.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String userName = userField.getText().trim();
					int startIndex = userName.indexOf("@") + 1;
					int endIndex = userName.indexOf(".");
					HostType host = HostType.NETEASE163;  // default host type
					if (-1 != startIndex && -1 != endIndex) {
						String suffix = userName.substring(startIndex, endIndex);
						if (suffix.equalsIgnoreCase("126")) {
							host = HostType.NETEASE126;
						}
						else if (suffix.equalsIgnoreCase("163")) {
							host = HostType.NETEASE163;
						}
						else if (suffix.equalsIgnoreCase("qq")) {
							host = HostType.TENCENT;
						}
						else if (suffix.equalsIgnoreCase("gmail")) {
							host = HostType.GMAIL;
						}
					}
					String pwd = passField.getText().trim();
					//用户名为空时提示用户输入  
	                if (userName.equals("")) {  
	                    JOptionPane.showMessageDialog(EmailAnalysis.this, "请输入用户名");  
	                    return;  
	                }
	                else if (pwd.equals("")) {
	                	JOptionPane.showMessageDialog(EmailAnalysis.this, "请输入密码");  
	                    return; 
	                }
	                else {
//	                	// connectMysql(from, replyTo, subject);
//	                	mysql mysqlObj = new mysql();
//	                	String sql = "select * from `test` where `name` = '" + userName + "' and `password` = '"
//	                				+ pwd + "'";
//	                	ResultSet rs = mysqlObj.getData(sql);
//	                	int rowCount = 0;    
//	                	try {
//	                		while(rs.next())    
//		                	{    
//		                	    rowCount++;    
//		                	}
//	                		if (rowCount > 0) {
//	                			JOptionPane.showMessageDialog(EmailAnalysis.this, "登录成功！");
//	                			tabbedPane.setEnabledAt(1, true);
//	                			tabbedPane.setEnabledAt(2, true);
//	                		}
//	                		else {
//	                			JOptionPane.showMessageDialog(EmailAnalysis.this, "用户名或密码不正确！");
//	                		}
//	                	}
//	                	catch (SQLException ex) {
//	                		System.err.println("sqlexception :" + ex.getMessage());
//	                	}
//	                	
//	                	mysqlObj.deConnectMysql();
	                	try {
	                		MailTest mailTest = new MailTest();
		            		mailTest.test(userName, pwd, host);
//		            		tabbedPane.setEnabledAt(1, true);
//                			tabbedPane.setEnabledAt(2, true);
                			addItem1(tabbedPane, "邮箱", EmailAnalysis.this, userName, pwd);
	                	}
	                	catch (Exception ex) {
	                		JOptionPane.showMessageDialog(EmailAnalysis.this, ex.getMessage());  
	                	}
	                	
	                }
				}
			});
		}
		return jButtonLogin;
	}

	/**
	 * This method initializes jButtonReset
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonReset() {
		if (jButtonReset == null) {
			jButtonReset = new JButton();
			jButtonReset.setText("重置");
			jButtonReset.setBounds(new Rectangle(93, 93, 64, 23));
			final JTextField user = this.jTextFieldUser;
			final JTextField password = this.jTextFieldPassword;
			jButtonReset.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					user.setText("");
					password.setText("");
				}
			});
		}
		return jButtonReset;
	}

} // @jve:decl-index=0:visual-constraint="10,0"
