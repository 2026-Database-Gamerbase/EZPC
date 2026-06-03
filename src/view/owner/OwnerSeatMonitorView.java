package view.owner;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import view.FontUtil;

// ==========================================
// 좌석 모니터링 탭 화면을 구성하는 Panel
// 메인 프레임에서 선택된 지점의 총 좌석 수에 맞추어 좌석 배치도를 동적으로 렌더링
// 해당 지점에 로그인 중인 손님(회원/비회원, 남은 시간) 목록을 실시간으로 표시
// TODO: 실시간 표시가 가능한 상태인지 확인
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
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // 1. 좌측: 동적 좌석 배치도 (껍데기만 먼저 생성)
        seatGridPanel = new JPanel();
        seatGridPanel.setBackground(new Color(240, 240, 240));
        seatGridPanel.setBorder(BorderFactory.createTitledBorder("좌석 배치도"));
        seatGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        seatScrollPane = new JScrollPane(seatGridPanel);
        splitPane.setLeftComponent(seatScrollPane);

        // 2. 우측: 사용 중인 사용자 정보 테이블
        JPanel userSessionPanel = new JPanel(new BorderLayout());
        userSessionPanel.setBorder(BorderFactory.createTitledBorder("로그인 중인 사용자"));
        userSessionPanel.setBackground(new Color(240, 240, 240));

        String[] columnNames = {"좌석", "사용자명", "타입", "남은 시간", "누적금액"};
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
        splitPane.setDividerLocation(500);

        add(splitPane, BorderLayout.CENTER);
    }

    // 통계 UI 헬퍼 메서드
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
    
    // 상단 통계 수치를 갱신합니다.
    public void updateSeatStats(int total, int used, int empty) {
        totalSeatsLabel.setText(String.valueOf(total));
        usedSeatsLabel.setText(String.valueOf(used));
        emptySeatsLabel.setText(String.valueOf(empty));
    }

    /**
     * Controller에서 선택된 지점의 총 좌석 수(totalSeats)를 받아와
     * 좌석 버튼 그리드를 동적으로 재생성합니다.
     * @param totalSeats 해당 지점의 총 좌석 수
     */
    public void renderSeatLayout(int totalSeats) {
        seatGridPanel.removeAll();
        
        // 보기 좋은 비율로 열(Column) 개수 자동 계산 (최대 10열)
        int cols = (totalSeats > 50) ? 10 : 6;
        int rows = (int) Math.ceil((double) totalSeats / cols);
        
        seatGridPanel.setLayout(new GridLayout(rows, cols, 5, 5));
        seatButtons = new JButton[totalSeats];

        // 기본 빈 좌석(초록색)으로 모두 렌더링
        for (int i = 0; i < totalSeats; i++) {
            JButton seatBtn = new JButton(String.valueOf(i + 1));
            seatBtn.setFont(FontUtil.getKoreanFontBold(12));
            seatBtn.setPreferredSize(new Dimension(50, 50));
            seatBtn.setBackground(new Color(144, 238, 144)); // 기본 빈 좌석
            seatBtn.setFocusPainted(false);
            
            seatButtons[i] = seatBtn;
            seatGridPanel.add(seatBtn);
        }
        
        // UI 강제 새로고침
        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }

    /**
     * Controller에서 로그인 중인 좌석 번호를 받아와 해당 버튼을 빨간색으로 변경
     * TODO: renderSeatLayout()이 먼저 호출된 후에 사용되어야 함
     * @param usedSeatNumbers 사용 중인 좌석 번호 배열 (예: [2, 5, 12])
     */
    public void updateUsedSeats(int[] usedSeatNumbers) {
        if (seatButtons == null) return;
        
        // 1. 전체를 초록색(빈 좌석)으로 초기화
        for (JButton btn : seatButtons) {
            btn.setBackground(new Color(144, 238, 144)); 
        }
        
        // 2. 전달받은 사용 중 좌석 번호만 빨간색으로 변경
        for (int seatNum : usedSeatNumbers) {
            // 좌석 번호는 1번부터 시작하므로 배열 인덱스는 -1
            int idx = seatNum - 1; 
            if (idx >= 0 && idx < seatButtons.length) {
                seatButtons[idx].setBackground(new Color(255, 99, 71));
            }
        }
    }

    /**
     * Controller에서 현재 로그인 중인 사용자 데이터를 받아 테이블에 표시합니다.
     * @param data Object[][] 형태의 배열 (좌석, 사용자명, 타입, 남은 시간, 누적금액)
     * TODO: 남은 시간 동기화 로직 구현 여부 확인
     */
    public void setUserSessionTableData(Object[][] data) {
        tableModel.setRowCount(0);
        if (data != null) {
            for (Object[] row : data) {
                tableModel.addRow(row);
            }
        }
    }
}