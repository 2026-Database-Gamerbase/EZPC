package view.owner;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import view.FontUtil;
import model.EventSalesReport;

// ==========================================
// 이벤트 전/후 성과 분석 팝업 (JDialog)
// ==========================================
public class OwnerEventAnalysisDialog extends JDialog {
    private JComboBox<String> eventComboBox;
    private JButton analyzeButton;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public OwnerEventAnalysisDialog(JFrame parentFrame) {
        super(parentFrame, "이벤트 전/후 성과 분석 리포트", true);
        setSize(750, 480);
        setLocationRelativeTo(parentFrame);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 240));

        // ==========================================
        // 상단: 이벤트 선택 콤보박스 패널
        // ==========================================
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(240, 240, 240));
        searchPanel.setBorder(BorderFactory.createTitledBorder("분석할 이벤트 선택"));

        searchPanel.add(new JLabel("진행 이벤트: "));
        eventComboBox = new JComboBox<>();
        eventComboBox.setFont(FontUtil.getKoreanFontPlain(12));
        eventComboBox.setPreferredSize(new Dimension(350, 25));
        searchPanel.add(eventComboBox);

        analyzeButton = new JButton("성과 분석하기");
        analyzeButton.setBackground(new Color(100, 150, 255));
        analyzeButton.setForeground(Color.WHITE);
        analyzeButton.setOpaque(true);
        analyzeButton.setBorderPainted(false);
        analyzeButton.setFocusPainted(false);
        searchPanel.add(analyzeButton);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // ==========================================
        // 중앙: CardLayout (placeholder <-> 결과)
        // ==========================================
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(240, 240, 240));
        contentPanel.add(buildPlaceholderPanel(), "placeholder");
        contentPanel.add(new JPanel(), "result");
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // ==========================================
        // 하단: 닫기 버튼
        // ==========================================
        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.add(closeButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel buildPlaceholderPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        JLabel hint = new JLabel("이벤트를 선택하고 [성과 분석하기]를 눌러주세요.");
        hint.setFont(FontUtil.getKoreanFontPlain(12));
        hint.setForeground(Color.GRAY);
        panel.add(hint);
        return panel;
    }

    // ==========================================
    // Controller에서 호출 — 분석 결과 표시
    // ==========================================
    public void showAnalysisResult(EventSalesReport event, EventSalesReport prev,
                                   String start, String end) {
        double rate      = event.getGrowthRate();
        Color  rateColor = rate > 0 ? new Color(0, 150, 80) : rate < 0 ? new Color(200, 50, 50) : Color.DARK_GRAY;
        String rateText  = (rate > 0 ? "▲ +" : rate < 0 ? "▼ " : "") + String.format("%.2f%%", rate);

        JPanel result = new JPanel(new BorderLayout(0, 10));
        result.setBackground(new Color(240, 240, 240));

        // 성장률 배너
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        banner.setBackground(Color.WHITE);
        banner.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel bannerTitle = new JLabel("이벤트 기간 매출 성장률");
        bannerTitle.setFont(FontUtil.getKoreanFontPlain(12));
        bannerTitle.setForeground(Color.GRAY);

        JLabel rateLabel = new JLabel(rateText);
        rateLabel.setFont(FontUtil.getKoreanFontBold(18));
        rateLabel.setForeground(rateColor);

        banner.add(bannerTitle);
        banner.add(rateLabel);
        result.add(banner, BorderLayout.NORTH);

        // 카드 2장
        JPanel cardsRow = new JPanel(new GridLayout(1, 2, 10, 0));
        cardsRow.setBackground(new Color(240, 240, 240));
        cardsRow.add(buildCard("이벤트 기간", start + " ~ " + end, event));
        cardsRow.add(buildCard("직전 동일 기간", "비교 기준 구간",  prev));
        result.add(cardsRow, BorderLayout.CENTER);

        contentPanel.add(result, "result");
        cardLayout.show(contentPanel, "result");
    }

    private JPanel buildCard(String title, String period, EventSalesReport r) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        // 타이틀 + 기간
        JPanel header = new JPanel(new BorderLayout(0, 3));
        header.setBackground(Color.WHITE);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FontUtil.getKoreanFontBold(13));

        JLabel periodLbl = new JLabel(period);
        periodLbl.setFont(FontUtil.getKoreanFontPlain(11));
        periodLbl.setForeground(Color.GRAY);

        header.add(titleLbl,  BorderLayout.NORTH);
        header.add(periodLbl, BorderLayout.SOUTH);
        card.add(header, BorderLayout.NORTH);

        // 매출 항목
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        body.add(Box.createVerticalStrut(8));
        body.add(buildRow("PC 요금 매출", String.format("%,d원", r.getChargeSales())));
        body.add(Box.createVerticalStrut(6));
        body.add(buildRow("음식 매출",    String.format("%,d원", r.getFoodSales())));
        body.add(Box.createVerticalStrut(10));
        body.add(buildTotalRow(String.format("%,d원", r.getTotalSales())));
        card.add(body, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JLabel lbl = new JLabel(label);
        lbl.setFont(FontUtil.getKoreanFontPlain(12));
        lbl.setForeground(Color.GRAY);

        JLabel val = new JLabel(value);
        val.setFont(FontUtil.getKoreanFontPlain(12));
        val.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    private JPanel buildTotalRow(String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(240, 240, 240));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JLabel lbl = new JLabel("총 매출");
        lbl.setFont(FontUtil.getKoreanFontBold(12));

        JLabel val = new JLabel(value);
        val.setFont(FontUtil.getKoreanFontBold(14));
        val.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    // ==========================================
    // Controller 연동용 메서드
    // ==========================================
    public void setEventList(String[] events) {
        eventComboBox.removeAllItems();
        if (events != null) {
            for (String evt : events) eventComboBox.addItem(evt);
        }
    }

    public int getSelectedEventIndex() {
        return eventComboBox.getSelectedIndex();
    }

    public void setAnalyzeButtonListener(ActionListener listener) {
        analyzeButton.addActionListener(listener);
    }
}
