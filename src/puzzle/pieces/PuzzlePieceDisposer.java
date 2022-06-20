/*
 * PuzzleStueckVerwalter.java
 *
 * Created on 27. August 2006, 15:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package puzzle.pieces;

import java.awt.Point;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import puzzle.edge.Edge;
import puzzle.storeage.JigsawPuzzleException;
import puzzle.storeage.LoadGameException;
import puzzle.storeage.SaveGameException;
import puzzle.storeage.StorageUtil;
import puzzle.storeage.Storeable;

/**
 * Holds the list of pusszles and all things related to the puzzle pieces.
 * @author Heinz
 */
/**
 * Cont�m a lista de gatinhos e todas as coisas relacionadas �s pe�as do quebra-cabe�a.
 * @autor Heinz
 */

public class PuzzlePieceDisposer implements Storeable {

	/**
	 * list of all puzzle currently pieces in the game
	 * initially this is a list of only consisting of single pieces
	 * if pieces are snapped together than the resulting multipiece
	 * will be included in the list and the two other will be deleted 
	 */
	/**
	 * lista de todas as pe�as do quebra-cabe�a atualmente no jogo
	 * inicialmente, esta � uma lista composta apenas por pe�as individuais
	 * se as pe�as forem encaixadas juntas do que o multipiece resultante
	 * ser� inclu�do na lista e os outros dois ser�o exclu�dos 
	 */
	private List<PuzzlePiece> puzzlePieces;

	public PuzzlePieceDisposer() {
		puzzlePieces = new Vector<PuzzlePiece>();
	}

	public int getPieceCount() {
		return puzzlePieces.size();
	}

	/**
	 * find a piece (single or multi) by its relative point)
	 * @param point the point where you search for
	 * @return one piece that lies under that point.
	 * If there are more than one under that point
	 * it will return an arbitrary one. If none found
	 * it returns null.
	 */
	/**
	 * encontre uma pe�a (�nica ou m�ltipla) por seu ponto relativo)
	 * @param aponta o ponto onde voc� procura
	 * @return uma pe�a que se encontra sob esse ponto.
	 * Se houver mais de um nesse ponto
	 * ele retornar� um arbitr�rio. Se nenhum for encontrado
	 * retorna nulo.
	 */
	public PuzzlePiece findbyPoint(Point point) {
		for (PuzzlePiece ps : puzzlePieces) {
			if (ps.isHit(point)) {

				int psint = puzzlePieces.lastIndexOf(ps);
				/* swap the current with the last piece
				 * this is done to make sure that the
				 * piece that was found will be painted
				 * last - never will be in background!
				 */
				/* troque a corrente com a �ltima pe�a
				 * isso � feito para garantir que o
				 * pe�a que foi encontrada ser� pintada
				 * �ltimo - nunca estar� em segundo plano!
				 */
				this.puzzlePieces.set(psint, puzzlePieces.get(puzzlePieces
						.size() - 1));
				this.puzzlePieces.set(puzzlePieces.size() - 1, ps);
				return ps;
			}
		}
		return null;
	}

	public void addPuzzleStueck(PuzzlePiece PS) {
		if (PS == null)
			throw new NullPointerException("PS null");
		puzzlePieces.add(PS);
	}

