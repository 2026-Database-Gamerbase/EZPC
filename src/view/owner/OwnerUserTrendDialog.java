package view.owner;

import java.awt.*;
import javax.swing.*;
import view.FontUtil;

// ==========================================
// 월별 이용자 추이 팝업 (JDialog)
// 고급 분석 4번: 월별 이용자 수 추이를 텍스트 기반 차트로 시각화하여 제공
// ==========================================
public class OwnerUserTrendDialog extends JDialog {
    private JTextArea trendTextArea;

    public OwnerUserTrendDialog(JFrame parentFrame) {
        super(parentFrame, "월별 이용자 수 추이 분석", true); // true = 모달 창 (이 창을 꺼야 메인창 제어 가능)
        setSize(500, 400);
        setLocationRelativeTo(parentFrame);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 240));

        // ==========================================
        // 상단: 타이틀
        // ==========================================
        JLabel titleLabel = new JLabel("최근 6개월 이용자 수 추이");
        titleLabel.setFont(FontUtil.getKoreanFontBold(16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

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
    // Controller 연동을 위한 Getter 및 Listener 메서드 모음
    // ==========================================


    // ==========================================
    // 상태 갱신용 메서드
    // ==========================================
    
    /**
     * Controller에서 텍스트 차트(String)를 통째로 전달받아 화면에 출력합니다.
     * @param textChartData ASCII 형태의 텍스트 그래프 데이터
     */
    public void setTrendData(String textChartData) {
        trendTextArea.setText(textChartData);
    }
}