package gdd;

import gdd.scene.BossFight;
import gdd.scene.LevelSelect;
import gdd.scene.Scene1;
import gdd.scene.TitleScene;
import javax.swing.JFrame;

public class Game extends JFrame  {

    TitleScene titleScene;
    LevelSelect levelSelect;
    Scene1 scene1;
    BossFight bossFight;

    public Game() {
        titleScene = new TitleScene(this);
        levelSelect = new LevelSelect(this);
        scene1 = new Scene1(this);
        initUI();
        loadTitle();
        //loadScene2();
    }

    private void initUI() {

        setTitle("Space Invaders");
        setSize(Global.BOARD_WIDTH, Global.BOARD_HEIGHT);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

    }

    public void loadTitle() {
        getContentPane().removeAll();
        //add(new Title(this));
        add(titleScene);
        titleScene.start();
        revalidate();
        repaint();
    }

    public void bossfight() {
        // Use scene1 score if available, otherwise start with 0
        int startingScore = (scene1 != null) ? scene1.score : 0;
        bossFight = new BossFight(this, startingScore);
        getContentPane().removeAll();
        add(bossFight);
        
        // Only stop scene1 if it exists and was actually started
        if (scene1 != null && scene1.isStarted()) {
            scene1.stop();
        }
        
        bossFight.start();
        revalidate();
        repaint();
    }

    public void loadLevelSelect() {
        getContentPane().removeAll();
        add(levelSelect);
        if (titleScene != null) titleScene.stop();
        levelSelect.start();
        revalidate();
        repaint();
    }

    public void loadscene1() {
        getContentPane().removeAll();
        add(scene1);
        if (titleScene != null) titleScene.stop();
        if (levelSelect != null) levelSelect.stop();
        scene1.start();
        revalidate();
        repaint();
    }
}
