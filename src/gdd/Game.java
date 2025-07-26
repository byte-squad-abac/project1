   package gdd;

import gdd.scene.BossFight;
import gdd.scene.Scene1;
import gdd.scene.TitleScene;
import javax.swing.JFrame;

public class Game extends JFrame  {

    TitleScene titleScene;
    Scene1 scene1;
    BossFight bossFight;

    public Game() {
        titleScene = new TitleScene(this);
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
        bossFight = new BossFight(this, scene1.score);
        getContentPane().removeAll();
        add(bossFight);
        scene1.stop();
        bossFight.start();
        revalidate();
        repaint();
    }

    public void loadscene1() {
        getContentPane().removeAll();
        add(scene1);
        titleScene.stop();
        scene1.start();
        revalidate();
        repaint();
    }
}