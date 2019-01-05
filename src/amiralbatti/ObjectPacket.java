package AmiralBatti;

import java.awt.Point;
import java.io.Serializable;

/**
 * Client ve server arası Exchange için
 *
 * Author : Mahmud Kasım
 */

class ObjectPacket implements Serializable
{
	// Gönderilen datanın tipi
	public static final int	STRING			= 1,
							POINT			= 2,
							PLAYING_FIELD	= 3,
							COMMAND			= 4,
							INTEGER			= 5;

	private int typeData;
	private String message;
	private Point point;
	private PlayingField field;
	private Integer integer;


	// integer constructor
	public ObjectPacket(Integer integer)
	{
		typeData = INTEGER;
		message = null;
		point = null;
		field = null;
		this.integer = integer;
	}

	// string constructor
	public ObjectPacket(String message)
	{
		typeData = STRING;
		this.message = message;
		point = null;
		field = null;
		integer = null;
	}

	// point constructor
	public ObjectPacket(Point point)
	{
		typeData = POINT;
		message = null;
		this.point = point;
		field = null;
		integer = null;
	}

	// playing field constructor
	public ObjectPacket(PlayingField field)
	{
		typeData = PLAYING_FIELD;
		message = null;
		point = null;
		this.field = field;
		integer = null;
	}

	// set function
	// 
	public void setCommand(boolean yes)
	{
		if (yes && (message != null)) typeData = COMMAND;
	}

	public String toString()
	{
		String string = null;
		if (message != null) string = message;
		if (point != null) string = point.toString();
		if (field != null) string = "Playing Field Object";
		return string;
	}

	// getter functions 
	public int getType()							{	return typeData;		}
	public String getMessage()					{	return message;		}
	public Point getPoint()						{	return point;			}
	public PlayingField getPlayingField()	{	return field;			}
	public Integer getInteger()				{	return integer;		}
}