/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * This file is part of PlantUML.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original Author:  Arnaud Roques
 */
package net.sourceforge.plantuml.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.graphic.GraphicStrings;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.version.PSystemVersion;

class ImageWindow2 extends JFrame {

	private final static Preferences prefs = Preferences.userNodeForPackage(ImageWindow2.class);
	private final static String KEY_ZOOM_FIT = "zoomfit";

	private SimpleLine2 simpleLine2;
	private final JScrollPane scrollPane;
	private final JButton next = new JButton("Next");
	private final JButton copy = new JButton("Copy");
	private final JButton previous = new JButton("Previous");
	private final JCheckBox zoomFitButt = new JCheckBox("Zoom fit");
	private final MainWindow2 main;

	private final ListModel listModel;
	private int index;

	private enum SizeMode {
		FULL_SIZE, ZOOM_FIT
	};

	private SizeMode sizeMode = SizeMode.FULL_SIZE;

	private int startX, startY;

	public ImageWindow2(SimpleLine2 simpleLine, final MainWindow2 main, ListModel listModel, int index) {
		super(simpleLine.toString());
		setIconImage(PSystemVersion.getPlantumlSmallIcon2());
		this.simpleLine2 = simpleLine;
		this.listModel = listModel;
		this.index = index;
		this.main = main;

		final JPanel north = new JPanel();
		north.add(previous);
		north.add(copy);
		north.add(next);
		north.add(zoomFitButt);
		copy.setFocusable(false);
		copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				copy();
			}
		});
		next.setFocusable(false);
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				next();
			}
		});
		previous.setFocusable(false);
		previous.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				previous();
			}
		});
		zoomFitButt.setFocusable(false);
		zoomFitButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				zoomFit();
			}
		});

		scrollPane = new JScrollPane(buildScrollablePicture());
		getContentPane().add(north, BorderLayout.NORTH);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		setSize(640, 400);
		this.setLocationRelativeTo(this.getParent());
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				main.closing(ImageWindow2.this);
			}
		});


		this.addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent e) {
				super.componentResized(e);
				refreshImage(false);
			}
		});

		final boolean zoomChecked = prefs.getBoolean(KEY_ZOOM_FIT, false);
		zoomFitButt.setSelected(zoomChecked);
		if (zoomChecked) {
			sizeMode = SizeMode.ZOOM_FIT;
		}

		this.setFocusable(true);
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_RIGHT) {
					next();
				} else if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_LEFT) {
					previous();
				} else if (evt.isAltDown() && evt.getKeyCode() == KeyEvent.VK_RIGHT) {
					next();
				} else if (evt.isAltDown() && evt.getKeyCode() == KeyEvent.VK_LEFT) {
					previous();
				} else if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
					imageRight();
				} else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
					imageLeft();
				} else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
					imageDown();
				} else if (evt.getKeyCode() == KeyEvent.VK_UP) {
					imageUp();
				} else if (evt.getKeyCode() == KeyEvent.VK_C) {
					copy();
				} else if (evt.getKeyCode() == KeyEvent.VK_Z) {
					zoomFitButt.setSelected(!zoomFitButt.isSelected());
					zoomFit();
				}
			}
		});

	}

	private void next() {
		index++;
		updateSimpleLine();
	}

	private void previous() {
		index--;
		updateSimpleLine();
	}

	private void imageDown() {
		final JScrollBar bar = scrollPane.getVerticalScrollBar();
		bar.setValue(bar.getValue() + bar.getBlockIncrement());
	}

	private void imageUp() {
		final JScrollBar bar = scrollPane.getVerticalScrollBar();
		bar.setValue(bar.getValue() - bar.getBlockIncrement());
	}

	private void imageLeft() {
		final JScrollBar bar = scrollPane.getHorizontalScrollBar();
		bar.setValue(bar.getValue() - bar.getBlockIncrement());
	}

	private void imageRight() {
		final JScrollBar bar = scrollPane.getHorizontalScrollBar();
		bar.setValue(bar.getValue() + bar.getBlockIncrement());
	}

	private void zoomFit() {
		final boolean selected = zoomFitButt.isSelected();
		prefs.putBoolean(KEY_ZOOM_FIT, selected);
		if (selected) {
			sizeMode = SizeMode.ZOOM_FIT;
		} else {
			sizeMode = SizeMode.FULL_SIZE;
		}
		refreshImage(false);
	}

	private void updateSimpleLine() {
		if (index < 0) {
			index = 0;
		}
		if (index > listModel.getSize() - 1) {
			index = listModel.getSize() - 1;
		}
		simpleLine2 = (SimpleLine2) listModel.getElementAt(index);
		setTitle(simpleLine2.toString());
		refreshImage(false);
	}

	private void refreshSimpleLine() {
		for (SimpleLine2 line : main.getCurrentDirectoryListing2()) {
			if (line.getFile().equals(simpleLine2.getFile())) {
				simpleLine2 = line;
				setTitle(simpleLine2.toString());
			}
		}
	}

	private ScrollablePicture buildScrollablePicture() {
		final GeneratedImage generatedImage = simpleLine2.getGeneratedImage();
		if (generatedImage == null) {
			return null;
		}
		final File png = generatedImage.getPngFile();
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(png.getAbsolutePath()));
			if (sizeMode == SizeMode.ZOOM_FIT) {
				final Dimension imageDim = new Dimension(image.getWidth(), image.getHeight());
				final Dimension newImgDim = ImageHelper
						.getScaledDimension(imageDim, scrollPane.getViewport().getSize());
				final RenderingHints hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				image = ImageHelper.getScaledInstance(image, newImgDim, hints, true);
			}
		} catch (IOException ex) {
			final String msg = "Error reading file: " + ex.toString();
			final GraphicStrings error = GraphicStrings.createDefault(Arrays.asList(msg), false);
			final ImageBuilder imageBuilder = new ImageBuilder(new ColorMapperIdentity(), 1.0, error.getBackcolor(),
					null, null, 0, 0, null, false);
			imageBuilder.setUDrawable(error);
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				imageBuilder.writeImageTOBEMOVED(new FileFormatOption(FileFormat.PNG), baos);
				baos.close();
				image = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		final ImageIcon imageIcon = new ImageIcon(image, simpleLine2.toString());
		final ScrollablePicture scrollablePicture = new ScrollablePicture(imageIcon, 1);
		
		scrollablePicture.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				super.mousePressed(me);
				startX = me.getX();
				startY = me.getY();
			}
		});
		scrollablePicture.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
                super.mouseDragged(me);
                final int diffX = me.getX() - startX;
                final int diffY = me.getY() - startY;

				final JScrollBar hbar = scrollPane.getHorizontalScrollBar();
				hbar.setValue(hbar.getValue() - diffX);
				final JScrollBar vbar = scrollPane.getVerticalScrollBar();
				vbar.setValue(vbar.getValue() - diffY);
            }
        });

		
		return scrollablePicture;
	}

	private void copy() {
		final GeneratedImage generatedImage = simpleLine2.getGeneratedImage();
		if (generatedImage == null) {
			return;
		}
		final File png = generatedImage.getPngFile();
		final Image image = Toolkit.getDefaultToolkit().createImage(png.getAbsolutePath());
		final ImageSelection imgSel = new ImageSelection(image);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
	}

	public SimpleLine2 getSimpleLine() {
		return simpleLine2;
	}

	private int v1;
	private int v2;

	public void refreshImage(boolean external) {
		final JScrollBar bar1 = scrollPane.getVerticalScrollBar();
		final JScrollBar bar2 = scrollPane.getHorizontalScrollBar();
		if (external && isError() == false) {
			v1 = bar1.getValue();
			v2 = bar2.getValue();
		}
		scrollPane.setViewportView(buildScrollablePicture());
		force();
		if (external) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					refreshSimpleLine();
					if (isError() == false) {
						bar1.setValue(v1);
						bar2.setValue(v2);
					}
				}
			});
		}
	}

	private boolean isError() {
		return simpleLine2.getGeneratedImage() != null && simpleLine2.getGeneratedImage().lineErrorRaw() != -1;

	}

	private void force() {
		// setVisible(true);
		repaint();
		// validate();
		// getContentPane().validate();
		// getContentPane().setVisible(true);
		// getContentPane().repaint();
		// scrollPane.validate();
		// scrollPane.setVisible(true);
		// scrollPane.repaint();
	}

}

// This class is used to hold an image while on the clipboard.
class ImageSelection implements Transferable {
	private Image image;

	public ImageSelection(Image image) {
		this.image = image;
	}

	// Returns supported flavors
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DataFlavor.imageFlavor };
	}

	// Returns true if flavor is supported
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return DataFlavor.imageFlavor.equals(flavor);
	}

	// Returns image
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (!DataFlavor.imageFlavor.equals(flavor)) {
			throw new UnsupportedFlavorException(flavor);
		}
		return image;
	}
}
