import java.awt.*; //user interface 
import java.awt.event.*; //set of classes for creating GUIs (action)
import java.util.HashSet; //hash table for storage, stores unique elements
import java.util.Random; //random
import javax.swing.*; //provides set of components for GUIs

public class PacMan extends JPanel implements ActionListener, KeyListener {
    
    class Block { 
        int x; //xpos
        int y; //ypos
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U'; // U D L R
        double velocityX = 0;
        double velocityY = 0;



        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();

            //step forward
            this.x += this.velocityX; 
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX; 
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }


        void updateVelocity() { //updating attributes of each block


            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize/4; //up 8 pixels
            }
            else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize/4; //down 8 pixels
            }
            else if (this.direction == 'L') {
                this.velocityX = -tileSize/4; //left 8 pixels
                this.velocityY = 0;
            }
            else if (this.direction == 'R') {
                this.velocityX = tileSize/4; //right left pixels
                this.velocityY = 0;
            }


        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }


    //inheritance so PacMan class can inherit JPanel class and add additional attributes
        private int rowCount = 21;
        private int columnCount = 19;
        private int tileSize = 32;
        private int boardWidth = columnCount * tileSize;
        private int boardHeight = rowCount * tileSize;

        //load and save images for game
        //all these images are attributes for the pacman class
        private Image wallImage;
        private Image blueGhostImage;
        private Image orangeGhostImage;
        private Image pinkGhostImage;
        private Image redGhostImage;

        private Image pacmanUpImage;
        private Image pacmanDownImage;
        private Image pacmanLeftImage;
        private Image pacmanRightImage;



        //array of strings is 2D array of characters (each string is a row)
        //if character is X, that indicates wall
        //empty tiles = food
        //if character is O, it is empty
        //r = red, b = blue, o = orange, p = pink, P = PacMan

        private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX" 
        };

        HashSet <Block> walls;
        HashSet <Block> foods;
        HashSet <Block> ghosts;
        Block pacman;

        Timer gameLoop;
        
        char [] directions = {'U', 'D', 'L', 'R'};
        Random random = new Random(); //for each ghost randomly choose direction

        int score = 0; 
        int lives = 3;
        boolean gameOver = false;

        int gameLevel = 1;

        

        PacMan() { //constructer
            setPreferredSize(new Dimension(boardWidth, boardHeight));
            setBackground(Color.BLACK); //built in methods
            addKeyListener(this); //takes on properties of keylistener
            setFocusable(true);

            //load images
            wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
                //getClass = PacMan class
                //getResource specifies file path
                // "./" = look from same folder as starting point 
                //specify image name (wall.png) --> .getImage()
            blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
            orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
            pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
            redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
            
            pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
            pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
            pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
            pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();
        

            loadMap();

            for (Block ghost : ghosts) {
                char newDirection = directions[random.nextInt(4)];
                ghost.updateDirection(newDirection); 
            }
            gameLoop = new Timer(50, this); //50 is delay, this refers to pacman object
            // every 50 milliseconds, you want to call actionPerformed, which calls repaint
            //1000 milliseconds in 1 second  (1000/50) = 20 frames per second
            gameLoop.start(); //draws over and over again

        }

        public void loadMap() {
            //initialize hashsets
            walls = new HashSet<Block>();
            foods = new HashSet<Block>();
            ghosts = new HashSet<Block>();

            //iterate through tilemap (given)
            for (int r = 0; r < rowCount; r++) {
                for (int c = 0; c < columnCount; c++) {
                    String row = tileMap[r]; //each string is a row of the array
                    char tileMapChar = row.charAt(c); //each character is a value in the string

                    //calculate coordinates everytime you iterate through 2D array 
                    int x = c * tileSize; //x pos is columns from left * pixels per block
                    int y = r * tileSize; //y pos is rows from top * pixels per block
                    
                    if (tileMapChar == 'X') {
                        Block wall = new Block(wallImage, x, y, tileSize, tileSize); //creates new block object to add to hashset
                        walls.add(wall); //adds to hashset
                    }
                    else if (tileMapChar == 'b') {
                        Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize); //creates new block object to add to hashset
                        ghosts.add(ghost); //adds to hashset
                    }
                    else if (tileMapChar == 'o') {
                        Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize); //creates new block object to add to hashset
                        ghosts.add(ghost); //adds to hashset
                    }
                    else if (tileMapChar == 'p') {
                        Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize); //creates new block object to add to hashset
                        ghosts.add(ghost); //adds to hashset
                    }
                    else if (tileMapChar == 'r') {
                        Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize); //creates new block object to add to hashset
                        ghosts.add(ghost); //adds to hashset
                    }
                    else if (tileMapChar == 'P') {
                        pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize); //pacman is already declared (initialized here)
                    }
                    else if (tileMapChar == ' ') {
                        Block food = new Block(null, x + 14, y + 14, 4, 4); //creates new block object to add to hashset
                        foods.add(food); //adds to hashset
                    }
                }
            }


        }

        public void paintComponent(Graphics g) {
            //super refers to method called paintComponent in jpanel class
            super.paintComponent(g); // we are in pacman class which is child class of jpanel
            draw(g);
        }

        public void draw(Graphics g) {
            g.drawImage(pacman.image,pacman.x, pacman.y, pacman.width, pacman.height, null);

            for (Block ghost : ghosts ) { //iteratate through ghosts hashset
                g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
            }

            for (Block wall : walls) { //iteratate through walls hashset
                g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);

            }

            g.setColor(Color.white);
            for (Block food: foods) { //iteratate through foods hashset
                g.fillRect(food.x, food.y, food.width, food.height);
            }

            //score
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            if (gameOver) {
                g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
            }
            else {
                g.drawString(String.valueOf(lives) + " Lives || " +  " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
            }
        }

        public void move() {

            pacman.x += pacman.velocityX;
            pacman.y += pacman.velocityY;

            if (pacman.x < 0) {
                pacman.x = boardWidth - pacman.width; //wraps to right
            }
            else if (pacman.x + pacman.width > boardWidth) {
                pacman.x = 0; //wraps to left
            }

            //undoes 
            for (Block wall: walls) {
                if (collision(pacman, wall)) {
                    pacman.x -=pacman.velocityX;
                    pacman.y -=pacman.velocityY;
                    break;
                }
            }

            for (Block ghost : ghosts) {
                if (collision(ghost, pacman)) {
                    lives--;
                    if (lives == 0) {
                        gameOver = true;
                        return;
                    }
                    resetPositions();
                }
                ghost.x += ghost.velocityX;
                ghost.y += ghost.velocityY;

                for (Block wall : walls) {
                    if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
                        ghost.updateDirection('U');
                    }
                    if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                        ghost.x -= ghost.velocityX;
                        ghost.y -= ghost.velocityY;
                        char newDirection = directions[random.nextInt(4)];
                        ghost.updateDirection(newDirection);
                    }
                }
            }
            Block foodEaten = null;
            for (Block food : foods) {
                if (collision(pacman, food)) {
                    foodEaten = food;
                    score+=10;

                }
            }
            foods.remove(foodEaten);

            if (foods.isEmpty()) {
                gameLevel++;
                updateTiles(gameLevel);
                loadMap();
                resetPositions();
            }

        }

        public boolean collision(Block a, Block b) {
            //detect collisions between pacman and ghost/wall/food
            return (a.x < b.x + b.width) && (a.x + a.width > b.x) && (a.y < b.y + b.height && a.y + a.height > b.y);

        }

        public void resetPositions() {
            pacman.reset();
            pacman.velocityX = 0;
            pacman.velocityY = 0;
            for (Block ghost : ghosts) {
                ghost.reset();
                char newDirection = directions[random.nextInt(4)];
                ghost.updateDirection(newDirection);
            }
        }

        
        void updateTiles(int gameLevel) {
            if (this.gameLevel == 2) {
                tileMap = new String []{
                    "XXXXXXXXXXXXXXXXXXX",
                    "X                 X",
                    "X XX XXX X XXX XX X",
                    "X     O   O     O X",
                    "X XX X XXXXX X XX X",
                    "X    X       X    X",
                    "XXXX XXXX XXXX XXXX",
                    "X                 X",
                    "XXXX X XXrXX X XXXX",
                    "X     b   p    o  X",
                    "XXXX X XXXXX X XXXX",
                    "X                 X",
                    "XXXX X XXXXX X XXXX",
                    "X O   O   O       X",
                    "X XX XXX X XXX XX X",
                    "X                 X",
                    "XX X X XXXXX X X XX",
                    "X    X   X   X    X",
                    "X XXXXXX X XXXXXX X",
                    "X        P        X",
                    "XXXXXXXXXXXXXXXXXXX"
                };
            }

            if (this.gameLevel == 3) {
                tileMap = new String[] {

                    "XXXXXXXXXXXXXXXXXXX",
                    "X   O         O   X",
                    "X XX XX XXXXX XX XX",
                    "X                 X",
                    "X XXXXXX XXX XXXXXX",
                    "X    X       X    X",
                    "XXXX XXXX XXXX XXXX",
                    "X    O   r   O    X",
                    "XXXX X XX XX X XXXX",
                    "X    b   P    p   X", 
                    "XXXX X XXXXX X XXXX",
                    "X    O       O    X",
                    "XXXX XXXX XXXX XXXX",
                    "X    X   o   X    X",
                    "X XXXXXX XXX XXXXXX",
                    "X                 X",
                    "XX XXX XXXXX XXX XX",
                    "X   O         O   X",
                    "X XXXXXX XXX XXXXXX",
                    "X                 X",
                    "XXXXXXXXXXXXXXXXXXX"
                    
                };
            }

            if (this.gameLevel == 4) {
                tileMap = new String[] {
                    "XXXXXXXXXXXXXXXXXXX",
                    "X   O     O     O X",
                    "X XXXXXX XXX XXXXXX",
                    "X    X       X    X",
                    "XXXX XXXX XXXX XXXX",
                    "X                 X",
                    "XX XXX XXXXX XXX XX",
                    "X   O   r   O   O X",
                    "XXXX X XXXXX X XXXX",
                    "X   b        p  o X", 
                    "XXXX X XXXXX X XXXX",
                    "X   O   P   O     X",
                    "XXXX XXXX XXXX XXXX",
                    "X    X   o   X    X",
                    "X XXXXXX XXX XXXXXX",
                    "X                 X",
                    "XX XXX XXXXX XXX XX",
                    "X   O     O     O X",
                    "X XXXXXX XXX XXXXXX",
                    "X                 X",
                    "XXXXXXXXXXXXXXXXXXX"
                };
                

            }

            if (this.gameLevel == 5) {
                tileMap = new String [] {
                    "XXXXXXXXXXXXXXXXXXX",
                    "X        X        X",
                    "X XX XXX X XXX XX X",
                    "X        o        X",
                    "X XX X XXXXX X XX X",
                    "X    X   r   X    X",
                    "XXXX XXXX XXXX XXXX",
                    "OOOX X       X XOOO",
                    "XXXX X XXrXX X XXXX",
                    "O       bpo       O",
                    "XXXX X XXXXX X XXXX",
                    "OOOX X       X XOOO",
                    "XXXX X XXXXX X XXXX",
                    "X   b    X    p   X",
                    "X XX XXX X XXX XX X",
                    "X  X     P     X  X",
                    "XX X X XXXXX X X XX",
                    "X    X   X   X    X",
                    "X XXXXXX X XXXXXX X",
                    "X        r        X",
                    "XXXXXXXXXXXXXXXXXXX" 

                };
            }

            for (Block ghost : ghosts) {
                ghost.updateVelocity();
            }
            pacman.updateVelocity();
        }
        
        

        @Override
        public void actionPerformed(ActionEvent e) {
            move(); //update positions of all objects 
            repaint(); //repaints everytime pacman moves
            if (gameOver) {
                gameLoop.stop();
            }

        }

        @Override
        public void keyTyped(KeyEvent e) { //when you type on key that has corresponding character
        }

        @Override
        public void keyPressed(KeyEvent e) { //actions when you press on any key
        }

        @Override
        public void keyReleased(KeyEvent e) { //only triggers action when you press on key and let go
            // System.out.println("Keyevent: " + e.getKeyCode());

            if (gameOver) {
                //press spacekey to get game to reset
                if ((e.getKeyCode() == KeyEvent.VK_SPACE)) {
                    loadMap(); 
                    resetPositions();
                    lives = 3;
                    score = 0;
                    gameOver = false;
                    gameLoop.start();
                }
                
            }

            if (e.getKeyCode() == KeyEvent.VK_UP) {
                pacman.updateDirection('U');
            }
            else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                pacman.updateDirection('D');
            }
            else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                pacman.updateDirection('L');
            }
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                pacman.updateDirection('R');

            }


            if (pacman.direction == 'U') {
                pacman.image = pacmanUpImage;
            }
            else if (pacman.direction == 'D') {
                pacman.image = pacmanDownImage;
            }
            else if (pacman.direction == 'L') {
                pacman.image = pacmanLeftImage;
            }
            else if (pacman.direction == 'R') {
                pacman.image = pacmanRightImage;
            }
        }



}
