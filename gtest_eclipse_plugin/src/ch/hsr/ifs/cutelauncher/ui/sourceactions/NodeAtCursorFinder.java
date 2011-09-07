package ch.hsr.ifs.cutelauncher.ui.sourceactions;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;

public class NodeAtCursorFinder extends ASTVisitor {
	int selOffset,dist=Integer.MAX_VALUE;
	boolean bounded=false;
	private IASTNode node;

	{
		shouldVisitDeclarations=true;//simple declaration, template declaration
		shouldVisitStatements=true;//expressionstmt
	}
	public NodeAtCursorFinder(int offset){
		selOffset=offset;
	}
	@Override
	public int leave(IASTDeclaration declaration) {
		IASTFileLocation tmp=declaration.getFileLocation();
		
		if(tmp==null)return super.leave(declaration);

		int nodeOffset = tmp.getNodeOffset();
		int nodeLength = declaration.getFileLocation().asFileLocation().getNodeLength();
		if(selOffset > nodeOffset && selOffset < (nodeOffset+ nodeLength) && dist>selOffset-nodeOffset) {
			bounded=true;
			setNode(declaration);
			dist=selOffset-nodeOffset;
		}
			
		return super.leave(declaration);
	}
	@Override
	public int leave(IASTStatement statement) {
		int nodeOffset = statement.getFileLocation().getNodeOffset();
		int nodeLength = statement.getFileLocation().asFileLocation().getNodeLength();
		if(selOffset > nodeOffset && selOffset < (nodeOffset+ nodeLength) && dist>selOffset-nodeOffset) {
			bounded=true;
			setNode(statement);
			dist=selOffset-nodeOffset;
		}
		
		return super.leave(statement);
	}
	public boolean getBounded(){return bounded;}
	//retrieve the node at the current cursor location
	int count=0;
	void setNode(IASTNode node) {
		this.node = node;
		count+=1;
	}
	public int get(){return count;}
	public IASTNode getNode() {
		return node;
	}
	
}