package fractalgraphics;

import java.awt.Color;
import java.util.Stack;

public class FractalGUIController {

    private final FractalGUIConfig defaultConfig;

    private final FractalGUIModel model;
    private final FractalGUIView view;
    private FractalGUIConfig currentConfig;
    private Stack<FractalGUIConfig> configHistory;
    private Stack<FractalGUIConfig> configUndoneHistory;

    public boolean hasConfigHistory() {

        return !configHistory.isEmpty();
    }

    public boolean hasConfigUndoneHistory() {

        return !configUndoneHistory.isEmpty();
    }

    public static void main(String[] args) {

        new FractalGUIController();
    }

    public FractalGUIController() {
        //        defaultConfig = new FractalGUIConfig(FractalGUIView.DEFAULT_X_RESOLUTION, FractalGUIView.DEFAULT_Y_RESOLUTION,
        //                MandelbrotCalculator.INITIAL_MIN_REAL, MandelbrotCalculator.INITIAL_MAX_REAL,
        //                MandelbrotCalculator.INITIAL_MIN_IMAGINARY, MandelbrotCalculator.INITIAL_MAX_IMAGINARY,
        //                MandelbrotCalculator.INITIAL_MAX_ITERATIONS, MandelbrotCalculator.DEFAULT_RADIUS_SQUARED,
        //                new ColorMapping(MandelbrotCalculator.INITIAL_MAX_ITERATIONS,
        //                        new Color[] { Color.WHITE, Color.red, Color.GREEN, Color.blue, Color.BLACK }));
        defaultConfig = new FractalGUIConfig(FractalGUIView.DEFAULT_X_RESOLUTION, FractalGUIView.DEFAULT_Y_RESOLUTION,
                MandelbrotCalculator.INITIAL_MIN_REAL, MandelbrotCalculator.INITIAL_MAX_REAL,
                MandelbrotCalculator.INITIAL_MIN_IMAGINARY, MandelbrotCalculator.INITIAL_MAX_IMAGINARY,
                MandelbrotCalculator.INITIAL_MAX_ITERATIONS, MandelbrotCalculator.DEFAULT_RADIUS_SQUARED,
                new ColorMapping(MandelbrotCalculator.INITIAL_MAX_ITERATIONS,
                        new Color[] {
                                new Color(0, 7, 100),
                                new Color(32, 107, 203),
                                new Color(237, 255, 255),
                                new Color(255, 170, 0),
                                new Color(0, 2, 0) }));

        currentConfig = defaultConfig;
        model = new FractalGUIModel(defaultConfig);
        view = new FractalGUIView(this, model.calcModel());
        model.addObserver(view);

        configHistory = new Stack<FractalGUIConfig>();
        configUndoneHistory = new Stack<FractalGUIConfig>();

    }

    public double realFromScreenX(int x) {

        return currentConfig.getMinReal() + (((double) x / currentConfig.getxResolution())
                * (currentConfig.getMaxReal() - currentConfig.getMinReal()));
    }

    public double imaginaryFromScreenY(int y) {

        return currentConfig.getMinImaginary() + (((double) y / currentConfig.getxResolution())
                * (currentConfig.getMaxImaginary() - currentConfig.getMinImaginary()));
    }

    public void reset() {

        applyNewConfig(defaultConfig);

    }

    public void redo() {

        configHistory.push(currentConfig);
        applyNewConfig(configUndoneHistory.pop());

    }

    public void undo() {

        configUndoneHistory.push(currentConfig);
        applyNewConfig(configHistory.pop());

    }

    public void applyNewConfig(FractalGUIConfig newConfig) {

        // Store config for undo
        configHistory.push(currentConfig);
        // reset to default
        currentConfig = newConfig;
        // update model
        model.setCurrentConfig(newConfig);
        // reset window
        view.setSize(newConfig.getxResolution(), newConfig.getyResolution());
    }

    /**
     * @param upperLeftX
     * @param upperLeftY
     * @param lowerRightX
     * @param lowerRightY
     */
    public void recentre(int leftX, int upperY, int rightX, int lowerY) {

        assert leftX < rightX;
        assert upperY > lowerY;
        assert Math.abs(realFromScreenX(0) - currentConfig.getMinReal()) < 0.00000001;
        assert Math.abs(realFromScreenX(view.getCurrentXSize()) - currentConfig.getMaxReal()) < 0.00000001;
        assert Math.abs(imaginaryFromScreenY(0) - currentConfig.getMinImaginary()) < 0.00000001;
        assert Math.abs(imaginaryFromScreenY(view.getCurrentXSize()) - currentConfig.getMaxImaginary()) < 0.00000001;
        applyNewConfig(new FractalGUIConfig(view.getCurrentXSize(), view.getCurrentYSize(), realFromScreenX(leftX),
                realFromScreenX(rightX), imaginaryFromScreenY(lowerY), imaginaryFromScreenY(upperY),
                currentConfig.getMaxIterations(), currentConfig.getRadiusSquared(), currentConfig.getColorMapping()));

    }

    public int getMaxIterations() {

        return currentConfig.getMaxIterations();
    }

    public void setMaxIterations(int newMaxIterations) {

        currentConfig.getColorMapping().setMaxValue(newMaxIterations);

        applyNewConfig(new FractalGUIConfig(currentConfig.getxResolution(), currentConfig.getyResolution(),
                currentConfig.getMinReal(), currentConfig.getMaxReal(), currentConfig.getMinImaginary(),
                currentConfig.getMaxImaginary(), newMaxIterations, currentConfig.getRadiusSquared(),
                currentConfig.getColorMapping()));

    }

    public ColorMapping getColorMapping() {

        return currentConfig.getColorMapping();
    }

}
