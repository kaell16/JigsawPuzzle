package puzzle.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;

import org.w3c.dom.css.RGBColor;
import puzzle.GameCommander;
import puzzle.GamePreferences;
import puzzle.PuzzleProperties;
import puzzle.edge.AbstractEdgeProducer;
import puzzle.edge.FlatEdgeProducer;
import puzzle.edge.ModernEdgeProducer;
import puzzle.edge.StandardEdgeProducer;
import puzzle.storeage.JigsawPuzzleException;
import puzzle.ui.extensions.ExtensionFileFilter;
import puzzle.ui.extensions.ImagePreview;
import puzzle.ImageFilter;

public class GameStartDialog extends JDialog {
	
	private static final Logger logger = Logger.getLogger(TutorialDialog.class);

	private final Dimension previewPanelSize = new Dimension(160, 200);

	// parts that are in the design
	// partes que estão no design

	// start of the sideLength
	// início do sideLength
	private JRadioButton side20Pixel;
	
	private JRadioButton side30Pixel; 

	private JRadioButton side40Pixel;
	
	private JRadioButton side50Pixel;

	private JRadioButton side60Pixel;
	// end of the sideLength
	// fim do sideLength

	private JRadioButton side100Pixel;
	
	private JRadioButton side200Pixel;
	
	private JCheckBox allowTurn;

	private JLabel imageInfo;

	private PreviewPanel previewPanel;

	private JButton findFileButton;

	private JButton cancelButton;

	private JButton startButton;

	// edges
	// bordas
	private JRadioButton standardEdge;
	
	private JRadioButton flatEdge;
	
	private JRadioButton modernEdge;
	
	// other stuff
	// outras coisas
	private JFileChooser imgFileChoo;

	private File currentFile;

	private PuzzleImage image;

	private int puzzlePieceSize;
	
	private ExtensionFileFilter eff;

	public GameStartDialog(Frame owner) {
		super(owner, true);
		this.setTitle(PuzzleProperties.getLocalized("newGameDialogHeader"));
		this.setLocationByPlatform(true);
		this.setResizable(true);

		this.initInteractive();
		this.initComponents();
		this.doPreferences();
	}
	
	private void showErrorMessage(String title, String message) {
		JOptionPane.showMessageDialog(this.getOwner(), title, message, JOptionPane.ERROR_MESSAGE);
	}

	private void doPreferences() {
		this.side20Pixel.setSelected(true);
		this.puzzlePieceSize = 20;
		this.standardEdge.setSelected(true);
	}

