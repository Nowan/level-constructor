import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;


public class CollectionsPanel extends JTabbedPane{
	
	public TilesTab tilesTab;
	
	public CollectionsPanel(){
		setPreferredSize(new Dimension(350,680));
		setSize(getPreferredSize());
		setTabPlacement(JTabbedPane.RIGHT);
		setFont(new Font("Liberation Serif", Font.PLAIN,13));

		//initialize content for tileTab
		tilesTab = new TilesTab();
		addTab("<html><body><div style=\"height: 150; padding-top: 35; text-align: center\">T<br>i<br>l<br>e<br>s</div></body></html>",null , tilesTab, "Tile placement tools");
		setMnemonicAt(0, KeyEvent.VK_1);
		
		JPanel panel2 = new JPanel();
		addTab("<html><body><div style=\"height: 150; padding-top: 5; text-align: center\">P<br>a<br>r<br>t<br>i<br>c<br>l<br>e<br>s</div></body></html>", null, panel2, "...");
		setMnemonicAt(1, KeyEvent.VK_2);
		
		JPanel panel3 = new JPanel();
		addTab("<html><body><div style=\"height: 150; padding-top: 0; text-align: center\">B<br>a<br>c<br>k<br>g<br>r<br>o<br>u<br>n<br>d<br>s</div></body></html>", null, panel3, "...");
		setMnemonicAt(1, KeyEvent.VK_3);
		
		JPanel panel4 = new JPanel();
		addTab("<html><body><div style=\"height: 150; padding-top: 20; text-align: center\">C<br>a<br>m<br>e<br>r<br>a<br>s</div></body></html>", null, panel4, "...");
		setMnemonicAt(2, KeyEvent.VK_4);
	}
	
	@Override 
	public void setEnabled(boolean arg0){
		super.setEnabled(arg0);
		tilesTab.setEnabled(arg0);
	}

}
