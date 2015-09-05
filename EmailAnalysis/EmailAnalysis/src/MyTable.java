import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;


public class MyTable extends JTable {
	 /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public MyTable()
    {
        
    }
    public MyTable(Vector rowData ,Vector columnNames)
    {
        super(rowData,columnNames);
    }
    
    /**
     * @Override
     */
    public JTableHeader getTableHeader()
    {
        JTableHeader tableHeader = super.getTableHeader();
        tableHeader.setReorderingAllowed(false);   //���ñ���в�������
        DefaultTableCellRenderer hr =(DefaultTableCellRenderer)tableHeader.getDefaultRenderer();  //��ñ��ͷ�ĵ�Ԫ�����
        hr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);  //��������
        return tableHeader;
        
    }
    /**
     * @Override
     */
    public TableCellRenderer getDefaultRenderer(Class<?>columnClass)
    {
        DefaultTableCellRenderer cr =(DefaultTableCellRenderer)super.getDefaultRenderer(columnClass);
        cr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);  //��Ԫ�����ݾ���
        return cr;
    }
    /**
     * @Override
     */
    public boolean isCellEditable(int row,int column)
    {
        return false;   //��Ԫ�񲻿��޸�
    }
}
