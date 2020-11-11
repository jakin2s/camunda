package com.example.externalClient.Model;

public class CamundaObject {
	

	private final long durchlaufzeit;
	private final long startzeit;
	private final long endzeit;
	
	
	
	public CamundaObject( long startzeit, long endzeit, long durchlaufzeit) {
		
		this.durchlaufzeit = durchlaufzeit;
		this.endzeit = endzeit;
		this.startzeit = startzeit;
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
