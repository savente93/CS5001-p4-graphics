package fractalgraphics;

import java.awt.Color;
import java.util.Stack;

import javax.swing.JOptionPane;

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
		defaultConfig = new FractalGUIConfig(FractalGUIView.DEFAULT_X_RESOLUTION, FractalGUIView.DEFAULT_Y_RESOLUTION,
				MandelbrotCalculator.INITIAL_MIN_REAL, MandelbrotCalculator.INITIAL_MAX_REAL,
				MandelbrotCalculator.INITIAL_MIN_IMAGINARY, MandelbrotCalculator.INITIAL_MAX_IMAGINARY,
				MandelbrotCalculator.INITIAL_MAX_ITERATIONS, MandelbrotCalculator.DEFAULT_RADIUS_SQUARED, Color.WHITE,
				Color.BLACK);

		currentConfig = defaultConfig;
		model = new FractalGUIModel(defaultConfig);
		view = new FractalGUIView(this,model.calcModel());
		model.addObserver(view);
	
		configHistory = new Stack<FractalGUIConfig>();
		configUndoneHistory = new Stack<FractalGUIConfig>();

	}

	public double realFromScreenX(int x) {
		
		return currentConfig.getMinReal() +(((double) x / currentConfig.getxResolution())
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
		applyNewConfig(new FractalGUIConfig(view.getCurrentXSize(), view.getCurrentYSize(),
				realFromScreenX(leftX), realFromScreenX(rightX), imaginaryFromScreenY(lowerY),
				imaginaryFromScreenY(upperY), currentConfig.getMaxIterations(), currentConfig.getRadiusSquared(),
				currentConfig.getStartingColor(), currentConfig.getEndColor()));

	}

}
