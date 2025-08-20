import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;

import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {
        class Block{
                int x;
                int y;
                int width;
                int height;
                Image image;

                int startX;
                int startY;
                char direction = 'U'; // U = Up, D = Down, L = Left, R = Right
                // Velocity in pixels per update
                int velocityX = 0;
                int velocityY = 0;


                Block(Image image, int x,int y, int width, int height){
                        this.image = image;
                        this.x = x;
                        this.y = y;
                        this.width = width;
                        this.height = height;
                        this.startX = x;
                        this.startY = y;
                }

                void updateDirection(char direction){
                        char prevDirection = this.direction;
                        this.direction = direction;
                        updateValocity();
                        this.x += this.velocityX;
                        this.y += this.velocityY;
                        for(Block wall: walls){
                                if(isCollision(this, wall)){
                                        this.x -= this.velocityX; // Revert position if collision
                                        this.y -= this.velocityY;
                                        this.direction = prevDirection; // Revert direction
                                        updateValocity(); // Update velocity based on reverted direction
                                }
                        }
                }

                void updateValocity(){
                        if (this.direction == 'U') {
                                this.velocityX = 0;
                                this.velocityY = -tileSize/4;
                        }
                        else if (this.direction == 'D'){
                                this.velocityX = 0;
                                this.velocityY = tileSize/4;
                        }
                        else if (this.direction == 'L'){
                                this.velocityX = -tileSize/4;
                                this.velocityY = 0;
                        }
                        else if (this.direction == 'R'){
                                this.velocityX = tileSize/4;
                                this.velocityY = 0;
                        }
                }
                void reset(){
                        this.x = this.startX;
                        this.y = this.startY;
                }
        }
        private int rowCount = 21;
        private int columnCount = 19;
        private int tileSize = 32;
        private int boardWidth = columnCount * tileSize;
        private int boardHeight = rowCount * tileSize;

        private Image wallImage;
        private Image blueGhostImage;
        private Image orangeGhostImage;
        private Image pinkGhostImage;
        private Image redGhostImage;

        private Image pacmanUpImage;
        private Image pacmanDownImage;
        private Image pacmanLeftImage;
        private Image pacmanRightImage;

        // Game elements: X = Walls, ' ' = Food, O = skip, P = Pac man, r = Red Ghost, b = Blue Ghost, p = Pink Ghost, o = Orange Ghost
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

        
        HashSet<Block> walls;
        HashSet<Block> ghosts;
        HashSet<Block> foods;
        Block pacman;

        Timer gameLoop;
        char[] directions = {'U', 'D', 'L', 'R'};
        Random random = new Random();
        int score = 0;
        int lives = 3;
        boolean gameOver = false;

        PacMan () {
                setPreferredSize(new Dimension(boardWidth, boardHeight));
                setBackground(Color.BLACK);

                addKeyListener(this);
                setFocusable(true);

                //Load images
                wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
                orangeGhostImage = new ImageIcon(getClass().getResource("./orangeghost.png")).getImage();
                blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
                pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
                redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

                pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
                pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
                pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
                pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

                loadMap();

                for(Block ghost : ghosts){
                        char newDirection = directions[random.nextInt(4)];
                        ghost.updateDirection(newDirection);
                }
                gameLoop = new Timer(50,this);
                gameLoop.start();

        }
        
        public void loadMap(){
                walls = new HashSet<Block>();
                foods = new HashSet<Block>();
                ghosts = new HashSet<Block>();

                for(int r = 0; r < rowCount; r++){
                        for(int c = 0; c < columnCount; c++){
                                String raw = tileMap[r];
                                char tileMapChar = raw.charAt(c);

                                int x = c * tileSize;
                                int y = r * tileSize;

                                if(tileMapChar == 'X'){ // Block wall
                                        Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                                        walls.add(wall);
                                } else if(tileMapChar == 'b'){ // Block blueghost
                                        Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                                        ghosts.add(ghost);
                                } else if(tileMapChar == 'o'){ // Block blueghost
                                        Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                                        ghosts.add(ghost);
                                } else if(tileMapChar == 'p'){ // Block blueghost
                                        Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                                        ghosts.add(ghost);
                                } else if(tileMapChar == 'r'){ // Block blueghost
                                        Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                                        ghosts.add(ghost);
                                } else if(tileMapChar == 'P'){ // Block blueghost
                                        pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                                } else if (tileMapChar == ' '){ // Block food
                                        Block food = new Block(null, x + 14, y + 14, 4, 4);
                                        foods.add(food);
                                }

                        }
                }
        }

        public void paintComponent(Graphics g) {
                super.paintComponent(g);
                draw(g);
        }

        public void draw(Graphics g){
                g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);
        
                for(Block ghost : ghosts){
                        g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
                }

                for(Block wall : walls){
                        g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
                }

                g.setColor(Color.YELLOW);
                for(Block food : foods){
                        g.fillRect(food.x, food.y, food.width, food.height);
                }

                g.setFont(new Font("Arial", Font.BOLD, 20));
                if(gameOver){
                        g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
                }else{
                        g.drawString("X : " + String.valueOf(lives) + "  Score: " + String.valueOf(score), tileSize/2, tileSize/2);
                }
        }

        public void movePacman(){
                pacman.x += pacman.velocityX;
                pacman.y += pacman.velocityY;

                for(Block wall : walls){
                        if (isCollision(pacman, wall)) {
                                pacman.x -= pacman.velocityX; // Revert position if collision
                                pacman.y -= pacman.velocityY;
                                break;
                        }
                }

                for(Block ghost : ghosts){
                        if(isCollision(ghost, pacman)){
                                lives -= 1;
                                if(lives == 0){
                                        gameOver = true;
                                        return;
                                }
                                resetPositions();
                        }
                        if(ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D' ){
                                ghost.updateDirection('U');
                        }
                        ghost.x += ghost.velocityX;
                        ghost.y += ghost.velocityY;
                        for(Block wall : walls){
                                if (isCollision(ghost, wall) || ghost.x <= 0 || ghost.x >= boardWidth - ghost.width ) {
                                        ghost.x -= ghost.velocityX; // Revert position if collision
                                        ghost.y -= ghost.velocityY;
                                        char newDirection = directions[random.nextInt(4)];
                                        ghost.updateDirection(newDirection);
                                }
                        }
                }
                Block foodEaten = null;
                for(Block food : foods){
                        if(isCollision(pacman, food)){
                                foodEaten = food;
                                score += 10;

                        }
                }
                foods.remove(foodEaten);

                if(foods.isEmpty()){
                        loadMap();
                        resetPositions();
                }

        }

        public boolean isCollision(Block a, Block b){
                return a.x < b.x + b.width &&    // Collision detection formula
                        a.x + a.width > b.x &&
                        a.y < b.y + b.height &&
                        a.y + a.height > b.y;
        }

        public void resetPositions(){
                pacman.reset();
                pacman.velocityX = 0;
                pacman.velocityY = 0;

                for(Block ghost : ghosts){
                        ghost.reset();
                        char newDirection = directions[random.nextInt(4)];
                        ghost.updateDirection(newDirection);
                }
        }
        @Override
        public void actionPerformed(ActionEvent e) {
                movePacman();
                repaint();
                if (gameOver) {
                        gameLoop.stop();
                }
        }

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {}

        @Override
        public void keyReleased(KeyEvent e) {
                if(gameOver){
                        loadMap();
                        resetPositions();
                        lives = 3;
                        score = 0;
                        gameOver = false;
                        gameLoop.start();
                }
                //System.out.println("Key Event : " + e.getKeyCode());
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

                if(pacman.direction == 'U'){
                        pacman.image = pacmanUpImage;
                
                }
                else if(pacman.direction == 'D'){
                        pacman.image = pacmanDownImage;
                }
                else if(pacman.direction == 'L'){
                        pacman.image = pacmanLeftImage;
                }
                else if(pacman.direction == 'R'){
                        pacman.image = pacmanRightImage;
                }
        }
}
