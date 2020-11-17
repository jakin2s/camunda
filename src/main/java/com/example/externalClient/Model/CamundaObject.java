package com.example.externalClient.Model;

public class CamundaObject {

	private final long startzeit;
	private final long endzeit;
	private final long durchlaufzeit;
	

	public CamundaObject( long startzeit, long endzeit, long durchlaufzeit) {

		this.endzeit = endzeit;
		this.startzeit = startzeit;
		this.durchlaufzeit = durchlaufzeit;
	}

	public long getStartzeit() {
		return startzeit;
	}

	public long getEndzeit() {
		return endzeit;
	}

	public long getDurchlaufzeit() {
		return durchlaufzeit;
	}
	
}
