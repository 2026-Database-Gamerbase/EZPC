// 좌석 선택 화면
package view.user;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import view.FontUtil;

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
    private boolean[][] seatAvailability = new boolean[ROWS][COLS];

    public UserSeatSelectView() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1));
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        branchNameLabel = new JLabel("지점명: 강남점");
        branchNameLabel.setFont(FontUtil.getKoreanFontBold(18));
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
                seatButton.setFont(FontUtil.getKoreanFontBold(12));
                seatButton.setPreferredSize(new Dimension(40, 40));
                seatButton.setBackground(new Color(144, 238, 144));
                seatButton.setFocusPainted(false);
                seatButtons[i][j] = seatButton;
                seatPanel.add(seatButton);
                seatAvailability[i][j] = true;
            }
        }

        centerPanel.add(seatPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2, 1));
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        seatStatusLabel = new JLabel("선택된 좌석: 없음");
        seatStatusLabel.setFont(FontUtil.getKoreanFontPlain(14));
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

    public void setBranchName(String branchName) {
        branchNameLabel.setText("지점명: " + branchName);
    }

    public void setSeatStatus(int row, int col, boolean isAvailable) {
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
            seatAvailability[row][col] = isAvailable;
            if (isAvailable) {
                seatButtons[row][col].setBackground(new Color(144, 238, 144));
                seatButtons[row][col].setEnabled(true);
            } else {
                seatButtons[row][col].setBackground(new Color(255, 99, 71));
                seatButtons[row][col].setEnabled(false);
            }
        }
    }

    public void setSeatButtonListener(int row, int col, ActionListener listener) {
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
            seatButtons[row][col].addActionListener(listener);
        }
    }

    public void setSelectedSeat(int row, int col) {
        if (selectedSeatRow >= 0 && selectedSeatCol >= 0) {
            boolean available = seatAvailability[selectedSeatRow][selectedSeatCol];
            seatButtons[selectedSeatRow][selectedSeatCol].setBackground(available ? new Color(144, 238, 144) : new Color(255, 99, 71));
        }

        this.selectedSeatRow = row;
        this.selectedSeatCol = col;
        if (row >= 0 && col >= 0) {
            seatStatusLabel.setText("선택된 좌석: " + (row * COLS + col + 1));
            seatButtons[row][col].setBackground(new Color(255, 215, 0));
        } else {
            seatStatusLabel.setText("선택된 좌석: 없음");
        }
    }

    public int getSelectedSeatRow() {
        return selectedSeatRow;
    }

    public int getSelectedSeatCol() {
        return selectedSeatCol;
    }

    public void setConfirmButtonListener(ActionListener listener) {
        confirmButton.addActionListener(listener);
    }

    public void setBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
}
