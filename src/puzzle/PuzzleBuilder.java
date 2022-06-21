/*
 * PuzzleBauer.java
 *
 * Created on 28. August 2006, 13:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package puzzle;

import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import puzzle.edge.Edge;
import puzzle.edge.Edge.Type;
import puzzle.pieces.PuzzlePiece;
import puzzle.pieces.SinglePiece;
import puzzle.storeage.JigsawPuzzleException;
import puzzle.ui.GameMainWindow;

/**
 * @author Heinz
 */
/**
 * @autor Heinz
 */
public class PuzzleBuilder {

	private PuzzlePiece[][] pieceArray;

	/**
	 * this variable increases every time a new piece is created until the
	 * maximum index is reached
	 */
	/**
	 * esta variável aumenta cada vez que uma nova peça é criada até que o 
	 * índice máximo seja alcançado
	 */
	private int edgeNumberIndex = 0;

	private List<Point> startPointList;

	private boolean isStartPointListInit;

	private Random rand;

	public PuzzleBuilder() {
		this.rand = new Random();
		this.isStartPointListInit = false;
	}

	public void makePieces(final int columns, final int rows) throws JigsawPuzzleException {
		if ((rows < 1) || (columns < 1))
			throw new IllegalArgumentException(
					"positive number for columns and rows expected");

		Edge[][][] edgeArray = new Edge[columns][rows][4];

		for (int columnIndex = 0; columnIndex < columns; columnIndex++) { // columns
			for (int rowIndex = 0; rowIndex < rows; rowIndex++) { // rows

				// left edge:
				// borda esquerda:
				if (columnIndex == 0)
					edgeArray[columnIndex][rowIndex][3] = null; // links = 3
				else {
					edgeArray[columnIndex][rowIndex][3] = this.getNewEdge(Edge.Type.LEFT,edgeArray,columnIndex,rowIndex); // links = 3
				}

				// top edge:
				// borda superior:
				if (rowIndex == 0)
					edgeArray[columnIndex][rowIndex][0] = null; // oben = 0
				else {
					edgeArray[columnIndex][rowIndex][0] = this.getNewEdge(Edge.Type.TOP,edgeArray,columnIndex,rowIndex); // 0 = Oben
				}

				// right edge:
				// borda direita:
				if (columnIndex == columns - 1)
					edgeArray[columnIndex][rowIndex][2] = null;
				else {
					edgeArray[columnIndex][rowIndex][2] = this.getNewEdge(Edge.Type.RIGHT,edgeArray,columnIndex,rowIndex); // rechts = 2;
				}

				// bottom edge:
				// borda inferior:
				if (rowIndex == rows - 1)
					edgeArray[columnIndex][rowIndex][1] = null;
				else {
					edgeArray[columnIndex][rowIndex][1] = this.getNewEdge(Edge.Type.BOTTOM,edgeArray,columnIndex,rowIndex); // rechts = 2;
				}
			}
		}

		pieceArray = new PuzzlePiece[columns][rows];
		initStartpoints(columns, rows);
		for (int s = 0; s < columns; s++) {
			for (int z = 0; z < rows; z++) {

				pieceArray[s][z] = new SinglePiece(edgeArray[s][z],
						GameCommander.getInstance().getPreferences().getImage()
								.getImage(s, z), getRandomPointFromList());
				GameCommander.getInstance().getPieceDisposer().addPuzzleStueck(
						pieceArray[s][z]);

			}
		}

		if (GameCommander.getInstance().getPreferences().isAllowTurn())
			turnRandom();

	}
	
	private Edge getNewEdge(Type type, Edge[][][] edgeArray, int columnIndex, int rowIndex) throws JigsawPuzzleException {
		int ident;
		GeneralPath generalPath;
		
		if (type == Edge.Type.LEFT || type == Edge.Type.TOP) {
			
			if(type == Edge.Type.LEFT) {
				ident = edgeArray[columnIndex - 1][rowIndex][2].getEdgePairNumber(); // rechts = 2
			}else {
				ident = edgeArray[columnIndex][rowIndex - 1][1].getEdgePairNumber(); // unten = 1
			}
			generalPath = GameCommander.getInstance().getEdgeDisposer().findContraryEdgeShape(ident,type);
		}else {
			ident = edgeNumberIndex;
			generalPath = GameCommander.getInstance().getEdgeDisposer().generateNewEdgeShape(ident,type);
			edgeNumberIndex++;
		}
		
		return new Edge(type, ident, generalPath);
	}

	private void initStartpoints(int spalten, int zeilen) {
		this.startPointList = new Vector<Point>();
		int x = 0;
		int y = 0;

		final int sideLength = GameCommander.getInstance().getPreferences()
				.getSideLength();

		for (int s = 0; s < spalten; s++) {
			x += 2 * sideLength;
			for (int z = 0; z < zeilen; z++) {
				y += 2 * sideLength;
				startPointList.add(new Point(x, y));
			}
			y = 0;
		}
		Point last = startPointList.get(startPointList.size() - 1);

		// some boundaries
		// alguns limites
		int xbound = last.x + 400;
		int ybound = last.y + 200;

		GameMainWindow.getInstance().setBoundaries(xbound, ybound);

		// the above thing no =============================
		// a coisa acima não =============================
		this.isStartPointListInit = true;
	}

	/**
	 * if this is a game where turning pieces is allowed, this procedure will
	 * randomly turn the pieces
	 */
	/**
	 * se este é um jogo onde é permitido virar as peças, este procedimento irá 
	 * virar as peças aleatoriamente
	 */
	private void turnRandom() throws JigsawPuzzleException {
		for (PuzzlePiece[] PSA : pieceArray) {
			for (PuzzlePiece ps : PSA) {
				// generate random number of turns
				int turns = rand.nextInt(4);

				while (turns > 0) {
					ps.turnDegrees(((SinglePiece)ps).getPoint(), 90);
					turns--;
				}

			}
		}
	}

	private Point getRandomPointFromList() {
		if (!isStartPointListInit)
			throw new RuntimeException("pointlist not been initiated");

		int random = rand.nextInt(this.startPointList.size());

		Point pt = startPointList.get(random);
		startPointList.remove(random);

		return pt;

	}

}
