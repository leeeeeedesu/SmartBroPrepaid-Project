import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.*;
import javax.swing.*;

// ang main class natin for today
public class SmartBroPrepaid {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppGUI(new PrepaidSystem()));
    }
}

// OOP domain classes

class UserAccount {
    private final String accountNumber;
    private final String password;
    private double balance;
    private Plan currentPlan;

    public UserAccount(String accountNumber, String password, double balance) {
        this.accountNumber = accountNumber;
        this.password = password;
        this.balance = balance;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getPassword() { return password; }
    public double getBalance() { return balance; }
    public Plan getCurrentPlan() { return currentPlan; }

    public void deposit(double amount) {
        if (amount > 0) balance += amount;
    }

    public void subscribe(Plan price) throws InsufficientBalanceException {
        if (price == null) return;
        if (balance < price.getPrice()) throw new InsufficientBalanceException("Not enough balance to buy " + price.getName());
        balance -= price.getPrice();
        currentPlan = price;
    }

    public void unsubscribe() {
        currentPlan = null;
    }
}

class Plan {
    private final String name;
    private final int gb;
    private final double price;

    public Plan(String name, int gb, double price) {
        this.name = name;
        this.gb = gb;
        this.price = price;
    }

    public String getName() { return name; }
    public int getGb() { return gb; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return name + " (" + gb + "GB) - " + price + " pesos";
    }
}

// exceptions 
class AuthenticationException extends Exception { public AuthenticationException(String m){ super(m); } }
class InsufficientBalanceException extends Exception { public InsufficientBalanceException(String m){ super(m); } }

// System class

class PrepaidSystem {
    private final Map<String, UserAccount> users = new HashMap<>();
    private final java.util.List<Plan> plans = new ArrayList<>();

        // Mga acc. data at mga plans tas ini-initialize sa constructor 
    public PrepaidSystem() {
        users.put("09389550197", new UserAccount(" User 1", "1234", 0.0));
        users.put("0000", new UserAccount(" User 2", "1234", 0.0));

        plans.add(new Plan("1GB for 50 pesos", 1, 50));
        plans.add(new Plan("5GB for 200 pesos", 5, 200));
        plans.add(new Plan("10GB for 350 pesos", 10, 350));
    }
    
    public UserAccount authenticate(String acc, String pwd) throws AuthenticationException {
        if (acc == null || pwd == null) throw new AuthenticationException("Invalid credentials");
        UserAccount u = users.get(acc);
        if (u == null) throw new AuthenticationException("Account does not exist.");
        if (!u.getPassword().equals(pwd)) throw new AuthenticationException("Incorrect password.");
        return u;
    }

    public java.util.List<Plan> getAvailablePlans() { return Collections.unmodifiableList(plans); }
}

// GUI class 

class AppGUI {
    private final PrepaidSystem system;
    private JFrame loginFrame;
    private JFrame homeFrame;
    private UserAccount currentUser;

    public AppGUI(PrepaidSystem system) {
        this.system = system;
        LoginUI();
    }

