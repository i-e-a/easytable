package org.vandeseer.easytable;

import lombok.Builder;
import lombok.experimental.SuperBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.function.Supplier;

@SuperBuilder
public class RepeatedHeaderTableDrawer extends TableDrawer {

    @Builder.Default
    private int numberOfRowsToRepeat = 1;
    private int firstRowToRepeat = 0;

    private Float headerHeight;

    @Override
    protected void drawPage(PageData pageData) {
        if (pageData.firstRowOnPage != 0) {
            float adaption = 0;
            for (int i = 0; i < numberOfRowsToRepeat; i++) {
                adaption += table.getRows().get(firstRowToRepeat + i).getHeight();
                Point2D.Float startPoint = new Point2D.Float(this.startX, this.startY + calculateHeightForFirstRows() - adaption);
                drawRow(startPoint, table.getRows().get(firstRowToRepeat + i), i, (drawer, drawingContext) -> {
                    drawer.drawBackground(drawingContext);
                    drawer.drawContent(drawingContext);
                    drawer.drawBorders(drawingContext);
                });
            }
        }

        drawerList.forEach(drawer ->
                drawWithFunction(pageData, new Point2D.Float(this.startX, this.startY), drawer)
        );
    }

    @Override
    public void draw(Supplier<PDDocument> documentSupplier, Supplier<PDPage> pageSupplier, float yOffset) throws IOException {
        super.draw(documentSupplier, pageSupplier, yOffset + calculateHeightForFirstRows());
    }

    private float calculateHeightForFirstRows() {
        if (headerHeight != null) {
            return headerHeight;
        }

        float height = 0;
        for (int i = 0; i < numberOfRowsToRepeat; i++) {
            height += table.getRows().get(firstRowToRepeat + i).getHeight();
        }

        // Cache and return
        headerHeight = height;
        return height;
    }

}
