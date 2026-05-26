package view.auth;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * SignUpView - 일반 회원가입 화면
 * 가입 시 기본 등급(bronze)과 일반 유저 타입('user')이 부여됩니다.
 */
public class SignUpView extends JFrame {
    private JTextField idField;
    private JTextField nameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField phoneField;
    private JButton signUpButton;
    private JButton cancelButton;
    private JLabel statusLabel;

    public SignUpView() {
        setTitle("PC방 회원가입");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        initializeUI();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // 제목
        JLabel titleLabel = new JLabel("회원가입");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // ID 입력
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        idField = new JTextField(15);
        mainPanel.add(idField, gbc);

        // 이름 입력
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("이름:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        nameField = new JTextField(15);
        mainPanel.add(nameField, gbc);

        // 비밀번호 입력
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("비밀번호:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(15);
        mainPanel.add(passwordField, gbc);

        // 비밀번호 확인
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("비밀번호 확인:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        confirmPasswordField = new JPasswordField(15);
        mainPanel.add(confirmPasswordField, gbc);

        // 전화번호 입력
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("전화번호:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        phoneField = new JTextField(15);
        mainPanel.add(phoneField, gbc);

        // 안내 메시지
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JLabel noteLabel = new JLabel("기본 등급(Bronze)과 유저 타입(User)이 부여됩니다.");
        noteLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        noteLabel.setForeground(new Color(100, 100, 100));
        mainPanel.add(noteLabel, gbc);

        // 상태 메시지
        gbc.gridy = 7;
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        mainPanel.add(statusLabel, gbc);

        // 회원가입 버튼
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        signUpButton = new JButton("회원가입");
        signUpButton.setPreferredSize(new Dimension(100, 35));
        mainPanel.add(signUpButton, gbc);

        // 취소 버튼
        gbc.gridx = 1;
        cancelButton = new JButton("취소");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        mainPanel.add(cancelButton, gbc);

        add(mainPanel);
    }

    // 입력 데이터 반환 (DB 연결 필요)
    public String getInputId() {
        return idField.getText();
    }

    public String getInputName() {
        return nameField.getText();
    }

    public String getInputPassword() {
        return new String(passwordField.getPassword());
    }

    public String getConfirmPassword() {
        return new String(confirmPasswordField.getPassword());
    }

    public String getInputPhoneNumber() {
        return phoneField.getText();
    }

    // 상태 메시지 설정
    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }

    // 회원가입 버튼 리스너 설정
    public void setSignUpButtonListener(ActionListener listener) {
        signUpButton.addActionListener(listener);
    }

    // 취소 버튼 리스너 설정
    public void setCancelButtonListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }

    // 입력값 초기화
    public void clearFields() {
        idField.setText("");
        nameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        phoneField.setText("");
        statusLabel.setText(" ");
    }
}
