package com.example.externalClient.Model;

import lombok.*;

import javax.annotation.Generated;

@Setter@Getter
@ToString
@AllArgsConstructor@NoArgsConstructor
@Builder
public class CamundaObject {

	private long camundaObject_id;
	private long startzeit;
	private long endzeit;
	private long durchlaufzeit_nano;
	private long durchlaufzeit_ms;
	private long durchlaufzeit_sec;

	public CamundaObject(long startzeit, long endzeit, long durchlaufzeit_nano, long durchlaufzeit_ms, long durchlaufzeit_sec) {
		this.startzeit = startzeit;
		this.endzeit = endzeit;
		this.durchlaufzeit_nano = durchlaufzeit_nano;
		this.durchlaufzeit_ms = durchlaufzeit_ms;
		this.durchlaufzeit_sec = durchlaufzeit_sec;
	}
}
