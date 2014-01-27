/*
 *  Copyright (C) 2010-2013 JPEXS
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jpexs.decompiler.flash.exporters;

import com.jpexs.decompiler.flash.SWF;
import com.jpexs.decompiler.flash.tags.Tag;
import com.jpexs.decompiler.flash.tags.base.ImageTag;
import com.jpexs.decompiler.flash.tags.base.ShapeTag;
import com.jpexs.decompiler.flash.types.FILLSTYLE;
import com.jpexs.decompiler.flash.types.GRADIENT;
import com.jpexs.decompiler.flash.types.GRADRECORD;
import com.jpexs.decompiler.flash.types.LINESTYLE2;
import com.jpexs.decompiler.flash.types.RECT;
import com.jpexs.decompiler.flash.types.RGB;
import com.jpexs.decompiler.flash.types.shaperecords.SHAPERECORD;
import com.jpexs.decompiler.flash.types.shaperecords.SerializableImage;
import com.jpexs.helpers.Cache;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JPEXS
 */
public class BitmapExporter extends ShapeExporterBase implements IShapeExporter {

    private static final Cache cache = Cache.getInstance(false);

    private BufferedImage image;
    private Graphics2D graphics;
    private final Color defaultColor;
    private double xMin;
    private double yMin;
    private final SWF swf;
    private GeneralPath path;
    private Paint fillPathPaint;
    private Paint fillPaint;
    private AffineTransform fillTransform;
    private Color lineColor;
    private Stroke lineStroke;
    private Stroke defaultStroke;

    public BitmapExporter(SWF swf, ShapeTag tag) {
        this(swf, tag, null);
    }

    public BitmapExporter(SWF swf, ShapeTag tag, Color defaultColor) {
        super(tag);
        this.defaultColor = defaultColor;
        this.swf = swf;
    }

    @Override
    public void export() {
        List<SHAPERECORD> records = tag.getShapes().shapeRecords;
        String key = "shape_" + records.hashCode() + "_" + (defaultColor == null ? "null" : defaultColor.hashCode());
        if (cache.contains(key)) {
            image = (BufferedImage) ((SerializableImage) cache.get(key)).getImage();
            return;
        }
        RECT bounds = SHAPERECORD.getBounds(records);
        xMin = bounds.Xmin / unitDivisor - 1;
        yMin = bounds.Ymin / unitDivisor - 1;
        image = new BufferedImage(
                (int) (bounds.getWidth() / unitDivisor + 3), (int) (bounds.getHeight() / unitDivisor + 3), BufferedImage.TYPE_INT_ARGB);
        graphics = (Graphics2D) image.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        defaultStroke = graphics.getStroke();
        super.export();
        cache.put(key, new SerializableImage(image));
    }

    public BufferedImage getImage() {
        return image;
    }

    public static void clearShapeCache() {
        cache.clear();
    }

    @Override
    public void beginShape() {
    }

    @Override
    public void endShape(double xMin, double yMin, double xMax, double yMax) {
    }

    @Override
    public void beginFills() {
    }

    @Override
    public void endFills() {
    }

    @Override
    public void beginLines() {
    }

    @Override
    public void endLines() {
        finalizePath();
    }

    @Override
    public void beginFill(RGB color) {
        finalizePath();
        fillPaint = color.toColor();
    }

