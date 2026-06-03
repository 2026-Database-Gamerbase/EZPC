// 한글 폰트 일괄 설정
package view;

import java.awt.Font;
import java.util.Enumeration;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class FontUtil {
    private static final String[] KOREAN_FONTS = {
        "나눔고딕",
        "맑은 고딕",
        "굴림",
        "돋움",
        "바탕",
        "Arial",
        "Dialog"
    };


    public static String getAvailableKoreanFont() {
        String[] fontNames = java.awt.GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames();
        
        for (String koreanFont : KOREAN_FONTS) {
            for (String systemFont : fontNames) {
                if (systemFont.equalsIgnoreCase(koreanFont)) {
                    return koreanFont;
                }
            }
        }

        return "Dialog";
    }

    // 기본 폰트를 한글로 설정
    public static void setDefaultFont() {
        setDefaultFont(12);
    }

    public static void setDefaultFont(int fontSize) {
        String koreanFont = getAvailableKoreanFont();
        Font font = new Font(koreanFont, Font.PLAIN, fontSize);
        FontUIResource fontUIResource = new FontUIResource(font);

        // UIManager의 모든 컴포넌트에 폰트 적용
        Enumeration<?> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontUIResource);
            }
        }
    }

    // JFrame 또는 JComponent의 폰트를 한글 폰트로 설정
    public static Font getKoreanFont(int style, int size) {
        String koreanFont = getAvailableKoreanFont();
        return new Font(koreanFont, style, size);
    }

    // 일반 폰트 크기
    public static Font getKoreanFontPlain(int size) {
        return getKoreanFont(Font.PLAIN, size);
    }

    // 굵은 폰트 크기
    public static Font getKoreanFontBold(int size) {
        return getKoreanFont(Font.BOLD, size);
    }

    // 기울임 폰트 크기
    public static Font getKoreanFontItalic(int size) {
        return getKoreanFont(Font.ITALIC, size);
    }
}
