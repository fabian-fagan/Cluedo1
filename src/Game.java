import javax.swing.*;
import javax.swing.border.EmptyBorder;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Game class, contains lists of players, weapons and cards as well as methods
 * to check suggestions/accusation refutes.
 */
public class Game extends JFrame implements Display{

    /* =========== fields =========== */
    private List<Player> players;
    private Player currentPlayer;
    private int playerID, playerCount;
    private Suggestion solution;
    private Board board;
    private List<Card> cards;
    private List<Weapon> weapons, allWeapons;
    private List<PCharacter> characters, allCharacters;
    private List<Room> rooms;
    private boolean finished = false;
    private JPanel p;
    private Cell[][] cells;
    JMenuBar menuBar;
    JMenu help, settings;
    JMenuItem exit, restart;
    JRadioButtonMenuItem rbMenuItem;
    JCheckBoxMenuItem cbMenuItem;

    private Game() throws IOException {
        super("Cluedo");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }
        /*
		initialise JFrame
		 */
        setBounds(100, 100, 820, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(0, 0));
        setResizable(true);
        setMinimumSize(this.getSize());//get screen size as java Dimension
        //set preferred size as new height and width
        setVisible(true);
       
        //initialize menu bar
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        settings = new JMenu("Settings");
        menuBar.add(settings);
        help = new JMenu("Help");
        menuBar.add(help);
        exit = new JMenuItem("Exit");
        restart = new JMenuItem("Restart");
        settings.add(exit);
        settings.add(restart);
        exit.addActionListener(new ActionListener() {  public void actionPerformed(ActionEvent arg0) {
            System.exit(0);
          }});

        
        
        board = new Board(this);
        getContentPane().add(board, BorderLayout.CENTER);

        cells = new Cell[24][25];
        playerID = 0;
        /*
         * Initialize all weapon, PCharacter and room card lists Doing both individual
         * lists of weapon, PCharacter and room objects as well as a single list of all
         * cards for testing purposes to see which is better
         */

        players = new ArrayList<Player>();
        weapons = new ArrayList<Weapon>(Board.weapons);
        characters = new ArrayList<PCharacter>(Board.characters);
        rooms = new ArrayList<Room>(Board.rooms);
        Collections.shuffle(weapons);
        Collections.shuffle(rooms);
        Collections.shuffle(characters);

        solution = new Suggestion(weapons.remove(0), characters.remove(0), rooms.remove(0));
        System.out.println(solution.toString());
        List<Card> cardsLeft = new ArrayList<Card>();
        cardsLeft.addAll(weapons);
        cardsLeft.addAll(characters);
        cardsLeft.addAll(rooms);


        playerCount = 0;
        while (playerCount < 3) {
            Scanner sc = new Scanner(System.in);
            System.out.println("How many players? (3-6)");
            playerCount = sc.nextInt();
            System.out.println("Amount of players chosen: " + playerCount);
            board.redraw();
        }
        for (int i = 0; i < playerCount; i++) {
            players.add(new Player(Board.characters.get(i).getName(), i, this));
            System.out.println("Player " + (i + 1) + ": " + players.get(i).toString());
        }

        // Deal the cards
        Collections.shuffle(cardsLeft);
        playerID = 0;
        double cardsInHand = cardsLeft.size() / playerCount;

        for (int i = 0; i < playerCount; i++) {
            Player p = players.get(i);
            for (int j = 0; j < cardsInHand; j++) {
                if (!cardsLeft.isEmpty())
                    p.addToHand(cardsLeft.remove(0));
            }
        }

        for (int i = 0; i < playerCount; i++) {
            for (int j = 0; j < cardsLeft.size() / playerCount; j++) {
                players.get(i).addToHand(cardsLeft.remove(0));
            }
        }

        for (Card c : cardsLeft)
            System.out.println(c.getName());

        // debugging purposes, check the hand of the first player
        System.out.println(players.get(0).printHand());
        // spawn players
        for (int i = 0; i < playerCount; ++i)
            players.get(i).spawn(board.playerSpawns.get(i));

        // spawn items
        for (int i = 0; i < weapons.size(); i++) {
            Cell spawn = board.getItemSpawn().get(i);
            spawn.setWeapon(weapons.get(i));
        }

        System.out.println(board.toString());
        int p = 0;
        while (true) {
            while (p < playerCount) {
                currentPlayer = players.get(p);
                currentPlayer.newTurn();
                p++;
            }
            p = 0;

        }

    }

    /*
     * checks all players hands if it contains any of the three cards if anyone has
     * a card then returns false else true
     *
     */
    public boolean checkSuggestionRefute(Player prosecutor, Suggestion s) {
        System.out.println("Checking refute");
        for (Player p : players) {
            if (p != prosecutor && p.canRefuteSuggestion(s))
                return false;
        }
        System.out.println("No one can refute");
        return true;
    }

    public boolean checkAccusationRefute(Player prosecutor, Accusation s) {
        System.out.println("Checking refute");
        for (Player p : players) {
            if (p != prosecutor && p.canRefuteAccusation(s))
                return false;
        }

        System.out.println("No one can refute"); // does not check if they have guessed correctly yet
        System.out.println("You win!!");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finished = true;
        System.exit(0);
        return true;
    }

    public Board getBoard() {
        return board;

    }

    public void setBoard(Board b) {
        board = b;
        board.redraw();

    }

    public List<PCharacter> getChars() {
        return characters;
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public List<Weapon> getAllWeapons() {
        return allWeapons;
    }

    public List<PCharacter> getAllChars() {
        return allCharacters;
    }

    public static void main(String[] args) {
        try {
            new Game();
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void showPlayerList(List<Player> players, Player currentPlayer) {

    }

    @Override
    public void displayMessage(String message) {

    }
}
