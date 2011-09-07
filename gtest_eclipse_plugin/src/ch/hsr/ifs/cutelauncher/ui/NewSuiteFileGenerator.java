package ch.hsr.ifs.cutelauncher.ui;

import java.util.ArrayList;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class NewSuiteFileGenerator {
	IASTTranslationUnit tu;
	public NewSuiteFileGenerator(IFile cppFile){
		try{
		tu=getASTTranslationUnit(cppFile);
		}catch(CoreException e){
			e.printStackTrace();
		}
	}

	public void parse(){
		TemplateVisitor tv=new TemplateVisitor();
		tu.accept(tv);	
		ArrayList al=tv.al;
		System.out.println();
	}
	
	//return the CDT representation of the file under modification 
	protected IASTTranslationUnit getASTTranslationUnit(IFile editorFile)
			throws CoreException {
		ITranslationUnit tu = CoreModelUtil.findTranslationUnit(editorFile);
		IIndex index = CCorePlugin.getIndexManager().getIndex(tu.getCProject());	
		IASTTranslationUnit astTu = tu.getAST(index, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
		return astTu;
	}
	
}

class TemplateVisitor extends ASTVisitor{
	{
		shouldVisitNames =true;
	}
	public ArrayList al=new java.util.ArrayList();
	
	@Override
	public int visit(IASTName name) {
		if(name instanceof ICPPASTTemplateId){
			ICPPASTTemplateId t=(ICPPASTTemplateId)name;
			IASTName name1=t.getTemplateName();
			if(name1.toString().equals("cute::runner"))
				al.add(name);
			System.out.println(name1.toString());
		}
		return PROCESS_CONTINUE;
	}
	
}