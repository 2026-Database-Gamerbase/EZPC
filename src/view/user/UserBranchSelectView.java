// PC방 지점 선택 화면 (현재 다섯 개 잇음)
package view.user;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import view.FontUtil;

public class UserBranchSelectView extends JPanel {
    private JButton[] branchButtons;
    private JLabel titleLabel;
    private JPanel buttonPanel;

    public UserBranchSelectView() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // 제목
        titleLabel = new JLabel("이용할 지점을 선택하세요");
        titleLabel.setFont(FontUtil.getKoreanFontBold(24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 버튼 패널
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 2, 15, 15));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));

        // DB 연결 필요: 활성화된 PC방 지점 목록 로드
        // 현재는 샘플 5개 지점으로 구성
        String[] branchNames = {
            "강남점",
            "홍대점",
            "명동점",
            "서초점",
            "노량진점"
        };

        branchButtons = new JButton[branchNames.length];
        for (int i = 0; i < branchNames.length; i++) {
            branchButtons[i] = new JButton(branchNames[i]);
            branchButtons[i].setFont(FontUtil.getKoreanFontPlain(18));
            branchButtons[i].setPreferredSize(new Dimension(150, 100));
            branchButtons[i].setBackground(new Color(100, 150, 255));
            branchButtons[i].setForeground(Color.WHITE);
            branchButtons[i].setFocusPainted(false);
            buttonPanel.add(branchButtons[i]);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    // 선택된 지점 ID 반환 (버튼 인덱스로 식별)
    public int getSelectedBranchIndex() {
        for (int i = 0; i < branchButtons.length; i++) {
            if (branchButtons[i].getModel().isPressed()) {
                return i;
            }
        }
        return -1;
    }

    // 지점 버튼에 리스너 설정
    public void setBranchButtonListener(int index, ActionListener listener) {
        if (index >= 0 && index < branchButtons.length) {
            branchButtons[index].addActionListener(listener);
        }
    }

    // 모든 지점 버튼에 리스너 설정
    public void setAllBranchButtonListener(ActionListener listener) {
        for (JButton button : branchButtons) {
            button.addActionListener(listener);
        }
    }
}
