package com.gino.citireapometre_pro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gino.citireapometre_pro.DB.ApometreDataSource;
import com.gino.citireapometre_pro.DB.Consum;
import com.gino.citireapometre_pro.DB.ConsumListHelper;
import com.gino.citireapometre_pro.DB.Locuinta;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

public class GraficFragment extends Fragment implements FragmentLifecycle
{
	public enum ScreenSize 
	{
		 HEIGHT,WIDTH;
	}
	private ApometreDataSource datasource;
	private int graphMargins;
	public LineGraphView graphView;
	private int BLUE_COLOR;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_grafic, container, false);

		datasource = new ApometreDataSource(this.getActivity());
		datasource.open();

		BLUE_COLOR = ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null);

		WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int screenHeight = display.getHeight();
		graphMargins = 0;//screenHeight * 3/100;

		if (((MainActivity) getActivity()).ddlLocations.getSelectedItem() != null)
		{
			long idLocuinta = ((Locuinta) ((MainActivity) getActivity()).ddlLocations.getSelectedItem()).getId();
			//renderGraph(view);
			renderGraphAChartEngine(idLocuinta, view);
		}
		return view;
	}
	@Override
	public void onResume() 
	{	     
	    super.onResume();
	     
	    datasource = new ApometreDataSource(this.getActivity());
		datasource.open();
			
		WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();	
		int screenHeight=display.getHeight();
		graphMargins = 0;//screenHeight * 3/100;

		if(((MainActivity) getActivity()).ddlLocations.getSelectedItem()!=null)
		{
			long idLocuinta = ((Locuinta) ((MainActivity) getActivity()).ddlLocations.getSelectedItem()).getId();
			//renderGraph(view);
			renderGraphAChartEngine(idLocuinta, getView());
		}
	}
	public void renderGraph(long idLocuinta,View view)
	{
		List<ConsumListHelper> clhList = datasource.getConsumListHelper(idLocuinta,"Grafic");
		
		TextView lblNoData = new TextView(view.getContext());
		lblNoData.setText(getActivity().getResources().getString(R.string.Trebuie_completate_minim_3_luni_cu_date_despre_consum_pentru_a_putea_desena_graficul)+"!");
		lblNoData.setTextSize(20);
		
		LinearLayout layout = (LinearLayout)view.findViewById(R.id.graph);
		
		layout.removeAllViews();
		if(clhList.size()>2)
		{		
			
    		List<Float> serieConsumRece=getSeries(getActivity().getResources().getString(R.string.Rece),clhList);		
    		List<Float> serieConsumCalda=getSeries(getActivity().getResources().getString(R.string.Calda),clhList);
    		
    		
    		
    		String []legendaX=new String[clhList.size()-1];//prima luna nu apare pe grafic
    		for (int i = 1; i < clhList.size(); i++)
            {			
    	        legendaX[i-1]=Common.getLuna(getActivity(), clhList.get(i).getData().substring(3,5));
            }
    		String []legendaY=new String[serieConsumRece.size()];		
    		for (int i = 0; i < serieConsumRece.size(); i++)
            {
    			List<Float>temp=new ArrayList<Float>(serieConsumRece);
    			Collections.sort(temp,Collections.reverseOrder());
    	        legendaY[i]=String.format("%.0f", temp.get(i));
            }
    		
    		
    		GraphViewData[] dataRece = new GraphViewData[serieConsumRece.size()];
    		for (int i = 0; i < serieConsumRece.size(); i++)
            {
    			dataRece[i] = new GraphViewData(i, serieConsumRece.get(i));
            }
    		GraphViewData[] dataCalda = new GraphViewData[serieConsumCalda.size()];
    		for (int i = 0; i < serieConsumRece.size(); i++)
            {
    			dataCalda[i] = new GraphViewData(i, serieConsumCalda.get(i));
            }
    		
            
            
            GraphViewSeries seriesRece = new GraphViewSeries("Consum Rece", new GraphViewSeriesStyle(BLUE_COLOR, 4), dataRece);
            GraphViewSeries seriesCalda = new GraphViewSeries("Consum Calda", new GraphViewSeriesStyle(Color.RED , 4), dataCalda);
             
            graphView = new LineGraphView(getActivity(), "");
            graphView.setDrawDataPoints(true);
            graphView.setDataPointsRadius(12f);
            graphView.setHorizontalLabels(legendaX);
            //graphView.setVerticalLabels(legendaY);
         // set styles
            graphView.getGraphViewStyle().setVerticalLabelsAlign(Align.CENTER);
            graphView.getGraphViewStyle().setGridColor(Color.BLACK);
            graphView.getGraphViewStyle().setHorizontalLabelsColor(BLUE_COLOR);
            graphView.getGraphViewStyle().setVerticalLabelsColor(BLUE_COLOR);
            graphView.getGraphViewStyle().setNumVerticalLabels(serieConsumRece.size());
            graphView.getGraphViewStyle().setTextSize(20);
            
            
            
            
            graphView.addSeries(seriesRece); // data
            graphView.addSeries(seriesCalda); // data
            
         // set legend
         	graphView.setShowLegend(true);
         	graphView.setLegendAlign(LegendAlign.TOP);
         	graphView.getGraphViewStyle().setLegendBorder(20);
         	graphView.getGraphViewStyle().setLegendSpacing(20);
         	graphView.getGraphViewStyle().setLegendWidth(200);
         	
         	 
         	graphView.setManualYAxisBounds(getMaxBounds(serieConsumRece, serieConsumCalda), 0);    	
         	
         	
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)layout.getLayoutParams();
            params.setMargins(graphMargins, graphMargins, graphMargins, graphMargins); //substitute parameters for left, top, right, bottom
            layout.setLayoutParams(params);
            layout.addView(graphView);
		}
		else 
		{			
			layout.addView(lblNoData);
		}
	}
	
	public void renderGraphAChartEngine(long idLocuinta,View view)
	{		
		List<ConsumListHelper> clhList = datasource.getConsumListHelper(idLocuinta,"Grafic");
		
		TextView lblNoData = new TextView(view.getContext());
		lblNoData.setText(getActivity().getResources().getString(R.string.Trebuie_completate_minim_3_luni_cu_date_despre_consum_pentru_a_putea_desena_graficul)+"!");
		lblNoData.setTextSize(20);
		
		LinearLayout layout = (LinearLayout)view.findViewById(R.id.graph);
		
		layout.removeAllViews();
		if(clhList.size()>2)
		{	
    		GraphicalView mChart;
    		String []mMonth=new String[clhList.size()];//prima luna nu apare pe grafic
    		for (int i = 0; i < clhList.size(); i++)
            {			
    			mMonth[i]=Common.getLuna(getActivity(), clhList.get(i).getData().substring(3,5));
            }    		
    		
    	    List<Float> listaConsumRece=getSeries(getActivity().getResources().getString(R.string.Rece),clhList);		
       		List<Float> listaConsumCalda=getSeries(getActivity().getResources().getString(R.string.Calda),clhList);
    	     
    	     XYSeries receSeries=new XYSeries(getActivity().getResources().getString(R.string.Rece));
    	     XYSeries caldaSeries=new XYSeries(getActivity().getResources().getString(R.string.Calda));	     
    	     for(int i=0;i<listaConsumRece.size();i++)
    	     {
    	      receSeries.add(i+1,listaConsumRece.get(i));
    	      caldaSeries.add(i+1, listaConsumCalda.get(i));
    	      //i+1 pt a adauga si prima luna, sa nu inceapa graficul de la 0
    	     }
    
    	        // Create a Dataset to hold the XSeries.	     
    	     XYMultipleSeriesDataset dataset=new XYMultipleSeriesDataset();	     
    	      // Add X series to the Dataset.   
    	     dataset.addSeries(receSeries);
    	     dataset.addSeries(caldaSeries);
    	     
    	     
    	      
    	     XYSeriesRenderer XrendererRece=new XYSeriesRenderer();
    	     XrendererRece.setColor(BLUE_COLOR);
    	     XrendererRece.setPointStyle(PointStyle.CIRCLE); 
    	     XrendererRece.setLineWidth(5);
    	     XrendererRece.setFillPoints(true);
    	     
    	     XYSeriesRenderer XrendererCalda=new XYSeriesRenderer();
    	     XrendererCalda.setColor(Color.RED);
    	     XrendererCalda.setPointStyle(PointStyle.CIRCLE); 
    	     XrendererCalda.setLineWidth(5);
    	     XrendererCalda.setFillPoints(true);
    	     
    	     // Create XYMultipleSeriesRenderer to customize the whole chart
    	     XYMultipleSeriesRenderer mRenderer=new XYMultipleSeriesRenderer();	     
    	     mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
    	     //mRenderer.setAxisTitleTextSize(0);
    	     //mRenderer.setChartTitleTextSize(0);
    	     mRenderer.setLabelsTextSize(getScreenPercentage(3, ScreenSize.WIDTH));
    	     mRenderer.setLegendTextSize(getScreenPercentage(2, ScreenSize.HEIGHT));	     
    	     mRenderer.setPointSize(getScreenPercentage(1.5f, ScreenSize.WIDTH));
    	     mRenderer.setMargins(new int[] { 0,//top 
    	    		 						  getScreenPercentage(7,	ScreenSize.WIDTH),//left
    	    		 						  getScreenPercentage(3, ScreenSize.HEIGHT),//bottom
    	    		 						  0 });//right
    	     mRenderer.setYLabelsPadding(getScreenPercentage(3, ScreenSize.WIDTH));
    	     mRenderer.setLegendHeight(getScreenPercentage(10, ScreenSize.HEIGHT));
    	     mRenderer.setPanEnabled(false);
    	     mRenderer.setGridColor(Color.BLACK);
    	     mRenderer.setAxesColor(Color.BLACK);
    	     mRenderer.setXLabelsColor(Color.BLACK);
    	     mRenderer.setYLabelsColor(0, Color.BLACK);
    	     mRenderer.setLabelsColor(Color.BLACK);
    	     mRenderer.setShowGrid(true);	     
    	     mRenderer.setZoomButtonsVisible(false);
    	     
    	     mRenderer.setYAxisMin(0);
    	     mRenderer.setXAxisMin(0);
    	     mRenderer.setXAxisMax(mMonth.length);
    	     mRenderer.setYAxisMax(getMaxBounds(listaConsumRece, listaConsumCalda));
    	     
    	     mRenderer.setXLabels(0);
    	     
    	     for(int i=0;i<mMonth.length;i++)
    	     {
    	      mRenderer.addXTextLabel(i, mMonth[i]);
    	     }
    	     
    	       // Adding the XSeriesRenderer to the MultipleRenderer. 
    	     mRenderer.addSeriesRenderer(XrendererRece);
    	     mRenderer.addSeriesRenderer(XrendererCalda);	     
    	    
    
    	   // Creating an intent to plot line chart using dataset and multipleRenderer
    	     
    	     mChart=(GraphicalView)ChartFactory.getLineChartView(view.getContext(), dataset, mRenderer); 
    	     
    	// Add the graphical view mChart object into the Linear layout .
    	     layout.addView(mChart);
		}
		else 
		{			
			layout.addView(lblNoData);
		}
	     
	}
	public int getScreenPercentage(float percentage,ScreenSize sizeType)
	{
		int sizePercentageValue;
		WindowManager wm = (WindowManager) this.getActivity().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();			
		int size=display.getHeight();
		if(sizeType==ScreenSize.WIDTH)
			size=display.getWidth();
		sizePercentageValue = Math.round(size * percentage/100);
		
		return sizePercentageValue;
	}

	
	public int getMaxBounds(List<Float> lista1, List<Float> lista2)
	{
		int bound=0;
		int max1 =Math.round(Collections.max(lista1));
		int max2 =Math.round(Collections.max(lista2));
		
		int rawBound=Math.max(max1, max2);
		
		bound =Math.round(rawBound + (rawBound * 20 / 100));
		
     	return bound;
	}
	public List<Float> getSeries(String Tip, List<ConsumListHelper> clhList)
	{		
		List<Float> consumuri = new ArrayList<Float>();		
		
		for (int i = 1; i < clhList.size(); i++)
        {
			ConsumListHelper currentClh = clhList.get(i);
			ConsumListHelper previousClh = getPreviousConsum(currentClh, clhList);
			
			float totalVechi=calculateTotal(previousClh.getListConsum(), Tip);
			float totalNou=calculateTotal(currentClh.getListConsum(), Tip);
			
			consumuri.add(totalNou-totalVechi);			
        }		
		return consumuri;
	}
	public float calculateTotal(List<Consum> consum, String Tip)
	{
		float total=0;
		for (int i = 0; i < consum.size(); i++)
		{			
			if (Tip.equals(getActivity().getResources().getString(R.string.Calda)))
			{
				total+=consum.get(i).getConsumCalda();
			}
			else 
			{
				total+=consum.get(i).getConsumRece();
			}			
		}
		return total;
	}
	public ConsumListHelper getPreviousConsum(ConsumListHelper consumCurent,List<ConsumListHelper> consumuri)
	{
		int currentPosition=0;
		for (int i = 0; i < consumuri.size(); i++)
		{
			///trebuie gasita pozitia consumului curent in lista totala
			if (consumCurent.getData().equals(consumuri.get(i).getData()))
			{
				currentPosition=i;
				break;
			}
		}
		if (currentPosition>0)
		{
			return consumuri.get(currentPosition - 1);
		}
		else 
		{
			return null;					
		}
	}
