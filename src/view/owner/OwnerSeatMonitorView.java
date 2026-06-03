package view.owner;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * OwnerSeatMonitorView - 좌석 모니터링 탭
 * 현재 매장의 전체 좌석 현황과 로그인 중인 손님
 * (회원/비회원 정보, 남은 시간)을 실시간으로 모니터링하는 화면입니다.
 */
public class OwnerSeatMonitorView extends JPanel {
    private JLabel totalSeatsLabel;
    private JLabel usedSeatsLabel;
    private JLabel emptySeatsLabel;
    private JButton[][] seatButtons;
    private JTable userSessionTable;
    private DefaultTableModel tableModel;
    private static final int ROWS = 5;
    private static final int COLS = 6;

    public OwnerSeatMonitorView() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // 상단: 통계 정보
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(1, 3, 10, 10));
        statsPanel.setBackground(new Color(240, 240, 240));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 전체 좌석 수
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(new Color(100, 150, 255));
        totalPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        JLabel totalLabel = new JLabel("전체 좌석");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalLabel.setForeground(Color.WHITE);
        totalSeatsLabel = new JLabel("30"); // DB 연결 필요
        totalSeatsLabel.setFont(new Font("Arial", Font.BOLD, 32));
        totalSeatsLabel.setForeground(Color.WHITE);
        totalSeatsLabel.setHorizontalAlignment(JLabel.CENTER);
        totalPanel.add(totalLabel, BorderLayout.NORTH);
        totalPanel.add(totalSeatsLabel, BorderLayout.CENTER);
        statsPanel.add(totalPanel);

        // 사용 중인 좌석
        JPanel usedPanel = new JPanel(new BorderLayout());
        usedPanel.setBackground(new Color(255, 99, 71));
        usedPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        JLabel usedLabel = new JLabel("사용 중");
        usedLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usedLabel.setForeground(Color.WHITE);
        usedSeatsLabel = new JLabel("12"); // DB 연결 필요
        usedSeatsLabel.setFont(new Font("Arial", Font.BOLD, 32));
        usedSeatsLabel.setForeground(Color.WHITE);
        usedSeatsLabel.setHorizontalAlignment(JLabel.CENTER);
        usedPanel.add(usedLabel, BorderLayout.NORTH);
        usedPanel.add(usedSeatsLabel, BorderLayout.CENTER);
        statsPanel.add(usedPanel);

        // 빈 좌석
        JPanel emptyPanel = new JPanel(new BorderLayout());
        emptyPanel.setBackground(new Color(144, 238, 144));
        emptyPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        JLabel emptyLabel = new JLabel("빈 좌석");
        emptyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emptyLabel.setForeground(Color.BLACK);
        emptySeatsLabel = new JLabel("18"); // DB 연결 필요
        emptySeatsLabel.setFont(new Font("Arial", Font.BOLD, 32));
        emptySeatsLabel.setForeground(Color.BLACK);
        emptySeatsLabel.setHorizontalAlignment(JLabel.CENTER);
        emptyPanel.add(emptyLabel, BorderLayout.NORTH);
        emptyPanel.add(emptySeatsLabel, BorderLayout.CENTER);
        statsPanel.add(emptyPanel);

        add(statsPanel, BorderLayout.NORTH);

        // 중앙: 좌측(좌석 배치도) 및 우측(사용자 세션 정보)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // 좌석 배치도
        JPanel seatPanel = new JPanel();
        seatPanel.setLayout(new GridLayout(ROWS, COLS, 3, 3));
        seatPanel.setBackground(new Color(240, 240, 240));
        seatPanel.setBorder(BorderFactory.createTitledBorder("좌석 배치도"));
        seatPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        seatButtons = new JButton[ROWS][COLS];
        // DB 연결 필요: 각 좌석의 상태 로드
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                JButton seatButton = new JButton((i * COLS + j + 1) + "");
                seatButton.setFont(new Font("Arial", Font.BOLD, 12));
                seatButton.setPreferredSize(new Dimension(40, 40));
                // 샘플 상태: 짝수번째는 사용중, 홀수번째는 빈 좌석
                if ((i * COLS + j) % 2 == 0) {
                    seatButton.setBackground(new Color(255, 99, 71)); // 빨강색: 사용 중
                } else {
                    seatButton.setBackground(new Color(144, 238, 144)); // 초록색: 빈 좌석
                }
                seatButton.setFocusPainted(false);
                seatButtons[i][j] = seatButton;
                seatPanel.add(seatButton);
            }
        }

        splitPane.setLeftComponent(new JScrollPane(seatPanel));

        // 사용 중인 사용자 정보 테이블
        JPanel userSessionPanel = new JPanel(new BorderLayout());
        userSessionPanel.setBorder(BorderFactory.createTitledBorder("로그인 중인 사용자"));
        userSessionPanel.setBackground(new Color(240, 240, 240));

        // DB 연결 필요: 현재 로그인 중인 사용자 목록 로드
        String[] columnNames = {"좌석", "사용자명", "타입", "남은 시간", "누적금액"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 샘플 데이터
        tableModel.addRow(new Object[]{"1번", "user001", "회원", "00:45:30", "15,000원"});
        tableModel.addRow(new Object[]{"2번", "손님", "비회원", "00:30:15", "0원"});
        tableModel.addRow(new Object[]{"4번", "user003", "회원", "01:20:00", "25,000원"});

        JTable userTable = new JTable(tableModel);
        userTable.setFont(new Font("Arial", Font.PLAIN, 11));
        userTable.setRowHeight(25);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScrollPane = new JScrollPane(userTable);
        userSessionPanel.add(tableScrollPane, BorderLayout.CENTER);

        splitPane.setRightComponent(userSessionPanel);
        splitPane.setDividerLocation(450);

        add(splitPane, BorderLayout.CENTER);
    }

    // DB 연결 필요: 좌석 통계 업데이트
    public void updateSeatStats(int total, int used, int empty) {
        totalSeatsLabel.setText(total + "");
        usedSeatsLabel.setText(used + "");
        emptySeatsLabel.setText(empty + "");
    }

    // DB 연결 필요: 좌석 상태 업데이트
    public void setSeatStatus(int row, int col, boolean isUsed) {
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
            if (isUsed) {
                seatButtons[row][col].setBackground(new Color(255, 99, 71)); // 빨강색: 사용 중
            } else {
                seatButtons[row][col].setBackground(new Color(144, 238, 144)); // 초록색: 빈 좌석
            }
        }
    }

    // DB 연결 필요: 사용자 세션 테이블 새로고침
    public void refreshUserSessionTable() {
        tableModel.setRowCount(0);
        // DB 연결 필요: 현재 로그인 중인 사용자 목록 다시 로드
        tableModel.addRow(new Object[]{"1번", "user001", "회원", "00:45:30", "15,000원"});
        tableModel.addRow(new Object[]{"2번", "손님", "비회원", "00:30:15", "0원"});
    }
}
