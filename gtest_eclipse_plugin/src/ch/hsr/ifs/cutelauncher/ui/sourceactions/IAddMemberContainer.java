package ch.hsr.ifs.cutelauncher.ui.sourceactions;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;

public interface IAddMemberContainer {

	public static final boolean InstanceType = true;
	public static final boolean ClassType = false;
	
	public abstract void add(Object element);

	public abstract String toString();
	public abstract void setSimpleDeclaration(IASTSimpleDeclaration simpleDeclaration);
	public abstract IASTSimpleDeclaration getSimpleDeclaration();
	public abstract void setMethods(ArrayList<IAddMemberMethod> methods);
	public abstract ArrayList<IAddMemberMethod> getMethods();
	public abstract boolean isInstance();
	public abstract String getClassTypeName();
}