package com.sevael.lgtool.model;

public class DashboardTopServiceModel {

	private String name;
	private int position;
	private int prevposition;
	private int value;
	
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPrevposition() {
		return prevposition;
	}
	public void setPrevposition(int prevposition) {
		this.prevposition = prevposition;
	}
	
}