	private void initInteractive() {
		this.side20Pixel = new JRadioButton(PuzzleProperties
				.getLocalized("size20px"));
		this.side20Pixel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				puzzlePieceSize = 20;
				retrieveAndShowImageInfo();
			}
		});
		this.side30Pixel = new JRadioButton(PuzzleProperties
				.getLocalized("size30px"));
		this.side30Pixel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				puzzlePieceSize = 30;
				retrieveAndShowImageInfo();
			}
		});
		this.side40Pixel = new JRadioButton(PuzzleProperties
				.getLocalized("size40px"));
		this.side40Pixel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				puzzlePieceSize = 40;
				retrieveAndShowImageInfo();
			}
		});
		this.side50Pixel = new JRadioButton(PuzzleProperties
				.getLocalized("size50px"));
		this.side50Pixel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				puzzlePieceSize = 50;
				retrieveAndShowImageInfo();
			}
		});
		this.side60Pixel = new JRadioButton(PuzzleProperties
				.getLocalized("size60px"));
		this.side60Pixel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				puzzlePieceSize = 60;
				retrieveAndShowImageInfo();
			}
		});
		
		this.side100Pixel = new JRadioButton(PuzzleProperties
				.getLocalized("size100px"));
		this.side100Pixel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				puzzlePieceSize = 100;
				retrieveAndShowImageInfo();
			}
		});
		
		this.side200Pixel = new JRadioButton(PuzzleProperties
				.getLocalized("size200px"));
		this.side200Pixel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				puzzlePieceSize = 200;
				retrieveAndShowImageInfo();
			}
		});

		this.allowTurn = new JCheckBox(PuzzleProperties
				.getLocalized("optionAllowTurn"));

		this.startButton = new JButton(PuzzleProperties
				.getLocalized("newGameDialogStart"));
		this.startButton.setBackground(new Color(173,255,47));
		this.startButton.setOpaque(true);

		this.startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				startClicked();
			}
		});

		this.cancelButton = new JButton(PuzzleProperties
				.getLocalized("newGameDialogCancel"));
		this.cancelButton.setBackground(new Color(255,160,122));
		this.cancelButton.setOpaque(true);

		this.cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cancelClicked();
			}
		});

		this.findFileButton = new JButton(PuzzleProperties
				.getLocalized("newGameDialogFindFile"));
		this.findFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					findFileClicked();
				} catch (JigsawPuzzleException e) {
					logger.error("couldn't load image: " + e.toString());
					showErrorMessage(PuzzleProperties.getLocalized("loadImageErrorTitle"), PuzzleProperties.getLocalized("loadImageErrorMessage"));
				}
			}
		});
	}

	private void initComponents() {
		this.setLayout(new BorderLayout());

		ButtonGroup sideLengthButtonGroup = new ButtonGroup();

		// add buttons to side Length group
		// adiciona botões ao grupo de comprimento lateral
		sideLengthButtonGroup.add(this.side20Pixel);
		sideLengthButtonGroup.add(this.side30Pixel);
		sideLengthButtonGroup.add(this.side40Pixel);
		sideLengthButtonGroup.add(this.side50Pixel);
		sideLengthButtonGroup.add(this.side60Pixel);
		sideLengthButtonGroup.add(this.side100Pixel);
		sideLengthButtonGroup.add(this.side200Pixel);

		JLabel pieceSizeLabel = new JLabel(PuzzleProperties
				.getLocalized("newGameDialogPieceSize"));
		JLabel additionalOptionsLabel = new JLabel(PuzzleProperties
				.getLocalized("newGameDialogOptions"));

		this.previewPanel = new PreviewPanel();
		this.previewPanel.setPreferredSize(this.previewPanelSize);
		this.previewPanel.setSize(this.previewPanelSize);

		this.imageInfo = new JLabel(PuzzleProperties
				.getLocalized("newGameDialogImageInfo"));

		JLabel edgeLabel = new JLabel(PuzzleProperties.getLocalized("newGameDialogEdges"));
		
		this.standardEdge = new JRadioButton(PuzzleProperties
				.getLocalized("edgeStandard"));
		this.flatEdge = new JRadioButton(PuzzleProperties
				.getLocalized("edgeFlat"));
		this.modernEdge = new JRadioButton(PuzzleProperties
				.getLocalized("edgeModern"));
		
		ButtonGroup edgeButtonGroup = new ButtonGroup();
		edgeButtonGroup.add(this.standardEdge);
		edgeButtonGroup.add(this.flatEdge);
		edgeButtonGroup.add(this.modernEdge);
		
		JPanel topPanel, bottomPanel;
		topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

		bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		JPanel topLeftPanel, topRightPanel;
		topLeftPanel = new JPanel();
		topLeftPanel
				.setLayout(new BoxLayout(topLeftPanel, BoxLayout.PAGE_AXIS));

		topLeftPanel.add(this.previewPanel);

		topRightPanel = new JPanel();
		topRightPanel.setLayout(new BoxLayout(topRightPanel,
				BoxLayout.PAGE_AXIS));

		topRightPanel.add(pieceSizeLabel);
		topRightPanel.add(this.side20Pixel);
		topRightPanel.add(this.side30Pixel);
		topRightPanel.add(this.side40Pixel);
		topRightPanel.add(this.side50Pixel);
		topRightPanel.add(this.side60Pixel);
		topRightPanel.add(this.side100Pixel);
		topRightPanel.add(this.side200Pixel);
		topRightPanel.add(new JSeparator());
		topRightPanel.add(additionalOptionsLabel);
		topRightPanel.add(this.allowTurn);
		topRightPanel.add(edgeLabel);
		topRightPanel.add(this.standardEdge);
		topRightPanel.add(this.flatEdge);
		topRightPanel.add(this.modernEdge);
		topRightPanel.add(new JSeparator());
		topRightPanel.add(this.imageInfo);
		topRightPanel.add(this.findFileButton);

		topPanel.add(topLeftPanel);
		topPanel.add(topRightPanel);

		bottomPanel.add(this.startButton);
		bottomPanel.add(this.cancelButton);

		this.add(topPanel, BorderLayout.CENTER);
		this.add(bottomPanel, BorderLayout.SOUTH);
		this.pack();
	}

	
	private void startClicked() {

		if (this.image == null)
			return;

		int choosenSideLength = getPuzzlePieceSize();

		boolean goodqualtitiy = this.image
				.isResizableToGoodQuality(choosenSideLength); // inits

		if (!goodqualtitiy) {
			String message = PuzzleProperties
					.getLocalized("noGoodQualityMessage");
			String header = PuzzleProperties
					.getLocalized("noGoodQualityHeader");
			int opcao =  JOptionPane.showConfirmDialog(this, message, header, JOptionPane.YES_NO_OPTION);
			
			if(opcao != 0) {
				return;
			}
		}
		this.image.resize(); // finally resize the image
							 // finalmente redimensionar a imagem

		boolean allowTurn = this.allowTurn.isSelected();

		AbstractEdgeProducer aep = getEdgeProducer();
		
		GamePreferences pref;
		pref = new GamePreferences(choosenSideLength, this.image, allowTurn,
				false, false, false, true, aep);
		pref.calcDeducedAttributes();
		pref.calcInitialPieces();

		try {
			GameCommander.getInstance().newGame(pref);
		} catch (JigsawPuzzleException e) {
			logger.error("could not instantiate new game: " + e.toString());
			showErrorMessage(PuzzleProperties.getLocalized("newGameDialogNewGameErrorTitle"), PuzzleProperties.getLocalized("newGameDialogNewGameErrorMessage"));
		}
		this.setVisible(false);
	}

	private int getPuzzlePieceSize() {
		return this.puzzlePieceSize;
	}
	
	private AbstractEdgeProducer getEdgeProducer() {
		if (this.standardEdge.isSelected()) {
			return new StandardEdgeProducer();
		} else if (this.flatEdge.isSelected()) {
			return new FlatEdgeProducer();
		} else if (this.modernEdge.isSelected()) {
			return new ModernEdgeProducer();
		} else {
			return null;
		}
	}

	private void cancelClicked() {
		this.setVisible(false);
	}

	private void findFileClicked() throws JigsawPuzzleException {

		if (this.imgFileChoo == null)
			initDialog();
		this.imgFileChoo.setVisible(true);
		if (this.imgFileChoo.showDialog(this, PuzzleProperties
				.getLocalized("loadImageFile")) == JFileChooser.APPROVE_OPTION) {
			this.currentFile = this.imgFileChoo.getSelectedFile();
			this.image = new PuzzleImage(this.currentFile);

			Image load = this.image.resizeToFit(this.previewPanel.getSize());

			retrieveAndShowImageInfo();

			this.previewPanel.loadImage(load);
		}
		this.repaint();
		this.imgFileChoo.setVisible(false);
	}

	/**
	 * retrive informations on the actual image file and show should show x, y,
	 * the image to fit in the window not in other ratio and the resulting
	 * puzzle pieces
	 */
	/**
	 * recuperar informações sobre o arquivo de imagem real e mostrar deve mostrar x, y,
	 * a imagem para caber na janela não em outra proporção e o resultado
	 * peças de quebra-cabeças
	 */
	private void retrieveAndShowImageInfo() {

		if (this.image == null) {
			return;
		}

		StringBuffer text = new StringBuffer();
		boolean is = this.image.isResizableToGoodQuality(getPuzzlePieceSize());

		Dimension d = this.image.getResampleSize();
		int pieces = (d.height / this.puzzlePieceSize)
				* (d.width / this.puzzlePieceSize);

		text.append(PuzzleProperties.getLocalized("puzzlePieces"));
		text.append(" " + pieces);
		if (!is) {
			text.append(" "
					+ PuzzleProperties
							.getLocalized("newGameDialogImageInfoProblem"));
		}
		this.imageInfo.setText(text.toString());
		this.repaint();
	}

	private void initDialog() {
		this.imgFileChoo = new JFileChooser(this.getClass().getClassLoader().getResource("").getPath() + "/images");
		this.imgFileChoo.setDialogTitle(PuzzleProperties
				.getLocalized("loadingImageFile"));
		this.imgFileChoo.setMultiSelectionEnabled(false);

		this.imgFileChoo.addChoosableFileFilter(new ImageFilter());
		this.imgFileChoo.setAcceptAllFileFilterUsed(false);

		this.imgFileChoo.setAccessory(new ImagePreview(this.imgFileChoo));
	}

	private final class PreviewPanel extends JPanel {

		private final Border border = BorderFactory
				.createEtchedBorder(EtchedBorder.RAISED);

		private JLabel label;

		PreviewPanel() {
			setResizable(false);
			this.setLayout(new BorderLayout());
			this.setBorder(this.border);
			this.label = new JLabel();
			this.label.setText(PuzzleProperties.getLocalized("noImagePresent"));
			this.add(this.label, BorderLayout.CENTER);
		}

		/**
		 * load this image (having right size) into the center of preview
		 * 
		 * @param img
		 */
		/**
		 * carregue esta imagem (com o tamanho certo) no centro da visualização
		 * 
		 * @param img
		 */
		void loadImage(Image img) {
			this.label.setIcon(new ImageIcon(img));
			// this.setSize(previewPanelSize);
			this.setPreferredSize(previewPanelSize);
			this.repaint();
		}

		/**
		 * unloads this image and resets the noImagePresent String from the
		 * current language defaults
		 */
		/**
		 * descarrega esta imagem e redefine a string noImagePresent do
		 * padrões de idioma atuais
		 */
		void unloadImage() {
			this.label.setText(PuzzleProperties.getLocalized("noImagePresent"));
		}

	}

}
