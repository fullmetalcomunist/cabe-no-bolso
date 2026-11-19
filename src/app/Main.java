package app;

import controller.ControlePlanejamento;
import java.io.File;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.TelaPrincipal;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            ControlePlanejamento controller = new ControlePlanejamento();
            File f = new File("dados/caso_referencia.ser");
            if (f.exists()) { try { controller.carregarArquivo(f); } catch (Exception ignored) {} }
            new TelaPrincipal(controller).setVisible(true);
        });
    }
}
