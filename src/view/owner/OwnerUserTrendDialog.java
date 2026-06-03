package view.owner;

import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import javax.swing.*;
import view.FontUtil;

// ==========================================
// 월별 이용자 수 추이 팝업 (JDialog)
// 월별 이용자 수 추이를 텍스트 기반 차트로 시각화하여 제공
// ==========================================
public class OwnerUserTrendDialog extends JDialog {
    private JComboBox<Integer> yearComboBox;
    private JButton searchButton;
    private JTextArea trendTextArea;

    public OwnerUserTrendDialog(JFrame parentFrame) {
        super(parentFrame, "월별 이용자 수 추이 분석", true);
        setSize(500, 420);
        setLocationRelativeTo(parentFrame);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 240));

        // ==========================================
        // 상단: 타이틀 + 연도 선택
        // ==========================================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 240, 240));

        JLabel titleLabel = new JLabel("월별 이용자 수 추이");
        titleLabel.setFont(FontUtil.getKoreanFontBold(16));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel yearPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        yearPanel.setBackground(new Color(240, 240, 240));

        yearComboBox = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear; y >= currentYear - 4; y--) {
            yearComboBox.addItem(y);
        }
        yearComboBox.setFont(FontUtil.getKoreanFontPlain(12));
        yearComboBox.setPreferredSize(new Dimension(80, 25));

        searchButton = new JButton("조회");
        searchButton.setFont(FontUtil.getKoreanFontPlain(12));

        yearPanel.add(new JLabel("연도:"));
        yearPanel.add(yearComboBox);
        yearPanel.add(searchButton);
        topPanel.add(yearPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ==========================================
        // 중앙: 텍스트 차트 영역
        // ==========================================
        trendTextArea = new JTextArea();
        trendTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        trendTextArea.setEditable(false);
        trendTextArea.setBackground(Color.BLACK);
        trendTextArea.setForeground(Color.GREEN);

        mainPanel.add(new JScrollPane(trendTextArea), BorderLayout.CENTER);

        // ==========================================
        // 하단: 닫기 버튼
        // ==========================================
        JButton closeButton = new JButton("닫기");
        closeButton.setFont(FontUtil.getKoreanFontPlain(12));
        closeButton.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.add(closeButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // ==========================================
    // Controller 연동용 메서드
    // ==========================================
    public int getSelectedYear() {
        return (Integer) yearComboBox.getSelectedItem();
    }

    public void setSearchButtonListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    public void setTrendData(String textChartData) {
        trendTextArea.setText(textChartData);
    }
}
