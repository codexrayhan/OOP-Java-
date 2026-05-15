import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.math.*;
import java.util.*;

/**
 * Calculator Engine and UI Logic
 */
class Calculator extends JFrame implements KeyListener {

    private BigDecimal num1 = BigDecimal.ZERO;
    private BigDecimal memory = BigDecimal.ZERO;
    private char operator = ' ';
    private boolean operatorPressed = false;
    private boolean freshResult = false;
    private static final MathContext MC = new MathContext(12, RoundingMode.HALF_UP);

    private final DefaultListModel<String> historyModel = new DefaultListModel<>();
    private boolean darkMode = true;

    // Colors
    private Color clrBg = new Color(18, 18, 18);
    private Color clrPanel = new Color(28, 28, 28);
    private Color clrDisplay = new Color(22, 22, 22);
    private Color clrFgMain = new Color(240, 240, 240);
    private Color clrFgSub = new Color(160, 160, 160);
    private Color clrNumBtn = new Color(50, 50, 50);
    private Color clrOpBtn = new Color(41, 128, 185);
    private Color clrFnBtn = new Color(70, 70, 70);
    private Color clrMemBtn = new Color(100, 60, 140);
    private Color clrDangerBtn = new Color(192, 57, 43);
    private Color clrEqualBtn = new Color(39, 174, 96);

    private JLabel expressionLabel, resultLabel;
    private JPanel displayPanel, mainPanel, historyPanel;
    private JList<String> historyList;
    private JScrollPane historyScroll;
    private JButton themeToggle;
    private final java.util.List<JButton> allButtons = new ArrayList<>();

    private static final String[] MEM_ROW = {"MC", "MR", "MS", "M+", "M-"};
    private static final String[][] GRID = {
            {"CLR", "DEL", "%", "1/x"},
            {"x²", "√", "(", ")"},
            {"7", "8", "9", "/"},
            {"4", "5", "6", "*"},
            {"1", "2", "3", "-"},
            {"(-)", "0", ".", "+"}
    };

