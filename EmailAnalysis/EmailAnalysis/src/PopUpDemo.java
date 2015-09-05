import java.awt.event.ActionEvent;
import java.sql.ResultSet;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.javamail.mysql.mysql;


// 筛选
public class PopUpDemo extends JPopupMenu {
    JMenuItem anItem;
    public PopUpDemo(final EmailAnalysis frame, final JTable table){
        anItem = new JMenuItem(new AbstractAction("筛选") {
        	public void actionPerformed(ActionEvent e) {
        		JDialog win = new JDialog(frame, true);
        		win.setBounds(0, 0, 400, 400);
        		win.setLayout(null);
        		final JLabel sentLabel = new JLabel("发件人");
        		sentLabel.setBounds(4, 4, 40, 40);
        		final JTextField filterTextField = new JTextField();
        		filterTextField.setBounds(50, 4, 100, 40);
        		final JLabel receiLabel = new JLabel("收件人");
        		receiLabel.setBounds(4, 50, 40, 40);
        		final JTextField receiTextField = new JTextField();
        		receiTextField.setBounds(50, 50, 100, 40);
        		final JLabel themeLabel = new JLabel("主题");
        		themeLabel.setBounds(4, 96, 40, 40);
        		final JTextField themeTextField = new JTextField();
        		themeTextField.setBounds(50, 96, 100, 40);
        		JButton confirmBtn = new JButton();
        		confirmBtn.setBounds(106, 150, 80, 30);
        		confirmBtn.setText("确定");
        		JButton recovBtn = new JButton();
        		recovBtn.setBounds(200, 150, 80, 30);
        		recovBtn.setText("复原");
        		confirmBtn.addActionListener(new java.awt.event.ActionListener() {
    				public void actionPerformed(java.awt.event.ActionEvent e) {
    					String sent = filterTextField.getText();
    					String rece = receiTextField.getText();
    					String theme = themeTextField.getText();
    					String condition = new String("");
    					if ("".equals(sent) && "".equals(rece) && "".equals(theme)) {
    						JOptionPane.showMessageDialog(null, "至少填一项", "提示", JOptionPane.ERROR_MESSAGE);
    						return;
    					}
    					else {
    						if (!"".equals(sent)) {
    							condition += " `from` like '%" + sent + "%' ";
    						}
    						if (!"".equals(rece)) {
    							if (condition.isEmpty()) {
    								condition += " `to` like '%" + rece + "%' ";
    							}
    							else {
    								condition += " and `to` like '%" + rece + "%' ";
    							}
    						}
    						if (!"".equals(theme)) {
    							if (condition.isEmpty()) {
    								condition += " `subject` like '%" + theme + "%' ";
    							}
    							else {
    								condition += " and `subject` like '%" + theme + "%' ";
    							}
    						}
    					}
    					try {
    						mysql mysqlObj = new mysql();
    						
    						String sql = "select * from `test` where " + condition;
    						ResultSet rs  = mysqlObj.getData(sql);
    						Object mailInfo[][] = new Object[500][7];

							int i = 0;

							while (rs.next()) {
								mailInfo[i] = new Object[7];
								mailInfo[i][0] = rs.getString(1);
								mailInfo[i][1] = rs.getString(2);
								mailInfo[i][2] = rs.getString(3);
								mailInfo[i][3] = rs.getString(4);
								mailInfo[i][4] = rs.getString(5);
								mailInfo[i][5] = rs.getString(6);
								mailInfo[i][6] = rs.getString(7);
								i++;
							}
							
							int count = table.getRowCount();
							TableModel tm = table.getModel();
							DefaultTableModel dtm = (DefaultTableModel)tm;
							i = count - 1;
							for (; i >= 0; i--) {
								dtm.removeRow(i);
							}
							
							for (i = 0; i < 500; i++) {
								dtm.addRow(mailInfo[i]);
							}
    					}
    					catch (Exception ex) {
    						ex.printStackTrace();
    					}
    				}
    			});
        		
        		recovBtn.addActionListener(new java.awt.event.ActionListener() {
    				public void actionPerformed(java.awt.event.ActionEvent e) {
    					try {
    						mysql mysqlObj = new mysql();
    						
    						String sql = "select * from `test`";
    						ResultSet rs  = mysqlObj.getData(sql);
    						Object mailInfo[][] = new Object[500][7];

							int i = 0;

							while (rs.next()) {
								mailInfo[i] = new Object[7];
								mailInfo[i][0] = rs.getString(1);
								mailInfo[i][1] = rs.getString(2);
								mailInfo[i][2] = rs.getString(3);
								mailInfo[i][3] = rs.getString(4);
								mailInfo[i][4] = rs.getString(5);
								mailInfo[i][5] = rs.getString(6);
								mailInfo[i][6] = rs.getString(7);
								i++;
							}
							
							int count = table.getRowCount();
							TableModel tm = table.getModel();
							DefaultTableModel dtm = (DefaultTableModel)tm;
							i = count - 1;
							for (; i >= 0; i--) {
								dtm.removeRow(i);
							}
							
							for (i = 0; i < 500; i++) {
								dtm.addRow(mailInfo[i]);
							}
    					}
    					catch (Exception ex) {
    						ex.printStackTrace();
    					}
    				}
    			});
        		
        		win.add(sentLabel);
        		win.add(filterTextField);
        		win.add(receiLabel);
        		win.add(receiTextField);
        		win.add(themeLabel);
        		win.add(themeTextField);
        		win.add(confirmBtn);
        		win.add(recovBtn);
        		win.setVisible(true);
            }
        });
        add(anItem);
    }
}