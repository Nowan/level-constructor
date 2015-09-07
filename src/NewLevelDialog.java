import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class NewLevelDialog extends JDialog{

	private static final long serialVersionUID = 1526624959264387028L;
	
	public NewLevelDialog(){
		super(ConstructorWindow.instance, "Prefab manager");
		this.setModal(true);
		setSize(300,300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(ConstructorWindow.instance);
		setLayout(new BorderLayout());
		setResizable(false);
		
		add(generateContent());
		setVisible(true);
	}
	
	private JPanel generateContent(){
		JPanel panel = new JPanel();
		return panel;
	}
	
}
