// 지점 선택 화면
package view.user;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import model.PcCafe;
import view.FontUtil;

public class UserBranchSelectView extends JPanel {
    private List<JButton> branchButtons;
    private JLabel titleLabel;
    private JPanel buttonPanel;

    public UserBranchSelectView() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        titleLabel = new JLabel("이용할 지점을 선택하세요");
        titleLabel.setFont(FontUtil.getKoreanFontBold(24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1, 15, 15));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));
        buttonPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        branchButtons = new ArrayList<>();
    }

    public void setBranches(java.util.List<PcCafe> pcCafes) {
        buttonPanel.removeAll();
        branchButtons.clear();

        if (pcCafes == null || pcCafes.isEmpty()) {
            JLabel emptyLabel = new JLabel("등록된 지점이 없습니다.");
            emptyLabel.setFont(FontUtil.getKoreanFontPlain(18));
            emptyLabel.setHorizontalAlignment(JLabel.CENTER);
            emptyLabel.setOpaque(true);
            emptyLabel.setBackground(new Color(255, 255, 255));
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(60, 0, 60, 0));
            buttonPanel.add(emptyLabel);
        } else {
            for (PcCafe cafe : pcCafes) {
                JButton button = new JButton(cafe.getPcName() + " (" + cafe.getPcId() + ")");
                button.setFont(FontUtil.getKoreanFontPlain(18));
                button.setPreferredSize(new Dimension(150, 70));
                button.setBackground(new Color(50, 120, 220));
                button.setForeground(Color.WHITE);
                button.setOpaque(true);
                button.setContentAreaFilled(true);
                button.setBorder(BorderFactory.createLineBorder(new Color(30, 90, 180), 2, true));
                button.setFocusPainted(false);
                button.setActionCommand(cafe.getPcId());
                buttonPanel.add(button);
                branchButtons.add(button);
            }
        }

        revalidate();
        repaint();
    }

    public void setBranchButtonListener(ActionListener listener) {
        for (JButton button : branchButtons) {
            button.addActionListener(listener);
        }
    }

    public void setAllBranchButtonListener(ActionListener listener) {
        setBranchButtonListener(listener);
    }
}
