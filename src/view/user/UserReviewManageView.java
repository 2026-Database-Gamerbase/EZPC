// 리뷰 관리 화면
package view.user;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Review;
import view.FontUtil;

public class UserReviewManageView extends JPanel {
    private JLabel branchNameLabel;
    private JSpinner ratingSpinner;
    private JTextArea reviewTextArea;
    private JButton submitReviewButton;
    private JButton clearButton;
    private JTable reviewListTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public UserReviewManageView() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // 상단: 제목 및 지점명
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("리뷰 관리");
        titleLabel.setFont(FontUtil.getKoreanFontBold(20));
        topPanel.add(titleLabel, BorderLayout.WEST);

        branchNameLabel = new JLabel("지점: 강남점"); // DB 연결 필요
        branchNameLabel.setFont(FontUtil.getKoreanFontBold(14));
        topPanel.add(branchNameLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // 중앙: 좌측(리뷰 작성) 및 우측(리뷰 목록)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBackground(new Color(240, 240, 240));

        // 좌측: 리뷰 작성 섹션
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBackground(new Color(240, 240, 240));
        leftPanel.setBorder(BorderFactory.createTitledBorder("리뷰 작성"));

        JPanel writePanel = new JPanel();
        writePanel.setLayout(new GridBagLayout());
        writePanel.setBackground(new Color(240, 240, 240));
        writePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 별점 선택
        JLabel ratingLabel = new JLabel("별점:");
        ratingLabel.setFont(FontUtil.getKoreanFontBold(12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        writePanel.add(ratingLabel, gbc);

        ratingSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        gbc.gridx = 1;
        writePanel.add(ratingSpinner, gbc);

        JLabel starsLabel = new JLabel("★★★★★");
        starsLabel.setFont(FontUtil.getKoreanFontPlain(14));
        starsLabel.setForeground(new Color(255, 215, 0));
        gbc.gridx = 2;
        writePanel.add(starsLabel, gbc);

        // 리뷰 텍스트 입력
        JLabel reviewLabel = new JLabel("후기:");
        reviewLabel.setFont(FontUtil.getKoreanFontBold(12));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        writePanel.add(reviewLabel, gbc);

        reviewTextArea = new JTextArea(8, 20);
        reviewTextArea.setFont(FontUtil.getKoreanFontPlain(12));
        reviewTextArea.setLineWrap(true);
        reviewTextArea.setWrapStyleWord(true);
        reviewTextArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        writePanel.add(reviewTextArea, gbc);

        // 버튼
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 240, 240));
        submitReviewButton = new JButton("작성 완료");
        submitReviewButton.setPreferredSize(new Dimension(100, 30));
        clearButton = new JButton("초기화");
        clearButton.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(submitReviewButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        writePanel.add(buttonPanel, gbc);

        leftPanel.add(writePanel, BorderLayout.CENTER);

        // 좌측 하단: 상태 메시지
        JPanel leftBottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftBottomPanel.setBackground(new Color(240, 240, 240));
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(FontUtil.getKoreanFontPlain(11));
        leftBottomPanel.add(statusLabel);
        leftPanel.add(leftBottomPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);

        // 우측: 리뷰 목록 섹션
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(new Color(240, 240, 240));
        rightPanel.setBorder(BorderFactory.createTitledBorder("다른 사용자 리뷰"));

        // DB 연결 필요: 해당 지점의 다른 사용자 리뷰 로드
        String[] columnNames = {"사용자", "별점", "후기", "작성일"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reviewListTable = new JTable(tableModel);
        reviewListTable.setFont(FontUtil.getKoreanFontPlain(11));
        reviewListTable.setRowHeight(25);
        reviewListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(reviewListTable);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(400);

        add(splitPane, BorderLayout.CENTER);
    }

    // DB 연결 필요: 지점명 설정
    public void setBranchName(String branchName) {
        branchNameLabel.setText("지점: " + branchName);
    }

    public void refreshReviews(List<Review> reviews) {
        tableModel.setRowCount(0);
        if (reviews == null || reviews.isEmpty()) {
            return;
        }
        for (Review review : reviews) {
            String starText = "";
            int rating = (int) review.getStarRating();
            for (int i = 0; i < rating; i++) {
                starText += "★";
            }
            tableModel.addRow(new Object[]{review.getMemberId(), starText, review.getReviewContent(), "-"});
        }
    }

    public void refreshReviewList() {
        tableModel.setRowCount(0);
    }

    // 입력된 별점 반환
    public int getSelectedRating() {
        return (int) ratingSpinner.getValue();
    }

    // 입력된 리뷰 텍스트 반환
    public String getReviewText() {
        return reviewTextArea.getText();
    }

    // 상태 메시지 설정
    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }

    // 작성 완료 버튼 리스너 설정
    public void setSubmitReviewButtonListener(ActionListener listener) {
        submitReviewButton.addActionListener(listener);
    }

    // 초기화 버튼 리스너 설정
    public void setClearButtonListener(ActionListener listener) {
        clearButton.addActionListener(listener);
    }

    // 리뷰 입력 초기화
    public void clearReviewInput() {
        ratingSpinner.setValue(5);
        reviewTextArea.setText("");
        statusLabel.setText(" ");
    }
}
