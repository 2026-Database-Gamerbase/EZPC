package view.user;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * UserSeatSelectView - 좌석 선택 화면
 * 선택한 지점의 실시간 좌석 배치도를 보여주고, 비어있는 좌석을 선택하는 화면입니다.
 */
public class UserSeatSelectView extends JPanel {
    private JButton[][] seatButtons;
    private JLabel branchNameLabel;
    private JLabel seatStatusLabel;
    private JButton confirmButton;
    private JButton backButton;
    private int selectedSeatRow = -1;
    private int selectedSeatCol = -1;
    private static final int ROWS = 5;
    private static final int COLS = 6;

    public UserSeatSelectView() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // 상단: 지점명과 범례
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1));
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        branchNameLabel = new JLabel("지점명: 강남점");
        branchNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(branchNameLabel);

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.setBackground(new Color(240, 240, 240));
        JButton emptyLegend = new JButton("빈 좌석");
        emptyLegend.setBackground(new Color(144, 238, 144));
        emptyLegend.setEnabled(false);
        JButton usedLegend = new JButton("사용 중");
        usedLegend.setBackground(new Color(255, 99, 71));
        usedLegend.setEnabled(false);
        legendPanel.add(new JLabel("범례:"));
        legendPanel.add(emptyLegend);
        legendPanel.add(usedLegend);
        topPanel.add(legendPanel);
        add(topPanel, BorderLayout.NORTH);

        // 중앙: 좌석 배치도
        // DB 연결 필요: 각 좌석의 상태(available, occupied) 로드
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(new Color(240, 240, 240));

        JPanel seatPanel = new JPanel();
        seatPanel.setLayout(new GridLayout(ROWS, COLS, 5, 5));
        seatPanel.setBackground(new Color(240, 240, 240));
        seatPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        seatButtons = new JButton[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                JButton seatButton = new JButton((i * COLS + j + 1) + "");
                seatButton.setFont(new Font("Arial", Font.BOLD, 12));
                seatButton.setPreferredSize(new Dimension(40, 40));
                // DB 연결 필요: 실제 상태에 따라 색상 설정
                seatButton.setBackground(new Color(144, 238, 144)); // 초기값: 빈 좌석
                seatButton.setFocusPainted(false);
                seatButtons[i][j] = seatButton;
                seatPanel.add(seatButton);
            }
        }

        centerPanel.add(seatPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 하단: 선택 상태 및 버튼
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2, 1));
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        seatStatusLabel = new JLabel("선택된 좌석: 없음");
        seatStatusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        bottomPanel.add(seatStatusLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 240, 240));
        confirmButton = new JButton("선택 완료");
        confirmButton.setPreferredSize(new Dimension(100, 35));
        backButton = new JButton("돌아가기");
        backButton.setPreferredSize(new Dimension(100, 35));
        buttonPanel.add(confirmButton);
        buttonPanel.add(backButton);
        bottomPanel.add(buttonPanel);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // 지점명 설정
    public void setBranchName(String branchName) {
        branchNameLabel.setText("지점명: " + branchName);
    }

    // 좌석 상태 설정 (row, col, isAvailable)
    // DB 연결 필요: 실제 상태 데이터로 업데이트
    public void setSeatStatus(int row, int col, boolean isAvailable) {
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
            if (isAvailable) {
                seatButtons[row][col].setBackground(new Color(144, 238, 144)); // 초록색: 사용가능
                seatButtons[row][col].setEnabled(true);
            } else {
                seatButtons[row][col].setBackground(new Color(255, 99, 71)); // 빨강색: 사용중
                seatButtons[row][col].setEnabled(false);
            }
        }
    }

    // 좌석 버튼에 리스너 설정
    public void setSeatButtonListener(int row, int col, ActionListener listener) {
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
            seatButtons[row][col].addActionListener(listener);
        }
    }

    // 선택된 좌석 설정
    public void setSelectedSeat(int row, int col) {
        this.selectedSeatRow = row;
        this.selectedSeatCol = col;
        if (row >= 0 && col >= 0) {
            seatStatusLabel.setText("선택된 좌석: " + (row * COLS + col + 1));
            seatButtons[row][col].setBackground(new Color(255, 215, 0)); // 노란색: 선택됨
        } else {
            seatStatusLabel.setText("선택된 좌석: 없음");
        }
    }

    // 선택된 좌석 반환
    public int getSelectedSeatRow() {
        return selectedSeatRow;
    }

    public int getSelectedSeatCol() {
        return selectedSeatCol;
    }

    // 확인 버튼 리스너 설정
    public void setConfirmButtonListener(ActionListener listener) {
        confirmButton.addActionListener(listener);
    }

    // 돌아가기 버튼 리스너 설정
    public void setBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
}