	/**
	 * Assambles two pieces by adding the smaller to the bigger one. The
	 * provided edges will be closed, if the procedure finds more related edges
	 * for those pieces it will close them also. Returns the resulting piece
	 * (the biggest multipiece or a new multipiece if both are singles
	 * @throws JigsawPuzzleException 
	 * 
	 */
	/**
	 * Combina duas pe�as adicionando a menor � maior. O
	 * as bordas fornecidas ser�o fechadas, se o procedimento encontrar mais bordas relacionadas
	 * para essas pe�as ir� fech�-las tamb�m. Retorna a pe�a resultante
	 * (a maior pe�a m�ltipla ou uma nova pe�a m�ltipla se ambos forem solteiros
	 * @throws JigsawPuzzleException 
	 * 
	 */
	public PuzzlePiece assamblyPieces(PuzzlePiece pp1, Edge pp1e,
			PuzzlePiece pp2, Edge pp2e) throws JigsawPuzzleException {
		if (pp1 == null)
			throw new NullPointerException("ps1 null");
		if (pp2 == null)
			throw new NullPointerException("ps2 null");

		MultiPiece biggerPiece; // this piece will be returned
								// esta pe�a ser� devolvida
		PuzzlePiece smallerPiece;
		boolean bothSinglss = false;

		if (pp1.getPieceCount() > pp2.getPieceCount()) {
			// pp1 bigger
			// pp1 maior
			biggerPiece = (MultiPiece) pp1;
			smallerPiece = pp2;
		} else if (pp1.getPieceCount() < pp2.getPieceCount()) {
			// pp2 bigger
			// pp2 maior
			biggerPiece = (MultiPiece) pp2;
			smallerPiece = pp1;
		} else if (pp2.getPieceCount() == 1) {
			// both singles
			// ambos solteiros
			pp1e.close();
			pp2e.close();
			biggerPiece = new MultiPiece((SinglePiece) pp1, (SinglePiece) pp2);
			smallerPiece = null;
			bothSinglss = true;
		} else {
			// both same size but not singles
			// ambos do mesmo tamanho, mas n�o solteiros
			biggerPiece = (MultiPiece) pp1;
			smallerPiece = pp2;
		}

		/* find all edges that should be closed, because they are not the two
		* provided as params but are others that are alos to be closed
		* be sure never to close NULL type edges because they cannot be closed! 
		*/
		/* encontre todas as arestas que devem ser fechadas, porque n�o s�o as duas
		 * fornecido como par�metros, mas s�o outros que tamb�m devem ser fechados
		 * certifique-se de nunca fechar as bordas do tipo NULL porque elas n�o podem ser fechadas!
		*/
		if (!bothSinglss) {
			int maxcount = Integer.MAX_VALUE;
			List<Edge> smallerPieceOpenEdges = smallerPiece.getOpenEdges();

			if (smallerPiece instanceof SinglePiece)
				maxcount = 4;
			else if (smallerPiece instanceof MultiPiece)
				maxcount = smallerPieceOpenEdges.size();

			for (Edge openEdgeBiggerPiece : biggerPiece.getOpenEdges()) {

				if (maxcount == 0)
					break;

				int edgeNumber = openEdgeBiggerPiece.getEdgePairNumber();
				Edge.Type contraryEdgeNumber = Edge.contraryEdgeChar(openEdgeBiggerPiece.getType());
				Edge contrary = null;

				for (Edge possibleContraryEdge : smallerPieceOpenEdges)
					if (possibleContraryEdge.getEdgePairNumber() == edgeNumber) {
						contrary = possibleContraryEdge;
						smallerPieceOpenEdges.remove(possibleContraryEdge);
						break;
					}

				if (contrary == null)
					continue;
				if (contrary.getType() != contraryEdgeNumber)
					throw new JigsawPuzzleException(
							"could not add, because edges are turned twisted");

				if (openEdgeBiggerPiece.getType() != Edge.Type.NULL) { // never close NULL type edges.
																	   // nunca feche as bordas do tipo NULL.
					openEdgeBiggerPiece.close();
					contrary.close();
					maxcount--; // another pair assembled
								// outro par montado
				}
				
			}
		}

		biggerPiece.addPiece(smallerPiece);

		this.puzzlePieces.remove(pp1);
		this.puzzlePieces.remove(pp2);
		this.puzzlePieces.add(biggerPiece);

		return biggerPiece;
	}

	public List<PuzzlePiece> getPuzzlePieces() {
		return this.puzzlePieces;
	}

	public boolean ends() {
		//Removido ifs
		// Removido ifs
		return (this.puzzlePieces.size() == 1);
	}

	public void reset() {
		this.puzzlePieces.clear();
	}

	@Override
	public void restore(Node current) throws LoadGameException {
		Node pieceDisposer = StorageUtil.findDirectChildNode(current, "PieceDisposer");
		
		Node piece;
		NodeList childs = pieceDisposer.getChildNodes();
		int size = childs.getLength();
		for (int i = 0; i<size; i++) {
			piece = childs.item(i);
			if (piece.getNodeName().equals("PuzzlePiece")) {
				
				Node multi = StorageUtil.findDirectChildNode(piece, "MultiPiece");
				Node single = StorageUtil.findDirectChildNode(piece, "SinglePiece");
				
				PuzzlePiece puzzlePiece = null;
				if (single != null) {
					puzzlePiece = new SinglePiece();
				} else if (multi != null) {
					puzzlePiece = new MultiPiece();
				} else {
					throw new LoadGameException("Neither a single nor a multipiece was found");
				}
				puzzlePiece.restore(piece);
				this.addPuzzleStueck(puzzlePiece);
			}
		}
	}

	@Override
	public void store(Node current) throws SaveGameException {
		Document doc = current.getOwnerDocument();
		Node pieceDisposer = doc.createElement("PieceDisposer");
		
		Node piece;
		for (PuzzlePiece p : this.puzzlePieces) {
			piece = doc.createElement("PuzzlePiece");
			p.store(piece);
			pieceDisposer.appendChild(piece);
		}
		current.appendChild(pieceDisposer);
	}

}
