/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2008 France Télécom

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/


package jade.semantics.ext.emotion.tools;

import jade.semantics.ext.emotion.EmotionalState;
import jade.semantics.ext.emotion.EmotionalStateObserver;
import jade.semantics.ext.emotion.Emotion.Type;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.GridLayout;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/** Interface Graphique des émotions */
public class InterfaceGraphEmotion extends JPanel implements EmotionalStateObserver {

	
	private JFreeChart chart;
	private JFreeChart chartEmp;
	
	private JFreeChart chartTimeSerieEmo;
	private JFreeChart chartTimeSerieEmoEmp;
	
	private TimeSeries seriesSatisf;
	//private TimeSeries seriesJoie;
	private TimeSeries seriesTristes;
	private TimeSeries seriesFrustr;
	private TimeSeries seriesEnerv;
	private TimeSeries seriesColere;
	
	private TimeSeries seriesSatisfEmp;
	//private TimeSeries seriesJoieEmp;
	private TimeSeries seriesTristesEmp;
	private TimeSeries seriesFrustrEmp;
	private TimeSeries seriesEnervEmp;
	private TimeSeries seriesColereEmp;
	
	
	private  CategoryDataset dataset;
	private CategoryDataset dataSetEmp;
	
	private XYDataset serieEmodataset;
	private XYDataset serieEmoEmpdataset;
	
	private EmotionalState etat_emo;
	
	private boolean timeSerieGraph;
	
	/** Constructeur de l'interface graphique */
	public InterfaceGraphEmotion(boolean timeSerieGraph){
		
		this.setLayout(new GridLayout(2, 1));
				
		this.timeSerieGraph = timeSerieGraph;
		
		this.setVisible(true);
	}
	
	/* (non-Javadoc)
	 * @see jade.semantics.ext.emotion.EmotionalStateObserver#emotionalStateChanged(jade.semantics.ext.emotion.EmotionalState)
	 */
	public void emotionalStateChanged(EmotionalState e) {
		if (this.etat_emo == null) {
			this.etat_emo = e;
			ChartPanel chartPanel=null;
			ChartPanel chartPanelEmp=null;
			
			if(!timeSerieGraph){
				this.dataset = createDataset();
				this.dataSetEmp=createDatasetEmpathie() ;
				
				this.chart = createChart(dataset, "Intensité des émotions");
				this.chartEmp=createChart(this.dataSetEmp, "Intensité des émotions d'empathie");
			
				chartPanel = new ChartPanel(this.chart);
				chartPanelEmp=new ChartPanel(this.chartEmp);
				
				this.add(chartPanel);
				this.add(chartPanelEmp);
			
			}else{
				this.serieEmodataset=createSerieEmodataset();
				this.serieEmoEmpdataset=createSerieEmoEmpdataset();
				
				this.chartTimeSerieEmo=createChartSerieEmo(this.serieEmodataset, "Evolution temporelle des émotions");
				this.chartTimeSerieEmoEmp=createChartSerieEmo(this.serieEmoEmpdataset, "Evolution temporelle des émotions d'empathie");
				
				chartPanel = new ChartPanel(this.chartTimeSerieEmo);
				chartPanelEmp=new ChartPanel(this.chartTimeSerieEmoEmp);
				this.add(chartPanel);
				this.add(chartPanelEmp);
				
			}
			chartPanel.setPreferredSize(new Dimension(400, 250));
			chartPanelEmp.setPreferredSize(new Dimension(400, 250));

		}
		
		if (!timeSerieGraph) {
			updateDataSet(e);
			updateDataSetEmp(e);
		}
		else {
			updateDataSetSerie(e);
			updateDataSetSerieEmp(e);
		}
		
	}
	
	
	public CategoryDataset createDataset() {
	
		String series1 = "value";
	
		//String joie = "Joie";
		String satisf = "Satisfaction";
		String frustr = "Frustration";
		String enerv = "Irritation";
		String tristes = "Tristesse";
		String colere = "Colère";
		
		DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();
		
		//dataset1.addValue(this.etat_emo.typeToEmotion(Emotion.Type.JOIE).getIntensity(), series1, joie);
		dataset1.addValue(this.etat_emo.typeToEmotion(Type.SATISF).getIntensity(), series1, satisf);
		dataset1.addValue(this.etat_emo.typeToEmotion(Type.FRUSTR).getIntensity(), series1, frustr);
		dataset1.addValue(this.etat_emo.typeToEmotion(Type.ENERV).getIntensity(), series1, enerv);
		dataset1.addValue(this.etat_emo.typeToEmotion(Type.TRISTES).getIntensity(), series1, tristes);
		dataset1.addValue(this.etat_emo.typeToEmotion(Type.COLERE).getIntensity(), series1, colere);
		
		return dataset1;
	}
	
	
	public CategoryDataset createDatasetEmpathie() {
			String series1 = "value";
			//String joie = "Joie";
			String satisf = "Satisfaction";
			String frustr = "Frustration";
			String enerv = "Irritation";
			String tristes = "Tristesse";
			String colere = "Colère";
			
			DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();
	
			//dataset1.addValue(this.etat_emo.getIntensityEmp(Emotion.Type.JOIE), series1, joie);
			dataset1.addValue(this.etat_emo.getIntensityEmp(Type.SATISF), series1, satisf);
			dataset1.addValue(this.etat_emo.getIntensityEmp(Type.FRUSTR), series1, frustr);
			dataset1.addValue(this.etat_emo.getIntensityEmp(Type.ENERV), series1, enerv);
			dataset1.addValue(this.etat_emo.getIntensityEmp(Type.TRISTES), series1, tristes);
			dataset1.addValue(this.etat_emo.getIntensityEmp(Type.COLERE), series1, colere);
			
			return dataset1;
		}
	
