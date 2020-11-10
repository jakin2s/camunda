package com.example.externalClient.Model;

public class CamundaObject {
	
	private final long id;
	private final long durchlaufzeit;
	private final long startzeit;
	private final long endzeit;
	
	public CamundaObject(long id, long startzeit, long endzeit, long durchlaufzeit) {
		this.id = id;
		this.durchlaufzeit = durchlaufzeit;
		this.endzeit = endzeit;
		this.startzeit = startzeit;
	}
	
	public long getId() {
		return id;
	}

	public long getDurchlaufzeit() {
		return durchlaufzeit;
	}

	public long getStartzeit() {
		return startzeit;
	}

	public long getEndzeit() {
		return endzeit;
	}
	
}
