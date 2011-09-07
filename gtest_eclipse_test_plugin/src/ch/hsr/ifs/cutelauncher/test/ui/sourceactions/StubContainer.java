package ch.hsr.ifs.cutelauncher.test.ui.sourceactions;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;

import ch.hsr.ifs.cutelauncher.ui.sourceactions.IAddMemberContainer;
import ch.hsr.ifs.cutelauncher.ui.sourceactions.IAddMemberMethod;

public class StubContainer implements IAddMemberContainer {

	String name, classTypeName;
	private final ArrayList<IAddMemberMethod> methods=new ArrayList<IAddMemberMethod>();
	public boolean isInstance=false;
		
	public StubContainer(String s, boolean isInstance2){
		this.name=s;
		this.isInstance=isInstance2;
		this.classTypeName="foo";
	}
	@Override
	public String toString(){
		return name;
	}
	public void add(Object element){methods.add((IAddMemberMethod)element);}

	public ArrayList<IAddMemberMethod> getMethods() {
		// TODO Auto-generated method stub
		return null;
	}
	public IASTSimpleDeclaration getSimpleDeclaration() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setMethods(ArrayList<IAddMemberMethod> methods) {
		// TODO Auto-generated method stub
	}
	public void setSimpleDeclaration(IASTSimpleDeclaration simpleDeclaration) {
		// TODO Auto-generated method stub
	}
	public boolean isInstance(){
		return isInstance;
	}
	public String getClassTypeName(){
		return classTypeName;
	}
}
