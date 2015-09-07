import java.awt.Font;

//This class contains all global variables, which are needed in different parts of the program
public class Globals {
	
	public static final Font DEFAULT_FONT = new Font("Verdana", Font.PLAIN,11);
	public static final Font INDEX_FONT = new Font("Calibri", Font.BOLD, 26);
	public static final Font PARAMETER_FONT = new Font("Calibri", Font.PLAIN,13);
	
	//Class to work with all xml files
	public static final XMLConverter xmlConverter = new XMLConverter();
	
	//All game objects in the system
	public static final GOBase goBase = new GOBase();
	
	//All tools of the constructor - insertion tool, cleaner, inspector, etc.  
	public static final ToolBox toolBox = new ToolBox();
	
	//Level data
	public Level level;
	
	public static final String TEXTURES_FOLDER="bin/textures/raw/";
	
	public static final String ATLASES_FOLDER="bin/textures/";
}
