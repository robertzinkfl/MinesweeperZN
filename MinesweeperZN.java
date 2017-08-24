/**
 * Robert Zink
 * robertzinkfl@gmail.com
 * MinesweeperZN
 * 24 August 2017
 *
 * MinesweeperZN.java
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MinesweeperZN extends Application {

    private final int TILE_SIZE = (int) Screen.getPrimary().getVisualBounds().getHeight() / 30;
    private final int WIDTH = (int) (Screen.getPrimary().getVisualBounds().getWidth() * 0.50);
    private final int HEIGHT = (int) (Screen.getPrimary().getVisualBounds().getHeight() * 0.60);

    private int X_TILES = 15;
    private int Y_TILES = 15;

    private double difficulty = 0.2;

    private Tile[][] grid;
    private BorderPane window;
    private Text bottomText;
    private final Image mine = new Image("mine.png");
    private final Image flag = new Image("flag.png");
    private final Image good = new Image("cool_guy.png");
    private final Image bad = new Image("dead_guy.png");
    private ImageView guy;

    private Parent createContent() {
        GridPane root = new GridPane();
        grid = new Tile[X_TILES][Y_TILES];

        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Tile tile = new Tile(x, y, Math.random() < difficulty);

                grid[x][y] = tile;
                root.add(tile,x,y);
            }
        }

        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Tile tile = grid[x][y];

                if (tile.hasBomb)
                    continue;

                long bombs = getNeighbors(tile).stream().filter(t -> t.hasBomb).count();

                if (bombs > 0) {
                    tile.text.setText(String.valueOf(bombs));

                    switch((int)bombs) {
                        case 1:
                            tile.text.setFill(Color.BLUE);
                            break;
                        case 2:
                            tile.text.setFill(Color.GREEN);
                            break;
                        case 3:
                            tile.text.setFill(Color.RED);
                            break;
                        case 4:
                            tile.text.setFill(Color.PURPLE);
                            break;
                        case 5:
                            tile.text.setFill(Color.MAROON);
                            tile.text.setId("manyNeighbors");
                            break;
                        case 6:
                            tile.text.setFill(Color.DARKTURQUOISE);
                            tile.text.setId("manyNeighbors");
                            break;
                        case 7:
                            tile.text.setFill(Color.GRAY);
                            tile.text.setId("manyNeighbors");
                            break;
                        case 8:
                            tile.text.setFill(Color.BLACK);
                            tile.text.setId("manyNeighbors");
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        root.setHgap(1);
        root.setVgap(1);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20,20,20,20));
        root.setGridLinesVisible(false);

        return root;
    }

    private List<Tile> getNeighbors(Tile tile) {
        List<Tile> neighbors = new ArrayList<>();

        int[] points = new int[] {
                -1, -1,
                -1, 0,
                -1, 1,
                0, -1,
                0, 1,
                1, -1,
                1, 0,
                1, 1
        };

        for (int i = 0; i < points.length; i++) {
            int dx = points[i];
            int dy = points[++i];

            int newX = tile.x + dx;
            int newY = tile.y + dy;

            if (newX >= 0 && newX < X_TILES
                    && newY >= 0 && newY < Y_TILES) {
                neighbors.add(grid[newX][newY]);
            }
        }

        return neighbors;
    }

    private class Tile extends StackPane {
        private final int x, y;
        private final boolean hasBomb;
        private boolean flagged = false;
        private boolean isOpen = false;
        private final ImageView tileImage = new ImageView();

        private final Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2);
        private final Text text = new Text();

        public Tile(int x, int y, boolean hasBomb) {
            this.x = x;
            this.y = y;
            this.hasBomb = hasBomb;

            tileImage.setFitHeight(TILE_SIZE);
            tileImage.setFitWidth(TILE_SIZE);

            if(hasBomb) {
                tileImage.setImage(mine);
            }

            border.setFill(Color.GRAY);
            border.setStroke(Color.BLACK);

            text.setFont(Font.font(18));
            text.setText(hasBomb ? "X" : "");
            text.setVisible(false);

            tileImage.setVisible(false);

            getChildren().addAll(border, text, tileImage);

            setOnMouseClicked(event -> {
                if(event.getButton() == MouseButton.PRIMARY) {
                    if(!flagged)
                        open();
                        checkForWin();
                }
                else if(event.getButton() == MouseButton.SECONDARY) {
                    if(!flagged) {
                        if(!isOpen) {
                            flagged = true;
                            border.setFill(Color.AQUAMARINE);
                            tileImage.setImage(flag);
                            tileImage.setVisible(true);
                            checkForWin();
                        }
                    }
                    else {
                        if(!isOpen) {
                            flagged = false;
                            border.setFill(Color.GRAY);
                            tileImage.setVisible(false);

                            if (hasBomb) {
                                tileImage.setImage(mine);
                            }
                        }
                    }
                }
            });
        }

        public void open() {
            if (isOpen)
                return;

            if (hasBomb) {
                // text.setVisible(true);
                tileImage.setVisible(true);
                border.setFill(Color.LIGHTSALMON);
                // System.out.println("Game Over");
                revealAllBombs();
                bottomText.setText("BOOM! You lose.");
                bottomText.setVisible(true);
                return;
            }

            isOpen = true;
            text.setVisible(true);
            border.setFill(Color.LIGHTGRAY);

            if (text.getText().isEmpty()) {
                getNeighbors(this).forEach(Tile::open);
            }
        }
    }

    private void revealAllBombs() {
        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                if(grid[x][y].hasBomb) {
                    grid[x][y].tileImage.setImage(mine);
                    grid[x][y].tileImage.setVisible(true);
                }
            }
        }

        guy.setImage(bad);
    }

    private void checkForWin() {
        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                if(grid[x][y].hasBomb && grid[x][y].flagged)
                    continue;
                if(grid[x][y].isOpen)
                    continue;
                else return;
            }
        }

        bottomText.setText("Yay! You win!");
        bottomText.setVisible(true);
    }

    private void setNumTiles(int numTiles) {
        X_TILES = numTiles;
        Y_TILES = numTiles;
        resetGrid();
    }

    private void setDifficulty(double mineRate) {
        difficulty = mineRate;
        resetGrid();
    }

    private void resetGrid() {
        window.setCenter(createContent());
        guy.setImage(good);
        bottomText.setVisible(false);
    }

    private void getAboutScreen() {
        GridPane aboutGrid = new GridPane();
        // Text aboutText;
        aboutGrid.setAlignment(Pos.CENTER);
        /*try {
            BufferedReader aboutReader = new BufferedReader(new InputStreamReader(new FileInputStream("src/README.txt")));
            List<Text> aboutTextArray = new ArrayList<>();
            String line;
            while ((line = aboutReader.readLine()) != null) {
                aboutTextArray.add(new Text(line));
            }
            for(int i = 0; i < aboutTextArray.size(); i++) {
                aboutTextArray.get(i).setId("#aboutText");
                aboutGrid.add(aboutTextArray.get(i), 0, i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        VBox aboutBox = new VBox();
        aboutBox.setAlignment(Pos.TOP_LEFT);
        try {
            InputStream in = ClassLoader.getSystemResourceAsStream("README.txt");
            BufferedReader aboutReader = new BufferedReader(new InputStreamReader(in));
            List<Text> aboutTextArray = new ArrayList<>();
            String line;
            while ((line = aboutReader.readLine()) != null) {
                aboutTextArray.add(new Text(line));
            }
            for (Text text : aboutTextArray) {
                text.setId("#aboutText");
                aboutBox.getChildren().add(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        // aboutText = new Text(aboutReader.);
        // aboutText.setVisible(true);

        // aboutGrid.add(aboutText,0,0);

        ScrollPane scrollPane = new ScrollPane(aboutBox);
        // ScrollBar scrollBar = new ScrollBar();
        // scrollBar.setOrientation(Orientation.VERTICAL);
        // scrollPane.setPrefSize(120,120);
        // scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setFitToHeight(true);

        BorderPane root = new BorderPane(scrollPane);
        root.setPadding(new Insets(15));
        root.getChildren().add(aboutBox);

        // scrollPane.setHmax(WIDTH);
        // scrollPane.setVmax(HEIGHT);
        // scrollPane.setContent(aboutBox);

        window.setCenter(root);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // this.stage = stage;
        stage.setMinWidth(Screen.getPrimary().getVisualBounds().getWidth() * 0.50);
        stage.setMinHeight(Screen.getPrimary().getVisualBounds().getHeight() * 0.65);

        window = new BorderPane();
        window.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, null, null)));

        MenuBar menuBar = new MenuBar();
        Menu options = new Menu("Options");
        MenuItem resetMenu = new MenuItem("Reset Grid");
        resetMenu.setOnAction(e -> resetGrid());
        MenuItem setSmallGrid = new MenuItem("Small Grid");
        setSmallGrid.setOnAction(e -> setNumTiles(7));
        MenuItem setMedGrid = new MenuItem("Medium Grid");
        setMedGrid.setOnAction(e -> setNumTiles(11));
        MenuItem setLrgGrid = new MenuItem("Large Grid");
        setLrgGrid.setOnAction(e -> setNumTiles(15));
        MenuItem setEasyDifficulty = new MenuItem("Easy Mode");
        setEasyDifficulty.setOnAction(e -> setDifficulty(0.08));
        MenuItem setNormalDifficulty = new MenuItem("Normal Mode");
        setNormalDifficulty.setOnAction(e -> setDifficulty(0.20));
        MenuItem setHardDifficulty = new MenuItem("Hard Mode");
        setHardDifficulty.setOnAction(e -> setDifficulty(0.35));
        options.getItems().addAll(resetMenu, setSmallGrid, setMedGrid, setLrgGrid, setEasyDifficulty, setNormalDifficulty, setHardDifficulty);

        Menu aboutMenu = new Menu("About");
        MenuItem aboutItem = new MenuItem("About Minesweeper ZN");
        aboutItem.setOnAction(e -> getAboutScreen());
        aboutMenu.getItems().addAll(aboutItem);
        menuBar.getMenus().addAll(options, aboutMenu);

        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(0,50,0,0));

        Text logoText = new Text("Minesweeper ZN");
        logoText.setFont(Font.font("Courier New", FontWeight.EXTRA_BOLD, 30));
        logoText.setFill(Color.BLACK);
        logoText.setVisible(true);

        Text versionText = new Text("Version 1.01");
        versionText.setFont(Font.font("Courier New", FontPosture.ITALIC, 12));
        versionText.setFill(Color.BLACK);
        versionText.setVisible(true);

        Button reset = new Button("Reset Grid");
        reset.setOnMouseClicked(e -> resetGrid());
        guy = new ImageView(good);
        guy.setFitWidth(200);
        guy.setFitHeight(200);
        guy.setVisible(true);
        vBox.getChildren().addAll(logoText, versionText, reset, guy);

        HBox hBox = new HBox();
        bottomText = new Text();
        bottomText.setVisible(false);
        hBox.getChildren().add(bottomText);

        window.setTop(menuBar);
        window.setRight(vBox);
        window.setCenter(createContent());
        window.setBottom(hBox);

        Scene scene = new Scene(window, WIDTH, HEIGHT);

        scene.getStylesheets().add(MinesweeperZN.class.getResource("MinesweeperZN.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Minesweeper ZN");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
