package org.gephi.layout.plugin.concentric;


import javax.swing.Icon;
import javax.swing.JPanel;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Helder Suzuki <heldersuzuki@gephi.org>
 */
@ServiceProvider(service = LayoutBuilder.class)
public class Concentric implements LayoutBuilder {

    private ConcentricLayoutUI ui = new ConcentricLayoutUI();

    @Override
    public String getName() {
        return NbBundle.getMessage(Concentric.class, "Concentric.name");
    }

    @Override
    public Layout buildLayout() {
        return new ConcentricLayout(this, 10.0f, 100.0f, "0.0", 0.6f);
    }

    @Override
    public LayoutUI getUI() {
        return ui;
    }

    private static class ConcentricLayoutUI implements LayoutUI {

        @Override
        public String getDescription() {
            return NbBundle.getMessage(Concentric.class, "Concentric.description");
        }

        @Override
        public Icon getIcon() {
            return null;
        }

        @Override
        public JPanel getSimplePanel(Layout layout) {
            return null;
        }

        @Override
        public int getQualityRank() {
            return -1;
        }

        @Override
        public int getSpeedRank() {
            return -1;
        }
    }
}