    public Calculator() {
        super("Advanced Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(460, 720);
        setMinimumSize(new Dimension(420, 660));
        setLayout(new BorderLayout());

        buildUI();
        addKeyListener(this);
        setFocusable(true);
        setVisible(true);
    }

    private void buildUI() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(clrBg);
        mainPanel.add(buildTopBar(), BorderLayout.NORTH);
        mainPanel.add(buildDisplay(), BorderLayout.CENTER);

        JPanel bottomHalf = new JPanel(new BorderLayout());
        bottomHalf.setBackground(clrBg);
        bottomHalf.add(buildMemoryRow(), BorderLayout.NORTH);
        bottomHalf.add(buildButtonGrid(), BorderLayout.CENTER);
        bottomHalf.add(buildEqualRow(), BorderLayout.SOUTH);

        mainPanel.add(bottomHalf, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buildHistoryPanel(), BorderLayout.EAST);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(clrBg);
        bar.setBorder(new EmptyBorder(8, 12, 4, 12));
        JLabel title = new JLabel("CALC");
        title.setForeground(clrFgSub);
        themeToggle = plainBtn("☀ Light", clrFnBtn);
        themeToggle.addActionListener(e -> toggleTheme());
        bar.add(title, BorderLayout.WEST);
        bar.add(themeToggle, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildDisplay() {
        displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        displayPanel.setBackground(clrDisplay);
        displayPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        expressionLabel = new JLabel(" ");
        expressionLabel.setFont(new Font("Courier New", Font.PLAIN, 16));
        expressionLabel.setForeground(clrFgSub);
        expressionLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        resultLabel = new JLabel("0");
        resultLabel.setFont(new Font("Courier New", Font.BOLD, 42));
        resultLabel.setForeground(clrFgMain);
        resultLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        displayPanel.add(Box.createVerticalGlue());
        displayPanel.add(expressionLabel);
        displayPanel.add(Box.createVerticalStrut(6));
        displayPanel.add(resultLabel);
        displayPanel.setPreferredSize(new Dimension(0, 130));
        return displayPanel;
    }

    private JPanel buildMemoryRow() {
        JPanel row = new JPanel(new GridLayout(1, 5, 4, 0));
        row.setBackground(clrPanel);
        row.setBorder(new EmptyBorder(6, 8, 4, 8));
        for (String lbl : MEM_ROW) {
            JButton b = styledBtn(lbl, clrMemBtn, Color.WHITE);
            b.addActionListener(e -> handleMemory(lbl));
            row.add(b);
            allButtons.add(b);
        }
        return row;
    }

    private JPanel buildButtonGrid() {
        JPanel grid = new JPanel(new GridLayout(GRID.length, 4, 5, 5));
        grid.setBackground(clrPanel);
        grid.setBorder(new EmptyBorder(4, 8, 4, 8));
        for (String[] row : GRID) {
            for (String lbl : row) {
                JButton b = styledBtn(lbl, buttonColor(lbl), Color.WHITE);
                b.addActionListener(e -> handleInput(lbl));
                grid.add(b);
                allButtons.add(b);
            }
        }
        return grid;
    }

    private JPanel buildEqualRow() {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(clrPanel);
        row.setBorder(new EmptyBorder(4, 8, 10, 8));
        JButton eq = styledBtn("=", clrEqualBtn, Color.WHITE);
        eq.setPreferredSize(new Dimension(0, 54));
        eq.addActionListener(e -> handleInput("="));
        allButtons.add(eq);
        row.add(eq, BorderLayout.CENTER);
        return row;
    }

    private JPanel buildHistoryPanel() {
        historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(new Color(15, 15, 15));
        historyPanel.setPreferredSize(new Dimension(140, 0));

        historyList = new JList<>(historyModel);
        historyList.setBackground(new Color(15, 15, 15));
        historyList.setForeground(clrFgSub);
        historyList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String entry = historyList.getSelectedValue();
                    if (entry != null && entry.contains("=")) {
                        String res = entry.substring(entry.lastIndexOf('=') + 1).trim();
                        setDisplay(res, "");
                        freshResult = true;
                    }
                }
            }
        });

        historyScroll = new JScrollPane(historyList);
        historyScroll.getViewport().setBackground(new Color(15, 15, 15));
        JButton clearHist = plainBtn("Clear", new Color(50, 30, 30));
        clearHist.addActionListener(e -> historyModel.clear());

        historyPanel.add(new JLabel(" History"), BorderLayout.NORTH);
        historyPanel.add(historyScroll, BorderLayout.CENTER);
        historyPanel.add(clearHist, BorderLayout.SOUTH);
        return historyPanel;
    }

    private void handleInput(String input) {
        String cur = resultLabel.getText().replace(",", "");
        switch (input) {
            case "CLR" -> clearAll();
            case "DEL" -> {
                if (freshResult) { clearAll(); return; }
                if (cur.length() <= 1) { setDisplay("0", expressionLabel.getText()); return; }
                setDisplay(cur.substring(0, cur.length() - 1), expressionLabel.getText());
            }
            case "=" -> calculate(cur);
            case "(-)" -> {
                BigDecimal v = new BigDecimal(cur).negate();
                setDisplay(format(v), expressionLabel.getText());
            }
            case "%" -> {
                BigDecimal v = new BigDecimal(cur).divide(BigDecimal.valueOf(100), MC);
                setDisplay(format(v), "");
            }
            case "x²" -> {
                BigDecimal v = new BigDecimal(cur).pow(2, MC);
                setDisplay(format(v), cur + "²");
                addHistory(cur + "² = " + format(v));
            }
            case "√" -> {
                BigDecimal v = new BigDecimal(cur);
                if (v.compareTo(BigDecimal.ZERO) < 0) { setDisplay("Error", ""); return; }
                BigDecimal r = BigDecimal.valueOf(Math.sqrt(v.doubleValue())).round(MC);
                setDisplay(format(r), "√" + cur);
                addHistory("√" + cur + " = " + format(r));
            }
            case "1/x" -> {
                BigDecimal v = new BigDecimal(cur);
                if (v.compareTo(BigDecimal.ZERO) == 0) { setDisplay("Div by 0", ""); return; }
                BigDecimal r = BigDecimal.ONE.divide(v, 10, RoundingMode.HALF_UP);
                setDisplay(format(r), "1/" + cur);
                addHistory("1/" + cur + " = " + format(r));
            }
            case "." -> { if (!cur.contains(".")) setDisplay(cur + ".", expressionLabel.getText()); }
            case "+", "-", "*", "/" -> pressOperator(input.charAt(0), cur);
            default -> {
                if (freshResult) { freshResult = false; setDisplay(input, ""); }
                else if (cur.equals("0")) setDisplay(input, expressionLabel.getText());
                else setDisplay(cur + input, expressionLabel.getText());
            }
        }
    }

    private void pressOperator(char op, String cur) {
        if (!operatorPressed) {
            num1 = new BigDecimal(cur);
            operator = op;
            operatorPressed = true;
            freshResult = false;
            setDisplay("0", format(num1) + " " + op + " ");
        } else {
            operator = op;
            String expr = expressionLabel.getText();
            if (expr.length() >= 3) {
                expr = expr.substring(0, expr.length() - 2) + op + " ";
                expressionLabel.setText(expr);
            }
        }
    }

    private void calculate(String curStr) {
        if (!operatorPressed) return;
        BigDecimal num2 = new BigDecimal(curStr);
        BigDecimal result;
        String expr = expressionLabel.getText() + curStr;
        try {
            result = switch (operator) {
                case '+' -> num1.add(num2, MC);
                case '-' -> num1.subtract(num2, MC);
                case '*' -> num1.multiply(num2, MC);
                case '/' -> num1.divide(num2, 10, RoundingMode.HALF_UP);
                default -> num2;
            };
        } catch (Exception e) { setDisplay("Error", ""); return; }

        String resStr = format(result);
        setDisplay(resStr, expr + " =");
        addHistory(expr + " = " + resStr);
        num1 = result;
        operatorPressed = false;
        freshResult = true;
    }

    private void handleMemory(String op) {
        String cur = resultLabel.getText().replace(",", "");
        BigDecimal val = new BigDecimal(cur);
        switch (op) {
            case "MS" -> memory = val;
            case "MR" -> setDisplay(format(memory), expressionLabel.getText());
            case "M+" -> memory = memory.add(val, MC);
            case "M-" -> memory = memory.subtract(val, MC);
            case "MC" -> memory = BigDecimal.ZERO;
        }
    }

    private void clearAll() {
        num1 = BigDecimal.ZERO;
        operator = ' ';
        operatorPressed = false;
        freshResult = false;
        setDisplay("0", " ");
    }

    private void setDisplay(String res, String expr) {
        resultLabel.setText(res);
        expressionLabel.setText(expr.isEmpty() ? " " : expr);
    }

    private String format(BigDecimal v) {
        return v.stripTrailingZeros().toPlainString();
    }

    private void addHistory(String entry) {
        historyModel.add(0, entry);
        if (historyModel.size() > 20) historyModel.remove(historyModel.size() - 1);
    }

    private void toggleTheme() {
        darkMode = !darkMode;
        if (darkMode) {
            clrBg = new Color(18, 18, 18); clrDisplay = new Color(22, 22, 22);
            clrFgMain = new Color(240, 240, 240); clrNumBtn = new Color(50, 50, 50);
            themeToggle.setText("☀ Light");
        } else {
            clrBg = new Color(240, 240, 240); clrDisplay = new Color(255, 255, 255);
            clrFgMain = new Color(20, 20, 20); clrNumBtn = new Color(200, 200, 200);
            themeToggle.setText("☾ Dark");
        }
        mainPanel.setBackground(clrBg);
        displayPanel.setBackground(clrDisplay);
        resultLabel.setForeground(clrFgMain);
        for (JButton b : allButtons) {
            if (b.getText().matches("[0-9.]")) {
                b.setBackground(clrNumBtn);
                b.setForeground(darkMode ? Color.WHITE : Color.BLACK);
            }
        }
    }

    private Color buttonColor(String lbl) {
        return switch (lbl) {
            case "CLR", "DEL" -> clrDangerBtn;
            case "+", "-", "*", "/" -> clrOpBtn;
            case "%", "x²", "√", "1/x", "(-)" -> clrFnBtn;
            default -> clrNumBtn;
        };
    }

    private JButton styledBtn(String label, Color bg, Color fg) {
        JButton b = new JButton(label);
        b.setFont(new Font("Segoe UI", Font.BOLD, 18));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusable(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        return b;
    }

    private JButton plainBtn(String label, Color bg) {
        JButton b = new JButton(label);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusable(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        return b;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();
        if (Character.isDigit(c) || "+-*/.=%".indexOf(c) >= 0) handleInput(String.valueOf(c));
        else if (c == '\n') handleInput("=");
        else if (c == '\b') handleInput("DEL");
        else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) handleInput("CLR");
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}

