import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MiniF1 extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel root = new JPanel(cardLayout);
    private final LoginPanel loginPanel = new LoginPanel();
    private final SetupPanel setupPanel = new SetupPanel();
    private final GamePanel gamePanel = new GamePanel();

    private static enum GamePhase { REACTION_WAIT, REACTION_GO, PLAYING, FINISHED }

    public MiniF1() {
        super("Mini F1");
        root.add(loginPanel, "LOGIN");
        root.add(setupPanel, "SETUP");
        root.add(gamePanel, "GAME");
        setContentPane(root);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        cardLayout.show(root, "LOGIN");
    }

    // ── LOGIN PANEL ────────────────────────────────────────────────────────────
    private class LoginPanel extends JPanel {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JButton loginButton;
        private JButton registerButton;
        private JLabel status;

        // set a hashmap named accounts to add and store all users
        private HashMap<String, String> accounts = new HashMap<>();

        public LoginPanel() {
            loadUsersFromFile();
            setPreferredSize(new Dimension(900, 600));
            setLayout(new GridBagLayout());
            setBackground(Color.BLACK);

            JLabel title = new JLabel("Mini F1");
            title.setForeground(Color.RED);
            title.setFont(new Font("Arial", Font.BOLD, 28));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            add(title, gbc);

            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.WEST;

            gbc.gridy = 1;
            gbc.gridx = 0;
            JLabel userLbl = new JLabel("Username:");
            userLbl.setForeground(Color.WHITE);
            add(userLbl, gbc);

            gbc.gridx = 1;
            usernameField = new JTextField(15);
            add(usernameField, gbc);

            gbc.gridy = 2;
            gbc.gridx = 0;
            JLabel passLbl = new JLabel("Password:");
            passLbl.setForeground(Color.WHITE);
            add(passLbl, gbc);

            gbc.gridx = 1;
            passwordField = new JPasswordField(15);
            passwordField.setEchoChar('!');
            add(passwordField, gbc);

            gbc.gridy = 3;
            gbc.gridx = 0;
            loginButton = new JButton("Login");
            add(loginButton, gbc);

            gbc.gridx = 1;
            registerButton = new JButton("Register");
            add(registerButton, gbc);

            gbc.gridy = 4;
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            status = new JLabel("VRooooooooooooooooooooooooooooM");
            status.setForeground(Color.RED);
            status.setFont(new Font("Arial", Font.PLAIN, 16));
            add(status, gbc);

            // add action listeners for login and register
            loginButton.addActionListener(e -> loginUser());
            registerButton.addActionListener(e -> registerUser());
        }

        private void loginUser() {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) { // if a field is empty
                status.setText("Fields cannot be empty");
                return;
            }
            if (!accounts.containsKey(username)) { // If the user is not in accounts
                status.setText("User not found");
                return;
            }
            if (accounts.get(username).equals(password)) { // if user is in accounts
                status.setText("Login successful!"); // successful and play game
                cardLayout.show(root, "SETUP");
            } else {
                status.setText("Incorrect password"); // not valid
            }
        }

        private void registerUser() {
            String username = usernameField.getText().trim(); // get just user string
            String password = new String(passwordField.getPassword()).trim(); // get just password string

            if (username.isEmpty() || password.isEmpty()) { // if a field is empty
                status.setText("Fields cannot be empty");
                return;
            }
            if (accounts.containsKey(username)) { // if user is already in accounts
                status.setText("User already exists");
                return;
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter("users.txt", true))) {
                writer.println(username + "," + password); // write user to users.txt
            } catch (IOException e) {
                System.out.println("error file write users.txt");
            }

            accounts.put(username, password); // put the user into accounts, name and password
            status.setText("Register successful!"); // say successful and submit
            usernameField.setText("");
            passwordField.setText("");
        }

        private void loadUsersFromFile() { // this is needed to load users from users.txt into accounts when restarting
            File file = new File("users.txt");
            if (!file.exists()) return;

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(",")) {
                        String[] parts = line.split(",");
                        accounts.put(parts[0], parts[1]); // load user into accounts
                    }
                }
            } catch (IOException e) {
                System.out.println("error users.txt");
            }
        }
    }

    // ── SETUP PANEL ────────────────────────────────────────────────────────────
    private class SetupPanel extends JPanel {
        private final JComboBox<String> teamBox = new JComboBox<>(new String[]{
                "Ferrari", "Mercedes", "Red Bull", "McLaren", "Aston Martin", // list of all the teams
                "Alpine", "Williams", "VRB", "Audi", "Haas", "Cadillac"
        });
        private final JTextField numberField = new JTextField("16", 10);
        private final JComboBox<String> countryBox = new JComboBox<>(new String[]{// add list of countries to race in
                "Italy", "Japan", "Brazil", "Qatar", "Las Vegas", "Miami","Monaco",
                "COTA", "Belguim"
        });


        SetupPanel() {
            setPreferredSize(new Dimension(900, 600));
            setLayout(new GridBagLayout());
            setBackground(new Color(14, 16, 18));

            JLabel title = new JLabel("Customization");
            title.setForeground(Color.WHITE);
            title.setFont(new Font("Arial", Font.BOLD, 24));

            JLabel teamLabel = new JLabel("Team:");
            teamLabel.setForeground(Color.LIGHT_GRAY);

            JLabel numberLabel = new JLabel("Number:");
            numberLabel.setForeground(Color.LIGHT_GRAY);

            JLabel countryLabel = new JLabel("Country:"); // country label
            countryLabel.setForeground(Color.LIGHT_GRAY);


            JButton startBtn = new JButton("Start Race");
            startBtn.addActionListener(e -> {
                String team = (String) teamBox.getSelectedItem();
                int num = parseDriverNumber(numberField.getText());
                String country = (String) countryBox.getSelectedItem(); // get selected country

                gamePanel.startNewGame(team, num, country);
                cardLayout.show(root, "GAME");
                gamePanel.requestFocusInWindow();
            });

            JButton backBtn = new JButton("Back to Login");
            backBtn.addActionListener(e -> cardLayout.show(root, "LOGIN"));

            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(10, 10, 10, 10);

            gc.gridx = 0;
            gc.gridy = 0;
            gc.gridwidth = 2;
            add(title, gc);

            gc.gridwidth = 1;

            gc.gridx = 0;
            gc.gridy = 1;
            gc.anchor = GridBagConstraints.EAST;
            add(teamLabel, gc);

            gc.gridx = 1;
            gc.gridy = 1;
            gc.anchor = GridBagConstraints.WEST;
            add(teamBox, gc);

            gc.gridx = 0;
            gc.gridy = 2;
            gc.anchor = GridBagConstraints.EAST;
            add(numberLabel, gc);

            gc.gridx = 1;
            gc.gridy = 2;
            gc.anchor = GridBagConstraints.WEST;
            add(numberField, gc);

            gc.gridx = 0; // new country add to screen
            gc.gridy = 3;
            gc.anchor = GridBagConstraints.EAST;
            add(countryLabel, gc);

            gc.gridx = 1;
            gc.gridy = 3;
            gc.anchor = GridBagConstraints.WEST;
            add(countryBox, gc);


            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
            btnPanel.setOpaque(false);
            btnPanel.add(backBtn);
            btnPanel.add(startBtn);

            gc.gridx = 0;
            gc.gridy = 4;
            gc.gridwidth = 2;
            gc.anchor = GridBagConstraints.CENTER;
            add(btnPanel, gc);

            JLabel note = new JLabel("Pick a team, number, and where to race");
            note.setForeground(new Color(170, 170, 180));
            gc.gridy = 5;
            add(note, gc);
        }

        private int parseDriverNumber(String s) {
            try {
                int n = Integer.parseInt(s.trim());
                if (n < 0) return 0;
                if (n > 99) return 99;
                return n;
            } catch (Exception ex) {
                return 0;
            }
        }
    }

    private static class Obstacle {
        Rectangle rect;
        int speedY;
        Image img;   // add image so it can be a png

        Obstacle(Rectangle r, int vy, Image img) {
            this.rect = r;
            this.speedY = vy;
            this.img = img;
        }
    }

    // power up extends obstace class
    private static class PowerUp extends Obstacle {
        PowerUp(Rectangle r, int vy, Image img) {
            super(r, vy, img);
        }
    }

    // ── GAME PANEL ─────────────────────────────────────────────────────────────
    private class GamePanel extends JPanel implements ActionListener, KeyListener {
        private static final int WIDTH = 900;
        private static final int HEIGHT = 600;


        private static final int GRASS = 80;
        private static final int KERB = 60;
        private static final int ROAD_LEFT = GRASS + KERB;
        private static final int ROAD_RIGHT = WIDTH - GRASS - KERB;

        private static final int CAR_WIDTH = 46;
        private static final int CAR_HEIGHT = 90;

        private Image playerImg;  // the PNG image
        private Image[] obstacleCarImgs; // the PNG images for obstacles
        private Image powerUpImg; // the PNG image for power ups

        private Color EdgeC; // edge colour
        private Color BarC; // barrier colour

        private boolean shield = false;

        private int baseSpeed = 5;

        private final Rectangle player = new Rectangle(0, 0, CAR_WIDTH, CAR_HEIGHT);

        private boolean left, right, up, down;

        private static int MOVE_SPEED = 6;

        private final List<Obstacle> obstacles = new ArrayList<>();
        private final Random random = new Random();
        private final Timer timer = new Timer(16, this);

        private int spawnTimer = 0;
        private int score = 0;
        private boolean gameOver = false;

        private String team = "";
        private int driverNumber = 0;
        private String country = "";

        private GamePhase phase = GamePhase.REACTION_WAIT;

        private long goTimestamp = -1;
        private long scheduledGoTime = -1;
        private boolean falseStart = false;
        private int reactionPoints = 0;

        GamePanel() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setFocusable(true);
            setBackground(Color.BLACK);
            addKeyListener(this);

            loadObstacleCarImages();// load it
            powerUpImg = new ImageIcon("Power_up.png").getImage(); 
        }

        // This method returns the colour based on the selected country and is used in startNewGame function
        private Color GrassColour(String countryName) { 
            if ("Italy".equals(countryName)){
                   return Color.decode("#29C253"); // we have to use color.decode to accept a string
            }
            if ("Japan".equals(countryName)){
                   return Color.decode("#E2A1E3");
            }
            if ("Brazil".equals(countryName)){
                   return Color.decode("#009B3A");
            }
            if ("Qatar".equals(countryName)){
                   return Color.decode("#BD8C28");
            }
            if ("Las Vegas".equals(countryName)){
                   return Color.decode("#000000"); 
            }
            if ("Miami".equals(countryName)){
                   return Color.decode("#5AC2AD");
            }
            if ("Monaco".equals(countryName)){
                   return Color.decode("#4D8AB8"); 
            }
            if ("COTA".equals(countryName)){
                   return Color.decode("#F02E26"); 
            }
            if ("Belguim".equals(countryName)){
                   return Color.decode("#FFF200"); 
            }
            return Color.GREEN; // default
        }

         // This method returns the colour based on the selected country and is used in startNewGame function
        private Color BarrierColour(String countryName) { 
            if ("Italy".equals(countryName)){
                   return Color.decode("#EB2117"); // we have to use color.decode to accept a string
            }
            if ("Japan".equals(countryName)){
                   return Color.decode("#239e29");
            }
            if ("Brazil".equals(countryName)){
                   return Color.decode("#F5FC17");
            }
            if ("Qatar".equals(countryName)){
                   return Color.decode("#990909");
            }
            if ("Las Vegas".equals(countryName)){
                   return Color.decode("#C7C7C7"); 
            }
            if ("Miami".equals(countryName)){
                   return Color.decode("#FC4C02");
            }
            if ("Monaco".equals(countryName)){
                   return Color.decode("#545252"); 
            }
            if ("COTA".equals(countryName)){
                   return Color.decode("#2525CF"); 
            }
            if ("Belguim".equals(countryName)){
                   return Color.decode("#F01616"); 
            }
            return Color.GREEN; // default
        }

        // This method returns the car image based on the selected team and is used in startNewGame function
        private Image TeamCar(String teamName) {
            if ("Ferrari".equals(teamName)){
                   return new ImageIcon("f1_car_Ferrari.png").getImage();
            }
            if ("Red Bull".equals(teamName)){
                return new ImageIcon("f1_car_RedBull.png").getImage();
            }
            if ("Mercedes".equals(teamName)){
                  return new ImageIcon("f1_car_Mercedes.png").getImage();
            }
            if ("McLaren".equals(teamName)){
                return new ImageIcon("f1_car_Mclaren.png").getImage();
            }
            if ("Alpine".equals(teamName)){
                   return new ImageIcon("f1_car_Alpine.png").getImage();
            }
            if ("Aston Martin".equals(teamName)){
                return new ImageIcon("f1_car_AstonMartin.png").getImage();
            }
            if ("Audi".equals(teamName)){
                  return new ImageIcon("f1_car_Audi.png").getImage();
            }
            if ("Cadillac".equals(teamName)){
                return new ImageIcon("f1_car_Cadillac.png").getImage();
            }
            if ("Haas".equals(teamName)){
                  return new ImageIcon("f1_car_Haas.png").getImage();
            }
            if ("Williams".equals(teamName)){
                return new ImageIcon("f1_car_Williams.png").getImage();
            }
            if ("VRB".equals(teamName)){
                   return new ImageIcon("f1_car_VRB.png").getImage();
            }

            // default 
            return new ImageIcon("f1_car_Ferrari.png").getImage();
        }

        // this method has all the images here so that we can use it to pick a random one
        private void loadObstacleCarImages() {
            obstacleCarImgs = new Image[] {
                new ImageIcon("f1_car_Ferrari.png").getImage(),
                new ImageIcon("f1_car_Mercedes.png").getImage(),
                new ImageIcon("f1_car_RedBull.png").getImage(),
                new ImageIcon("f1_car_Mclaren.png").getImage(),
                new ImageIcon("f1_car_AstonMartin.png").getImage(),
                new ImageIcon("f1_car_Alpine.png").getImage(),
                new ImageIcon("f1_car_Williams.png").getImage(),
                new ImageIcon("f1_car_VRB.png").getImage(),
                new ImageIcon("f1_car_Audi.png").getImage(),
                new ImageIcon("f1_car_Haas.png").getImage(),
                new ImageIcon("f1_car_Cadillac.png").getImage()
            };
        }

        


        void startNewGame(String teamName, int number, String countryName) {
            this.team = teamName;
            this.driverNumber = number;
            this.country = countryName; // add countryname to constructor

            playerImg = TeamCar(teamName);
            this.EdgeC = GrassColour(countryName);
            this.BarC = BarrierColour(countryName);

            score = 0;
            gameOver = false;
            obstacles.clear();
            left = right = up = down = false;

            int startX = (ROAD_LEFT + ROAD_RIGHT - CAR_WIDTH) / 2;
            int startY = HEIGHT - 180;
            player.setBounds(startX, startY, CAR_WIDTH, CAR_HEIGHT);

            phase = GamePhase.REACTION_WAIT;
            falseStart = false;
            reactionPoints = 0;
            goTimestamp = -1;

            long now = System.currentTimeMillis();
            scheduledGoTime = now + 1000 + random.nextInt(3000); // 1–4 seconds

            spawnTimer = 50; // initial delay
            timer.start();
            requestFocusInWindow();
            repaint();
        }

        private void beginRace() {
            if (reactionPoints > 0) {
                score += reactionPoints;
            }
            phase = GamePhase.PLAYING;
            spawnTimer = 40;
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            //grass
            g2.setColor(EdgeC);
            g2.fillRect(0, 0, GRASS, HEIGHT);
            g2.fillRect(WIDTH - GRASS, 0, GRASS, HEIGHT);

            // Asphalt 
            g2.setColor(new Color(105, 105, 105));
            g2.fillRect(GRASS, 0, WIDTH - GRASS * 2, HEIGHT);

            // Kerbs
            g2.setColor(BarC);
            g2.fillRect(GRASS, 0, KERB, HEIGHT);
            g2.fillRect(WIDTH - GRASS - KERB, 0, KERB, HEIGHT);

            // Obstacles 
            for (Obstacle o : obstacles) { // for every obstacle in obstacle list
                if (o.img != null) { // check if it has an image and then draw it on screen
                    g2.drawImage(o.img, o.rect.x, o.rect.y, o.rect.width, o.rect.height, null);
                } 
            }


            // Player car if player picked a team display that img
            g2.drawImage(playerImg, player.x, player.y, player.width, player.height, null);
            
            // Shield visual
            if (shield) {
                g2.setColor(Color.BLUE);
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(player.x - 6, player.y - 6, player.width + 6 * 2, player.height + 6 * 2);
            }

            // Driver number
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            String num = String.valueOf(driverNumber);
            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(num);
            g2.drawString(num,
                    player.x + (player.width - w) / 2,
                    player.y + player.height / 2 + 6);

            // HUD
            g2.setFont(new Font("Arial", Font.BOLD, 18)); // add country to top left
            g2.drawString("Team: " + team + "  #" + driverNumber + "  (" + country + ")", 160, 30);
            if (phase == GamePhase.PLAYING || phase == GamePhase.FINISHED) {
                g2.drawString("Score: " + score, 160, 60);
            }

            // Reaction overlay
            if (phase == GamePhase.REACTION_WAIT || phase == GamePhase.REACTION_GO) {
                g2.setColor(new Color(0, 0, 0, 160));
                g2.fillRect(0, 0, WIDTH, HEIGHT);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 48));
                String title = "REACTION TEST";
                int tw = g2.getFontMetrics().stringWidth(title);
                g2.drawString(title, (WIDTH - tw) / 2, HEIGHT / 2 - 90);

                g2.setFont(new Font("Arial", Font.BOLD, 42));
                String text = (phase == GamePhase.REACTION_GO) ? "GO!!!" : "WAIT...";
                tw = g2.getFontMetrics().stringWidth(text);
                g2.drawString(text, (WIDTH - tw) / 2, HEIGHT / 2);

                g2.setFont(new Font("Arial", Font.PLAIN, 18));
                g2.setColor(new Color(220, 220, 220));
                String tip = "Press SPACE as soon as you see GO!   Faster = more points";
                tw = g2.getFontMetrics().stringWidth(tip);
                g2.drawString(tip, (WIDTH - tw) / 2, HEIGHT / 2 + 60);
            }

            // Game over overlay
            if (phase == GamePhase.FINISHED) {
                g2.setColor(new Color(0, 0, 0, 160));
                g2.fillRect(0, 0, WIDTH, HEIGHT);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 54));
                String over = "GAME OVER";
                int ow = g2.getFontMetrics().stringWidth(over);
                g2.drawString(over, (WIDTH - ow) / 2, HEIGHT / 2 - 30);

                g2.setFont(new Font("Arial", Font.PLAIN, 20));
                String instr = "Press R to restart   •   ESC to menu";
                int iw = g2.getFontMetrics().stringWidth(instr);
                g2.drawString(instr, (WIDTH - iw) / 2, HEIGHT / 2 + 40);
            }

            g2.dispose();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameOver) {
                repaint();
                return;
            }

            long now = System.currentTimeMillis();

            if (phase == GamePhase.REACTION_WAIT) {
                if (now >= scheduledGoTime) {
                    phase = GamePhase.REACTION_GO;
                    goTimestamp = now;
                }
                repaint();
                return;
            }

            if (phase == GamePhase.REACTION_GO) {
                repaint();
                return;
            }

            if (phase == GamePhase.PLAYING) {
                // Player movement
                int dx = 0, dy = 0;
                if (left)  dx -= MOVE_SPEED;
                if (right) dx += MOVE_SPEED;
                if (up)    dy -= MOVE_SPEED;
                if (down)  dy += MOVE_SPEED;

                player.x += dx;
                player.y += dy;

                // Keep inside road horizontally, generous vertical range
                player.x = Math.max(ROAD_LEFT, Math.min(ROAD_RIGHT - CAR_WIDTH, player.x));
                player.y = Math.max(10, Math.min(HEIGHT - CAR_HEIGHT - 10, player.y));

                // Spawn new obstacle
                // Spawn new obstacle (and sometimes a power-up)
                spawnTimer--;
                if (spawnTimer <= 0) {
                    spawnObstacle();

                    // 20% chance to spawn a power-up 
                    if (random.nextInt(5) == 0) {
                        spawnPowerUp();
                    }
                    int base = 48;
                    int faster = Math.max(20, base - score / 4);
                    spawnTimer = faster;
                }

                // Move & collide obstacles
                for (int i = obstacles.size() - 1; i >= 0; i--) {
                    Obstacle obs = obstacles.get(i);
                    obs.rect.y += obs.speedY;
                    if (obs.rect.intersects(player)) {

                        // if obstacle is an instance of power up then give player a power up and dont end the game
                        if (obs instanceof PowerUp) {
                            score += 1;
                            obstacles.remove(i);
                            // give random power up of the 4
                            int r = random.nextInt(4);
                            if (r == 0) { // increase movement speed of player
                                resetSpeed();
                                MOVE_SPEED = 12;
                            } 
                            else if (r == 1) {// increase movement speed of obstacles
                                resetSpeed();
                                baseSpeed =20;
                            } 
                            else if (r == 2) { // player gets a puncture and is very slow
                                resetSpeed();
                                MOVE_SPEED = 2;
                            }
                            else if (r == 3) { // give shield
                                resetSpeed();
                                shield = true;
                            }
                            continue;
                        }
                        if (shield) {// if player has a shield then they dont die
                            shield = false;
                            obstacles.remove(i);
                            continue;
                        }
                        
                        resetSpeed(); // if player dies reset speeds
                        gameOver = true;
                        phase = GamePhase.FINISHED;
                        timer.stop();

                        // when the game ends write the score and customizations to score.txt
                        try (PrintWriter writer = new PrintWriter(new FileWriter("score.txt", true))) {
                            writer.println("Score: " + score +". " + team + ", #: " + driverNumber + ", " + country);
                        } catch (IOException z) {
                            System.out.println("error");
                        }
                        break;
                    }
                    if (obs.rect.y > HEIGHT) {
                        obstacles.remove(i);
                        score++;
                    }
                }
                repaint();
            }
        }

        // resets speed for powerups
        private void resetSpeed() {
            MOVE_SPEED = 6;
            baseSpeed =5;
        }

        private void spawnObstacle() {
            int xMin = ROAD_LEFT + 8;
            int xMax = ROAD_RIGHT - CAR_WIDTH - 8;
            int x = xMin + random.nextInt(Math.max(1, xMax - xMin + 1));
            int y = -CAR_HEIGHT - random.nextInt(180);

            int extra = Math.min(6, score / 25);
            int vy = baseSpeed + random.nextInt(4) + extra;

            Rectangle r = new Rectangle(x, y, CAR_WIDTH, CAR_HEIGHT);
            
            // pick a random PNG from the 11
            Image img = obstacleCarImgs[random.nextInt(obstacleCarImgs.length)]; 
            obstacles.add(new Obstacle(r, vy, img));//update
        }

        // create spawn power up method that is basically the same as spawn obstacle
        private void spawnPowerUp() {
            int xMin = ROAD_LEFT + 8;
            int xMax = ROAD_RIGHT - CAR_WIDTH - 8;
            int x = xMin + random.nextInt(Math.max(1, xMax - xMin + 1));
            int y = -CAR_HEIGHT - random.nextInt(250);

            int extra = Math.min(6, score / 25);
            int vy = baseSpeed + random.nextInt(4) + extra;

            int s = 40; // square 
            Rectangle r = new Rectangle(x, y, s, s);

            // Add as a PowerUp (extends Obstacle)
            obstacles.add(new PowerUp(r, vy, powerUpImg));
        }


        private void calculateReactionBonus(long pressTime) {
            if (falseStart) {
                reactionPoints = 0;
                beginRace();
                return;
            }
            long ms = pressTime - goTimestamp;
            if (ms < 0) ms = 0;
            if      (ms <= 130) reactionPoints = 30;
            else if (ms <= 190) reactionPoints = 20;
            else if (ms <= 260) reactionPoints = 12;
            else if (ms <= 340) reactionPoints = 7;
            else if (ms <= 450) reactionPoints = 4;
            else                reactionPoints = 1;
            beginRace();
        }

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();

            if (code == KeyEvent.VK_ESCAPE) {
                timer.stop();
                cardLayout.show(root, "SETUP");
                return;
            }

            if (code == KeyEvent.VK_R && phase == GamePhase.FINISHED) {
                startNewGame(team, driverNumber, country);
                return;
            }

            if (code == KeyEvent.VK_SPACE) {
                long now = System.currentTimeMillis();
                if (phase == GamePhase.REACTION_WAIT) {
                    falseStart = true;
                    reactionPoints = 0;
                    beginRace();
                } else if (phase == GamePhase.REACTION_GO) {
                    calculateReactionBonus(now);
                }
                return;
            }

            if (phase != GamePhase.PLAYING) return;

            if (code == KeyEvent.VK_LEFT  || code == KeyEvent.VK_A) left  = true;
            if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) right = true;
            if (code == KeyEvent.VK_UP    || code == KeyEvent.VK_W) up    = true;
            if (code == KeyEvent.VK_DOWN  || code == KeyEvent.VK_S) down  = true;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int code = e.getKeyCode();
            if (code == KeyEvent.VK_LEFT  || code == KeyEvent.VK_A) left  = false;
            if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) right = false;
            if (code == KeyEvent.VK_UP    || code == KeyEvent.VK_W) up    = false;
            if (code == KeyEvent.VK_DOWN  || code == KeyEvent.VK_S) down  = false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MiniF1().setVisible(true));
    }
}