//	public String getLuna(String NrLuna)
//	{
//		String luna="Ian";
//		if(NrLuna.equals("01"))
//			luna="Dec";
//		if(NrLuna.equals("02"))
//			luna="Ian";
//		if(NrLuna.equals("03"))
//			luna="Feb";
//		if(NrLuna.equals("04"))
//			luna="Mar";
//		if(NrLuna.equals("05"))
//			luna="Apr";
//		if(NrLuna.equals("06"))
//			luna="Mai";
//		if(NrLuna.equals("07"))
//			luna="Iun";
//		if(NrLuna.equals("08"))
//			luna="Iul";
//		if(NrLuna.equals("09"))
//			luna="Aug";
//		if(NrLuna.equals("10"))
//			luna="Sep";
//		if(NrLuna.equals("11"))
//			luna="Oct";
//		if(NrLuna.equals("12"))
//			luna="Nov";	
//		
//		return luna;
//	}	
	@Override
    public void onResumeFragment(Locuinta location)
	{
		//renderGraph(getView());
		if (this.getActivity() != null)
			if (location != null)
				renderGraphAChartEngine(location.getId(), getView());
	}

	@Override
	public void onLocationChange(Locuinta location)
	{
		if(this.getActivity()!=null)
		{
			if (datasource == null)
				datasource = new ApometreDataSource(this.getActivity());
			datasource.open();

			renderGraphAChartEngine(location.getId(), getView());
		}
	}

}
