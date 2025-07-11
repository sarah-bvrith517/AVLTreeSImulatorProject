import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashSet;

class AVLNode {
    int key, height;
    AVLNode left, right;

    AVLNode(int key) {
        this.key = key;
        this.height = 1;
    }
}

class AVLTree {
    private AVLNode root;
    private HashSet<Integer> keys = new HashSet<>();

    // Get height of a node
    private int height(AVLNode node) {
        return (node == null) ? 0 : node.height;
    }

    // Right rotate
    private AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    // Left rotate
    private AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    // Get balance factor of a node
    private int getBalance(AVLNode node) {
        return (node == null) ? 0 : height(node.left) - height(node.right);
    }

    // Insert a node
    public AVLNode insert(AVLNode node, int key) throws IllegalArgumentException {
        if (keys.contains(key)) {
            throw new IllegalArgumentException("Duplicate values are not allowed.");
        }

        if (node == null) {
            keys.add(key);
            return new AVLNode(key);
        }

        if (key < node.key) {
            node.left = insert(node.left, key);
        } else if (key > node.key) {
            node.right = insert(node.right, key);
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && key < node.left.key) {
            return rightRotate(node);
        }

        if (balance < -1 && key > node.right.key) {
            return leftRotate(node);
        }

        if (balance > 1 && key > node.left.key) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && key < node.right.key) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public AVLNode delete(AVLNode node, int key) {
        if (node == null) {
            return null;
        }

        if (key < node.key) {
            node.left = delete(node.left, key);
        } else if (key > node.key) {
            node.right = delete(node.right, key);
        } else {
            if (node.left == null || node.right == null) {
                AVLNode temp = (node.left != null) ? node.left : node.right;
                node = (temp != null) ? temp : null;
            } else {
                AVLNode temp = getMinValueNode(node.right);
                node.key = temp.key;
                node.right = delete(node.right, temp.key);
            }
        }

        if (node == null) {
            return null;
        }

        node.height = Math.max(height(node.left), height(node.right)) + 1;

        int balance = getBalance(node);

        if (balance > 1 && getBalance(node.left) >= 0) {
            return rightRotate(node);
        }

        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && getBalance(node.right) <= 0) {
            return leftRotate(node);
        }

        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private AVLNode getMinValueNode(AVLNode node) {
        AVLNode current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    public AVLNode getRoot() {
        return root;
    }

    public void insert(int key) throws IllegalArgumentException {
        root = insert(root, key);
    }

    public void delete(int key) throws IllegalArgumentException {
        if (!keys.contains(key)) {
            throw new IllegalArgumentException("Value not found in the tree.");
        }
        root = delete(root, key);
        keys.remove(key);
    }
}

// GUI Class for AVL Tree Visualization
public class AVLTreeSimulator extends JPanel {
    private AVLTree avlTree = new AVLTree();

    public AVLTreeSimulator() {
        JFrame frame = new JFrame("AVL Tree Simulator");
        frame.setSize(1000, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField inputField = createRoundedTextField();
        JButton insertButton = createButton("Insert", new Color(144, 238, 144));
        JButton deleteButton = createButton("Delete", new Color(255, 160, 122));

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(240, 240, 245));
        controlPanel.add(new JLabel("Value:"));
        controlPanel.add(inputField);
        controlPanel.add(insertButton);
        controlPanel.add(deleteButton);

        insertButton.addActionListener(e -> {
            try {
                int value = Integer.parseInt(inputField.getText());
                avlTree.insert(value);
                repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input! Please enter a number.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        deleteButton.addActionListener(e -> {
            try {
                int value = Integer.parseInt(inputField.getText());
                avlTree.delete(value);
                repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input! Please enter a number.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        frame.setLayout(new BorderLayout());
        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(this, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JTextField createRoundedTextField() {
        JTextField textField = new JTextField(10);
        textField.setBorder(new EmptyBorder(5, 10, 5, 10));
        return textField;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3)); // Thicker lines
        if (avlTree.getRoot() != null) {
            drawTree(g2d, avlTree.getRoot(), getWidth() / 2, 100, getWidth() / 4);
        }
    }

    private void drawTree(Graphics2D g2d, AVLNode node, int x, int y, int xOffset) {
        if (node != null) {
            if (node.left != null) {
                g2d.setColor(Color.BLACK);
                g2d.drawLine(x, y, x - xOffset, y + 75);
                drawTree(g2d, node.left, x - xOffset, y + 75, xOffset / 2);
            }

            if (node.right != null) {
                g2d.setColor(Color.BLACK);
                g2d.drawLine(x, y, x + xOffset, y + 75);
                drawTree(g2d, node.right, x + xOffset, y + 75, xOffset / 2);
            }

            Color nodeColor = getNodeColor(node);
            int radius = (node == avlTree.getRoot()) ? 50 : 40; // Larger radius for root
            g2d.setColor(nodeColor);
            g2d.fillOval(x - radius / 2, y - radius / 2, radius, radius);

            g2d.setColor(Color.WHITE);
            g2d.drawString(String.valueOf(node.key), x - 10, y + 5);
        }
    }

    private Color getNodeColor(AVLNode node) {
        if (node == avlTree.getRoot()) {
            return Color.ORANGE; // Root Node
        }
        if (node.left == null && node.right == null) {
            return Color.RED; // Leaf Node
        }
        return Color.GREEN; // Parent Nodes
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AVLTreeSimulator::new);
    }
}