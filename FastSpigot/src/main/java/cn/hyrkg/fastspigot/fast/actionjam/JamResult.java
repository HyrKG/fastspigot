package cn.hyrkg.fastspigot.fast.actionjam;

import java.util.UUID;

public class JamResult
{
	public static final int tick = -1;
	public static final int successful = 0;
	public static final int exit = 1;
	public static final int extrude = 2;
	public static final int failure = 3;

	public final Object result;
	public final int resultType;
	public final UUID uuid;

	public JamResult(Object result, int type, UUID uid)
	{
		this.result = result;
		this.resultType = type;
		this.uuid = uid;
	}

	public boolean isSuccessful()
	{
		return resultType == successful;
	}

	public boolean isPlayerExited()
	{
		return resultType == exit;
	}

	public boolean isJamExtruded()
	{
		return resultType == extrude;
	}

	public boolean isFailured()
	{
		return resultType == failure;
	}

	public boolean isTicking()
	{
		return resultType == tick;
	}
}
