
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Calculator implements ActionListener, KeyListener {

    JFrame frame;
    JTextField textfield;
    JButton[] numberButtons = new JButton[10];
    JButton[] functionButtons = new JButton[9];
    JButton addButton, subButton, mulButton, divButton;
    JButton decButton, equalButton, delButton, clrButton, negButton;
    JPanel panel;

    Font myfont = new Font("Times New Roman", Font.BOLD, 30);

    double num1 = 0, num2 = 0, result = 0;
    char operator;
    boolean isOperatorPressed = false;

    Calculator() {

        frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 550);
        frame.setLayout(null);

        textfield = new JTextField();
        textfield.setBounds(50, 25, 300, 50);
        textfield.setFont(myfont);
        textfield.setEditable(false);
        textfield.addKeyListener(this);

        addButton = new JButton("+");
        subButton = new JButton("-");
        mulButton = new JButton("*");
        divButton = new JButton("/");
        decButton = new JButton(".");
        equalButton = new JButton("=");
        delButton = new JButton("DEL");
        clrButton = new JButton("CLR");
        negButton = new JButton("(-)");

        functionButtons[0] = addButton;
        functionButtons[1] = subButton;
        functionButtons[2] = mulButton;
        functionButtons[3] = divButton;
        functionButtons[4] = decButton;
        functionButtons[5] = equalButton;
        functionButtons[6] = delButton;
        functionButtons[7] = clrButton;
        functionButtons[8] = negButton;

        for (int i = 0; i < 9; i++) {
            functionButtons[i].addActionListener(this);
            functionButtons[i].setFont(myfont);
            functionButtons[i].setFocusable(false);
        }

        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new JButton(String.valueOf(i));
            numberButtons[i].addActionListener(this);
            numberButtons[i].setFont(myfont);
            numberButtons[i].setFocusable(false);
        }

        negButton.setBounds(50, 430, 100, 50);
        delButton.setBounds(150, 430, 100, 50);
        clrButton.setBounds(250, 430, 100, 50);

        panel = new JPanel();
        panel.setBounds(50, 100, 300, 300);
        panel.setLayout(new GridLayout(4, 4, 10, 10));

        panel.add(numberButtons[1]);
        panel.add(numberButtons[2]);
        panel.add(numberButtons[3]);
        panel.add(addButton);
        panel.add(numberButtons[4]);
        panel.add(numberButtons[5]);
        panel.add(numberButtons[6]);
        panel.add(subButton);
        panel.add(numberButtons[7]);
        panel.add(numberButtons[8]);
        panel.add(numberButtons[9]);
        panel.add(mulButton);
        panel.add(decButton);
        panel.add(numberButtons[0]);
        panel.add(equalButton);
        panel.add(divButton);


        for (int i = 1; i <= 9; i++) {
            numberButtons[i].setBackground(new Color(243, 156, 18));
        }

        frame.add(panel);
        frame.add(negButton);
        frame.add(delButton);
        frame.add(clrButton);
        frame.add(textfield);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    public static void main(String[] args) {
        Calculator calc = new Calculator();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < 10; i++) {
            if (e.getSource() == numberButtons[i]) {
                textfield.setText(textfield.getText().concat(String.valueOf(i)));
                isOperatorPressed = false;
            }
        }
        if (e.getSource() == decButton) {
            textfield.setText(textfield.getText().concat("."));
        }
        if (e.getSource() == addButton || e.getSource() == subButton ||
                e.getSource() == mulButton || e.getSource() == divButton) {

            if (!isOperatorPressed) { // Avoid multiple operator presses
                num1 = Double.parseDouble(textfield.getText());
                operator = ((JButton) e.getSource()).getText().charAt(0);
                textfield.setText(textfield.getText() + " " + operator + " ");
                isOperatorPressed = true;
            }
        }
        if (e.getSource() == equalButton) {
            String[] tokens = textfield.getText().split(" ");
            if (tokens.length == 3) {
                num2 = Double.parseDouble(tokens[2]);

                try {
                    switch (operator) {
                        case '+':
                            result = num1 + num2;
                            break;
                        case '-':
                            result = num1 - num2;
                            break;
                        case '*':
                            result = num1 * num2;
                            break;
                        case '/':
                            if (num2 == 0) {
                                throw new ArithmeticException();
                            }
                            result = num1 / num2;
                            break;
                    }
                    textfield.setText(textfield.getText() + " = " + result);
                    num1 = result;
                } catch (ArithmeticException ex) {
                    textfield.setText("Cannot Divide by Zero");
                }
            }
        }

        if (e.getSource() == clrButton) {
            textfield.setText("");
        }
        if (e.getSource() == delButton) {
            String string = textfield.getText();
            textfield.setText("");
            for (int i = 0; i < string.length() - 1; i++) {
                textfield.setText(textfield.getText() + string.charAt(i));
            }
        }
        if (e.getSource() == negButton) {
            double temp = Double.parseDouble(textfield.getText());
            temp *= -1;
            textfield.setText(String.valueOf(temp));
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();

        if (Character.isDigit(c)) {
            textfield.setText(textfield.getText() + c);
            isOperatorPressed = false;
        } else if (c == '.') {
            textfield.setText(textfield.getText() + ".");
        } else if (c == '+' || c == '-' || c == '*' || c == '/') {
            if (!isOperatorPressed) {
                num1 = Double.parseDouble(textfield.getText());
                operator = c;
                textfield.setText(textfield.getText() + " " + operator + " ");
                isOperatorPressed = true;
            }
        } else if (c == '\n') {
            String[] tokens = textfield.getText().split(" ");
            if (tokens.length == 3) {
                num2 = Double.parseDouble(tokens[2]);

                try {
                    switch (operator) {
                        case '+':
                            result = num1 + num2;
                            break;
                        case '-':
                            result = num1 - num2;
                            break;
                        case '*':
                            result = num1 * num2;
                            break;
                        case '/':
                            if (num2 == 0) {
                                throw new ArithmeticException();
                            }
                            result = num1 / num2;
                            break;
                    }
                    textfield.setText(textfield.getText() + " = " + result);
                    num1 = result;
                } catch (ArithmeticException ex) {
                    textfield.setText("Cannot Divide by Zero");
                }
            }
        } else if (c == '\b') {
            String string = textfield.getText();
            textfield.setText("");
            for (int i = 0; i < string.length() - 1; i++) {
                textfield.setText(textfield.getText() + string.charAt(i));
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
