package view.owner;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import view.FontUtil;

// ==========================================
// 좌석 모니터링 탭 화면을 구성하는 Panel
// 메인 프레임에서 선택된 지점의 총 좌석 수에 맞추어 좌석 배치도를 동적으로 렌더링
// 해당 지점에 로그인 중인 손님(회원/비회원, 잔여 시간) 목록을 실시간으로 표시
// TODO: 새로고침 버튼 수정해서 잔여시간 등 업데이트 가능하게 수정
// ==========================================
public class OwnerSeatMonitorView extends JPanel {
    private JLabel totalSeatsLabel;
    private JLabel usedSeatsLabel;
    private JLabel emptySeatsLabel;
    
    private JPanel seatGridPanel;
    private JScrollPane seatScrollPane;
    private JButton[] seatButtons;
    
    private JTable userSessionTable;
    private DefaultTableModel tableModel;
    private JSplitPane splitPane;

    public OwnerSeatMonitorView() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // ==========================================
        // 상단: 통계 정보 
        // ==========================================
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(1, 3, 10, 10));
        statsPanel.setBackground(new Color(240, 240, 240));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        totalSeatsLabel = createStatPanel(statsPanel, "전체 좌석", new Color(100, 150, 255), Color.WHITE);
        usedSeatsLabel  = createStatPanel(statsPanel, "사용 중", new Color(255, 99, 71), Color.WHITE);
        emptySeatsLabel = createStatPanel(statsPanel, "빈 좌석", new Color(144, 238, 144), Color.BLACK);

        add(statsPanel, BorderLayout.NORTH);

        // ==========================================
        // 중앙: 좌측(동적 좌석 배치도) 및 우측(사용자 세션 정보)
        // ==========================================
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // 1. 좌측: 동적 좌석 배치도
        seatGridPanel = new JPanel();
        seatGridPanel.setBackground(new Color(240, 240, 240));
        seatGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        seatScrollPane = new JScrollPane(seatGridPanel);
        splitPane.setLeftComponent(seatScrollPane);

        // 2. 우측: 사용 중인 사용자 정보 테이블
        JPanel userSessionPanel = new JPanel(new BorderLayout());
        userSessionPanel.setBorder(BorderFactory.createTitledBorder("로그인 중인 사용자"));
        userSessionPanel.setBackground(new Color(240, 240, 240));

        String[] columnNames = {"좌석", "사용자명", "타입", "잔여 시간"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        userSessionTable = new JTable(tableModel);
        userSessionTable.setFont(FontUtil.getKoreanFontPlain(12));
        userSessionTable.setRowHeight(25);
        userSessionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        userSessionPanel.add(new JScrollPane(userSessionTable), BorderLayout.CENTER);
        splitPane.setRightComponent(userSessionPanel);
        splitPane.setDividerLocation(680); 
        splitPane.setContinuousLayout(true);

        add(splitPane, BorderLayout.CENTER);
    }

    private JLabel createStatPanel(JPanel parent, String title, Color bgColor, Color fgColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FontUtil.getKoreanFontBold(14));
        titleLabel.setForeground(fgColor);
        
        JLabel valueLabel = new JLabel("0");
        valueLabel.setFont(FontUtil.getKoreanFontBold(32));
        valueLabel.setForeground(fgColor);
        valueLabel.setHorizontalAlignment(JLabel.CENTER);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        parent.add(panel);
        
        return valueLabel;
    }

    
    // ==========================================
    // 상태 갱신용 메서드
    // ==========================================
    
    // 상단 통계 수치 갱신
    public void updateSeatStats(int total, int used, int empty) {
        totalSeatsLabel.setText(String.valueOf(total));
        usedSeatsLabel.setText(String.valueOf(used));
        emptySeatsLabel.setText(String.valueOf(empty));
    }

    // 총 좌석 수에 맞춰 좌석 그리드를 재생성
    public void renderSeatLayout(int totalSeats) {
        seatGridPanel.removeAll();
        
        int cols = (totalSeats > 50) ? 10 : 6;
        int rows = (int) Math.ceil((double) totalSeats / cols);
        
        seatGridPanel.setLayout(new GridLayout(rows, cols, 8, 8)); 
        seatButtons = new JButton[totalSeats];

        for (int i = 0; i < totalSeats; i++) {
            JButton seatBtn = new JButton(String.valueOf(i + 1));
            seatBtn.setFont(FontUtil.getKoreanFontBold(13));
            seatBtn.setPreferredSize(new Dimension(75, 65));
            seatBtn.setMargin(new Insets(0, 0, 0, 0));
            seatBtn.setOpaque(true);
            seatBtn.setContentAreaFilled(true); 
            seatBtn.setBorderPainted(false);
            seatBtn.setBackground(new Color(46, 204, 113));
            seatBtn.setForeground(Color.WHITE);
            seatBtn.setFocusPainted(false);
            
            seatButtons[i] = seatBtn;
            seatGridPanel.add(seatBtn);
        }
        
        splitPane.setDividerLocation(680);
        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }

    // 로그인 중인 좌석 번호 배열을 받아 해당 버튼의 전체 배경색을 빨간색으로 변경
    public void updateUsedSeats(int[] usedSeatNumbers) {
        if (seatButtons == null) return;
        
        // 1. 전체를 초록색으로 초기화
        for (JButton btn : seatButtons) {
            btn.setBackground(new Color(46, 204, 113)); 
            btn.setForeground(Color.WHITE);
        }
        
        // 2. 사용 중 좌석 번호만 빨간색으로 변경
        for (int seatNum : usedSeatNumbers) {
            int idx = seatNum - 1; 
            if (idx >= 0 && idx < seatButtons.length) {
                seatButtons[idx].setBackground(new Color(231, 76, 60)); 
                seatButtons[idx].setForeground(Color.WHITE);
            }
        }
    }


    public void setUserSessionTableData(Object[][] data) {
        tableModel.setRowCount(0);
        if (data != null) {
            for (Object[] row : data) {
                tableModel.addRow(row);
            }
        }
    }
}