import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.border.LineBorder;

class Main {

    static int coloumns = 17 , rows = 10;
    static double obstacleChance;
    static JButton[][] playground;
    static Color spaceColor;
    static Color obstacleColor;
    static Color borderColor;
    static int playerCount = 4;
    static Player[] players = new Player[playerCount];
    public static Icon[] playerIcon={new ImageIcon("player-w.png"),new ImageIcon("player-a.png"),new ImageIcon("player-s.png"),new ImageIcon("player-d.png")};
    public static Icon[] enemyIcon={new ImageIcon("enemy-w.png"),new ImageIcon("enemy-a.png"),new ImageIcon("enemy-s.png"),new ImageIcon("enemy-d.png")};
    public static Icon healthIcon = new ImageIcon("health.png");
    public static JFrame gameFrame;
    static long start;
    static java.util.Timer timer;

    public static void main(final String[] args) {
        // Setting Up
        playground = new JButton[rows][coloumns];
        spaceColor = Color.decode("#e85d04");
        borderColor = Color.decode("#370617");
        obstacleColor = Color.decode("#6a040f");
        obstacleChance = (0.4 * coloumns * rows);
        // Launching
        drawStartMenu();
        // startGame();
    }

    private static void startGame() {
        timer = new java.util.Timer();
        for (int i = 0; i < playerCount; i++) {
            players[i] = new Player();
            if (i == 0) {
                players[i].icon = playerIcon[3];
            } else {
                players[i].icon = enemyIcon[3];
            }
        }
        start = System.currentTimeMillis();
        //Drawing Playground
        drawPlayground();
        //Spawning Enemies
        for(int i=0; i<playerCount-1; i++){
            spawnPlayer(players[i+1]);
            players[i+1].tag="BOT";
        }
        //Spawning Player
        spawnPlayer(players[0]);
        players[0].tag="Player";
        //Activating AI
        activateAI();
        //Scheduling Health Packs
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                spawnHealth();
            }
        }, 20000,20000);
    }

    private static void activateAI(){
        for(int i = 1; i<playerCount; i++){
            final int index = i;
            timer.schedule(new TimerTask(){
                @Override
                public void run() {
                    if(players[index].lives>0){
                        decideForAI(players[index]);
                    }
                }   
            },0,1000);
        }
    }

    private static void decideForAI(Player player){

        char direction = player.direction;
        int x = player.x; int y = player.y;
        switch(direction){
            case 'w':
                for(int i = x-1;i>0;i--){
                    if(playground[i][y].getBackground()==obstacleColor || playground[i][y].getBackground()==borderColor){
                        break;
                    }
                    if(isPlayerInCell(i, y)){
                        fire(player);
                        return;
                    }
                }
                break;
            case 'a':
                for(int i = y-1;i>0;i--){
                    if(playground[x][i].getBackground()==obstacleColor || playground[x][i].getBackground()==borderColor){
                        break;
                    }
                    if(isPlayerInCell(x, i)){
                        fire(player);
                        return;
                    }
                }
                break;
            case 's':
                for(int i = x+1;i<rows;i++){
                    if(playground[i][y].getBackground()==obstacleColor || playground[i][y].getBackground()==borderColor){
                        break;
                    }
                    if(isPlayerInCell(i, y)){
                        fire(player);
                        return;
                    }
                }
                break;
            case 'd':
                for(int i = y+1;y<coloumns;i++){
                    if(playground[x][i].getBackground()==obstacleColor || playground[x][i].getBackground()==borderColor){
                        break;
                    }
                    if(isPlayerInCell(x, i)){
                        fire(player);
                        return;
                    }
                }
                break;
        }

        AIRandomMove(player);
    }

    private static void AIRandomMove(Player player){
        int random = new Random().nextInt(4);
        switch(random){
            case 0:
                changeAIDirectons(player, 'w');
                movePlayer(player, 'w');
                break;
            case 1:
                changeAIDirectons(player, 'a');
                movePlayer(player, 'a');
                break;
            case 2:
                changeAIDirectons(player, 's');
                movePlayer(player, 's');
                break;
            case 3:
                changeAIDirectons(player, 'd');
                movePlayer(player, 'd');
                break;
        }
    }

    private static void spawnPlayer(Player player){
            int iRand = new Random().nextInt(rows-2)+1;
            int jRand = new Random().nextInt(coloumns-2)+1;
            if(playground[iRand][jRand].getBackground()==obstacleColor || playground[iRand][jRand].getText()==player.tag || isPlayerInCell(iRand, jRand)){
                spawnPlayer(player);
                return;
            }
            playground[iRand][jRand].setText(player.lives+"");
            playground[iRand][jRand].setIcon(player.icon);
            player.x=iRand;
            player.y=jRand;
    }

    private static void spawnHealth(){
        int iRand = new Random().nextInt(rows-2)+1;
        int jRand = new Random().nextInt(coloumns-2)+1;
        if(playground[iRand][jRand].getBackground()==obstacleColor || isPlayerInCell(iRand, jRand)){
            spawnHealth();
            return;
        }
        playground[iRand][jRand].setIcon(healthIcon);
    }

    private static void drawPlayground(){
        final JFrame frame=new JFrame();
        frame.setSize(1280,720);
        frame.setUndecorated(true);
        frame.setLayout(new GridLayout(rows,coloumns));
        //Drawing Playground
        for(int i=0;i<rows;i++){
            for(int j=0;j<coloumns;j++){
                playground[i][j]=new JButton();
                playground[i][j].setOpaque(true);
                playground[i][j].setBorderPainted(false);
                playground[i][j].setForeground(borderColor);
                playground[i][j].setHorizontalTextPosition(JButton.CENTER);
                playground[i][j].setVerticalTextPosition(JButton.CENTER);
                playground[i][j].setForeground(borderColor);
                playground[i][j].addKeyListener(new GameKeyListener());
                //Coloring The Borders
                if(j==0 || j== coloumns-1 || i==0 || i ==rows-1){
                    playground[i][j].setBackground(borderColor);
                }else{
                    playground[i][j].setBackground(spaceColor);
                }
                //Spawning Obstables
                Random random = new Random();
                double randomGuess = random.nextInt(coloumns*rows);
                boolean val = randomGuess < obstacleChance ;
                if( val && j<coloumns-1 && i<rows-1 && i>0 && j>0){
                    playground[i][j].setBackground(obstacleColor);
                }
                playground[i][j].setBorderPainted(true);
                playground[i][j].setBorder(new LineBorder(borderColor,1));
                frame.add(playground[i][j]);
            }
        }
        processFrame(frame);
        gameFrame = frame;
        frame.setVisible(true);
    }

    private static void drawStartMenu(){
        //Setting Up Window
        final JFrame frame=new JFrame();
        frame.setSize(500,500);
        frame.setUndecorated(true);
        //Setting Up Welcome Label
        final JLabel welcomeLable=new JLabel("    Welcome To Counter Strike : Guilan University    ");
        welcomeLable.setHorizontalAlignment(JLabel.CENTER);
        welcomeLable.setForeground(Color.WHITE);
        //Setting Up Button Box
        final Box box=Box.createVerticalBox();    
        //Setting Up playground
        final JButton playButton=new JButton("Play Offline!");
        playButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(final ActionEvent e)
          {
            frame.dispose();
            startGame();
          }
        });
        final JButton recordsButton=new JButton("Play Online!");
        recordsButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(final ActionEvent e)
          {
              exit();
          }
        });
        final JButton exitButton=new JButton("Online Server");
        exitButton.setPreferredSize(new Dimension(40,40));
        exitButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(final ActionEvent e)
          {
            exit();
          }
        });
        //Adding Components To The Box
        box.add(Box.createVerticalStrut(10));
        box.add(welcomeLable);
        box.add(Box.createVerticalStrut(10));
        box.add(playButton);
        box.add(Box.createVerticalStrut(10));
        box.add(recordsButton);
        box.add(Box.createVerticalStrut(10));
        box.add(exitButton);
        welcomeLable.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        recordsButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        playButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        //Adding Components To Frame
        frame.add(box);
        //Setting Window Visible
        frame.pack();
        processFrame(frame);
        frame.setVisible(true);
    }

    private static void exit(){
        System.exit(0);
    }

    private static void processFrame(final JFrame frame){
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(borderColor);
    }

    private static void moveCharacter(char direction){
        changePlayerDirection(players[0], direction);
        movePlayer(players[0],direction);
    }

    private static boolean isPlayerInCell(int i, int j){
        JButton temp = playground[i][j];
        for(int m=0;m<4;m++){
            if(temp.getIcon()==enemyIcon[m] || temp.getIcon()==playerIcon[m]){
                return true;
            }
        }
        return false;
    }

    private static void movePlayer(Player player,char direction){
        int i = player.x; int j = player.y;
        int newI = 0, newJ = 0;
        switch(direction){
            case 'w':
                newI=i-1;
                newJ=j;
                if(newI<1 || playground[newI][newJ].getBackground()==obstacleColor || isPlayerInCell(newI,newJ))
                    return;
                break;
            case 'a':
                newI=i;
                newJ=j-1;
                if(newJ<1 || playground[newI][newJ].getBackground()==obstacleColor || isPlayerInCell(newI,newJ))
                    return;
                break;
            case 's':
                newI=i+1;
                newJ=j;
                if(newI>rows-2 || playground[newI][newJ].getBackground()==obstacleColor || isPlayerInCell(newI,newJ))
                return;
                break;
            case 'd': 
                newI=i;
                newJ=j+1;
                if(newJ>coloumns-2 || playground[newI][newJ].getBackground()==obstacleColor || isPlayerInCell(newI,newJ))
                return;
                break;
        }
        
        player.x=newI;
        player.y=newJ;
        if(playground[newI][newJ].getIcon()==healthIcon && player.lives<4){
            player.lives++;
        }
        if(player.lives>0){
            playground[newI][newJ].setIcon(playground[i][j].getIcon());
            playground[newI][newJ].setText(player.lives+"");
            playground[i][j].setText("");
            playground[i][j].setIcon(null);
        }    
    }

    private static void changePlayerDirection(Player player, char direction){

        player.direction = direction;
        int i = player.x,j =player.y;
        switch(direction){
            case 'w':
                playground[i][j].setIcon(playerIcon[0]);
                player.icon=playerIcon[0];
                break;
            case 'a':
                playground[i][j].setIcon(playerIcon[1]);
                player.icon=playerIcon[1];
                break;
            case 's':
                playground[i][j].setIcon(playerIcon[2]);
                player.icon=playerIcon[2];
                break;
            case 'd':
                playground[i][j].setIcon(playerIcon[3]);
                player.icon=playerIcon[3];
                break;
        }
    }

    private static void changeAIDirectons(Player player, char direction){
        if(player.lives<1){
            return;
        }
        player.direction = direction;
        int i = player.x,j =player.y;
        switch(direction){
            case 'w':
                playground[i][j].setIcon(enemyIcon[0]);
                player.icon=enemyIcon[0];
                break;
            case 'a':
                playground[i][j].setIcon(enemyIcon[1]);
                player.icon=enemyIcon[1];
                break;
            case 's':
                playground[i][j].setIcon(enemyIcon[2]);
                player.icon=enemyIcon[2];
                break;
            case 'd':
                playground[i][j].setIcon(enemyIcon[3]);
                player.icon=enemyIcon[3];
                break;
        }
    }

    private static void reducePlayerLivesAtPosition(int x , int y){
        for(int i =0; i< playerCount;i++){
            if(players[i].x==x && players[i].y==y){
                players[i].lives--;
                if(players[i].lives<1){
                    players[i].icon=null;
                    playground[x][y].setIcon(null);
                    playground[x][y].setText("");
                    long end = System.currentTimeMillis();
                    if(i==0){
                        gameFrame.dispose();
                        int passed = (int)((end - start)/1000);
                        timer.cancel();
                        new java.util.Timer().schedule(new TimerTask(){
                            @Override
                            public void run() {
                                JOptionPane.getRootFrame().dispose();   
                                drawStartMenu();
                            }
                        }, 5000);
                        JOptionPane.showMessageDialog(null,"You Lost In \n "+passed+" Seconds");
                    }else{
                        for(int m =1;m<playerCount; m++){
                            if(players[m].lives>0){
                                return;
                            }
                        }
                        int passed = (int)((end - start)/1000);
                        gameFrame.dispose();
                        timer.cancel();
                        new java.util.Timer().schedule(new TimerTask(){
                            @Override
                            public void run() {
                                JOptionPane.getRootFrame().dispose();   
                                drawStartMenu();
                            }
                        }, 5000);
                        JOptionPane.showMessageDialog(null, "You Won In \n "+passed+" Seconds");         
                    }
                }else{
                    playground[x][y].setText(players[i].lives+"");
                }
                break;
            }
        }
    }

    private static void fire(Player player){
        char direction = player.direction;
        int x = player.x;int y = player.y;
        switch(direction){
            case 'w':
                for(int i = x-1;i>0;i--){
                    if(playground[i][y].getBackground()==obstacleColor || playground[i][y].getBackground()==borderColor){
                        return;
                    }
                    if(isPlayerInCell(i, y)){
                        reducePlayerLivesAtPosition(i,y);
                    }
                }
                break;
            case 'a':
                for(int i = y-1;i>0;i--){
                    if(playground[x][i].getBackground()==obstacleColor || playground[x][i].getBackground()==borderColor){
                        return;
                    }
                    if(isPlayerInCell(x, i)){
                        reducePlayerLivesAtPosition(x,i);
                    }
                }
                break;
            case 's':
                for(int i = x+1;i<rows;i++){
                    if(playground[i][y].getBackground()==obstacleColor || playground[i][y].getBackground()==borderColor){
                        return;
                    }
                    if(isPlayerInCell(i, y)){
                        reducePlayerLivesAtPosition(i,y);
                    }
                }
                break;
            case 'd':
                for(int i = y+1;y<coloumns;i++){
                    if(playground[x][i].getBackground()==obstacleColor || playground[x][i].getBackground()==borderColor){
                        return;
                    }
                    if(isPlayerInCell(x, i)){
                        reducePlayerLivesAtPosition(x,i);
                    }
                }
                break;
        }
    }

    public static class GameKeyListener extends KeyAdapter{

        boolean cooldown = false;

        @Override
        public void keyPressed(KeyEvent ke){
            int code = ke.getKeyCode();
            switch(code){
                case KeyEvent.VK_RIGHT:
                    moveCharacter('d');
                    break;
                case KeyEvent.VK_LEFT:
                    moveCharacter('a');
                    break;
                case KeyEvent.VK_UP:
                    moveCharacter('w');
                    break;
                case KeyEvent.VK_DOWN:
                    moveCharacter('s');
                    break;
                case KeyEvent.VK_SPACE:
                    if(!cooldown){
                        fire(players[0]);
                        cooldown=true;
                        timer.schedule(new TimerTask(){
                            @Override
                            public void run() {
                                cooldown = false;
                            }
                        }, 1000);
                    }
                    break;
            }
        }
    }
}
