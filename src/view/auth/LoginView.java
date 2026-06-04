package view.auth;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import view.FontUtil;

/**
 * LoginView - 통합 로그인 화면
 * 입력된 ID의 member_type을 식별하여 손님(user)이면 좌석 선택으로,
 * 사장님(owner)이면 관리자 창으로 자동 라우팅합니다.
 */
public class LoginView extends JFrame {
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;
    private JButton guestButton;
    private JLabel statusLabel;

    public LoginView() {
        setTitle("PC방 통합 로그인");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
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

        // 제목 (3열 전체 span)
        JLabel titleLabel = new JLabel("PC방 로그인");
        titleLabel.setFont(FontUtil.getKoreanFontBold(24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // ID 입력
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel idLabel = new JLabel("ID:");
        idLabel.setFont(FontUtil.getKoreanFontPlain(14));
        gbc.gridx = 0;
        mainPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        idField = new JTextField(20);
        idField.setFont(FontUtil.getKoreanFontPlain(14));
        mainPanel.add(idField, gbc);

        // 비밀번호 입력
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel pwLabel = new JLabel("Password:");
        pwLabel.setFont(FontUtil.getKoreanFontPlain(14));
        mainPanel.add(pwLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        passwordField.setFont(FontUtil.getKoreanFontPlain(14));
        mainPanel.add(passwordField, gbc);

        // 상태 메시지
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        statusLabel = new JLabel(" ");
        statusLabel.setFont(FontUtil.getKoreanFontPlain(12));
        statusLabel.setForeground(Color.RED);
        mainPanel.add(statusLabel, gbc);

        // 버튼 3개 (로그인 / 회원가입 / 비회원)
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        loginButton = new JButton("로그인");
        loginButton.setFont(FontUtil.getKoreanFontBold(14));
        mainPanel.add(loginButton, gbc);

        gbc.gridx = 1;
        signUpButton = new JButton("회원가입");
        signUpButton.setFont(FontUtil.getKoreanFontBold(14));
        mainPanel.add(signUpButton, gbc);

        gbc.gridx = 2;
        guestButton = new JButton("비회원");
        guestButton.setFont(FontUtil.getKoreanFontBold(14));
        mainPanel.add(guestButton, gbc);

        add(mainPanel);
    }

    // ID 입력값 반환 (DB 연결 필요)
    public String getInputId() {
        return idField.getText();
    }

    // 비밀번호 입력값 반환 (DB 연결 필요)
    public String getInputPassword() {
        return new String(passwordField.getPassword());
    }

    // 상태 메시지 설정
    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }

    // 로그인 버튼 리스너 설정
    public void setLoginButtonListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    // 회원가입 버튼 리스너 설정
    public void setSignUpButtonListener(ActionListener listener) {
        signUpButton.addActionListener(listener);
    }

    // 비회원 입장 버튼 리스너 설정
    public void setGuestButtonListener(ActionListener listener) {
        guestButton.addActionListener(listener);
    }

    // 입력값 초기화
    public void clearFields() {
        idField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
    }
}
