import javax.swing.Icon;

public class Player {

    String tag = "BOT";
    int lives = 3;
    char direction;
    Icon icon;
    int x;
    int y;

    public void resetLives(){
        lives = 3;
    }
}