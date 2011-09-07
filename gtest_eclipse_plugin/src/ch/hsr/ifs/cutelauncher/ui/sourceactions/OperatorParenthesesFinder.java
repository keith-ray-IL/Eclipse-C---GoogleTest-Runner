package ch.hsr.ifs.cutelauncher.ui.sourceactions;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;

public class OperatorParenthesesFinder extends ASTVisitor {
	ArrayList<IASTName> al=new ArrayList<IASTName>();
	
	{
		shouldVisitNames=true;
		//shouldVisitDeclarators=true;
	}
	
	@Override
	public int leave(IASTName name) {
		if(name.toString().equals("operator ()")){
			if(name.getParent() instanceof ICPPASTFunctionDeclarator){
				ICPPASTFunctionDeclarator fdeclarator=(ICPPASTFunctionDeclarator)name.getParent();
				IASTParameterDeclaration fpara[]=fdeclarator.getParameters();
				if(!fdeclarator.takesVarArgs() && fpara.length==0)al.add(name);
			}
		}
		return super.leave(name);
	}
	public ArrayList<IASTName> getAL(){return al;}
	
	public void printParent(){
		for(Object i:al){
			IASTName nn=(IASTName)i;
			
			IASTDeclaration declaration=(IASTDeclaration)nn.getParent().getParent();
			
			if(declaration instanceof IASTSimpleDeclaration){
				String name=ASTHelper.getClassStructName((IASTSimpleDeclaration)declaration);
				if(name.equals("")){
					declaration=(IASTDeclaration)declaration.getParent().getParent();
					String name2=ASTHelper.getClassStructName((IASTSimpleDeclaration)declaration);
					System.out.println(name2);
				}else System.out.println(name);
					
			}
			else if(declaration instanceof IASTFunctionDefinition){
				IASTDeclaration declaration1=(IASTDeclaration)declaration.getParent().getParent();	
				System.out.println(ASTHelper.getClassStructName((IASTSimpleDeclaration)declaration1));
			}else{
				System.out.println(declaration);
			}
		}
		
	}
}
