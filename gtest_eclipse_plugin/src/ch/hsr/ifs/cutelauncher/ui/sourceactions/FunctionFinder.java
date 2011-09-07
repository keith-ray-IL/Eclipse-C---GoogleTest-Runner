package ch.hsr.ifs.cutelauncher.ui.sourceactions;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;

public class FunctionFinder extends ASTVisitor {
	ArrayList<IASTDeclaration> al=new ArrayList<IASTDeclaration>();
	ArrayList<IASTSimpleDeclaration> alSimpleDeclarationOnly=new ArrayList<IASTSimpleDeclaration>();
	boolean parseForSimpleDeclaration=false;
	ArrayList<IASTSimpleDeclaration> alClassStructOnly=new ArrayList<IASTSimpleDeclaration>();
	ArrayList<IASTSimpleDeclaration> alVariables=new ArrayList<IASTSimpleDeclaration>();
	
	boolean parseClassStructOnly=false;
	
	{
		shouldVisitDeclarations=true;//Visbility, SimpleDeclaration,TemplateDeclaration, Function Defn
	}
	
	@Override
	public int leave(IASTDeclaration declaration) {
		al.add(declaration);
		return super.leave(declaration);		
	}
	public ArrayList getAL(){return al;}
	
	public ArrayList<IASTSimpleDeclaration> getSimpleDeclaration(){
		if(!parseForSimpleDeclaration){
			for(IASTDeclaration i:al){
				if(i instanceof IASTSimpleDeclaration)alSimpleDeclarationOnly.add((IASTSimpleDeclaration)i);
			}
			parseForSimpleDeclaration=true;
		}
		return alSimpleDeclarationOnly;
	}
	
	//template class are also returned
	public ArrayList<IASTSimpleDeclaration> getClassStruct(){
		if(!parseClassStructOnly){
			ArrayList<IASTSimpleDeclaration> altmp=getSimpleDeclaration();
			
			for(IASTSimpleDeclaration i:altmp){
				IASTDeclSpecifier declspecifier=i.getDeclSpecifier();
				if(declspecifier != null && declspecifier instanceof ICPPASTCompositeTypeSpecifier){
					alClassStructOnly.add(i);
				}else
					alVariables.add(i);
			}
			
			parseClassStructOnly=true;
		}
		return alClassStructOnly;
	}
	public ArrayList<IASTSimpleDeclaration> getVariables(){
		getClassStruct();
		return alVariables;
	}
}
