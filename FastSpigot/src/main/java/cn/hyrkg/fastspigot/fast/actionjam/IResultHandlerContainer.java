package cn.hyrkg.fastspigot.fast.actionjam;

public class IResultHandlerContainer<E>
{
	public final IResultHandler handler;
	public E obj;

	public IResultHandlerContainer(IResultHandler theHandler, E theObj)
	{
		this.handler = theHandler;
		this.obj = theObj;
	}
}