    @Override
    public void beginGradientFill(int type, GRADRECORD[] gradientRecords, Matrix matrix, int spreadMethod, int interpolationMethod, float focalPointRatio) {
        finalizePath();
        switch (type) {
            case FILLSTYLE.LINEAR_GRADIENT: {
                List<Color> colors = new ArrayList<>();
                List<Float> ratios = new ArrayList<>();
                for (int i = 0; i < gradientRecords.length; i++) {
                    if ((i > 0) && (gradientRecords[i - 1].ratio == gradientRecords[i].ratio)) {
                        continue;
                    }
                    ratios.add(gradientRecords[i].getRatioFloat());
                    colors.add(gradientRecords[i].color.toColor());
                }

                float[] ratiosArr = new float[ratios.size()];
                for (int i = 0; i < ratios.size(); i++) {
                    ratiosArr[i] = ratios.get(i);
                }
                Color[] colorsArr = colors.toArray(new Color[colors.size()]);

                MultipleGradientPaint.CycleMethod cm = MultipleGradientPaint.CycleMethod.NO_CYCLE;
                if (spreadMethod == GRADIENT.SPREAD_PAD_MODE) {
                    cm = MultipleGradientPaint.CycleMethod.NO_CYCLE;
                } else if (spreadMethod == GRADIENT.SPREAD_REFLECT_MODE) {
                    cm = MultipleGradientPaint.CycleMethod.REFLECT;
                } else if (spreadMethod == GRADIENT.SPREAD_REPEAT_MODE) {
                    cm = MultipleGradientPaint.CycleMethod.REPEAT;
                }

                fillPathPaint = null;
                fillPaint = new LinearGradientPaint(new java.awt.Point(-16384, 0), new java.awt.Point(16384, 0), ratiosArr, colorsArr, cm);
                fillTransform = matrixToTransform(matrix);
            }
            break;
            case FILLSTYLE.RADIAL_GRADIENT: {
                List<Color> colors = new ArrayList<>();
                List<Float> ratios = new ArrayList<>();
                for (int i = 0; i < gradientRecords.length; i++) {
                    if ((i > 0) && (gradientRecords[i - 1].ratio == gradientRecords[i].ratio)) {
                        continue;
                    }
                    ratios.add(gradientRecords[i].getRatioFloat());
                    colors.add(gradientRecords[i].color.toColor());
                }

                float[] ratiosArr = new float[ratios.size()];
                for (int i = 0; i < ratios.size(); i++) {
                    ratiosArr[i] = ratios.get(i);
                }
                Color[] colorsArr = colors.toArray(new Color[colors.size()]);

                MultipleGradientPaint.CycleMethod cm = MultipleGradientPaint.CycleMethod.NO_CYCLE;
                if (spreadMethod == GRADIENT.SPREAD_PAD_MODE) {
                    cm = MultipleGradientPaint.CycleMethod.NO_CYCLE;
                } else if (spreadMethod == GRADIENT.SPREAD_REFLECT_MODE) {
                    cm = MultipleGradientPaint.CycleMethod.REFLECT;
                } else if (spreadMethod == GRADIENT.SPREAD_REPEAT_MODE) {
                    cm = MultipleGradientPaint.CycleMethod.REPEAT;
                }

                Color endColor = gradientRecords[gradientRecords.length - 1].color.toColor();
                fillPathPaint = endColor;
                fillPaint = new RadialGradientPaint(new java.awt.Point(0, 0), 16384, ratiosArr, colorsArr, cm);
                fillTransform = matrixToTransform(matrix);
            }
            break;
            case FILLSTYLE.FOCAL_RADIAL_GRADIENT: {
                List<Color> colors = new ArrayList<>();
                List<Float> ratios = new ArrayList<>();
                for (int i = 0; i < gradientRecords.length; i++) {
                    if ((i > 0) && (gradientRecords[i - 1].ratio == gradientRecords[i].ratio)) {
                        continue;
                    }
                    ratios.add(gradientRecords[i].getRatioFloat());
                    colors.add(gradientRecords[i].color.toColor());
                }

                float[] ratiosArr = new float[ratios.size()];
                for (int i = 0; i < ratios.size(); i++) {
                    ratiosArr[i] = ratios.get(i);
                }
                Color[] colorsArr = colors.toArray(new Color[colors.size()]);

                MultipleGradientPaint.CycleMethod cm = MultipleGradientPaint.CycleMethod.NO_CYCLE;
                if (spreadMethod == GRADIENT.SPREAD_PAD_MODE) {
                    cm = MultipleGradientPaint.CycleMethod.NO_CYCLE;
                } else if (spreadMethod == GRADIENT.SPREAD_REFLECT_MODE) {
                    cm = MultipleGradientPaint.CycleMethod.REFLECT;
                } else if (spreadMethod == GRADIENT.SPREAD_REPEAT_MODE) {
                    cm = MultipleGradientPaint.CycleMethod.REPEAT;
                }

                Color endColor = gradientRecords[gradientRecords.length - 1].color.toColor();
                fillPathPaint = endColor;
                fillPaint = new RadialGradientPaint(new java.awt.Point(0, 0), 16384, new java.awt.Point((int) (focalPointRatio * 16384), 0), ratiosArr, colorsArr, cm);
                fillTransform = matrixToTransform(matrix);
            }
            break;
        }
    }

