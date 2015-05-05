import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class ScatterPlot {
	
	public ScatterPlot(String name, final ArrayList<ArrayList<Double>> sizes, final ArrayList<ArrayList<Double>> colors)
	{
		final int years = sizes.size();
		final int offset = 50;
		double maxS = 0.0;
		double minS = 10.0;
		double maxC = 0.0;
		double minC = 10.0;
		double tempSize = 5.0;
		double tempColor = 5.0;
		for (int i = 0; i < years; i++)
		{
			for (int j = 0; j < sizes.get(i).size(); j++)
			{
				tempSize = sizes.get(i).get(j);
				tempColor = colors.get(i).get(j);
				if (tempSize > maxS) maxS = tempSize;
				if (tempSize < minS) minS = tempSize;
				if (tempColor > maxC) maxC = tempColor;
				if (tempColor < minC) minC = tempColor;
			}
		}
		final double maxSize = Math.ceil(maxS);
		final double minSize = Math.floor(minS);
		final double maxColor = maxC;
		final double minColor = minC;
		
		final int pointRadius = 5;
		JFrame frame = new JFrame(name);
		frame.setSize(1000, 600);
		JPanel pane = new JPanel()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = -3160834898041312539L;

			protected void paintComponent(Graphics g)
			{
				int h = getHeight();
				int w = getWidth();
				super.paintComponent(g);
				g.setColor(Color.WHITE);
				g.fillRect(offset, offset, w - 2*offset, h-2*offset);
				
				g.setColor(Color.BLACK);
				g.drawLine(offset, offset, offset, h - offset);
				g.drawLine(offset, h - offset, w - offset, h-offset);
				g.drawString(String.valueOf(maxSize), offset/2, offset/2);
				g.drawString(String.valueOf(minSize), offset/2, h - offset/2);
				g.drawString("0", offset/4, h - offset);
				g.drawString(String.valueOf(years), w - offset/2, h - offset);
				g.setColor(new Color(0,0,0,0.2f));
				for (int i = 0; i < maxSize - minSize; i++)
				{
					int yLine = (int) ((h - 2*offset)*((double) i)/(maxSize - minSize) + offset);
					g.drawLine(offset, yLine, w - offset, yLine);
				}
				
				for(int i = 0; i < years; i++)
				{
					for(int j = 0; j < sizes.get(i).size(); j++)
					{
						float red = (float) ((colors.get(i).get(j).doubleValue() - minColor) / (maxColor - minColor));
						float blue = (float) (((maxColor - minColor) - (colors.get(i).get(j).doubleValue() - minColor)) / (maxColor - minColor));
						g.setColor(new Color(red, 0, blue, 0.05f));
						double x = ((double) i)/ years * (w - 2*offset) + offset;
						double invY = ((sizes.get(i).get(j) - minSize) / (maxSize-minSize)) * (h - 2*offset) + offset;
						double y = h - invY;
						g.fillOval((int) x, (int)y, pointRadius, pointRadius);
					}
				}				
			}
		};
		
		frame.getContentPane().add(pane);
		frame.setVisible(true);
	}
}
