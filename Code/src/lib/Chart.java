package lib;

import org.knowm.xchart.XYChartBuilder;	// XChart chart builder
import org.knowm.xchart.style.Styler;	// XChart theme
import org.knowm.xchart.XChartPanel;	// XChart panel
import org.knowm.xchart.XYChart;		// XChart chart

import java.awt.GridLayout;				// Grid layout
import javax.swing.JFrame;				// Frame
import javax.swing.JPanel;				// Panel
import java.awt.Color;					// Color

import java.util.ArrayList;				// ArrayList
import java.util.Arrays;				// Arrays


public class Chart {
	
	// Boolean variables
	private boolean							empty;										// Empty chart
	private boolean					legendVisible	= false;							// Set legend visible
	//ArrayList matrix of doubles
	private ArrayList<ArrayList<Double>>	yData	= new ArrayList<>();				// Y data
	private ArrayList<ArrayList<Double>>	xData	= new ArrayList<>();				// X data
	private ArrayList<XYChart>				charts	= new ArrayList<>();				// Charts
	// Create a panel to hold the charts and the frame to hold the panel
	private JPanel 							panel	;									// Panel with grid layout
	private JFrame 							frame	;									// Frame with title

	/**
	 * Constructor
	 * @param TITLES	the titles
	 * @param Y_TITLES	the y-axis titles
	 * @param X_TITLES	the x-axis titles
	 * @param COLORS	the colors
	 */
	public Chart(final String[] TITLES, final String[] Y_TITLES, final String[] X_TITLES, final String[] COLORS) {
		try{
			this.panel	= new JPanel(new GridLayout(2, 2));	// Panel with grid layout
			this.frame	= new JFrame("Progresses");			// Frame with title
		}catch(Exception e){ throw new RuntimeException("Error creating the chart"); }

		for (int i = 0; i < TITLES.length; i++) {
			this.addChart(TITLES[i], Y_TITLES[i], X_TITLES[i], COLORS[i]);
		}
		this.empty = false;
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public Chart() {
		this(new String[]{"Title"}, new String[]{"X-axis"}, new String[]{"Y-axis"}, new String[]{"BLUE"});
		this.empty = true;
	}

	/**
	 * Creates a chart with the specified title, x-axis title, and y-axis title.
	 * @param TITLE the title
	 * @param Y_TITLE the y-axis title
	 * @param X_TITLE the x-axis title
	 * @return the chart
	 */
	private XYChart createChart(final String TITLE, final String Y_TITLE, final String X_TITLE) {
		XYChart chart = new XYChartBuilder()
				.width(400)
				.height(300)
				.title(TITLE)
				.xAxisTitle(X_TITLE)
				.yAxisTitle(Y_TITLE)
				.theme(Styler.ChartTheme.XChart)
				.build();
		return chart;
	}

	/**
	 * Get color from string
	 * @param COLOR	the color name
	 * @return		the color object
	 */
	private Color getColor(final String COLOR) {
		for(final var C : Color.class.getDeclaredFields()) {
			if(C.getName().equalsIgnoreCase(COLOR)) {
				try { return (Color) C.get(null); }
				catch(IllegalArgumentException | IllegalAccessException e) { e.printStackTrace(); }
			}
		}
		return Color.BLUE;
	}

	/**
	 * Creates a chart with the specified title, x-axis title, and y-axis title.
	 * @param CHART	the chart
	 * @return		the index of the chart
	 */
	private int getIndex(final XYChart CHART) {
		for (int i = 0; i < this.charts.size(); i++) {
			if (this.charts.get(i) == CHART) return i;
		}
		return -1;
	}

	/**
	 * Creates a chart with the specified title, x-axis title, and y-axis title.
	 * @param TITLE		the title
	 * @param Y_TITLE	the x-axis title
	 * @param X_TITLE	the y-axis title
	 * @param COLOR		the color
	 * @return			the chart
	 */
	public XYChart addChart(final String TITLE, final String Y_TITLE, final String X_TITLE, final String COLOR) {
		final XYChart CHART = this.createChart(TITLE, Y_TITLE, X_TITLE);		// Create chart
		
		if (this.empty){
			this.charts = new ArrayList<XYChart>(Arrays.asList(CHART));			// Initialize charts
			this.yData	= new ArrayList<ArrayList<Double>>();					// Initialize y data
			this.xData	= new ArrayList<ArrayList<Double>>();					// Initialize x data
			this.empty	= false;
		}else{
			this.charts.add(CHART);												// Add chart to the array
			this.yData.add(new ArrayList<Double>());							// Initialize y data
			this.xData.add(new ArrayList<Double>());							// Initialize x data
		}

		CHART.getStyler().setMarkerSize(0);										// Set chart markers
		CHART.getStyler().setLegendVisible(false);								// hide series name from legend
		CHART.getStyler().setSeriesColors(new Color[]{this.getColor(COLOR)});	// Set colors
		this.panel.add(new XChartPanel<>(CHART));								// Add charts to the panel
		
		this.frame.getContentPane().add(this.panel);							// Add panel to the frame
		this.frame.pack();														// Pack the frame
		// Initialize series with dummy data
		CHART.addSeries((this.charts.size()-1)+"", new double[]{0}, new double[]{0});

		return CHART;
	}

	/**
	 * Add data to the chart
	 * @param INDEX		Index of the chart
	 * @param Y_DATA	Y data
	 * @param X_DATA	X data
	 * @param UPDATE	Update the chart
	 */
	public void addData(final int INDEX, final double Y_DATA, final double X_DATA, final boolean UPDATE){
		this.yData.get(INDEX).add(Y_DATA);	// Add data y to the array
		this.xData.get(INDEX).add(X_DATA);	// Add data x to the array
		if(UPDATE) this.updateChart(INDEX);	// Update the chart
	}
	public void addData(final XYChart CHART, final double Y_DATA, final double X_DATA, final boolean UPDATE){
		this.addData(this.getIndex(CHART), Y_DATA, X_DATA, UPDATE);
	}
	public void addData(final int INDEX, final double[] Y_DATA, final double[] X_DATA, final boolean UPDATE){
		this.yData.set(INDEX, new ArrayList<Double>() {{ for (double d : Y_DATA) add(d); }});	// Add data y to the array
		this.xData.set(INDEX, new ArrayList<Double>() {{ for (double d : X_DATA) add(d); }});	// Add data x to the array
		if(UPDATE) this.updateChart(INDEX);														// Update the chart
	}
	public void addData(final XYChart CHART, final double[] Y_DATA, final double[] X_DATA, final boolean UPDATE){
		this.addData(this.getIndex(CHART), Y_DATA, X_DATA, UPDATE); 
	}

	/**
	 * Update the chart
	 */
	public void updateAll() {
		for (int INDEX = 0; INDEX < this.charts.size(); INDEX++) this.updateChart(INDEX);
	}

	/**
	 * Update the chart
	 * @param INDEX	Index of the chart
	 */
	public void updateChart(final int INDEX){
		if (!this.legendVisible){
			this.frame.setVisible(true);	// Set frame visible
			this.legendVisible = true;		// Set legend visible
		}
		this.charts.get(INDEX).updateXYSeries(INDEX+"", this.xData.get(INDEX), this.yData.get(INDEX), null);
			
		// Repaint the chart panel
		panel.validate();	// Validate the panel
		panel.repaint();	// Repaint the panel
	}
}
