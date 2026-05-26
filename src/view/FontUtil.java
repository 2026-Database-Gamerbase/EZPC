package view;

import java.awt.Font;
import java.util.Enumeration;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * FontUtil - 한글 폰트 설정 유틸리티 클래스
 * Swing 컴포넌트에 한글 폰트를 일괄 설정하여
 * 한글 깨짐 문제를 해결합니다.
 */
public class FontUtil {
    
    // 한글 지원 폰트 목록 (우선순위 순)
    private static final String[] KOREAN_FONTS = {
        "나눔고딕",           // Noto Sans CJK KR
        "맑은 고딕",          // Segoe UI (한글 포함)
        "굴림",               // Gulim
        "돋움",               // Dotum
        "바탕",               // Batang
        "Arial",              // 영문 폴백
        "Dialog"              // JVM 기본 폰트
    };

    /**
     * 시스템에서 사용 가능한 한글 폰트를 찾아 반환
     */
    public static String getAvailableKoreanFont() {
        String[] fontNames = java.awt.GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames();
        
        for (String koreanFont : KOREAN_FONTS) {
            for (String systemFont : fontNames) {
                if (systemFont.equalsIgnoreCase(koreanFont)) {
                    System.out.println("✓ 한글 폰트 선택: " + koreanFont);
                    return koreanFont;
                }
            }
        }
        
        // 폴백: Dialog 폰트 (모든 시스템에 존재)
        System.out.println("⚠ 기본 한글 폰트를 찾을 수 없어 Dialog 폰트 사용");
        return "Dialog";
    }

    /**
     * Swing 컴포넌트의 기본 폰트를 한글 폰트로 설정
     */
    public static void setDefaultFont() {
        setDefaultFont(12);
    }

    /**
     * Swing 컴포넌트의 기본 폰트를 한글 폰트로 설정 (폰트 크기 지정)
     */
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

    /**
     * JFrame 또는 JComponent의 폰트를 한글 폰트로 설정
     */
    public static Font getKoreanFont(int style, int size) {
        String koreanFont = getAvailableKoreanFont();
        return new Font(koreanFont, style, size);
    }

    /**
     * 평문 폰트 크기
     */
    public static Font getKoreanFontPlain(int size) {
        return getKoreanFont(Font.PLAIN, size);
    }

    /**
     * 굵은 폰트 크기
     */
    public static Font getKoreanFontBold(int size) {
        return getKoreanFont(Font.BOLD, size);
    }

    /**
     * 기울임 폰트 크기
     */
    public static Font getKoreanFontItalic(int size) {
        return getKoreanFont(Font.ITALIC, size);
    }
}
