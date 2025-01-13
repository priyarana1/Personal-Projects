import javax.swing.JFrame; //window

public class App {
    public static void main(String[] args) throws Exception {
        
        //divide the game window into squares
        //0-20 rows & 0-18 columns --> 32 px per tile
        //width = 19 columns x 32 px
        //height = 21 rows x 32 px

        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame frame = new JFrame("Pac Man");
        //frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false); //dont want user to expand or decrease size of window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //terminate when user clicks on X

        PacMan pacmanGame = new PacMan(); //instance of jpanel
        frame.add(pacmanGame); //add jpanel to frame
        frame.pack(); //fullsize of jpanel on frame
        pacmanGame.requestFocus();
        frame.setVisible(true); //only make the frame visible once you added jpanel
        //once you run, the background is completely black




    }
}
