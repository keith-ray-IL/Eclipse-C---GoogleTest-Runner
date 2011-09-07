package ch.hsr.ifs.cutelauncher.test.ui.sourceactions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
public class MyDynamicProxyClass implements java.lang.reflect.InvocationHandler
{

	Object obj;
	public static HashSet<String> hs=new HashSet<String>();
	public MyDynamicProxyClass(Object obj)
	{ this.obj = obj; }

	public Object invoke(Object proxy, Method m, Object[] args) throws Throwable
	{
		try {
			
			if(args!=null && args.length>0){
				if(proxy==args[0] && m.toString().equals("public boolean java.lang.Object.equals(java.lang.Object)"))
					return true;
			}
			//Beware, it is obj, not via proxy
			//invoke(proxy,args) results in recursive loop
			//System.out.println(m);
			hs.add(m.toString());
			
			return m.invoke(obj, args);

			// do something
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		} catch (Exception e) {
			throw e;
		}
		// return something

	}

	static public Object newInstance(Object obj, Class[] interfaces)
	{
		return java.lang.reflect.Proxy.newProxyInstance(obj.getClass().getClassLoader(),
				interfaces,
				new MyDynamicProxyClass(obj));
	}
	public static HashSet getUniqueCall(){
		return hs;
	}
	public static void printUniqueCall(){
		for(String i:hs){
			System.out.println(i);
		}
	}
}
//@see http://www.javaworld.com/javaworld/jw-11-2000/jw-1110-proxy.html?page=1
//Explore the Dynamic Proxy API