	public XYDataset createSerieEmodataset(){
		this.seriesSatisf = new TimeSeries("Satisf", Second.class);
		//this.seriesJoie= new TimeSeries("Joie", Second.class);
		this.seriesTristes= new TimeSeries("Triste", Second.class);
		this.seriesFrustr= new TimeSeries("Frustr", Second.class);
		this.seriesEnerv= new TimeSeries("Irrit", Second.class);
		this.seriesColere= new TimeSeries("Colere", Second.class);
		
		this.seriesSatisf.add(new Second(), this.etat_emo.typeToEmotion(Type.SATISF).getIntensity());
		//this.seriesJoie.add(new Second(), this.etat_emo.typeToEmotion(Type.JOIE).getIntensity());
		this.seriesTristes.add(new Second(), this.etat_emo.typeToEmotion(Type.TRISTES).getIntensity());
		this.seriesFrustr.add(new Second(), this.etat_emo.typeToEmotion(Type.FRUSTR).getIntensity());
		this.seriesEnerv.add(new Second(), this.etat_emo.typeToEmotion(Type.ENERV).getIntensity());
		this.seriesColere.add(new Second(), this.etat_emo.typeToEmotion(Type.COLERE).getIntensity());
	
		
		TimeSeriesCollection dataset1 = new TimeSeriesCollection();
		dataset1.addSeries(this.seriesSatisf);
		//dataset1.addSeries(this.seriesJoie);
		dataset1.addSeries(this.seriesTristes);
		dataset1.addSeries(this.seriesFrustr);
		dataset1.addSeries(this.seriesEnerv);
		dataset1.addSeries(this.seriesColere);
		
		
        return dataset1;
	}
	