    private void LoginUI() {
        loginFrame = new JFrame("Login - SmartBroPrepaid System");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(420,180);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setLayout(new java.awt.BorderLayout());
     
        Color loginBg = new Color(46, 125, 50);
        loginFrame.getContentPane().setBackground(loginBg);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(loginBg);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.EAST;
        JLabel accLabel = new JLabel("Account Number: ");
        accLabel.setForeground(Color.WHITE);
        form.add(accLabel, c);
        c.gridx = 1; c.anchor = GridBagConstraints.WEST;
        JTextField accField = new JTextField(15);
        form.add(accField, c);

        c.gridx = 0; c.gridy = 1; c.anchor = GridBagConstraints.EAST;
        JLabel passLabel = new JLabel("Password: ");
        passLabel.setForeground(Color.WHITE);
        form.add(passLabel, c);
        c.gridx = 1; c.anchor = GridBagConstraints.WEST;
        JPasswordField passField = new JPasswordField(15);
        form.add(passField, c);

        JPanel buttons = new JPanel();
        buttons.setBackground(loginBg);
        JButton loginBtn = new JButton("Login");
        JButton cancelBtn = new JButton("Cancel");
       
        loginBtn.setBackground(new Color(27,94,32)); loginBtn.setForeground(Color.WHITE); loginBtn.setOpaque(true); loginBtn.setFocusPainted(false);
        cancelBtn.setBackground(new Color(200,230,201)); cancelBtn.setForeground(Color.BLACK); cancelBtn.setOpaque(true); cancelBtn.setFocusPainted(false);
        buttons.add(loginBtn);
        buttons.add(cancelBtn);

        loginFrame.add(form, java.awt.BorderLayout.CENTER);
        loginFrame.add(buttons, java.awt.BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> {
            String acc = accField.getText().trim();
            String pwd = new String(passField.getPassword()).trim();
            //exception try catch bes
            try {
                if (acc.isEmpty() || pwd.isEmpty()) throw new IllegalArgumentException("Please enter both Account Number and Password.");
                currentUser = system.authenticate(acc, pwd);
                HomeUI();
                loginFrame.setVisible(false);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(loginFrame, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (AuthenticationException ex) {
                JOptionPane.showMessageDialog(loginFrame, ex.getMessage(), "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> { accField.setText(""); passField.setText(""); });

        loginFrame.setVisible(true);
    }
    // Home ui G
    private void HomeUI() {
        homeFrame = new JFrame("SmartBroPrepaid System - Account #" + currentUser.getAccountNumber());
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeFrame.setSize(500,330);
        homeFrame.setLocationRelativeTo(null);
        homeFrame.setLayout(new java.awt.BorderLayout());

        JLabel title = new JLabel("Welcome to SmartBroPrepaid System!", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        homeFrame.add(title, java.awt.BorderLayout.NORTH);

       
        JPanel center = new JPanel(new java.awt.GridBagLayout());
        JButton subscribeBtn = new JButton("Subscribe to Plan");
        JButton addBtn = new JButton("Add Money");
        JButton checkBtn = new JButton("Check Current Plan / Balance");
        JButton viewBtn = new JButton("View Available Plans");
        JButton logoutBtn = new JButton("Logout");
        JButton exitBtn = new JButton("Exit");

        
        Color primary = new Color(70,130,180); 
        Color success = new Color(60,179,113); 
        Color info = new Color(123,104,238); 
        Color warn = new Color(255,255,255); 
        Color danger = new Color(220,20,60); 

        
        subscribeBtn.setBackground(primary); subscribeBtn.setForeground(Color.WHITE); subscribeBtn.setOpaque(true); subscribeBtn.setFocusPainted(false);
        addBtn.setBackground(new Color(255, 193, 7)); addBtn.setForeground(Color.BLACK); addBtn.setOpaque(true); addBtn.setFocusPainted(false);
        checkBtn.setBackground(success); checkBtn.setForeground(Color.WHITE); checkBtn.setOpaque(true); checkBtn.setFocusPainted(false);
        viewBtn.setBackground(info); viewBtn.setForeground(Color.WHITE); viewBtn.setOpaque(true); viewBtn.setFocusPainted(false);
        logoutBtn.setBackground(warn); logoutBtn.setForeground(Color.BLACK); logoutBtn.setOpaque(true); logoutBtn.setFocusPainted(false);
        exitBtn.setBackground(danger); exitBtn.setForeground(Color.WHITE); exitBtn.setOpaque(true); exitBtn.setFocusPainted(false);

        
        Dimension medium = new Dimension(360, 44);
        subscribeBtn.setPreferredSize(medium);
        addBtn.setPreferredSize(medium);
        checkBtn.setPreferredSize(medium);
        viewBtn.setPreferredSize(medium);

        
        logoutBtn.setPreferredSize(new Dimension(120,36));
        exitBtn.setPreferredSize(new Dimension(80,36));

        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0; gbc.fill = java.awt.GridBagConstraints.NONE; gbc.anchor = java.awt.GridBagConstraints.CENTER;
        gbc.insets = new Insets(8,0,8,0);
        gbc.gridy = 0; center.add(subscribeBtn, gbc);
        gbc.gridy = 1; center.add(addBtn, gbc);
        gbc.gridy = 2; center.add(checkBtn, gbc);
        gbc.gridy = 3; center.add(viewBtn, gbc);

        JPanel bottomRow = new JPanel(new FlowLayout());
        bottomRow.add(logoutBtn); bottomRow.add(exitBtn);
        homeFrame.add(center, java.awt.BorderLayout.CENTER);
        homeFrame.add(bottomRow, java.awt.BorderLayout.SOUTH);

        subscribeBtn.addActionListener(e -> showSubscribeDialog());
        addBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(homeFrame, "Enter amount to add funds (pesos):", "Add Money", JOptionPane.PLAIN_MESSAGE);
            if (input == null) return; 
            input = input.trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(homeFrame, "Please enter an amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                double mon = Double.parseDouble(input);
                if (mon <= 0) {
                    JOptionPane.showMessageDialog(homeFrame, "Please enter a positive amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                currentUser.deposit(mon);
                JOptionPane.showMessageDialog(homeFrame, "Added " + mon + " pesos to your balance.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(homeFrame, "Invalid number format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        checkBtn.addActionListener(e -> {
            String msg = "Current Balance: " + currentUser.getBalance() + " pesos";
            if (currentUser.getCurrentPlan() != null) msg = "Current Plan: " + currentUser.getCurrentPlan().getName() + "\n" + msg;
            else msg = "No active plan.\n" + msg;
            int opt = JOptionPane.showOptionDialog(homeFrame, msg, "Account Info",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, new Object[]{"OK", "Unsubscribe"}, "OK");

            if (opt == 1) { 
                if (currentUser.getCurrentPlan() == null) {
                    JOptionPane.showMessageDialog(homeFrame, "You have no active plan to unsubscribe.", "Info", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    int confirm = JOptionPane.showConfirmDialog(homeFrame, "Unsubscribe from " + currentUser.getCurrentPlan().getName() + "?", "Confirm Unsubscribe", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        currentUser.unsubscribe();
                        JOptionPane.showMessageDialog(homeFrame, "You have unsubscribed.", "Unsubscribed", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        viewBtn.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            java.util.List<Plan> plans = system.getAvailablePlans();
            sb.append("Available Plans:\n");
            for (int i=0;i<plans.size();i++) {
                sb.append(i+1).append(". ").append(plans.get(i).toString()).append("\n");
            }
            JOptionPane.showMessageDialog(homeFrame, sb.toString(), "Message", JOptionPane.INFORMATION_MESSAGE);
        });

        logoutBtn.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(homeFrame, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                homeFrame.dispose();
                currentUser = null;
                LoginUI();
            }
        });

        exitBtn.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(homeFrame, "Exit System?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) System.exit(0);
        });

        homeFrame.setVisible(true);
    }

    private void showSubscribeDialog() {
        java.util.List<Plan> plans = system.getAvailablePlans();

        
        JDialog dialog = new JDialog(homeFrame, "Subscribe to Plan", true);
        dialog.setLayout(new java.awt.BorderLayout());

        JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT));
        info.add(new JLabel("Select the plan to subscribe:"));
        dialog.add(info, java.awt.BorderLayout.NORTH);

        
        int cols = Math.min(3, Math.max(1, plans.size()));
        int rows = (plans.size() + cols - 1) / cols;
        JPanel grid = new JPanel(new GridLayout(rows, cols, 8, 8));

        for (Plan p : plans) {
            JButton b = new JButton(p.toString());
            b.addActionListener(ev -> {
                try {
                    currentUser.subscribe(p);
                    JOptionPane.showMessageDialog(homeFrame, "You have subscribed to: " + p.getName(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } catch (InsufficientBalanceException ex) {
                    JOptionPane.showMessageDialog(homeFrame, ex.getMessage(), "Purchase Failed", JOptionPane.ERROR_MESSAGE);
                }
            });
            grid.add(b);
        }

        dialog.add(grid, java.awt.BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        JButton cancelBtn = new JButton("Cancel");
        bottom.add(cancelBtn);
        dialog.add(bottom, java.awt.BorderLayout.SOUTH);
        

        cancelBtn.addActionListener(ev -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(homeFrame);
        dialog.setVisible(true);
    }
}
