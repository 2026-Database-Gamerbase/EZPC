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
    private JButton refreshButton;
    
    private int selectedSeatRow = -1;
    private int selectedSeatCol = -1;
    
    private int rows;
    private int cols;
    private int totalSeats;
    private boolean[][] seatAvailability;
    
    private JPanel seatContainerPanel;

    public UserSeatSelectView() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // 상단 패널 (지점명, 새로고침 버튼, 범례)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 새로고침 버튼
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        refreshPanel.setBackground(new Color(240, 240, 240));
        
        refreshButton = new JButton("새로고침");
        refreshButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 11));
        refreshButton.setPreferredSize(new Dimension(85, 25));
        refreshButton.setBackground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshPanel.add(refreshButton);
        
        topPanel.add(refreshPanel, BorderLayout.NORTH);

        // 지점명 및 범례 패널
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 1));
        infoPanel.setBackground(new Color(240, 240, 240));

        JPanel branchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        branchPanel.setBackground(new Color(240, 240, 240));
        branchNameLabel = new JLabel("지점명: 강남점");
        branchNameLabel.setFont(FontUtil.getKoreanFontBold(18));
        branchPanel.add(branchNameLabel);
        infoPanel.add(branchPanel);

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        legendPanel.setBackground(new Color(240, 240, 240));
        JButton emptyLegend = new JButton("빈 좌석");
        emptyLegend.setBackground(new Color(144, 238, 144));
        emptyLegend.setEnabled(false);
        JButton usedLegend = new JButton("사용 중");
        usedLegend.setBackground(new Color(255, 99, 71));
        usedLegend.setForeground(Color.WHITE);
        usedLegend.setEnabled(false);
        legendPanel.add(new JLabel("범례: "));
        legendPanel.add(emptyLegend);
        legendPanel.add(usedLegend);
        infoPanel.add(legendPanel);

        topPanel.add(infoPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);

        // 중앙 패널 (좌석 현황)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(new Color(240, 240, 240));

        seatContainerPanel = new JPanel(new BorderLayout());
        seatContainerPanel.setBackground(new Color(240, 240, 240));
        centerPanel.add(seatContainerPanel, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);

        // 하단 패널 (좌석 상태 및 버튼)
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
    
    public void setupSeats(int totalSeats) {
        this.totalSeats = totalSeats;
        this.cols = 10; // 💡 한 줄에 10개씩 배치하도록 설정 (원하면 6이나 8로 변경 가능)
        this.rows = (int) Math.ceil((double) totalSeats / cols); // 필요한 줄 수 계산

        this.seatButtons = new JButton[rows][cols];
        this.seatAvailability = new boolean[rows][cols];

        JPanel seatPanel = new JPanel();
        seatPanel.setLayout(new GridLayout(rows, cols, 5, 5));
        seatPanel.setBackground(new Color(240, 240, 240));
        seatPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int seatNum = i * cols + j + 1;
                
                if (seatNum <= totalSeats) {
                    // 실제 존재하는 좌석만 버튼 생성
                    JButton seatButton = new JButton(seatNum + "");
                    seatButton.setFont(FontUtil.getKoreanFontBold(12));
                    seatButton.setPreferredSize(new Dimension(40, 40));
                    seatButton.setBackground(new Color(144, 238, 144));
                    seatButton.setFocusPainted(false);
                    
                    seatButtons[i][j] = seatButton;
                    seatPanel.add(seatButton);
                    seatAvailability[i][j] = true;
                } else {
                    // 마지막 줄의 남는 빈칸은 레이아웃 정렬용으로 투명 라벨 배치
                    seatPanel.add(new JLabel(""));
                    seatButtons[i][j] = null;
                    seatAvailability[i][j] = false;
                }
            }
        }

        // 컨테이너 갱신 및 새로고침
        seatContainerPanel.removeAll();
        seatContainerPanel.add(seatPanel, BorderLayout.CENTER);
        seatContainerPanel.revalidate();
        seatContainerPanel.repaint();
    }

    // 🚀 [추가] 컨트롤러에서 루프를 돌릴 수 있도록 Getter 제공
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getTotalSeats() { return totalSeats; }

    public void setBranchName(String branchName) {
        branchNameLabel.setText("지점명: " + branchName);
    }

    public void setSeatStatus(int row, int col, boolean isAvailable) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            if (seatButtons[row][col] == null) return; // 공백 구역 패스
            
            seatAvailability[row][col] = isAvailable;
            if (isAvailable) {
                seatButtons[row][col].setBackground(new Color(144, 238, 144));
                seatButtons[row][col].setForeground(Color.BLACK);
                seatButtons[row][col].setEnabled(true);
            } else {
                seatButtons[row][col].setBackground(new Color(255, 99, 71));
                seatButtons[row][col].setForeground(Color.WHITE);
                seatButtons[row][col].setEnabled(true);
            }
        }
    }

    public void setSeatButtonListener(int row, int col, ActionListener listener) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            if (seatButtons[row][col] != null) {
                seatButtons[row][col].addActionListener(listener);
            }
        }
    }

    public void setSelectedSeat(int row, int col) {
        if (selectedSeatRow >= 0 && selectedSeatCol >= 0) {
            if (seatButtons[selectedSeatRow][selectedSeatCol] != null) {
                boolean available = seatAvailability[selectedSeatRow][selectedSeatCol];
                seatButtons[selectedSeatRow][selectedSeatCol].setBackground(available ? new Color(144, 238, 144) : new Color(255, 99, 71));
            }
        }

        this.selectedSeatRow = row;
        this.selectedSeatCol = col;
        if (row >= 0 && col >= 0 && seatButtons[row][col] != null) {
            seatStatusLabel.setText("선택된 좌석: " + (row * cols + col + 1)); // 🚀 COLS -> cols 변경
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

    public void setRefreshButtonListener(ActionListener listener) {
        if (refreshButton != null) {
            refreshButton.addActionListener(listener);
        }
    }
}