	public XYDataset createSerieEmoEmpdataset(){
		this.seriesSatisfEmp = new TimeSeries("Satisf", Second.class);
		//this.seriesJoieEmp= new TimeSeries("Joie", Second.class);
		this.seriesTristesEmp= new TimeSeries("Triste", Second.class);
		this.seriesFrustrEmp= new TimeSeries("Frustr", Second.class);
		this.seriesEnervEmp= new TimeSeries("Irrit", Second.class);
		this.seriesColereEmp= new TimeSeries("Colere", Second.class);
		
		this.seriesSatisfEmp.add(new Second(), this.etat_emo.getIntensityEmp(Type.SATISF));
		//this.seriesJoieEmp.add(new Second(), this.etat_emo.getIntensityEmp(Type.JOIE));
		this.seriesTristesEmp.add(new Second(), this.etat_emo.getIntensityEmp(Type.TRISTES));
		this.seriesFrustrEmp.add(new Second(), this.etat_emo.getIntensityEmp(Type.FRUSTR));
		this.seriesEnervEmp.add(new Second(), this.etat_emo.getIntensityEmp(Type.ENERV));
		this.seriesColereEmp.add(new Second(), this.etat_emo.getIntensityEmp(Type.COLERE));
	
		
		TimeSeriesCollection dataset1 = new TimeSeriesCollection();
		dataset1.addSeries(this.seriesSatisfEmp);
		//dataset1.addSeries(this.seriesJoieEmp);
		dataset1.addSeries(this.seriesTristesEmp);
		dataset1.addSeries(this.seriesFrustrEmp);
		dataset1.addSeries(this.seriesEnervEmp);
		dataset1.addSeries(this.seriesColereEmp);
		
		
        return dataset1;
	
	}
	private static JFreeChart createChart(CategoryDataset dataset, String titre) {
		JFreeChart chart1 = ChartFactory.createBarChart(
				titre, // chart title
				"", // domain axis label
				"", // range axis label
				dataset, // data
				PlotOrientation.HORIZONTAL, // orientation
				false, // include legend
				false, // tooltips?
				false // URLs?
			);
		
		chart1.setBackgroundPaint(Color.white);
	
		CategoryPlot plot = chart1.getCategoryPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
		 	
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0.0, 1.0);
		
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setDrawBarOutline(false);
		
		
		GradientPaint gp0 = new GradientPaint(
				0.0f, 0.0f, Color.DARK_GRAY,
				0.0f, 0.0f, new Color(0, 0, 64)
				);
		
		renderer.setSeriesPaint(0, gp0);
		CategoryItemRenderer renderer2 = plot.getRenderer();
        renderer2.setItemLabelGenerator(
                new StandardCategoryItemLabelGenerator());
        renderer2.setSeriesItemLabelsVisible(0, Boolean.TRUE);
        