    @Override
    public void beginBitmapFill(int bitmapId, Matrix matrix, boolean repeat, boolean smooth) {
        finalizePath();
        ImageTag image = null;
        for (Tag t : swf.tags) {
            if (t instanceof ImageTag) {
                ImageTag i = (ImageTag) t;
                if (i.getCharacterId() == bitmapId) {
                    image = i;
                    break;
                }
            }
        }
        if (image != null) {
            BufferedImage img = image.getImage(swf.tags);
            if (img != null) {
                fillPaint = new TexturePaint(img, new java.awt.Rectangle(img.getWidth(), img.getHeight()));
                matrix.translateX -= xMin;
                matrix.translateY -= yMin;
                fillTransform = matrixToTransform(matrix);
            }
        }
    }

    @Override
    public void endFill() {
        finalizePath();
        fillPaint = null;
    }

    @Override
    public void lineStyle(double thickness, RGB color, boolean pixelHinting, String scaleMode, int startCaps, int endCaps, int joints, int miterLimit) {
        finalizePath();
        lineColor = color.toColor();
        int capStyle = BasicStroke.CAP_ROUND;
        switch (startCaps) {
            case LINESTYLE2.NO_CAP:
                capStyle = BasicStroke.CAP_BUTT;
                break;
            case LINESTYLE2.ROUND_CAP:
                capStyle = BasicStroke.CAP_ROUND;
                break;
            case LINESTYLE2.SQUARE_CAP:
                capStyle = BasicStroke.CAP_SQUARE;
                break;
        }
        int joinStyle = BasicStroke.JOIN_ROUND;
        switch (joints) {
            case LINESTYLE2.BEVEL_JOIN:
                joinStyle = BasicStroke.JOIN_BEVEL;
                break;
            case LINESTYLE2.MITER_JOIN:
                joinStyle = BasicStroke.JOIN_MITER;
                break;
            case LINESTYLE2.ROUND_JOIN:
                joinStyle = BasicStroke.JOIN_ROUND;
                break;
        }
        if (joinStyle == BasicStroke.JOIN_MITER) {
            lineStroke = new BasicStroke((float) thickness, capStyle, joinStyle, miterLimit);
        } else {
            lineStroke = new BasicStroke((float) thickness, capStyle, joinStyle);
        }
    }

    @Override
    public void lineGradientStyle(int type, GRADRECORD[] gradientRecords, Matrix matrix, int spreadMethod, int interpolationMethod, float focalPointRatio) {
    }

    @Override
    public void moveTo(double x, double y) {
        path.moveTo(x - xMin, y - yMin);
    }

    @Override
    public void lineTo(double x, double y) {
        path.lineTo(x - xMin, y - yMin);
    }

    @Override
    public void curveTo(double controlX, double controlY, double anchorX, double anchorY) {
        path.quadTo(controlX - xMin, controlY - yMin, anchorX - xMin, anchorY - yMin);
    }

    protected void finalizePath() {
        final int maxRepeat = 10; // TODO: better handle gradient repeating
        if (path != null) {
            if (fillPaint != null) {
                if (fillPaint instanceof MultipleGradientPaint) {
                    AffineTransform oldAf = graphics.getTransform();
                    if (fillPathPaint != null) {
                        graphics.setPaint(fillPathPaint);
                    }
                    graphics.fill(path);
                    graphics.setClip(path);
                    graphics.setTransform(fillTransform);

                    graphics.setPaint(fillPaint);
                    graphics.fill(new java.awt.Rectangle(-16384 * maxRepeat, -16384 * maxRepeat, 16384 * 2 * maxRepeat, 16384 * 2 * maxRepeat));
                    graphics.setTransform(oldAf);
                    graphics.setClip(null);
                } else if (fillPaint instanceof TexturePaint) {
                    AffineTransform oldAf = graphics.getTransform();
                    graphics.setClip(path);
                    graphics.setTransform(fillTransform);

                    graphics.setPaint(fillPaint);
                    graphics.fill(new java.awt.Rectangle(-16384 * maxRepeat, -16384 * maxRepeat, 16384 * 2 * maxRepeat, 16384 * 2 * maxRepeat));
                    graphics.setTransform(oldAf);
                    graphics.setClip(null);
                } else {
                    graphics.setPaint(fillPaint);
                    graphics.fill(path);
                }
            }
            if (lineColor != null) {
                graphics.setColor(lineColor);
                graphics.setStroke(lineStroke == null ? defaultStroke : lineStroke);
                graphics.draw(path);
            }
        }
        path = new GeneralPath();
    }

    public static AffineTransform matrixToTransform(Matrix mat) {
        AffineTransform transform = new AffineTransform(mat.scaleX, mat.rotateSkew0,
                mat.rotateSkew1, mat.scaleY,
                mat.translateX, mat.translateY);
        return transform;
    }
}