       plot.setRenderer(renderer2);
        
		
		return chart1;
	}
	
	private static JFreeChart createChartSerieEmo(XYDataset dataset, String titre){
		 JFreeChart chart = ChartFactory.createTimeSeriesChart(
	           titre,
	            "", 
	            "",
	            dataset,
	            true,
	            false,
	            false
	        );
		 chart.setBackgroundPaint(Color.white);
		 XYPlot plot = (XYPlot) chart.getPlot();
		 plot.setBackgroundPaint(Color.WHITE);
		 plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		 
		 
		 ValueAxis axis = plot.getRangeAxis();
		 axis.setRange(0.0, 1.0);
		 	 
		 plot.setDomainCrosshairVisible(true);
		 plot.setRangeCrosshairVisible(true);
	
		 XYStepRenderer renderer = new XYStepRenderer();
		 
		 renderer.setSeriesPaint(0, Color.BLUE);
		 renderer.setSeriesPaint(1, Color.RED);
		 renderer.setSeriesPaint(2, Color.GREEN);
		 renderer.setSeriesPaint(3, Color.CYAN);
		 renderer.setSeriesPaint(4, Color.MAGENTA);
		// renderer.setSeriesPaint(5, Color.ORANGE);
		
		 plot.setRenderer(renderer);
		 
		 return chart;
	}
	
	/** Mise à jour de l'affichage 
	 * @param e nouvel état émotionnel
	 */
	public void updateDataSet(EmotionalState e){
		this.etat_emo=e;
		
		String series1 = "value";
	//	String joie = "Joie";
		String satisf = "Satisfaction";
		String frustr = "Frustration";
		String enerv = "Irritation";
		String tristes = "Tristesse";
		String colere = "Colère";
				
	//	((DefaultCategoryDataset)this.dataset).setValue(this.etat_emo.typeToEmotion(Type.JOIE).getIntensity(), series1, joie);
		((DefaultCategoryDataset)this.dataset).setValue(this.etat_emo.typeToEmotion(Type.SATISF).getIntensity(), series1, satisf);
		((DefaultCategoryDataset)this.dataset).setValue(this.etat_emo.typeToEmotion(Type.FRUSTR).getIntensity(), series1, frustr);
		((DefaultCategoryDataset)this.dataset).setValue(this.etat_emo.typeToEmotion(Type.ENERV).getIntensity(), series1, enerv);
		((DefaultCategoryDataset)this.dataset).setValue(this.etat_emo.typeToEmotion(Type.TRISTES).getIntensity(), series1, tristes);
		((DefaultCategoryDataset)this.dataset).setValue(this.etat_emo.typeToEmotion(Type.COLERE).getIntensity(), series1, colere);

		
	}
	
	/** Mise à jour de l'affichage 
	 * @param e nouvel état émotionnel
	 */
	public void updateDataSetSerie(EmotionalState e){
		this.etat_emo=e;
		
		this.seriesSatisf.addOrUpdate(new Second(), this.etat_emo.typeToEmotion(Type.SATISF).getIntensity());
	//	this.seriesJoie.addOrUpdate(new Second(), this.etat_emo.typeToEmotion(Type.JOIE).getIntensity());
		this.seriesTristes.addOrUpdate(new Second(), this.etat_emo.typeToEmotion(Type.TRISTES).getIntensity());
		this.seriesFrustr.addOrUpdate(new Second(), this.etat_emo.typeToEmotion(Type.FRUSTR).getIntensity());
		this.seriesEnerv.addOrUpdate(new Second(), this.etat_emo.typeToEmotion(Type.ENERV).getIntensity());
		this.seriesColere.addOrUpdate(new Second(), this.etat_emo.typeToEmotion(Type.COLERE).getIntensity());

	}
	
	/** Mise à jour de l'affichage 
	 * @param e nouvel état émotionnel
	 */
	public void updateDataSetSerieEmp(EmotionalState e){
		this.etat_emo=e;
		
		this.seriesSatisfEmp.addOrUpdate(new Second(), this.etat_emo.getIntensityEmp(Type.SATISF));
		//this.seriesJoieEmp.addOrUpdate(new Second(), this.etat_emo.getIntensityEmp(Type.JOIE));	
		this.seriesTristesEmp.addOrUpdate(new Second(), this.etat_emo.getIntensityEmp(Type.TRISTES));
		this.seriesFrustrEmp.addOrUpdate(new Second(), this.etat_emo.getIntensityEmp(Type.FRUSTR));
		this.seriesEnervEmp.addOrUpdate(new Second(), this.etat_emo.getIntensityEmp(Type.ENERV));
		this.seriesColereEmp.addOrUpdate(new Second(), this.etat_emo.getIntensityEmp(Type.COLERE));

	}
	
	/** Mise à jour de l'affichage 
	 * @param e nouvel état émotionnel
	 */
	public void updateDataSetEmp(EmotionalState e){
		this.etat_emo=e;
		
		String series1 = "value";
		//String joie = "Joie";
		String satisf = "Satisfaction";
		String frustr = "Frustration";
		String enerv = "Irritation";
		String tristes = "Tristesse";
		String colere = "Colère";
				
		//((DefaultCategoryDataset)this.dataSetEmp).setValue(this.etat_emo.getIntensityEmp(Type.JOIE), series1, joie);
		((DefaultCategoryDataset)this.dataSetEmp).setValue(this.etat_emo.getIntensityEmp(Type.SATISF), series1, satisf);
		((DefaultCategoryDataset)this.dataSetEmp).setValue(this.etat_emo.getIntensityEmp(Type.FRUSTR), series1, frustr);
		((DefaultCategoryDataset)this.dataSetEmp).setValue(this.etat_emo.getIntensityEmp(Type.ENERV), series1, enerv);
		((DefaultCategoryDataset)this.dataSetEmp).setValue(this.etat_emo.getIntensityEmp(Type.TRISTES), series1, tristes);
		((DefaultCategoryDataset)this.dataSetEmp).setValue(this.etat_emo.getIntensityEmp(Type.COLERE), series1, colere);

		
	}

}
